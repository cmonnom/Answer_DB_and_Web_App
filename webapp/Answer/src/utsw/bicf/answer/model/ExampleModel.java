package utsw.bicf.answer.model;
//package utsw.bicf.answer.model;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.JoinTable;
//import javax.persistence.ManyToMany;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import utsw.bicf.nucliavault.model.DnaHyb;
//import utsw.bicf.nucliavault.model.DnaLibPrep;
//import utsw.bicf.nucliavault.model.NucExtract;
//import utsw.bicf.nucliavault.model.ProjectOrder;
//import utsw.bicf.nucliavault.model.RnaLibPrep;
//import utsw.bicf.nucliavault.model.Subject;
//
//@Entity
//@Table(name="sample")
//public class Sample {
//	
//	public Sample() {
//		
//	}
//	
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(name="sample_id")
//	Integer sampleId;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="subject_id")
//	Subject subject;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="dna_lib_prep_id")
//	DnaLibPrep dnaLibPrep;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="rna_lib_prep_id")
//	RnaLibPrep rnaLibPrep;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="project_order_id")
//	ProjectOrder projectOrder;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="nuc_extract_id")
//	NucExtract nucExtract;
//	
//	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
//	@JoinTable(name="sample_dna_hyb",
//	joinColumns=@JoinColumn(name="sample_id"),
//	inverseJoinColumns=@JoinColumn(name="dna_hyb_id"))
//	List<DnaHyb> dnaHybs;
//	
//	@Column(name="nuc_type")
//	String nucType;
//	
//	@Column(name="sample_lab_id")
//	String sampleLabId;
//	
//	@Column(name="sample_lab_name")
//	String sampleLabName;
//	
//	@Column(name="demultiplex_name")
//	String demultiplexName;
//	
//	@Column(name="t_class")
//	String tClass;
//	
//	@Column(name="assay")
//	String assay;
//	
//	@Column(name="tissue_type")
//	String tissueType;
//	
//	@Column(name="tumor_type")
//	String tumorType;
//	
//	@Column(name="lims_id")
//	String limsId;
//	
//	@Column(name="assession_date")
//	LocalDateTime assessionDate;	
//	
//	@Column(name="fixation")
//	String fixation;
//	
//
//	public Integer getSampleId() {
//		return sampleId;
//	}
//
//	public void setSampleId(Integer sampleId) {
//		this.sampleId = sampleId;
//	}
//
//	public Subject getSubject() {
//		return subject;
//	}
//
//	public void setSubject(Subject subject) {
//		this.subject = subject;
//	}
//
//	public String getNucType() {
//		return nucType;
//	}
//
//	public void setNucType(String nucType) {
//		this.nucType = nucType;
//	}
//
//	public String getSampleLabId() {
//		return sampleLabId;
//	}
//
//	public void setSampleLabId(String sampleLabId) {
//		this.sampleLabId = sampleLabId;
//	}
//
//	public String getSampleLabName() {
//		return sampleLabName;
//	}
//
//	public void setSampleLabName(String sampleLabName) {
//		this.sampleLabName = sampleLabName;
//	}
//
//	public String getDemultiplexName() {
//		return demultiplexName;
//	}
//
//	public void setDemultiplexName(String demultiplexName) {
//		this.demultiplexName = demultiplexName;
//	}
//
//	public String gettClass() {
//		return tClass;
//	}
//
//	public void settClass(String tClass) {
//		this.tClass = tClass;
//	}
//
//	public String getAssay() {
//		return assay;
//	}
//
//	public void setAssay(String assay) {
//		this.assay = assay;
//	}
//
//	public DnaLibPrep getDnaLibPrep() {
//		return dnaLibPrep;
//	}
//
//	public void setDnaLibPrep(DnaLibPrep dnaLibPrep) {
//		this.dnaLibPrep = dnaLibPrep;
//	}
//
//	public RnaLibPrep getRnaLibPrep() {
//		return rnaLibPrep;
//	}
//
//	public void setRnaLibPrep(RnaLibPrep rnaLibPrep) {
//		this.rnaLibPrep = rnaLibPrep;
//	}	
//
//	public boolean isDna() {
//		return "dna".equals(this.getNucType());
//	}
//	
//	public boolean isRna() {
//		return "rna".equals(this.getNucType());
//	}
//
//	public ProjectOrder getProjectOrder() {
//		return projectOrder;
//	}
//
//	public void setProjectOrder(ProjectOrder projectOrder) {
//		this.projectOrder = projectOrder;
//	}
//
//	public String getTissueType() {
//		return tissueType;
//	}
//
//	public void setTissueType(String tissueType) {
//		this.tissueType = tissueType;
//	}
//
//	public String getTumorType() {
//		return tumorType;
//	}
//
//	public void setTumorType(String tumorType) {
//		this.tumorType = tumorType;
//	}
//	
//	public String getLimsId() {
//		return limsId;
//	}
//
//	public void setLimsId(String limsId) {
//		this.limsId = limsId;
//	}
//
//	public LocalDateTime getAssessionDate() {
//		return assessionDate;
//	}
//
//	public void setAssessionDate(LocalDateTime assessionDate) {
//		this.assessionDate = assessionDate;
//	}
//
//	public String getFixation() {
//		return fixation;
//	}
//
//	public void setFixation(String fixation) {
//		this.fixation = fixation;
//	}
//
//	public NucExtract getNucExtract() {
//		return nucExtract;
//	}
//
//	public void setNucExtract(NucExtract nucExtract) {
//		this.nucExtract = nucExtract;
//	}
//
//	public List<DnaHyb> getDnaHybs() {
//		return dnaHybs;
//	}
//
//	public void setDnaHybs(List<DnaHyb> dnaHybs) {
//		this.dnaHybs = dnaHybs;
//	}
//	
//}
