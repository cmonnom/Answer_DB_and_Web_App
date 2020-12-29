package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_genomic_info_summary")
public class GenieGenomicInfoSummary {
	
	public GenieGenomicInfoSummary() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_genomic_info_summary_id")
	Integer genieGenomicInfoSummaryId;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	@Column(name="assay_id")
	String assayId;

	public Integer getGenieGenomicInfoSummaryId() {
		return genieGenomicInfoSummaryId;
	}

	public void setGenieGenomicInfoSummaryId(Integer genieGenomicInfoSummaryId) {
		this.genieGenomicInfoSummaryId = genieGenomicInfoSummaryId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
	}

	public String getAssayId() {
		return assayId;
	}

	public void setAssayId(String assayId) {
		this.assayId = assayId;
	}
	


}
