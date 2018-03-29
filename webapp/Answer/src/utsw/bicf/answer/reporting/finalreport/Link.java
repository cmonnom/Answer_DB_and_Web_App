package utsw.bicf.answer.reporting.finalreport;

public class Link {

	String urlLabel;
	String url;
	
	public Link(String urlLabel, String url) {
		super();
		this.urlLabel = urlLabel;
		this.url = url;
	}
	public String getUrlLabel() {
		return urlLabel;
	}
	public void setUrlLabel(String urlLabel) {
		this.urlLabel = urlLabel;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Link) {
			Link objLink = (Link) obj;
			return urlLabel.equals(objLink.urlLabel);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return url;
	}
	
}
