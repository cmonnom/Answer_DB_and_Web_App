package utsw.bicf.answer.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="report_group")
public class ReportGroup {
	
	public ReportGroup() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="report_group_id")
	Integer reportGroupId;
	
	@Column(name="group_name")
	String groupName;
	
	@Column(name="description")
	String description;
	
	@Column(name="link")
	String link;

	@OneToMany(mappedBy="reportGroup", fetch=FetchType.EAGER, cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	List<GeneToReport> genesToReport;
	
	public Integer getReportGroupId() {
		return reportGroupId;
	}

	public void setReportGroupId(Integer reportGroupId) {
		this.reportGroupId = reportGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<GeneToReport> getGenesToReport() {
		return genesToReport;
	}

	public void setGenesToReport(List<GeneToReport> genesToReport) {
		this.genesToReport = genesToReport;
	}

	


	
}
