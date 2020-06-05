package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

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
public class JaxCKBRequestUtils extends AbstractRequestUtils{

	JaxCKBProperties jaxCKBProps;

	public JaxCKBRequestUtils(JaxCKBProperties jaxCKBProps, OtherProperties otherProps) {
		this.jaxCKBProps = jaxCKBProps;
		this.otherProps = otherProps;
		this.setupClient();
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
					summary.setMoreInfoUrl2(jaxCKBProps.getGenePaidUrl() + entrezId);
				}
			}
		}
		else {
			logger.info("Something went wrong JaxCKBRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return summary;
	}
	
	/**
	 * 
	 * @param query needs to be HUGO + " " + oncokbVariantName (eg. NRAS Q61R)
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws UnsupportedOperationException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public LookupSummary getVariantSummary(String query)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(jaxCKBProps.getVariantServlet()).append(URLEncoder.encode(query, "UTF-8"));
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			JaxCKBResponse[] jaxJson = mapper.readValue(response.getEntity().getContent(), JaxCKBResponse[].class);
			for (JaxCKBResponse item : jaxJson) {
				summary.setSummary(item.getVariantDescription());
				summary.setMoreInfoUrl(jaxCKBProps.getVariantUrl() + item.getId());
				summary.setMoreInfoUrl2(jaxCKBProps.getVariantPaidUrl() + item.getId());
			}
		}
		else {
			logger.info("Something went wrong JaxCKBRequestUtils:130 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return summary;
	}

}
