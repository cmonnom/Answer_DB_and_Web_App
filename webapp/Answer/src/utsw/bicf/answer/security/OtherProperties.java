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
	String rootUrl;
	String webappName;

	public static final String AUTH_LDAP = "ldap";
	public static final String AUTH_AZURE_OAUTH = "azure_oauth";
	public static final String AUTH_LOCAL = "local";
	 // CAREFUL WITH THIS PARAM. All users would use the same pwd stored in the Token table (dev-login).
	//DO NOT USE THIS PARAM on test or prod. Only on local instances like AnswerVM
	public static final String AUTH_DEV = "dev";
	

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

	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public String getWebappName() {
		return webappName;
	}

	public void setWebappName(String webappName) {
		this.webappName = webappName;
	}

}
