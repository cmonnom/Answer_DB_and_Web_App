package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import utsw.bicf.answer.clarity.api.utils.UDFUtils;

@JacksonXmlRootElement(localName = "process")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uri;
	String limsid;
	ClarityValue type;
	@JacksonXmlProperty(localName = "date-run")
	String dateRun;
	@JacksonXmlElementWrapper(localName = "input-output-map", useWrapping = false)
	@JacksonXmlProperty(localName = "input-output-map")
	ClarityInputOutputMap[] ioMaps;
	@JacksonXmlElementWrapper(localName = "field", useWrapping = false)
	@JacksonXmlProperty(localName = "field", namespace = "udf")
	ClarityUDFField[] udfField;
	ClarityTechnician technician;

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

	public ClarityProcess() {
		super();
	}

	public ClarityValue getType() {
		return type;
	}

	public void setType(ClarityValue type) {
		this.type = type;
	}

	public String getDateRun() {
		return dateRun;
	}

	public void setDateRun(String dateRun) {
		this.dateRun = dateRun;
	}

	public String toString() {
		if (type != null) {
			return type.getValue() + " " + getDateRun() + " " + getUri() + " " + getLimsid();
		} else {
			return getDateRun() + " " + getUri() + " " + getLimsid();
		}

	}

	public ClarityInputOutputMap[] getIoMaps() {
		return ioMaps;
	}

	public void setIoMaps(ClarityInputOutputMap[] ioMaps) {
		this.ioMaps = ioMaps;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getLimsid()).toHashCode();
	}

	public ClarityUDFField[] getUdfField() {
		return udfField;
	}

	public void setUdfField(ClarityUDFField[] udfField) {
		this.udfField = udfField;
	}

	public ClarityTechnician getTechnician() {
		return technician;
	}

	public void setTechnician(ClarityTechnician technician) {
		this.technician = technician;
	}

	public String getFlowCellId() {
		return UDFUtils.getUDFValue(getUdfField(), "Flow Cell ID");
	}
	
	public String getSeqRunId() {
		return UDFUtils.getUDFValue(getUdfField(), "Run ID");
	}

	public String getMachineName() {
		return UDFUtils.getUDFValue(getUdfField(), "Experiment Name");
	}

}
