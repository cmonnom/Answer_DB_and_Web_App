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
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.DevPassword;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.model.User;
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
	
	/**
	 * CAREFUL WHEN USING THIS METHOD.
	 * The goal is to bypass LDAP during development
	 *  All users would use the pwd stored in dev_password table.
	 *  DO NOT USE THIS PARAM on test or prod. Only on training instances
	 *  
	 *  
	 * @param ajaxResponse
	 * @param userCreds
	 * @param user 
	 */
	public void checkDevCredentials(AjaxResponse ajaxResponse, UserCredentials userCreds, User user) {
		//TODO remove this
//		encodePassword(userCreds.getPassword());
		DevPassword pwd = modelDAO.getDevPasswordForUser(user.getUserId());
		if (TypeUtils.notNullNotEmpty(userCreds.getPassword()) && pwd != null 
				&& TypeUtils.notNullNotEmpty(pwd.getPassword()) 
				&& verifyDevPassword(pwd.getPassword(), userCreds.getPassword())) {
			ajaxResponse.setIsAllowed(true);
			ajaxResponse.setSuccess(true);
		}
		else {
			ajaxResponse.setIsAllowed(false);
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Wrong username or password");
		}
	}

	public static String encodePassword(String originalPwd) {
		String generatedPwd = BCrypt.hashpw(originalPwd, BCrypt.gensalt(12));
		//TODO remove this
//		System.out.println(generatedPwd);
		return generatedPwd;
	}
	
	public static boolean verifyDevPassword(String dbPassword, String userInput) {
		return BCrypt.checkpw(userInput, dbPassword);
	}

}
