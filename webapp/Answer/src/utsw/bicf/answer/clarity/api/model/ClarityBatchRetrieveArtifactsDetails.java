//package utsw.bicf.answer.clarity.api.model;
//
//
//import java.io.Serializable;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//
//@JacksonXmlRootElement(localName = "details", namespace = "art")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ClarityBatchRetrieveArtifactsDetails implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	@JacksonXmlElementWrapper(localName = "artifact", useWrapping = false)
//	@JacksonXmlProperty(localName = "artifact")
//	ClarityArtifact[] artifacts;
//	//Fields populated manually
//	//to record which process type an artifact is from
//	Map<String, List<String>> urisByProcessType;
//	Map<String, LocalDate> processDateByProcessType;
//	Map<String, ClarityTechnician> technicianByProcessType;
//
//	public ClarityArtifact[] getArtifacts() {
//		return artifacts;
//	}
//
//	public void setArtifacts(ClarityArtifact[] artifacts) {
//		this.artifacts = artifacts;
//	}
//
//	public Map<String, List<String>> getUrisByProcessType() {
//		return urisByProcessType;
//	}
//
//	public void setUrisByProcessType(Map<String, List<String>> urisByProcessType) {
//		this.urisByProcessType = urisByProcessType;
//	}
//
//	public Map<String, LocalDate> getProcessDateByProcessType() {
//		return processDateByProcessType;
//	}
//
//	public void setProcessDateByProcessType(Map<String, LocalDate> processDateByProcessType) {
//		this.processDateByProcessType = processDateByProcessType;
//	}
//
//	public Map<String, ClarityTechnician> getTechnicianByProcessType() {
//		return technicianByProcessType;
//	}
//
//	public void setTechnicianByProcessType(Map<String, ClarityTechnician> technicianByProcessType) {
//		this.technicianByProcessType = technicianByProcessType;
//	}
//
//}
