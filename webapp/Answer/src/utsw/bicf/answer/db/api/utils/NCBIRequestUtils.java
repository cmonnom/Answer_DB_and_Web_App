package utsw.bicf.answer.db.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.model.extmapping.ncbigene.ESearch;
import utsw.bicf.answer.model.extmapping.pubmed.EPost;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.aop.AOPAspect;

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
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

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
		} else {
			client = HttpClientBuilder.create().build();
		}
	}

	public List<PubMed> getPubmedDetails(Set<String> pmIds, ModelDAO modelDAO)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
//		logger.info("Requesting PubMed details: " + pmIds.stream().collect(Collectors.joining(" ")));
		List<PubMed> pubmeds = new ArrayList<PubMed>();
		String pmIdParam = pmIds.stream().collect(Collectors.joining(",")).replaceAll(" ", "");
		StringBuilder sbUrl = new StringBuilder(ncbiProps.getUrl());
		sbUrl.append(ncbiProps.getEpost()).append(pmIdParam);
		// sbUrl.append(this.getEmailAndTool());
		sbUrl.append(this.getAPIKey());
		URI uri = new URI(sbUrl.toString());
		// System.out.println(uri.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

//		logger.info("URI " + uri);
//		logger.info(context.getAttribute("http.connection"));
//		logger.info(context.getAttribute("http.route"));

		int statusCode = response.getStatusLine().getStatusCode();
//		logger.info("status code: " + statusCode);
		if (statusCode == HttpStatus.SC_OK) {
			InputStreamReader isReader = new InputStreamReader(response.getEntity().getContent());
			// Creating a BufferedReader object
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb1 = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb1.append(str);
			}
//			logger.info(sb1.toString());
			Document doc = null;
//			If for some reason NCBI is returning an error page (could be because our IP got blocked)
//			the code will switch to a local copy of PubMed which need to be manually curated
//			see MySQL table temp_pubmed
			if (sb1.toString().contains("DOCTYPE html")) { // something is wrong (NBCI is blocking us?)
				logger.info("using local cache");
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				String xmlDoc = modelDAO.getPubmedContent().trim();
				InputStream targetStream = new ByteArrayInputStream(xmlDoc.getBytes());
				doc = builder.parse(targetStream);
			} else {
				EPost ePost = mapper.readValue(sb1.toString(), EPost.class);
//				logger.info("epost webenv: " +ePost.getWebEnv() );
				sbUrl = new StringBuilder(ncbiProps.getUrl());
				sbUrl.append(ncbiProps.getEsummary()).append(ncbiProps.getQueryKey()).append("=")
						.append(ePost.getQueryKey()).append("&").append(ncbiProps.getWebEnv()).append("=")
						.append(ePost.getWebEnv()).append("&retype=xml");
				// sbUrl.append(this.getEmailAndTool());
				sbUrl.append(this.getAPIKey());
				uri = new URI(sbUrl.toString());
				requestGet = new HttpGet(uri);
				HttpResponse response2 = client.execute(requestGet);
				statusCode = response2.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();

					doc = builder.parse(response2.getEntity().getContent());

				}
				else {
					logger.info("Something went wrong NCBIRequest:152 HTTP_STATUS: " + statusCode);
				}
			}
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
				boolean isValidPmId = true;

				NodeList children = node.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node n = children.item(j);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element elt = (Element) n;
						if (elt.getNodeName().equals("Id")) {
							String pubmedId = elt.getFirstChild().getNodeValue();
							if (!pmIds.contains(pubmedId)) {
								isValidPmId = false;
							}
							pubmed.setPmid(pubmedId);
						} else if (elt.getNodeName().equals("Item")) {
							if (elt.getAttribute("Name").equals("EPubDate")) { // could be either date
								if (elt.hasChildNodes()) {
									if (date == null || date.length() < elt.getFirstChild().getNodeValue().length()) {
										date = this.getNodeValue(elt);
									}
								}
							}
							if (elt.getAttribute("Name").equals("PubDate")) { // could be either date
								if (elt.hasChildNodes()) {
									if (date == null || date.length() < elt.getFirstChild().getNodeValue().length()) {
										date = this.getNodeValue(elt);
									}
								}
							} else if (elt.getAttribute("Name").equals("Source")) {
								source = this.getNodeValue(elt);
							} else if (elt.getAttribute("Name").equals("Volume")) {
								volume = this.getNodeValue(elt);
							} else if (elt.getAttribute("Name").equals("Issue")) {
								issue = this.getNodeValue(elt);
							} else if (elt.getAttribute("Name").equals("Pages")) {
								pages = this.getNodeValue(elt);
							} else if (elt.getAttribute("Name").equals("Title")) {
								pubmed.setTitle(this.getNodeValue(elt));
							} else if (elt.getAttribute("Name").equals("AuthorList")) {
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
				// all values should have been populated by now
				pubmed.setDate(date);
				StringBuilder sb = new StringBuilder();
				sb.append(source).append(". ").append(pubmed.getDate()).append(";").append(volume).append("(")
						.append(issue).append(")").append(":").append(pages).append(".");
				pubmed.setDescription(sb.toString());
				pubmed.setAuthors(authors.stream().collect(Collectors.joining(", ")));
				if (isValidPmId) {
					pubmeds.add(pubmed);
				}
			}
			reader.close();
		}
		else {
			logger.info("Something went wrong NCBIRequest:229 HTTP_STATUS: " + statusCode);
		}
		logger.info("Pubmeds count: " + pubmeds.size());
		return pubmeds;
	}
	
	public LookupSummary getGeneSummary(String geneTerm, ModelDAO modelDAO)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(ncbiProps.getUrl());
		sbUrl.append(ncbiProps.getEsearchGene()).append(geneTerm);
		sbUrl.append(this.getAPIKey());
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			InputStreamReader isReader = new InputStreamReader(response.getEntity().getContent());
			// Creating a BufferedReader object
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb1 = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb1.append(str);
			}
			Document doc = null;
