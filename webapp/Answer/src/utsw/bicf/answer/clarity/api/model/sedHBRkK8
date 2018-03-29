//package utsw.bicf.answer.clarity.api.model;
//
//import java.io.Serializable;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//
//import utsw.bicf.answer.clarity.api.utils.UDFUtils;
//
//@JacksonXmlRootElement(localName = "artifact", namespace = "art")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ClarityArtifact implements Comparable<ClarityArtifact>,  Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	@JsonIgnore
//	public static final String ANALYTE = "Analyte";
//	@JsonIgnore
//	public static final String RESULTFILE = "ResultFile";
//
//	String name;
//	String uri;
//	String limsid;
//	@JacksonXmlElementWrapper(localName = "field", useWrapping = false)
//	@JacksonXmlProperty(localName = "field", namespace = "udf")
//	ClarityUDFField[] udfField;
//	@JacksonXmlElementWrapper(localName = "sample", useWrapping = false)
//	@JacksonXmlProperty(localName = "sample")
//	ClaritySample[] samples;
//	@JacksonXmlProperty(localName = "qc-flag")
//	ClarityValue qcFlag;
//	@JacksonXmlProperty(localName = "date-run")
//	ClarityValue dateRun;
//	@JacksonXmlProperty(localName = "reagent-label")
//	ClarityReagent bcIndex;
//	@JacksonXmlProperty(localName = "parent-process")
//	ClarityProcess parentProcess;
//	@JacksonXmlProperty(localName = "output-type")
//	ClarityValue outputType;
//	ClarityValue fakeValue;
//
//	public ClarityArtifact() {
//	}
//
//	public ClarityUDFField[] getUdfField() {
//		return udfField;
//	}
//
//	public void setUdfField(ClarityUDFField[] udfFields) {
//		this.udfField = udfFields;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	/**
//	 * Iterate through all udf:field in the artifact to find the Concentration value
//	 * 
//	 * @return
//	 */
//	public Double getConcentration() {
//		return UDFUtils.getUDFDoubleValue(getUdfField(), "Concentration");
//	}
//
//	public Double getRegion1Concentration() {
//		return UDFUtils.getUDFDoubleValue(getUdfField(), "Region 1 Conc.");
//	}
//	
//	public Double getDIN() {
//		return UDFUtils.getUDFDoubleValue(getUdfField(), "DIN");
//	}
//
//	public Double getDNARNATotalYield() {
//		return UDFUtils.getUDFDoubleValue(getUdfField(), "Total Yield (ng)");
//	}
//
//	public Double getArea() {
//		Double area = UDFUtils.getUDFDoubleValue(getUdfField(), "Marked Tumor Area (mm^2)");
//		if (area == null) {
//			area = UDFUtils.getUDFDoubleValue(getUdfField(), "Marked Normal Area (mm^2)");
//		}
//		return area;
//	}
//
//	public Integer getNumSlides() {
//		return UDFUtils.getUDFIntegerValue(getUdfField(), "Number of Slides");
//	}
//
//	public Integer getMeanLibFragSize() {
//		return UDFUtils.getUDFIntegerValue(getUdfField(), "Region 1 Average Size - bp");
//	}
//
//	public Double getVolume() {
//		Double volume = UDFUtils.getUDFDoubleValue(getUdfField(), "Volume (mL)");
//		if (volume == null) {
//			volume = UDFUtils.getUDFDoubleValue(getUdfField(), "Volume (uL)");
//		}
//		return volume;
//	}
//
//	public Integer getCellCount() {
//		Double countInMillions = UDFUtils.getUDFDoubleValue(getUdfField(), "Cell Count (Millions)");
//		if (countInMillions != null) {
//			return (int) Math.round(countInMillions * 1000000);
//		}
//		return null;
//	}
//
//	public Double getRNAPctOver200nt() {
//		return UDFUtils.getUDFDoubleValue(getUdfField(), "Region 1 % of Total");
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
//	public String getLimsid() {
//		return limsid;
//	}
//
//	public void setLimsid(String limsid) {
//		this.limsid = limsid;
//	}
//
//	/**
//	 * Parse the ClarityValue to return a Boolean. Might need to handle more cases
//	 * here
//	 * 
//	 * @return
//	 */
//	public Boolean getDnaExtractPassedValue() {
//		String qcFlagValue = qcFlag.getValue();
//		switch (qcFlagValue) {
//		case "PASSED":
//			return true;
//		case "UNKNOWN":
//			return null;
//		default:
//			return false;
//		}
//	}
//
//	public ClarityValue getQcFlag() {
//		return qcFlag;
//	}
//
//	public void setQcFlag(ClarityValue qcFlag) {
//		this.qcFlag = qcFlag;
//	}
//
//	public ClarityValue getDateRun() {
//		return dateRun;
//	}
//
//	public void setDateRun(ClarityValue dateRun) {
//		this.dateRun = dateRun;
//	}
//
//	public ClarityReagent getBcIndex() {
//		return bcIndex;
//	}
//
//	public void setBcIndex(ClarityReagent bcIndex) {
//		this.bcIndex = bcIndex;
//	}
//
//	public ClarityProcess getParentProcess() {
//		return parentProcess;
//	}
//
//	public void setParentProcess(ClarityProcess parentProcess) {
//		this.parentProcess = parentProcess;
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("Name: ").append(name).append(" Lims: ").append(limsid);
//		if (getParentProcess() != null) {
//			if (getParentProcess().getDateRun() == null) {
//				sb.append(" ").append("No Date");
//			} else {
//				sb.append(" *").append(getParentProcess().getDateRun());
//			}
//		} else {
//			sb.append(" ").append("No Parent");
//		}
//		return sb.toString();
//	}
//
//	public ClarityValue getOutputType() {
//		return outputType;
//	}
//
//	public void setOutputType(ClarityValue outputType) {
//		this.outputType = outputType;
//	}
//
//	@Override
//	public int compareTo(ClarityArtifact o) {
//		// compare by parent process date
//
//		if (o.getParentProcess() != null && o.getParentProcess().getDateRun() != null) { // o.parentProcess not null
//			if (getParentProcess() != null && getParentProcess().getDateRun() != null) { // parentProcess not null
//				int compare = getParentProcess().getDateRun().compareTo(o.getParentProcess().getDateRun());
//				if (compare == 0) {
//					String[] oLimsToken =  o.getLimsid().split("-");
//					String[] limsToken =  getLimsid().split("-");
//					if (oLimsToken.length == 2 && limsToken.length == 2) {
//						Integer oLimsNumber = Integer.parseInt(oLimsToken[1]);
//						Integer limsNumber = Integer.parseInt(limsToken[1]);
//						compare = limsNumber.compareTo(oLimsNumber);
//						return compare;
//					}
//					return 0;
//				}
//				return compare;
//			} else { // parentProcess null
//				return -1;
//			}
//
//		} else { // o.parentProcess null
//			if (getParentProcess() != null) { // parentProcess not null
//				return 1;
//			} else { // parentProcess null
//				return 0;
//			}
//
//		}
//	}
//}
