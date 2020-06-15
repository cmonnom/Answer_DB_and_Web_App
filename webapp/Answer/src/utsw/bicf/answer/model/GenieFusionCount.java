package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_fusion_count")
public class GenieFusionCount {
	
	public GenieFusionCount() {
		
	}

	public GenieFusionCount(String oncotreeCode, Integer caseCount) {
		super();
		this.oncotreeCode = oncotreeCode;
		this.caseCount = caseCount;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_fusion_count_id")
	Integer genieFusionCountId;
	
	@Column(name="oncotree_code")
	String oncotreeCode;
	
	@Column(name="case_count")
	Integer caseCount;

	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
	}

	public Integer getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(Integer caseCount) {
		this.caseCount = caseCount;
	}

	public void incrementCount() {
		this.caseCount++;
	}

	public Integer getGenieFusionCountId() {
		return genieFusionCountId;
	}

	public void setGenieFusionCountId(Integer genieFusionCountId) {
		this.genieFusionCountId = genieFusionCountId;
	}

}
