package utsw.bicf.answer.reporting.finalreport;

import utsw.bicf.answer.model.extmapping.CNV;

public class CNVReportWithHighestTier {

	CNV cnv;
	String highestAnnotationTier;
	String breadth;

	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}

	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}

	public CNVReportWithHighestTier(CNV cnv, String highestAnnotationTier) {
		super();
		this.highestAnnotationTier = highestAnnotationTier;
		this.cnv = cnv;
	}

	public CNV getCnv() {
		return cnv;
	}

	public void setCnv(CNV cnv) {
		this.cnv = cnv;
	}

	public String getBreadth() {
		return breadth;
	}

	public void setBreadth(String breadth) {
		this.breadth = breadth;
	}

	
	
}
