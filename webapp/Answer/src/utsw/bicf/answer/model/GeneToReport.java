package utsw.bicf.answer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="gene_to_report")
public class GeneToReport {
	
	public GeneToReport() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="gene_to_report_id")
	Integer geneToReportId;
	
	@Column(name="gene_name")
	String geneName;
	
//	@Transient
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="report_group_id")
	ReportGroup reportGroup;


	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public ReportGroup getReportGroup() {
		return reportGroup;
	}

	public void setReportGroup(ReportGroup reportGroup) {
		this.reportGroup = reportGroup;
	}

	public Integer getGeneToReportId() {
		return geneToReportId;
	}

	public void setGeneToReportId(Integer geneToReportId) {
		this.geneToReportId = geneToReportId;
	}
	


	
}
