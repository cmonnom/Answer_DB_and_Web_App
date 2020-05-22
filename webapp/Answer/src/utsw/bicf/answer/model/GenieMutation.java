package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_mutation")
public class GenieMutation {
	
	public GenieMutation() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_mutation_id")
	Integer genieMutationId;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	@Column(name="entrez_id")
	Integer entrezId;
	
	@Column(name="variant_classification")
	String variantClassification;
	
	@Column(name="tumor_sample_barcode")
	String tumorSampleBarcode;
	
	@Column(name="genie_sample_id")
	Integer genieSampleId;
	
	@Column(name="variant_notation")
	String variantNotation;
	
	@Column(name="amino_acid_position")
	Integer aminoAcidPosition;
	
	@Column(name="variant_change")
	String variantChange;
	
	@Column(name="amino_acid_notation")
	String aminoAcidNotation;
	
	@Column(name="variant_type")
	String variantType;

	public Integer getGenieMutationId() {
		return genieMutationId;
	}

	public void setGenieMutationId(Integer genieMutationId) {
		this.genieMutationId = genieMutationId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
	}

	public Integer getEntrezId() {
		return entrezId;
	}

	public void setEntrezId(Integer entrezId) {
		this.entrezId = entrezId;
	}

	public String getVariantClassification() {
		return variantClassification;
	}

	public void setVariantClassification(String variantClassification) {
		this.variantClassification = variantClassification;
	}

	public String getTumorSampleBarcode() {
		return tumorSampleBarcode;
	}

	public void setTumorSampleBarcode(String tumorSampleBarcode) {
		this.tumorSampleBarcode = tumorSampleBarcode;
	}

	public Integer getGenieSampleId() {
		return genieSampleId;
	}

	public void setGenieSampleId(Integer genieSampleId) {
		this.genieSampleId = genieSampleId;
	}

	public String getVariantNotation() {
		return variantNotation;
	}

	public void setVariantNotation(String variantNotation) {
		this.variantNotation = variantNotation;
	}

	public Integer getAminoAcidPosition() {
		return aminoAcidPosition;
	}

	public void setAminoAcidPosition(Integer aminoAcidPosition) {
		this.aminoAcidPosition = aminoAcidPosition;
	}

	public String getAminoAcidNotation() {
		return aminoAcidNotation;
	}

	public void setAminoAcidNotation(String aminoAcidNotation) {
		this.aminoAcidNotation = aminoAcidNotation;
	}

	public String getVariantChange() {
		return variantChange;
	}

	public void setVariantChange(String variantChange) {
		this.variantChange = variantChange;
	}

	public String getVariantType() {
		return variantType;
	}

	public void setVariantType(String variantType) {
		this.variantType = variantType;
	}

}
