//package utsw.bicf.answer.clarity.api.model;
//
//import java.io.Serializable;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//
//@JacksonXmlRootElement(localName = "artifacts", namespace = "art")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ClarityArtifacts implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	String uri;
//	@JacksonXmlElementWrapper(localName = "artifact", useWrapping = false)
//	@JacksonXmlProperty(localName = "artifact")
//	ClarityArtifact[] artifacts;
//
//	public ClarityArtifacts() {
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
//	public ClarityArtifact[] getArtifacts() {
//		return artifacts;
//	}
//
//	public void setArtifacts(ClarityArtifact[] artifacts) {
//		this.artifacts = artifacts;
//	}
//
//
//
//}
