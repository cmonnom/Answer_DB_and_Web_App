package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import utsw.bicf.answer.clarity.api.utils.UDFUtils;

@JacksonXmlRootElement(localName = "project", namespace = "prj")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityProject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uri;
	String limsid;
	String name;
	@JacksonXmlElementWrapper(localName = "field", useWrapping = false)
	@JacksonXmlProperty(localName = "field", namespace = "udf")
	ClarityUDFField[] udfField;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLimsid() {
		return limsid;
	}
	public void setLimsid(String limsid) {
		this.limsid = limsid;
	}

	public ClarityProject() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTumorTissueType() {
		return UDFUtils.getUDFValue(getUdfField(), "Tumor tissue type");
	}
	
	public String getNormalTissueType() {
		return UDFUtils.getUDFValue(getUdfField(), "Normal tissue type");
	}
	
	public String getICD10() {
		return UDFUtils.getUDFValue(getUdfField(), "ICD10");
	}
	
	public String getMRN() {
		return UDFUtils.getUDFValue(getUdfField(), "Medical record number");
	}
	
	public String getPatientName() {
		return UDFUtils.getUDFValue(getUdfField(), "Patient name");
	}
	
	public String getDateOfBirth() {
		return UDFUtils.getUDFValue(getUdfField(), "Date of birth");
	}
	
	public String getGender() {
		return UDFUtils.getUDFValue(getUdfField(), "Gender");
	}
	
	public String getTumorCollectionDate() {
		return UDFUtils.getUDFValue(getUdfField(), "Tumor collection date");
	}
	
	public String getComments() {
		return UDFUtils.getUDFValue(getUdfField(), "Comments");
	}
	
	public String getCatchall() {
		return UDFUtils.getUDFValue(getUdfField(), "Catchall");
	}
	
	public String getOrderingProvider() {
		return UDFUtils.getUDFValue(getUdfField(), "Ordering provider");
	}
	
	public String getPatientStatus() {
		return UDFUtils.getUDFValue(getUdfField(), "Patient status");
	}
	
	public String getAuthorizingProvider() {
		return UDFUtils.getUDFValue(getUdfField(), "Authorizing provider");
	}
	public String getReferringInstitution() {
		return UDFUtils.getUDFValue(getUdfField(), "Referring institution");
	}
	public String getEpicOrderDate() {
		return UDFUtils.getUDFValue(getUdfField(), "Epic order date");
	}
	
	public String getEpicOrderNb() {
		return UDFUtils.getUDFValue(getUdfField(), "Epic order number");
	}
	
	public ClarityUDFField[] getUdfField() {
		return udfField;
	}
	public void setUdfField(ClarityUDFField[] udfField) {
		this.udfField = udfField;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Name: ");
		sb.append(getName()).append("| LIMS: ").append(getLimsid()).append("| Tissue Type: ")
		.append(getTumorTissueType()).append("|  ICD10: ").append(getICD10());
		return sb.toString();
	}

}
