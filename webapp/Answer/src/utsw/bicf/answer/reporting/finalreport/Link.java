package utsw.bicf.answer.reporting.finalreport;

import org.apache.pdfbox.pdmodel.PDPage;

public class Link {

	String urlLabel;
	String url;
	Integer destinationPageNb;
	int top;
	
	public Link(String urlLabel, String url) {
		super();
		this.urlLabel = urlLabel;
		this.url = url;
	}
	public Link(String urlLabel, Integer destinationPageNb, int top) {
		super();
		this.urlLabel = urlLabel;
		this.destinationPageNb = destinationPageNb;
		this.top = top;
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
	public Integer getDestinationPageNb() {
		return destinationPageNb;
	}
	public void setDestinationPageNb(Integer destinationPageNb) {
		this.destinationPageNb = destinationPageNb;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	
}