//			If for some reason NCBI is returning an error page (could be because our IP got blocked)
//			the code will switch to a local copy of PubMed which need to be manually curated
//			see MySQL table temp_pubmed
			if (sb1.toString().contains("DOCTYPE html")) { // something is wrong (NBCI is blocking us?)
				logger.info("using local cache");
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				String xmlDoc = modelDAO.getPubmedContent().trim();
				InputStream targetStream = new ByteArrayInputStream(xmlDoc.getBytes());
				doc = builder.parse(targetStream);
			} else {
				ESearch eSearch = mapper.readValue(sb1.toString(), ESearch.class);
				if ( eSearch.getIdList() != null && ! eSearch.getIdList().isEmpty()) {
					summary.setMoreInfoUrl(ncbiProps.getNcbiGeneUrl() + eSearch.getIdList().get(0));
					sbUrl = new StringBuilder(ncbiProps.getUrl());
					sbUrl.append(ncbiProps.getEsummaryGene()).append("id=")
					.append(eSearch.getIdList().get(0)).append("&retype=xml");
					sbUrl.append(this.getAPIKey());
					uri = new URI(sbUrl.toString());
					requestGet = new HttpGet(uri);
					HttpResponse response2 = client.execute(requestGet);
					statusCode = response2.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						
						doc = builder.parse(response2.getEntity().getContent());
						
					}
				}
				else {
					logger.info("Something went wrong NCBIRequest:152 HTTP_STATUS: " + statusCode);
				}
			}
			NodeList docSums = doc.getElementsByTagName("Summary");
			for (int i = 0; i < docSums.getLength(); i++) {
				summary.setSummary(docSums.item(i).getTextContent());
			}
			reader.close();
		}
		else {
			logger.info("Something went wrong NCBIRequest:229 HTTP_STATUS: " + statusCode);
		}
		
		return summary;
	}

	public LookupSummary getGeneSummary(String entrezId)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(ncbiProps.getUrl());
		sbUrl.append(ncbiProps.getEsummaryGene()).append("id=")
		.append(entrezId).append("&retype=xml");
		sbUrl.append(this.getAPIKey());
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document doc = builder.parse(response.getEntity().getContent());
			NodeList docSums = doc.getElementsByTagName("Summary");
			for (int i = 0; i < docSums.getLength(); i++) {
				summary.setSummary(docSums.item(i).getTextContent());
				summary.setMoreInfoUrl(ncbiProps.getNcbiGeneUrl() + entrezId);
			}
		}
		else {
			logger.info("Something went wrong NCBIRequest:152 HTTP_STATUS: " + statusCode);
		}
		return summary;
	}

	private String getNodeValue(Element elt) {
		if (elt == null || !elt.hasChildNodes()) {
			return "";
		}
		// sometimes HTML code is in the paper's title
		// need to not escape single quotes
		return policy.sanitize(StringEscapeUtils.unescapeHtml4(elt.getFirstChild().getNodeValue())).replaceAll("&#39;",
				"'");
	}

	private String getAPIKey() {
		return "&api_key=" + ncbiProps.getApiKey();
	}

}
