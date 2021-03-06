package utsw.bicf.answer.model.hybrid;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.ReportGroup;

public class ReportGroupForDisplay implements Comparable<ReportGroupForDisplay>{
	
	String groupName;
	String description;
	String link;
	Boolean required;
	List<String> genesToReport;
	
	public ReportGroupForDisplay() {
		
	}
	
	public ReportGroupForDisplay(ReportGroup reportGroup) {
		this.groupName = reportGroup.getGroupName();
		this.description = reportGroup.getDescription();
		this.link = reportGroup.getLink();
		this.required = reportGroup.getRequired();
		genesToReport = reportGroup.getGenesToReport().stream()
				.map(g -> g.getGeneName()).sorted().collect(Collectors.toList());
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
	public List<String> getGenesToReport() {
		return genesToReport;
	}
	public void setGenesToReport(List<String> genesToReport) {
		this.genesToReport = genesToReport;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	@Override
	public int compareTo(ReportGroupForDisplay o) {
		if (o != null && o.getGroupName() != null) {
			return this.getGroupName().compareTo(o.getGroupName());
		}
		return 0;
		
	}

}
