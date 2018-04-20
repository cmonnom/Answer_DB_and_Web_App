package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

/**
 * All API requests to the annotation DB should
 * be here.
 * @author Guillaume
 *
 */
public class RequestUtils {
	
	ModelDAO modelDAO;
	AnswerDBCredentials dbProps;
	
	public RequestUtils(ModelDAO modelDAO) {
		super();
		this.modelDAO = modelDAO;
		this.dbProps = modelDAO.getAnswerDBCredentials();
	}

	
	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpClient client = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper();
	
	private void addAuthenticationHeader(HttpGet requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}
	
	private void addAuthenticationHeader(HttpPut requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}
	
	private String createAuthHeader() {
		String auth = dbProps.getUsername() + ":" + dbProps.getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth);
		return authHeader;
	}

	public OrderCase[] getActiveCases() throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("cases");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase[] cases = mapper.readValue(response.getEntity().getContent(), OrderCase[].class);
			return cases;
		}
		return null;
	}

	public AjaxResponse assignCaseToUser(List<User> users, String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		String userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.joining(","));
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("assigncase?caseId=")
		.append(caseId).append("&userIds=").append(userIds);
		URI uri = new URI(sbUrl.toString());
		
		requestPut = new HttpPut(uri);
		
		addAuthenticationHeader(requestPut);

		HttpResponse response = client.execute(requestPut);

		AjaxResponse ajaxResponse = new AjaxResponse();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		}
		else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return ajaxResponse;
	}

	public OrderCase getCaseDetails(String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId);
		URI uri = new URI(sbUrl.toString());
		
		requestGet = new HttpGet(uri);
		
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}

	
}
