package utsw.bicf.answer.reporting.parse;

public class BiomarkerTrialsRow {
	
	public static final String HEADER_SELECTED_BIOMARKER = "Selected Biomarker(s)*";
	public static final String HEADER_RELEVANT_BIOMARKER = "Relevant Biomarker(s)*";
	public static final String HEADER_DRUGS = "Drugs**";
	public static final String HEADER_TITLE = "Title";
	public static final String HEADER_NCTID = "NCTID";
	public static final String HEADER_MDACC_PROTOCOL_ID = "MDACC Protocol ID";
	public static final String HEADER_PHASE = "Phase";
	public static final String HEADER_PI = "PI";
	public static final String HEADER_DEPT = "Dept";
	public static final String HEADER_ADD_REQUIRED_BIOMARKERS = "Additional Required Biomarker(s)";
	
	String selectedBiomarker;
	String relevantBiomarker;
	String drugs;
	String title;
	String nctid;
	String mdaddProtocolId;
	String phase;
	String pi;
	String dept;
	String additionalRequiredBiomarkers;
	
	public String getSelectedBiomarker() {
		return selectedBiomarker;
	}
	public void setSelectedBiomarker(String selectedBiomarker) {
		this.selectedBiomarker = selectedBiomarker;
	}
	public String getRelevantBiomarker() {
		return relevantBiomarker;
	}
	public void setRelevantBiomarker(String relevantBiomarker) {
		this.relevantBiomarker = relevantBiomarker;
	}
	public String getDrugs() {
		return drugs;
	}
	public void setDrugs(String drugs) {
		this.drugs = drugs;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNctid() {
		return nctid;
	}
	public void setNctid(String nctid) {
		this.nctid = nctid;
	}
	public String getMdaddProtocolId() {
		return mdaddProtocolId;
	}
	public void setMdaddProtocolId(String mdaddProtocolId) {
		this.mdaddProtocolId = mdaddProtocolId;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	
	public void prettyPrint() {
		String biomarker = selectedBiomarker != null ? selectedBiomarker : relevantBiomarker;
		System.out.println("biomarkers: " + biomarker + " NCTID: " + nctid + " PI: " + pi + " Drugs: " + drugs );
		
	}
	public String getAdditionalRequiredBiomarkers() {
		return additionalRequiredBiomarkers;
	}
	public void setAdditionalRequiredBiomarkers(String additionalRequiredBiomarkers) {
		this.additionalRequiredBiomarkers = additionalRequiredBiomarkers;
	}

}
