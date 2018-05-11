package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.Variant;

public class OpenCaseRow {
	
	String oid; //variant id in MangoDB
	String chrom;
	Integer pos;
	String chromPos;
	String geneName;
	String geneVariant;
	String effects;
	String notation;
	String tumorAltFrequency;
	Integer tumorAltDepth;
	Integer tumorTotalDepth;
	String normalAltFrequency;
	Integer normalAltDepth;
	Integer normalTotalDepth;
	String rnaAltFrequency;
	Integer rnaAltDepth;
	Integer rnaTotalDepth;
	List<String> callSet;
	String type;
	List<Integer> cosmicPatients;
	List<String> externalIds; //list of external database ids (dbsnp, cosmic, etc)
	String alt;
	List<String> filters; //list of filers
	FlagValue iconFlags;
	Boolean isSelected;
	Boolean mdaAnnotated;
	Boolean utswAnnotated;
	
	
	List<Button> buttons = new ArrayList<Button>();
	
	public OpenCaseRow(Variant variant) {
		this.oid = variant.getMangoDBId().getOid();
		this.chrom = variant.getChrom();
		this.pos = variant.getPos();
		this.chromPos = variant.getChrom().toUpperCase() + ":" + variant.getPos();
		this.geneName = variant.getGeneName();
		this.geneVariant = variant.getGeneName() + " " + variant.getNotation();
		this.effects = variant.getEffects() != null ? variant.getEffects().stream().collect(Collectors.joining("<br/>")) : null;
		this.notation = variant.getNotation();
		this.tumorAltFrequency = variant.getTumorAltFrequency() != null ? String.format("%.2f", Float.parseFloat(variant.getTumorAltFrequency()) * 100) : null;
		this.tumorAltDepth = variant.getTumorAltDepth();
		this.tumorTotalDepth = variant.getTumorTotalDepth();
		this.normalAltFrequency = variant.getNormalAltFrequency() != null ? String.format("%.2f", Float.parseFloat(variant.getNormalAltFrequency()) * 100) : null;
		this.normalAltDepth = variant.getNormalAltDepth();
		this.normalTotalDepth = variant.getNormalTotalDepth();
		this.rnaAltFrequency = variant.getRnaAltFrequency() != null ? String.format("%.2f", Float.parseFloat(variant.getRnaAltFrequency()) * 100) : null;
		this.rnaAltDepth = variant.getRnaAltDepth();
		this.rnaTotalDepth = variant.getRnaTotalDepth();
		this.callSet = variant.getCallSet();
		this.type = variant.getType();
		this.cosmicPatients = variant.getCosmicPatients();
		this.externalIds = variant.getIds();
		this.alt = variant.getAlt();
		this.filters = variant.getFilters();
		this.isSelected = variant.getSelected();
		this.mdaAnnotated = variant.getMdaAnnotated();
		this.utswAnnotated = variant.getUtswAnnotated();
		
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		boolean failed = filters.contains(Variant.VALUE_FAIL);
		if (failed) {
			icons.add(new VuetifyIcon("cancel", "red", "Failed QC"));
		}
		else {
			icons.add(new VuetifyIcon("check_circle", "green", "Passed QC"));
		}
		if (mdaAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "green", "MDA Annotations"));
		}
		else {
			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No MDA Annotations"));
		}
		if (utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
		}
		else {
			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
		}
		iconFlags = new FlagValue(icons);
		
	}


	public List<Button> getButtons() {
		return buttons;
	}




	public String getEffects() {
		return effects;
	}


	public String getNotation() {
		return notation;
	}


	public String getTumorAltFrequency() {
		return tumorAltFrequency;
	}


	public String getChromPos() {
		return chromPos;
	}

	public Integer getTumorAltDepth() {
		return tumorAltDepth;
	}


	public String getGeneVariant() {
		return geneVariant;
	}


	public String getChrom() {
		return chrom;
	}


	public Integer getPos() {
		return pos;
	}


	public List<String> getCallSet() {
		return callSet;
	}


	public String getType() {
		return type;
	}


	public List<Integer> getCosmicPatients() {
		return cosmicPatients;
	}


	public List<String> getExternalIds() {
		return externalIds;
	}


	public String getAlt() {
		return alt;
	}


	public List<String> getFilters() {
		return filters;
	}


	public FlagValue getIconFlags() {
		return iconFlags;
	}


	public Integer getTumorTotalDepth() {
		return tumorTotalDepth;
	}


	public String getNormalAltFrequency() {
		return normalAltFrequency;
	}


	public Integer getNormalAltDepth() {
		return normalAltDepth;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public String getRnaAltFrequency() {
		return rnaAltFrequency;
	}


	public Integer getRnaAltDepth() {
		return rnaAltDepth;
	}


	public Integer getRnaTotalDepth() {
		return rnaTotalDepth;
	}


	public String getGeneName() {
		return geneName;
	}


	public String getOid() {
		return oid;
	}


	public Boolean getIsSelected() {
		return isSelected;
	}



}
