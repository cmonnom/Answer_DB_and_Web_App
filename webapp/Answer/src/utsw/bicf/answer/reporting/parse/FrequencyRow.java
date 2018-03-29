package utsw.bicf.answer.reporting.parse;

public class FrequencyRow {
	
	String gene;
	String alteration;
	String cBio;
	String cosmic;
	String cms50;
	String t200;
	String germlineInT200Dataset;
	
	public static final String HEADER_GENE = "Gene";
	public static final String HEADER_ALTERATION = "Alteration";
	public static final String HEADER_CBIO = "cBIO";
	public static final String HEADER_COSMIC = "COSMIC";
	public static final String HEADER_CMS50 = "CMS50";
	public static final String HEADER_T200 = "T200";
	public static final String HEADER_GERMLINE = "*Germline in T200 dataset";
	
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getAlteration() {
		return alteration;
	}
	public void setAlteration(String alteration) {
		this.alteration = alteration;
	}
	public String getcBio() {
		return cBio;
	}
	public void setcBio(String cBio) {
		this.cBio = cBio;
	}
	public String getCosmic() {
		return cosmic;
	}
	public void setCosmic(String cosmic) {
		this.cosmic = cosmic;
	}
	public String getCms50() {
		return cms50;
	}
	public void setCms50(String cms50) {
		this.cms50 = cms50;
	}
	public String getT200() {
		return t200;
	}
	public void setT200(String t200) {
		this.t200 = t200;
	}
	public String getGermlineInT200Dataset() {
		return germlineInT200Dataset;
	}
	public void setGermlineInT200Dataset(String germlineInT200Dataset) {
		this.germlineInT200Dataset = germlineInT200Dataset;
	}
	
	public void prettyPrint() {
		System.out.println("Gene: " + gene + " Cosmic: " + cosmic + " germline: " + germlineInT200Dataset );
		
	}

}
