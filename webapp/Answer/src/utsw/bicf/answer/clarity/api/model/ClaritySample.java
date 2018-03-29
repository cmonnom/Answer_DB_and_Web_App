//package utsw.bicf.answer.clarity.api.model;
//
//import java.io.Serializable;
//
//import org.apache.commons.lang3.builder.HashCodeBuilder;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//
//import utsw.bicf.answer.clarity.api.model.nuclia.DNAHyb;
//import utsw.bicf.answer.clarity.api.model.nuclia.DNALibPrep;
//import utsw.bicf.answer.clarity.api.model.nuclia.DNARNAExtract;
//import utsw.bicf.answer.clarity.api.model.nuclia.RNALibPrep;
//
//@JacksonXmlRootElement(localName = "sample")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ClaritySample implements Serializable{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	String uri;
//	String limsid;
//	String name;
//	ClarityProject project;
//	Double din;
//	@JacksonXmlProperty(localName = "date-received")
//	ClarityValue dateReceived; //Assession Date
//	
//	DNARNAExtract extract;
//	DNALibPrep dnaLibrary;
//	DNAHyb dnaHyb;
//	RNALibPrep rnaLibPrep;
//	ClarityProcess seqRun;
//	
//	boolean isDoneSequencing; //indicator that not need to fetch it with the API
//	
//	
//	//properties matching NuCLIA
//	String tissueType;
//	String tClass;
//	String nucType;
//	
//	boolean inQCPipeline;
//	
//	public String getUri() {
//		return uri;
//	}
//	public void setUri(String uri) {
//		this.uri = uri;
//	}
//	public String getLimsid() {
//		return limsid;
//	}
//	public void setLimsid(String limsid) {
//		this.limsid = limsid;
//	}
//
//	public ClaritySample() {
//		super();
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public ClarityProject getProject() {
//		return project;
//	}
//	public void setProject(ClarityProject project) {
//		this.project = project;
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("Lims: ").append(limsid)
//		.append(" | Name: ").append(name);
//		if (getDateReceived() != null) {
//			sb.append(" | Date Received: ").append(getDateReceived().getValue());
//		}
//		sb.append(" | DIN: ").append(getDin())
//		.append(" | TCLass: ").append(getTClass())
//		.append(" | Extract: ").append(extract)
//		.append(" | Library: ").append(dnaLibrary)
//		.append(" | DNA Hyb: ").append(dnaHyb);
//		return sb.toString();
//	}
//	
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder().append(getLimsid()).toHashCode();
//	}
//	public Double getDin() {
//		return din;
//	}
//	public void setDin(Double din) {
//		this.din = din;
//	}
//	public String getTissueType() {
//		return tissueType;
//	}
//	public void setTissueType(String tissueType) {
//		this.tissueType = tissueType;
//	}
//	public String getTClass() {
//		return tClass;
//	}
//	public void setTClass(String tClass) {
//		this.tClass = tClass;
//	}
//	public ClarityValue getDateReceived() {
//		return dateReceived;
//	}
//	public DNARNAExtract getExtract() {
//		return extract;
//	}
//	public void setExtract(DNARNAExtract extract) {
//		this.extract = extract;
//	}
//	public String gettClass() {
//		return tClass;
//	}
//	public void settClass(String tClass) {
//		this.tClass = tClass;
//	}
//	public void setDateReceived(ClarityValue dateReceived) {
//		this.dateReceived = dateReceived;
//	}
//	public DNALibPrep getDnaLibrary() {
//		return dnaLibrary;
//	}
//	public void setDnaLibrary(DNALibPrep dnaLibrary) {
//		this.dnaLibrary = dnaLibrary;
//	}
//	public DNAHyb getDnaHyb() {
//		return dnaHyb;
//	}
//	public void setDnaHyb(DNAHyb dnaHyb) {
//		this.dnaHyb = dnaHyb;
//	}
//	public String getNucType() {
//		return nucType;
//	}
//	public void setNucType(String nucType) {
//		this.nucType = nucType;
//	}
//	@JsonIgnore
//	public boolean isDNA() {
//		return nucType.equals("dna");
//	}
//	@JsonIgnore
//	public boolean isRNA() {
//		return nucType.equals("rna");
//	}
//	public RNALibPrep getRnaLibPrep() {
//		return rnaLibPrep;
//	}
//	public void setRnaLibPrep(RNALibPrep rnaLibPrep) {
//		this.rnaLibPrep = rnaLibPrep;
//	}
//	public boolean isInQCPipeline() {
//		return inQCPipeline;
//	}
//	public void setInQCPipeline(boolean inQCPipeline) {
//		this.inQCPipeline = inQCPipeline;
//	}
//	public void generateTClass() {
//		if (name.contains("_T_") || name.endsWith("_T")) {
//			tClass = "Tumor";
//		}
//		else {
//			tClass = "Normal";
//		}
//		
//	}
//	public boolean isDoneSequencing() {
//		return isDoneSequencing;
//	}
//	public void setDoneSequencing(boolean isDoneSequencing) {
//		this.isDoneSequencing = isDoneSequencing;
//	}
//	public ClarityProcess getSeqRun() {
//		return seqRun;
//	}
//	public void setSeqRun(ClarityProcess seqRun) {
//		this.seqRun = seqRun;
//	}
//}
