package utsw.bicf.answer.security;

public class OtherProperties {
	
	String proxyHostname;
	int proxyPort;
	String oncoKBGeniePortalUrl;
	String authenticateWith;
	String authMessage;
	String authUrl;
	Boolean productionEnv;
	String mutationalSignatureUrl;

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

	public String getAuthMessage() {
		return authMessage;
	}

	public void setAuthMessage(String authMessage) {
		this.authMessage = authMessage;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public Boolean getProductionEnv() {
		return productionEnv;
	}

	public void setProductionEnv(Boolean productionEnv) {
		this.productionEnv = productionEnv;
	}

	public String getMutationalSignatureUrl() {
		return mutationalSignatureUrl;
	}

	public void setMutationalSignatureUrl(String mutationalSignatureUrl) {
		this.mutationalSignatureUrl = mutationalSignatureUrl;
	}

}
