package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_fusion")
public class GenieFusion {
	
	public GenieFusion() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_fusion_id")
	Integer genieFusionId;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	@Column(name="tumor_sample_barcode")
	String tumorSampleBarcode;
	
	@Column(name="genie_sample_id")
	Integer genieSampleId;
	
	@Column(name="fusion_name")
	String fusionName;

	public Integer getGenieFusionId() {
		return genieFusionId;
	}

	public void setGenieFusionId(Integer genieFusionId) {
		this.genieFusionId = genieFusionId;
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

	public String getFusionName() {
		return fusionName;
	}

	public void setFusionName(String fusionName) {
		this.fusionName = fusionName;
	}


}
