package utsw.bicf.answer.model.hybrid;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.CNV;

public class CNVRow {
	
	String oid; //variant id in MongoDB
	String genes;
	String chrom;
	String start;
	String end;
	String aberrationType;
	Integer copyNumber;
	Float score;
	Boolean utswAnnotated;
	FlagValue iconFlags;
	Boolean isSelected;
	
	public CNVRow(CNV cnv) {
		this.oid = cnv.getMongoDBId().getOid();
		this.genes = formatHTMLGenes(cnv.getGenes().stream().sorted().collect(Collectors.toList()));
		this.genes = cnv.getGenes().stream().sorted().collect(Collectors.joining(" "));
		this.chrom = TypeUtils.formatChromosome(cnv.getChrom());
		this.start = NumberFormat.getInstance().format(cnv.getStart());
		this.end = NumberFormat.getInstance().format(cnv.getEnd());
		this.aberrationType = cnv.getAberrationType();
		this.copyNumber = cnv.getCopyNumber();
		this.score = cnv.getScore();
		this.utswAnnotated = cnv.getUtswAnnotated();
		this.isSelected = cnv.getSelected();
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		if (utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
		}
//		else {
//			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
//		}
		iconFlags = new FlagValue(icons);
	}

	private String formatHTMLGenes(List<String> genes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < genes.size(); i++) {
			String gene = genes.get(i);
			sb.append(gene);
			if (i % 6 == 0 && i > 0) {
				sb.append("<br>");
			}
			else {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	public String getOid() {
		return oid;
	}


	public String getGenes() {
		return genes;
	}


	public String getChrom() {
		return chrom;
	}


	public String getStart() {
		return start;
	}


	public String getEnd() {
		return end;
	}


	public String getAberrationType() {
		return aberrationType;
	}


	public Integer getCopyNumber() {
		return copyNumber;
	}


	public Float getScore() {
		return score;
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
