package utsw.bicf.answer.model.extmapping;

import java.text.NumberFormat;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNVReport {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String genes;
	String chrom;
	Integer start;
	Integer end;
	String startFormatted;
	String endFormatted;
	Integer copyNumber;
	String comment;
	String loci;
	String cytoband;
	boolean readonly;
	String aberrationType;
	String cytobandTruncated;
	String highestAnnotationTier;
	
	public CNVReport() {
		
	}


	public CNVReport(String text, CNV c) {
		this.mongoDBId = c.mongoDBId;
		this.genes = c.getGenes().stream().collect(Collectors.joining(" "));
		this.chrom = c.chrom;
		this.start = c.start;
		this.end = c.end;
		this.startFormatted = c.startFormatted;
		this.endFormatted = c.endFormatted;
		this.copyNumber = c.copyNumber;
		this.comment = text;
		this.loci = this.chrom + ":" + this.start + "-" + this.end;
		this.cytoband = c.getCytoband();
		this.aberrationType = "";
		if ("gain".equals(c.getAberrationType())
				|| "amplification".equals(c.getAberrationType())) {
			aberrationType = "Gain ";
		}
		else if (c.getAberrationType() != null && c.getAberrationType().contains("loss")) {
			aberrationType = "Loss ";
		}
		this.cytobandTruncated = c.getCytoband().substring(0, 1);
	}


	public CNVReport(String text, CNV c, String highestAnnotationTier) {
		this.mongoDBId = c.mongoDBId;
		this.genes = c.getGenes().stream().collect(Collectors.joining(" "));
		this.chrom = c.chrom;
		this.start = c.start;
		this.end = c.end;
		this.startFormatted = c.startFormatted;
		this.endFormatted = c.endFormatted;
		this.copyNumber = c.copyNumber;
		this.comment = text;
		this.loci = this.chrom + ":" + this.start + "-" + this.end;
		this.cytoband = c.getCytoband();
		this.highestAnnotationTier = highestAnnotationTier;
		this.aberrationType = c.getAberrationType();
		this.cytobandTruncated = c.getCytoband().substring(0, 1);
	}


	public String getChrom() {
		return chrom;
	}


	public void setChrom(String chrom) {
		this.chrom = chrom;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}


	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}


	public String getGenes() {
		return genes;
	}


	public void setGenes(String genes) {
		this.genes = genes;
	}


	public Integer getStart() {
		return start;
	}


	public void setStart(Integer start) {
		this.start = start;
	}


	public Integer getEnd() {
		return end;
	}


	public void setEnd(Integer end) {
		this.end = end;
	}




	public Integer getCopyNumber() {
		return copyNumber;
	}


	public void setCopyNumber(Integer copyNumber) {
		this.copyNumber = copyNumber;
	}




	public String getStartFormatted() {
		if (startFormatted == null && start != null) {
			startFormatted = NumberFormat.getInstance().format(start);
		}
		return startFormatted;
	}


	public void setStartFormatted(String startFormatted) {
		this.startFormatted = startFormatted;
	}


	public String getEndFormatted() {
		if (endFormatted == null && end != null) {
			endFormatted = NumberFormat.getInstance().format(end);
		}
		return endFormatted;
	}


	public void setEndFormatted(String endFormatted) {
		this.endFormatted = endFormatted;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getLoci() {
		return loci;
	}


	public void setLoci(String loci) {
		this.loci = loci;
	}


	public String getCytoband() {
		return cytoband;
	}


	public void setCytoband(String cytoband) {
		this.cytoband = cytoband;
	}


	public boolean isReadonly() {
		return readonly;
	}


	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}


	public String getAberrationType() {
		return aberrationType;
	}


	public void setAberrationType(String aberrationType) {
		this.aberrationType = aberrationType;
	}


	public String getCytobandTruncated() {
		return cytobandTruncated;
	}


	public void setCytobandTruncated(String cytobandTruncated) {
		this.cytobandTruncated = cytobandTruncated;
	}


	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}


	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}






}
