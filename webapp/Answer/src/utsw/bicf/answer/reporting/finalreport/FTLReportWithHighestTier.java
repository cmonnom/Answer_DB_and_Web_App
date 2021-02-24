package utsw.bicf.answer.reporting.finalreport;

import utsw.bicf.answer.model.extmapping.Translocation;

public class FTLReportWithHighestTier {

	Translocation ftl;
	String highestAnnotationTier;

	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}

	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}

	public FTLReportWithHighestTier(Translocation ftl, String highestAnnotationTier) {
		super();
		this.highestAnnotationTier = highestAnnotationTier;
		this.ftl = ftl;
	}

	public Translocation getFtl() {
		return ftl;
	}

	public void setFtl(Translocation ftl) {
		this.ftl = ftl;
	}


	
	
}
