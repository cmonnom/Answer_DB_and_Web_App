package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.User;

public class ReportGroupTableRow {
	
	Integer reportGroupId;
	String groupName;
	String description;
	String referenceUrl;
	String genes;
	String createdBy;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public ReportGroupTableRow(ReportGroup reportGroup, User currentUser) {
		this.reportGroupId = reportGroup.getReportGroupId();
		this.groupName = reportGroup.getGroupName();
		this.description = reportGroup.getDescription();
		this.referenceUrl = reportGroup.getLink();
		if (reportGroup.getGenesToReport() != null) {
			genes = reportGroup.getGenesToReport().stream()
					.map(gr -> gr.getGeneName())
					.sorted()
					.collect(Collectors.joining(" "));
		}
		this.createdBy = reportGroup.getCreatedBy().getFullName();
		if (ControllerUtil.isOwnerOrAdmin(currentUser, reportGroup.getCreatedBy())) {
			buttons.add(new Button("create", "editReportGroup", "Edit Gene Set", "info"));
			//shouldn't be able to delete required gene sets like ACMG
			if (reportGroup.getRequired() == null || !reportGroup.getRequired()) {
				buttons.add(new Button("delete", "deleteReportGroup", "Delete Gene Set (irreversible!)", "error"));
			}
		}
	}

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

	public String getReferenceUrl() {
		return referenceUrl;
	}

	public void setReferenceUrl(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}

	public String getGenes() {
		return genes;
	}

	public void setGenes(String genes) {
		this.genes = genes;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}




}
