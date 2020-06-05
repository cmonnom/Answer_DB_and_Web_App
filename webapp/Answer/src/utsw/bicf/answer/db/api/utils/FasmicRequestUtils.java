package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
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
import utsw.bicf.answer.model.extmapping.fasmic.FasmicResponse;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.model.extmapping.oncokb.EvidenceResponse;
import utsw.bicf.answer.model.extmapping.oncokb.OncoKBResponse;
import utsw.bicf.answer.security.FasmicProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class FasmicRequestUtils extends AbstractRequestUtils {

	FasmicProperties fasmicProps;

	public FasmicRequestUtils(FasmicProperties fasmicProps, OtherProperties otherProps) {
		this.fasmicProps = fasmicProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	public LookupSummary getVariantSummary(String geneTerm, String variant)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(fasmicProps.getSearchUrl());
		sbUrl.append("%22").append(geneTerm).append("%22&include_docs=true");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			FasmicResponse[] fasmicJson = mapper.readValue(response.getEntity().getContent(), FasmicResponse[].class);
			for (int i = 0; i < fasmicJson.length; i++) {
				FasmicResponse f = fasmicJson[i];
				if (f.getGene().equals(geneTerm) && f.getAaChange().equals(variant)) {
					summary.setMoreInfoUrl(fasmicProps.getWebpageUrl());
					summary.setSummary(StringUtils.capitalize(f.getFinalCall()));
				}
			}
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return summary;
	}
	
}
