package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.Virus;

public class VirusRow {
	
	String oid; //variant id in MongoDB
	String virusName;
	String virusDescription;
	String virusAcc;
	Integer virusReadCount;
	Integer numCasesSeen;
	String numCasesSeenFormatted;
	Boolean utswAnnotated;
	FlagValue iconFlags;
	Boolean isSelected;
	String tumorNormalLabel = "Unknown";
	
	Map<Integer, AnnotatorSelection> selectionPerAnnotator;
	String highestTier;
	
	
	
	public VirusRow(Virus virus, Map<Integer, AnnotatorSelection> selectionPerAnnotator, Integer totalCases) {
		this.oid = virus.getMongoDBId().getOid();
		this.virusName = virus.getVirusName();
		this.utswAnnotated = virus.getUtswAnnotated();
		this.isSelected = virus.getSelected();
		this.virusAcc = virus.getVirusAcc();
		this.virusReadCount = virus.getVirusReadCount();
		this.numCasesSeen = virus.getNumCasesSeen();
		this.virusDescription = virus.getVirusDescription();
		if (virus.getSampleId() != null && virus.getSampleId().contains("_N_")) {
			this.tumorNormalLabel = "Normal";
		}
		else if (virus.getSampleId() != null && virus.getSampleId().contains("_T_")) {
			this.tumorNormalLabel = "Tumor";
		}
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
//		if (utswAnnotated != null && utswAnnotated) {
//			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
//		}
		if (utswAnnotated == null || !utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
		}
		iconFlags = new FlagValue(icons);
		
		this.selectionPerAnnotator = selectionPerAnnotator;
		
		this.numCasesSeenFormatted = this.numCasesSeen + "/" + totalCases;
		
		this.highestTier = virus.getHighestTier();
		
	}


	public String getOid() {
		return oid;
	}


	public String getVirusName() {
		return virusName;
	}


	public void setVirusName(String virusName) {
		this.virusName = virusName;
	}


	public Boolean getUtswAnnotated() {
		return utswAnnotated;
	}


	public void setUtswAnnotated(Boolean utswAnnotated) {
		this.utswAnnotated = utswAnnotated;
	}


	public FlagValue getIconFlags() {
		return iconFlags;
	}


	public void setIconFlags(FlagValue iconFlags) {
		this.iconFlags = iconFlags;
	}


	public Boolean getIsSelected() {
		return isSelected;
	}


	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Map<Integer, AnnotatorSelection> getSelectionPerAnnotator() {
		return selectionPerAnnotator;
	}


	public void setSelectionPerAnnotator(Map<Integer, AnnotatorSelection> selectionPerAnnotator) {
		this.selectionPerAnnotator = selectionPerAnnotator;
	}


	public void setOid(String oid) {
		this.oid = oid;
	}


	public String getVirusDescription() {
		return virusDescription;
	}


	public void setVirusDescription(String virusDescription) {
		this.virusDescription = virusDescription;
	}


	public String getVirusAcc() {
		return virusAcc;
	}


	public void setVirusAcc(String virusAcc) {
		this.virusAcc = virusAcc;
	}


	public Integer getVirusReadCount() {
		return virusReadCount;
	}


	public void setVirusReadCount(Integer virusReadCount) {
		this.virusReadCount = virusReadCount;
	}


	public Integer getNumCasesSeen() {
		return numCasesSeen;
	}


	public void setNumCasesSeen(Integer numCasesSeen) {
		this.numCasesSeen = numCasesSeen;
	}


	public String getNumCasesSeenFormatted() {
		return numCasesSeenFormatted;
	}


	public void setNumCasesSeenFormatted(String numCasesSeenFormatted) {
		this.numCasesSeenFormatted = numCasesSeenFormatted;
	}


	public String getTumorNormalLabel() {
		return tumorNormalLabel;
	}


	public void setTumorNormalLabel(String tumorNormalLabel) {
		this.tumorNormalLabel = tumorNormalLabel;
	}







}
