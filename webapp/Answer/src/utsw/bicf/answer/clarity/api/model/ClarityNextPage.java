package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "next-page")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityNextPage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uri;
	
	public ClarityNextPage() {
		super();
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
