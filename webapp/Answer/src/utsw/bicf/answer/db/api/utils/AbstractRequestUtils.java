package utsw.bicf.answer.db.api.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.security.OtherProperties;

public class AbstractRequestUtils {
	protected HttpGet requestGet = null;
	protected HttpPost requestPost = null;
	protected HttpPut requestPut = null;
	protected HttpClient client = null;
	protected HttpHost proxy = null;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	protected static final Logger logger = Logger.getLogger(AOPAspect.class);
	
	public void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxy).build();
		} else {
			client = HttpClientBuilder.create().build();
		}
	}
	
	public void closeGetRequest() {
		if (requestGet != null) {
			requestGet.releaseConnection();
		}
	}
	
	public void closePostRequest() {
		if (requestPost != null) {
			requestPost.releaseConnection();
		}
	}
	
	public void closePutRequest() {
		if (requestPut != null) {
			requestPut.releaseConnection();
		}
	}

}
