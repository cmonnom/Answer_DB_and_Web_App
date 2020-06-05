package utsw.bicf.answer.model.extmapping.lookup;

import java.util.List;

import utsw.bicf.answer.model.MSKHotspot;
import utsw.bicf.answer.model.extmapping.clinicaltrials.ClinicalTrial;

public class LookupVariantDashboardSummary {
	
	LookupGeneSummaries variantSummaries;
	LookupOncoKBVariantSummary oncoKBSummary;
	LookupSummary fasmicSummary;
	LookupSummary uniprotSummary;
	MSKHotspot hotspot;
	LookupSummary hotspotSummary;
	List<ClinicalTrial> clinicalTrials;
	String clinicalTrialStudyUrl;

	public LookupVariantDashboardSummary() {
		super();
	}

	public LookupGeneSummaries getVariantSummaries() {
		return variantSummaries;
	}

	public void setVariantSummaries(LookupGeneSummaries variantSummaries) {
		this.variantSummaries = variantSummaries;
	}

	public LookupOncoKBVariantSummary getOncoKBSummary() {
		return oncoKBSummary;
	}

	public void setOncoKBSummary(LookupOncoKBVariantSummary oncoKBSummary) {
		this.oncoKBSummary = oncoKBSummary;
	}

	public LookupSummary getFasmicSummary() {
		return fasmicSummary;
	}

	public void setFasmicSummary(LookupSummary fasmicSummary) {
		this.fasmicSummary = fasmicSummary;
	}

	public LookupSummary getUniprotSummary() {
		return uniprotSummary;
	}

	public void setUniprotSummary(LookupSummary uniprotSummary) {
		this.uniprotSummary = uniprotSummary;
	}

	public MSKHotspot getHotspot() {
		return hotspot;
	}

	public void setHotspot(MSKHotspot hotspot) {
		this.hotspot = hotspot;
	}

	public LookupSummary getHotspotSummary() {
		return hotspotSummary;
	}

	public void setHotspotSummary(LookupSummary hotspotSummary) {
		this.hotspotSummary = hotspotSummary;
	}

	public List<ClinicalTrial> getClinicalTrials() {
		return clinicalTrials;
	}

	public void setClinicalTrials(List<ClinicalTrial> clinicalTrials) {
		this.clinicalTrials = clinicalTrials;
	}

	public String getClinicalTrialStudyUrl() {
		return clinicalTrialStudyUrl;
	}

	public void setClinicalTrialStudyUrl(String clinicalTrialStudyUrl) {
		this.clinicalTrialStudyUrl = clinicalTrialStudyUrl;
	}

	

}
