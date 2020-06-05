package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import utsw.bicf.answer.model.extmapping.civic.CivicResponse;
import utsw.bicf.answer.model.extmapping.civic.CivicVariant;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.security.CivicProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class CivicRequestUtils  extends AbstractRequestUtils{

	CivicProperties civicProps;

	public CivicRequestUtils(CivicProperties civicProps, OtherProperties otherProps) {
		this.civicProps = civicProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	private CivicResponse getGene(String geneTerm) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(civicProps.getQueryUrl());
		sbUrl.append(geneTerm).append("?identifier_type=entrez_symbol");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			CivicResponse civicJson = mapper.readValue(response.getEntity().getContent(), CivicResponse.class);
			this.closeGetRequest();
			return civicJson;
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return null;
	}
	
	public LookupSummary getGeneSummary(String geneTerm)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		CivicResponse civicJson = this.getGene(geneTerm);
		if (civicJson != null) {
			summary.setSummary(civicJson.getDescription());
			summary.setMoreInfoUrl(civicProps.getCivicGeneUrl() + civicJson.getId());
		}
		return summary;
	}
	
	public LookupSummary getVariantSummary(String geneTerm, String oncokbVariantName)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		CivicResponse civicJson = this.getGene(geneTerm);
		if (civicJson != null) {
			List<CivicVariant> variants = civicJson.getVariants();
			if (variants != null) {
				for (CivicVariant v : variants) {
					if (v.getName().equals(oncokbVariantName)) {
						StringBuilder sbUrl = new StringBuilder(civicProps.getVariantServlet());
						sbUrl.append(v.getId());
						URI uri = new URI(sbUrl.toString());
						requestGet = new HttpGet(uri);
						HttpClientContext context = HttpClientContext.create();
						HttpResponse response = client.execute(requestGet, context);

						int statusCode = response.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							ObjectMapper mapper = new ObjectMapper();
							civicJson = mapper.readValue(response.getEntity().getContent(), CivicResponse.class);
							summary.setSummary(civicJson.getDescription());
							summary.setMoreInfoUrl(civicProps.getCivicGeneUrl() + v.getId() 
							+ "/summary/variants/" + civicJson.getId()
							+ "/summary#variant");
						}
						else {
							logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
						}
						this.closeGetRequest();
						return summary;
					}
				}
			}
		}
		return summary;
		
		
	}

}
