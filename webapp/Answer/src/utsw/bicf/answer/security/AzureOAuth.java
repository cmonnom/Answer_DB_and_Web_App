package utsw.bicf.answer.security;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class AzureOAuth {

	String msGraphUrl;
	OtherProperties otherProps;
	private HttpHost proxy = null;
	private HttpClient client = null;
	
	private void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxy).build();
		}
		else {
			client = HttpClientBuilder.create().build();
		}
	}

	public boolean isUserValid(OtherProperties otherProps, String token) {
		this.otherProps = otherProps;
		try {
			if (token == null || token.equals("")) {
				return false;
			}
			HttpGet requestGet = new HttpGet(msGraphUrl);
			requestGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			setupClient();
			HttpResponse response = client.execute(requestGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false; // authentication failed. Wrong password or other server issue
		}
	}
	
	public String getMsGraphUrl() {
		return msGraphUrl;
	}

	public void setMsGraphUrl(String msGraphUrl) {
		this.msGraphUrl = msGraphUrl;
	}
}
