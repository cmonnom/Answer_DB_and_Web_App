package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.model.extmapping.interpro.InterProResponse;
import utsw.bicf.answer.security.InterProProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class InterProRequestUtils {

	InterProProperties interProProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public InterProRequestUtils(InterProProperties interProProps, OtherProperties otherProps) {
		this.interProProps = interProProps;
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

	public InterProResponse getPfam(String uniprotId) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(interProProps.getApiUrl());
		sbUrl.append(uniprotId).append("/?search=&extra_fields=short_name");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			InterProResponse interproJson = mapper.readValue(response.getEntity().getContent(), InterProResponse.class);
			return interproJson;
		}
		else {
			logger.info("Something went wrong InterProRequestUtils:77 HTTP_STATUS: " + statusCode);
		}
		return null;
	}
	

}
