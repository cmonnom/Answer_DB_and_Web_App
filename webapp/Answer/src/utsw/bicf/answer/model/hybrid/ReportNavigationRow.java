package utsw.bicf.answer.model.hybrid;

public class ReportNavigationRow implements Comparable<ReportNavigationRow> {

	String gene;
	int indicatedTherapyCount;
	int clinicalTrialCount;
	int strongClinicalSignificanceCount;
	int possibleClinicalSignificanceCount;
	int unknownClinicalSignificanceCount;
	int cnvCount;
	int fusionCount;
	String label;
	
	public ReportNavigationRow() {
	}
	
	public ReportNavigationRow(String geneName, String label) {
		this.gene = geneName;
		this.label = label;
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

	@Override
	public int compareTo(ReportNavigationRow o) {
		int thisTotal = 0;
		int oTotal = 0;
		if (strongClinicalSignificanceCount > 0) {
			thisTotal += 7;
		}
		if (possibleClinicalSignificanceCount > 0) {
			thisTotal += 3;
		}
		if (unknownClinicalSignificanceCount > 0) {
			thisTotal += 1;
		}
		if (o.strongClinicalSignificanceCount > 0) {
			oTotal += 7;
		}
		if (o.possibleClinicalSignificanceCount > 0) {
			oTotal += 3;
		}
		if (o.unknownClinicalSignificanceCount > 0) {
			oTotal += 1;
		}
		if (thisTotal < oTotal) {
			return 1;
		}
		if (thisTotal > oTotal) {
			return -1;
		}
		if (thisTotal == oTotal) {
			return gene.compareTo(o.gene);
		}
		return 0;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
