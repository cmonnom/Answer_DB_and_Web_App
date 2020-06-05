package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_cna")
public class GenieCNA {
	
	public GenieCNA() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_cna_id")
	Integer genieCNAId;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	@Column(name="tumor_sample_barcode")
	String tumorSampleBarcode;
	
	@Column(name="genie_sample_id")
	Integer genieSampleId;
	
	@Column(name="cna_value")
	Float cnaValue;

	public Integer getGenieCNAId() {
		return genieCNAId;
	}

	public void setGenieCNAId(Integer genieCNAId) {
		this.genieCNAId = genieCNAId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
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

	public Float getCnaValue() {
		return cnaValue;
	}

	public void setCnaValue(Float cnaValue) {
		this.cnaValue = cnaValue;
	}
	

}
