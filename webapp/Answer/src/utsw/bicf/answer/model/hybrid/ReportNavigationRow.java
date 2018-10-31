package utsw.bicf.answer.model.hybrid;

public class ReportNavigationRow {

	String gene;
	int indicatedTherapyCount;
	int clinicalTrialCount;
	int strongClinicalSignificanceCount;
	int possibleClinicalSignificanceCount;
	int unknownClinicalSignificanceCount;
	int cnvCount;
	int fusionCount;
	
	public ReportNavigationRow() {
	}
	
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public int getIndicatedTherapyCount() {
		return indicatedTherapyCount;
	}
	public void setIndicatedTherapyCount(int indicatedTherapyCount) {
		this.indicatedTherapyCount = indicatedTherapyCount;
	}
	public int getClinicalTrialCount() {
		return clinicalTrialCount;
	}
	public void setClinicalTrialCount(int clinicalTrialCount) {
		this.clinicalTrialCount = clinicalTrialCount;
	}
	public int getStrongClinicalSignificanceCount() {
		return strongClinicalSignificanceCount;
	}
	public void setStrongClinicalSignificanceCount(int strongClinicalSignificanceCount) {
		this.strongClinicalSignificanceCount = strongClinicalSignificanceCount;
	}
	public int getPossibleClinicalSignificanceCount() {
		return possibleClinicalSignificanceCount;
	}
	public void setPossibleClinicalSignificanceCount(int possibleClinicalSignificanceCount) {
		this.possibleClinicalSignificanceCount = possibleClinicalSignificanceCount;
	}
	public int getUnknownClinicalSignificanceCount() {
		return unknownClinicalSignificanceCount;
	}
	public void setUnknownClinicalSignificanceCount(int unknownClinicalSignificanceCount) {
		this.unknownClinicalSignificanceCount = unknownClinicalSignificanceCount;
	}
	public int getCnvCount() {
		return cnvCount;
	}
	public void setCnvCount(int cnvCount) {
		this.cnvCount = cnvCount;
	}
	public int getFusionCount() {
		return fusionCount;
	}
	public void setFusionCount(int fusionCount) {
		this.fusionCount = fusionCount;
	}
}
