package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import utsw.bicf.answer.model.extmapping.pubmed.EPost;
import utsw.bicf.answer.model.extmapping.pubmed.PubmedArticle;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class NCBIRequestUtils {
	
	NCBIProperties ncbiProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();

	public NCBIRequestUtils(NCBIProperties ncbiProps, OtherProperties otherProps) {
		this.ncbiProps = ncbiProps;
		this.otherProps = otherProps;
		this.setupClient();
	}
	
	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpHost proxy = null;
	private HttpClient client = null;
	private XmlMapper mapper = new XmlMapper();
	
	private void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxy).build();
		}
		else {
			client = HttpClientBuilder.create().build();
		}
	}
	
	public List<PubMed> getPubmedDetails(Set<String> pmIds) throws URISyntaxException, ClientProtocolException, IOException, JAXBException, UnsupportedOperationException, SAXException, ParserConfigurationException {
		
		List<PubMed> pubmeds = new ArrayList<PubMed>();
		String pmIdParam = pmIds.stream().collect(Collectors.joining(",")).replaceAll(" ", "");
		StringBuilder sbUrl = new StringBuilder(ncbiProps.getUrl());
		sbUrl.append(ncbiProps.getEpost())
		.append(pmIdParam);
		sbUrl.append(this.getEmailAndTool());
		URI uri = new URI(sbUrl.toString());
//		System.out.println(uri.toString());
		requestGet = new HttpGet(uri);
		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			EPost ePost = mapper.readValue(response.getEntity().getContent(), EPost.class);
			sbUrl = new StringBuilder(ncbiProps.getUrl());
			sbUrl.append(ncbiProps.getEsummary())
			.append(ncbiProps.getQueryKey()).append("=").append(ePost.getQueryKey())
			.append("&").append(ncbiProps.getWebEnv()).append("=").append(ePost.getWebEnv())
			.append("&retype=xml");
			sbUrl.append(this.getEmailAndTool());
			uri = new URI(sbUrl.toString());
			requestGet = new HttpGet(uri);
			HttpResponse response2 = client.execute(requestGet);
			statusCode = response2.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {

				DocumentBuilderFactory factory =
						DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
				
				Document doc = builder.parse(response2.getEntity().getContent());
				NodeList docSums = doc.getElementsByTagName("DocSum");
				for (int i = 0; i < docSums.getLength(); i++) {
					Node node = docSums.item(i);
					PubMed pubmed = new PubMed();
					String source = null;
					String volume = null;
					String issue = null;
					String pages = null;
					String date = null;
					List<String> authors = new ArrayList<String>();
					
					NodeList children = node.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node n = children.item(j);
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							Element elt = (Element) n;
							if (elt.getNodeName().equals("Id")) {
								pubmed.setPmid(elt.getFirstChild().getNodeValue());
							}
							else if (elt.getNodeName().equals("Item")) {
								if (elt.getAttribute("Name").equals("EPubDate")) {  //could be either date
									if (elt.hasChildNodes()) {
										if (date == null || date.length() < elt.getFirstChild().getNodeValue().length()) {
											date =  this.getNodeValue(elt);
										}
									}
								}
								if (elt.getAttribute("Name").equals("PubDate")) { //could be either date
									if (elt.hasChildNodes()) {
										if (date == null || date.length() < elt.getFirstChild().getNodeValue().length()) {
											date =  this.getNodeValue(elt);
										}
									}
								}
								else if (elt.getAttribute("Name").equals("Source")) {
									source =  this.getNodeValue(elt);
								}
								else if (elt.getAttribute("Name").equals("Volume")) {
									volume =  this.getNodeValue(elt);
								}
								else if (elt.getAttribute("Name").equals("Issue")) {
									issue = this.getNodeValue(elt);
								}
								else if (elt.getAttribute("Name").equals("Pages")) {
									pages =  this.getNodeValue(elt);
								}
								else if (elt.getAttribute("Name").equals("Title")) {
									pubmed.setTitle(this.getNodeValue(elt));
								}
								else if (elt.getAttribute("Name").equals("AuthorList")) {
									for (int k = 0; k < elt.getChildNodes().getLength(); k++) {
										Node eltAuthor = elt.getChildNodes().item(k);
										if (eltAuthor.getNodeType() == Node.ELEMENT_NODE) {
											Element a = (Element) eltAuthor;
											if (a.hasChildNodes()) {
												authors.add(a.getFirstChild().getNodeValue());
											}
										}
									}
								}
							}
						}
					}
					//all values should have been populated by now
					pubmed.setDate(date);
					StringBuilder sb = new StringBuilder();
					sb.append(source).append(". ")
					.append(pubmed.getDate()).append(";")
					.append(volume).append("(").append(issue).append(")")
					.append(":").append(pages).append(".");
					pubmed.setDescription(sb.toString());
					pubmed.setAuthors(authors.stream().collect(Collectors.joining(", ")));
					pubmeds.add(pubmed);
				}
			}
		}
		return pubmeds;
	}

	private String getNodeValue(Element elt) {
		if (elt == null || !elt.hasChildNodes()) {
			return "";
		}
		//sometimes HTML code is in the paper's title
		//need to not escape single quotes
		return policy.sanitize(StringEscapeUtils.unescapeHtml4(elt.getFirstChild().getNodeValue())).replaceAll("&#39;", "'");
	}
	
	private String getEmailAndTool() {
		return "&email=" + ncbiProps.getEmail() + "&tool=" + ncbiProps.getTool();
	}


}
