package utsw.bicf.answer.db.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

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
import org.xml.sax.SAXException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.UniProtProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class UniProtRequestUtils {

	UniProtProperties uniprotProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public UniProtRequestUtils(UniProtProperties uniprotProps, OtherProperties otherProps) {
		this.uniprotProps = uniprotProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

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

	public LookupSummary getGeneSummary(String geneTerm)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(uniprotProps.getQueryUrl());
		sbUrl.append(geneTerm).append("+AND+organism:9606&limit=1&format=tab&columns=id,comment(FUNCTION)");
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
				sb1.append(str).append("\n");
			}
			String tsv = sb1.toString();
			if (tsv != null && !tsv.isEmpty() ) {
				String[] lines = tsv.split("\n");
				String[] dataItems = lines[1].split("\t");
				if (dataItems.length >= 2) {
					String geneId = dataItems[0];
					String description = dataItems[1].split("\\{ECO:")[0];
					summary.setMoreInfoUrl(uniprotProps.getUniprotGeneUrl() + geneId);
					summary.setSummary(description.replaceFirst("FUNCTION: ", ""));
				}
			}
			reader.close();
			
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		return summary;
	}

}
