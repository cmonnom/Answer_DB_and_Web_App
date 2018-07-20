package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationSearchResult {
	
	List<Annotation> utswAnnotations;
	List<Annotation> mdaAnnotations;
	Integer seenInCasesCount;
	String geneName;
	String variantName;
	String leftGene;
	String rightGene;
	String variantType;
	
	
	public AnnotationSearchResult() {
		
	}


	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public List<Annotation> getUtswAnnotations() {
		return utswAnnotations;
	}


	public List<Annotation> getMdaAnnotations() {
		return mdaAnnotations;
	}


	public Integer getSeenInCasesCount() {
		return seenInCasesCount;
	}


	public String getGeneName() {
		return geneName;
	}


	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}


	public String getVariantName() {
		return variantName;
	}


	public void setVariantName(String variantName) {
		this.variantName = variantName;
	}


	public String getLeftGene() {
		return leftGene;
	}


	public void setLeftGene(String leftGene) {
		this.leftGene = leftGene;
	}


	public String getRightGene() {
		return rightGene;
	}


	public void setRightGene(String rightGene) {
		this.rightGene = rightGene;
	}


	public String getVariantType() {
		return variantType;
	}


	public void setVariantType(String variantType) {
		this.variantType = variantType;
	}


	public void setUtswAnnotations(List<Annotation> utswAnnotations) {
		this.utswAnnotations = utswAnnotations;
	}


	public void setMdaAnnotations(List<Annotation> mdaAnnotations) {
		this.mdaAnnotations = mdaAnnotations;
	}


	public void setSeenInCasesCount(Integer seenInCasesCount) {
		this.seenInCasesCount = seenInCasesCount;
	}

}
