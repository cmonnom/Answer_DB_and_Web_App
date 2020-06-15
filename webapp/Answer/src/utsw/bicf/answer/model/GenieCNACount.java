package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_cna_count")
public class GenieCNACount {
	
	public GenieCNACount() {
		
	}

	public GenieCNACount(String oncotreeCode, String hugoSymbol, Integer caseCount) {
		super();
		this.oncotreeCode = oncotreeCode;
		this.hugoSymbol = hugoSymbol;
		this.caseCount = caseCount;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_cna_count_id")
	Integer genieCNACountId;
	
	@Column(name="oncotree_code")
	String oncotreeCode;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	
	@Column(name="case_count")
	Integer caseCount;

	public Integer getGenieCNACountId() {
		return genieCNACountId;
	}

	public void setGenieCNACountId(Integer genieCNACountId) {
		this.genieCNACountId = genieCNACountId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
	}

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

}
