package utsw.bicf.answer.security;

public class OtherProperties {
	
	String proxyHostname;
	int proxyPort;
	String oncoKBGeniePortalUrl;
	String authenticateWith;

	public static final String AUTH_LDAP = "ldap";
	public static final String AUTH_LOCAL = "local";
	

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyHostname() {
		return proxyHostname;
	}

	public void setProxyHostname(String proxyHostname) {
		this.proxyHostname = proxyHostname;
	}

	public String getOncoKBGeniePortalUrl() {
		return oncoKBGeniePortalUrl;
	}

	public void setOncoKBGeniePortalUrl(String oncoKBGeniePortalUrl) {
		this.oncoKBGeniePortalUrl = oncoKBGeniePortalUrl;
	}

	public String getAuthenticateWith() {
		return authenticateWith;
	}

	public void setAuthenticateWith(String authenticateWith) {
		this.authenticateWith = authenticateWith;
	}

}
