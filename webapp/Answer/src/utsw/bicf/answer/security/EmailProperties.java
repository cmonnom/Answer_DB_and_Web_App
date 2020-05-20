package utsw.bicf.answer.security;

public class EmailProperties {

	String from;
	String signature;
	String rootUrl;
	Boolean doSend;
	String remoteDesktopName;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getRootUrl() {
		return rootUrl;
	}
	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	public Boolean getDoSend() {
		return doSend;
	}
	public void setDoSend(Boolean doSend) {
		this.doSend = doSend;
	}
	public String getRemoteDesktopName() {
		return remoteDesktopName;
	}
	public void setRemoteDesktopName(String remoteDesktopName) {
		this.remoteDesktopName = remoteDesktopName;
	}
	
	
	
}
