package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cosmic_sample_fusion")
public class CosmicSampleFusion {
	
	public CosmicSampleFusion() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="comsic_sample_fusion_id")
	Integer cosmicSampleFusionId;
	
	
	@Column(name="cosmic_sample_id")
	String cosmicSampleId;
	
	@Column(name="cosmic_fusion_id")
	Integer cosmicFusionId;

	public Integer getCosmicSampleFusionId() {
		return cosmicSampleFusionId;
	}

	public void setCosmicSampleFusionId(Integer cosmicSampleFusionId) {
		this.cosmicSampleFusionId = cosmicSampleFusionId;
	}


	public Integer getCosmicFusionId() {
		return cosmicFusionId;
	}

	public void setCosmicFusionId(Integer cosmicFusionId) {
		this.cosmicFusionId = cosmicFusionId;
	}

	public String getCosmicSampleId() {
		return cosmicSampleId;
	}

	public void setCosmicSampleId(String cosmicSampleId) {
		this.cosmicSampleId = cosmicSampleId;
	}
	

}
