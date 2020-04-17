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
import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.security.EnsemblProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class EnsemblRequestUtils {

	EnsemblProperties ensemblProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public EnsemblRequestUtils(EnsemblProperties ensemblProps, OtherProperties otherProps) {
		this.ensemblProps = ensemblProps;
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

	public EnsemblResponse fetchEnsembl(String geneTerm)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		StringBuilder sbUrl = new StringBuilder(ensemblProps.getFetchUrl());
		sbUrl.append(geneTerm);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		requestGet.addHeader("Content-Type", "application/json;charset=utf-8");
		requestGet.addHeader("Accept", "application/json;charset=utf-8");
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			EnsemblResponse ensembl = mapper.readValue(response.getEntity().getContent(), EnsemblResponse.class);
			return ensembl;
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		return null;
	}

}
