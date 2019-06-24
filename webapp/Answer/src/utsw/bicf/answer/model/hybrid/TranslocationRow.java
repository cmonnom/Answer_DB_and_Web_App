package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.Translocation;

public class TranslocationRow {
	
	String oid; //variant id in MongoDB
	String fusionName;
	String leftGene;
	String rightGene;
	String leftBreakpoint;
	String rightBreakpoint;
	String leftStrand;
	String rightStrand;
	Integer rnaReads;
	Integer dnaReads;
	Boolean utswAnnotated;
	FlagValue iconFlags;
	Boolean isSelected;
	String leftExons;
	String rightExons;
	
	String fusionType;
	String annotations;
	
	Map<Integer, AnnotatorSelection> selectionPerAnnotator;
	
	public TranslocationRow(Translocation translocation, Map<Integer, AnnotatorSelection> selectionPerAnnotator) {
		this.oid = translocation.getMongoDBId().getOid();
		this.fusionName = translocation.getFusionName();
		this.leftGene = translocation.getLeftGene();
		this.rightGene = translocation.getRightGene();
		this.leftBreakpoint = translocation.getLeftBreakpoint();
		this.rightBreakpoint = translocation.getRightBreakpoint();
		this.leftStrand = translocation.getLeftStrand();
		this.rightStrand = translocation.getRightStrand();
		this.rnaReads = translocation.getRnaReads();
		this.dnaReads = translocation.getDnaReads();
		this.utswAnnotated = translocation.getUtswAnnotated();
		this.isSelected = translocation.getSelected();
		this.leftExons = translocation.getFirstExon();
		this.rightExons = translocation.getLastExon();
		this.fusionType = translocation.getFusionType();
		this.annotations = translocation.getAnnot();
		if (this.annotations != null && this.annotations.length() > 1) {
			//remove outer brackets, remove quotes, replace comma with new line
			this.annotations = this.annotations.substring(1, this.annotations.length() - 2).replaceAll("\"", "").replaceAll(",", "<br/>");
		}
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		if (utswAnnotated != null && utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
		}
//		else {
//			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
//		}
		iconFlags = new FlagValue(icons);
		
		this.selectionPerAnnotator = selectionPerAnnotator;
		
	}


	public String getOid() {
		return oid;
	}


	public String getFusionName() {
		return fusionName;
	}


	public String getLeftGene() {
		return leftGene;
	}


	public String getRightGene() {
		return rightGene;
	}


	public String getLeftBreakpoint() {
		return leftBreakpoint;
	}


	public String getRightBreakpoint() {
		return rightBreakpoint;
	}


	public String getLeftStrand() {
		return leftStrand;
	}


	public String getRightStrand() {
		return rightStrand;
	}


	public Integer getRnaReads() {
		return rnaReads;
	}


	public Integer getDnaReads() {
		return dnaReads;
	}


	public Boolean getUtswAnnotated() {
		return utswAnnotated;
	}


	public FlagValue getIconFlags() {
		return iconFlags;
	}


	public Boolean getIsSelected() {
		return isSelected;
	}


	public String getLeftExons() {
		return leftExons;
	}


	public String getRightExons() {
		return rightExons;
	}


	public Map<Integer, AnnotatorSelection> getSelectionPerAnnotator() {
		return selectionPerAnnotator;
	}


	public String getFusionType() {
		return fusionType;
	}


	public String getAnnotations() {
		return annotations;
	}





}
