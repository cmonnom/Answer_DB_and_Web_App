package utsw.bicf.answer.reporting.finalreport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CNV;

public class CNVClinicalSignificance {

	CNVReportWithHighestTier v;
	String genes;
	List<Annotation> annotations;

	public CNVReportWithHighestTier getV() {
		return v;
	}

	public void setV(CNVReportWithHighestTier v) {
		this.v = v;
	}

	public String getGenes() {
		return genes;
	}

	public void setGenes(String genes) {
		this.genes = genes;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public CNVClinicalSignificance(CNVReportWithHighestTier v, String genes, List<Annotation> annotations) {
		super();
		this.v = v;
		this.genes = genes;
		this.annotations = annotations;
	}

	public Map<String, String> getAnnotationsByCategory() {
		return annotations.stream().collect(Collectors.groupingBy(Annotation::getCategory,
				Collectors.mapping(Annotation::getText, Collectors.joining(" "))));
	}

}
