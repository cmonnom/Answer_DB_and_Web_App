package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.interpro.InterProResponse;
import utsw.bicf.answer.security.InterProProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class InterProRequestUtils extends AbstractRequestUtils{

	InterProProperties interProProps;

	public InterProRequestUtils(InterProProperties interProProps, OtherProperties otherProps) {
		this.interProProps = interProProps;
		this.otherProps = otherProps;
		this.setupClient();
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
			this.closeGetRequest();
			return interproJson;
		}
		else {
			logger.info("Something went wrong InterProRequestUtils:77 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return null;
	}
	

}
