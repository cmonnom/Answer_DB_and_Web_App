package utsw.bicf.answer.model.hybrid;

import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleLowCoverageFromQC {
	
	Integer minDepth;
	Integer maxDepth;
	Integer medianDepth;
	Integer avgDepth;
	Integer cStart;
	String cStartFormatted;
	Integer cEnd;
	String cEndFormatted;
	String chr;
	String gene;
	Integer exonNb;
	String locus;
	
	public SampleLowCoverageFromQC() {
	}
	
	public SampleLowCoverageFromQC(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "minDepth": minDepth = (Integer) values[i]; break;
			case "maxDepth": maxDepth = (Integer) values[i]; break;
			case "medianDepth": medianDepth = (Integer) values[i]; break;
			case "avgDepth": avgDepth = (Integer) values[i]; break;
			case "cStart": cStart = (Integer) values[i]; break;
			case "cEnd": cEnd = (Integer) values[i]; break;
			case "chr": chr = formatChrName(values[i]); break;
			case "gene": gene = (String) values[i]; break;
			case "exonNb": exonNb = (Integer) values[i]; break;
			}
		}
		cStartFormatted = NumberFormat.getInstance().format(cStart);
		cEndFormatted =  NumberFormat.getInstance().format(cEnd);
		locus = chr.replace("CHR0", "CHR") + ":" + cStart + "-" + cEnd;
	}

	private String formatChrName(Object value) {
		String chromNbString = ((String) value).replace("chr", "");
		if (chromNbString.length() == 1) { //could be a single digit
			try {
				Integer chrNb = Integer.parseInt(chromNbString);
				String formattedChromName = "CHR " + String.format(Locale.US, "%02d", chrNb);
				return formattedChromName;
			} catch (NumberFormatException e) { //not a number, keep as is.
				return ((String) value).toUpperCase();
			}
		}
		return ((String) value).toUpperCase();
	}
	
	public Integer getMinDepth() {
		return minDepth;
	}

	public void setMinDepth(Integer minDepth) {
		this.minDepth = minDepth;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public Integer getMedianDepth() {
		return medianDepth;
	}

	public void setMedianDepth(Integer medianDepth) {
		this.medianDepth = medianDepth;
	}

	public Integer getAvgDepth() {
		return avgDepth;
	}

	public void setAvgDepth(Integer avgDepth) {
		this.avgDepth = avgDepth;
	}


	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public Integer getcStart() {
		return cStart;
	}

	public void setcStart(Integer cStart) {
		this.cStart = cStart;
	}

	public Integer getcEnd() {
		return cEnd;
	}

	public void setcEnd(Integer cEnd) {
		this.cEnd = cEnd;
	}

	public String getcStartFormatted() {
		return cStartFormatted;
	}

	public void setcStartFormatted(String cStartFormatted) {
		this.cStartFormatted = cStartFormatted;
	}

	public String getcEndFormatted() {
		return cEndFormatted;
	}

	public void setcEndFormatted(String cEndFormatted) {
		this.cEndFormatted = cEndFormatted;
	}

	public Integer getExonNb() {
		return exonNb;
	}

	public void setExonNbs(Integer exonNb) {
		this.exonNb = exonNb;
	}

	public String getLocus() {
		return locus;
	}

	public void setLocus(String locus) {
		this.locus = locus;
	}

}
