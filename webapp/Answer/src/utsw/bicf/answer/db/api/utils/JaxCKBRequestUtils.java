package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.model.extmapping.JaxCKBResponse;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.security.JaxCKBProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class JaxCKBRequestUtils {

	JaxCKBProperties jaxCKBProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public JaxCKBRequestUtils(JaxCKBProperties jaxCKBProps, OtherProperties otherProps) {
		this.jaxCKBProps = jaxCKBProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpHost proxy = null;
	private HttpClient client = null;

	private void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxy).build();
		} else {
			client = HttpClientBuilder.create().build();
		}
	}

	public LookupSummary getGeneSummary(String geneName, String entrezId)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		if (entrezId == null) {
			return summary;
		}
		StringBuilder sbUrl = new StringBuilder(jaxCKBProps.getGeneServlet()).append(geneName);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			JaxCKBResponse[] jaxJson = mapper.readValue(response.getEntity().getContent(), JaxCKBResponse[].class);
			Integer entrezIdInt = Integer.parseInt(entrezId);
			for (JaxCKBResponse item : jaxJson) {
				if (item.getId().equals(entrezIdInt)) {
					summary.setSummary(item.getGeneDesc());
					summary.setMoreInfoUrl(jaxCKBProps.getGeneUrl() + entrezId);
				}
			}
		}
		else {
			logger.info("Something went wrong JaxCKBRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		return summary;
	}

}
