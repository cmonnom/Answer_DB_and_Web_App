package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.Translocation;

public class TranslocationRow {
	
	String oid; //variant id in MangoDB
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
	
	
	public TranslocationRow(Translocation translocation) {
		this.oid = translocation.getMangoDBId().getOid();
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
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		if (utswAnnotated != null && utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
		}
		else {
			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
		}
		iconFlags = new FlagValue(icons);
		
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





}
