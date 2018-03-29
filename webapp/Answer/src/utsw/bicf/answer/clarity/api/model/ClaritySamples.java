//package utsw.bicf.answer.clarity.api.model;
//
//import java.io.Serializable;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//
//@JacksonXmlRootElement(localName = "samples", namespace = "smp")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ClaritySamples implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	String uri;
//	@JacksonXmlElementWrapper(localName = "sample", useWrapping = false)
//	@JacksonXmlProperty(localName = "sample")
//	ClaritySample[] samples;
//
//	public ClaritySamples() {
//	}
//
//	public String getUri() {
//		return uri;
//	}
//
//	public void setUri(String uri) {
//		this.uri = uri;
//	}
//
//	public ClaritySample[] getSamples() {
//		return samples;
//	}
//
//	public void setSamples(ClaritySample[] samples) {
//		this.samples = samples;
//	}
//
//
//}
