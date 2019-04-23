package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class AuthUtils {

	ModelDAO modelDAO;
	OtherProperties otherProps;

	public AuthUtils(ModelDAO modelDAO, OtherProperties otherProps) {
		super();
		this.modelDAO = modelDAO;
		this.otherProps = otherProps;
		this.setupClient();
	}
	

	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpClient client = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper();
	
	private void setupClient() {
		//disable ssh check for test environment because of self signed certificate
		if (otherProps.getProductionEnv()) {
			client = HttpClientBuilder.create().build();
		}
		else {
			client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		}
	}

	public void addUser(AjaxResponse ajaxResponse, UserCredentials userCreds)
			throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(otherProps.getAuthUrl());
		sbUrl.append("users/adduser/");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(userCreds), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AjaxResponse mongoDBResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
			ajaxResponse.setSuccess(mongoDBResponse.getSuccess());
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
	}
	
	public void updateUser(AjaxResponse ajaxResponse, UserCredentials userCreds)
			throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(otherProps.getAuthUrl());
		sbUrl.append("users/adduser/");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(userCreds), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AjaxResponse mongoDBResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
			ajaxResponse.setSuccess(mongoDBResponse.getSuccess());
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
	}
	
	public void checkUserCredentials(AjaxResponse ajaxResponse, UserCredentials userCreds)
			throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(otherProps.getAuthUrl());
		sbUrl.append("login/local");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(userCreds), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AjaxResponse mongoDBResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
			ajaxResponse.setSuccess(mongoDBResponse.getSuccess());
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Wrong username or password");
		}
	}


}
