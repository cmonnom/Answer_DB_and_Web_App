package utsw.bicf.answer.model.hybrid;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseAll {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	String assignedTo;
	List<String> assignedToIds;
	List<String> groupIds;
	String patientName;
	FlagValue typeFlags;
	String caseType;
	boolean active;
	String oncotreeDiagnosis;
	String uploadedDate; //date the case was uploaded into Answer
	
	List<Button> buttons = new ArrayList<Button>();
	FlagValue progressFlags;
	String caseOwnerId;
	
	public OrderCaseAll(OrderCase orderCase, List<User> users, User currentUser) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate =orderCase.getEpicOrderDate();
		this.oncotreeDiagnosis = orderCase.getOncotreeDiagnosis();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		this.assignedToIds = orderCase.getAssignedTo();
		this.groupIds = orderCase.getGroupIds();
		this.patientName = orderCase.getPatientName();
		this.active = orderCase.getActive();
		this.caseOwnerId = orderCase.getCaseOwner();
		List<String> userNames = new ArrayList<String>();
		for (String userId : orderCase.getAssignedTo()) {
			for (User user : users) {
				if (userId.equals(user.getUserId().toString())) {
					if (userId.equals(orderCase.getCaseOwner())) {
						userNames.add("<b>" + user.getFullName() + "</b>");
					}
					else {
						userNames.add(user.getFullName());
					}
				}
			}
		}
		this.assignedTo = userNames.stream().collect(Collectors.joining("<br/>"));
		if (currentUser.getIndividualPermission().getCanAssign()) {
			buttons.add(new Button("mdi-account-arrow-left", "assignToUser", "Assign To", "info"));
		}
		if (currentUser.getIndividualPermission().getCanView()) {
			buttons.add(new Button("visibility", "./openCaseReadOnly/" + caseId, "Open in View Only Mode", "info", true));
			if (CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_REPORTING)
					|| CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_FINALIZED)) {
				boolean isAssigned = ControllerUtil.isUserAssignedToCase(orderCase, currentUser);
				if (currentUser.getIndividualPermission().getCanReview() && isAssigned) {
					buttons.add(new Button("assignment", "./openReport/" + caseId, "View/Edit Report", "info", true));
				}
				else {
					buttons.add(new Button("assignment", "./openReportReadOnly/" + caseId , "Open Report in View Only Mode", "info", true));
				}
			}
		}
		if (currentUser.getIndividualPermission().getAdmin()) {
			buttons.add(new Button("mdi-account-group", "assignToGroup", "Set Group(s)", "info"));
		}
		
		
		List<VuetifyIcon> typeIcons = new ArrayList<VuetifyIcon>();
		String iconName = null;
		String tooltip = null;
		if (OrderCase.TYPE_CLINICAL.equals(orderCase.getType())) {
			iconName = "fa-user-md";
			tooltip = OrderCase.TYPE_CLINICAL + " case";
		}
		else if (OrderCase.TYPE_RESEARCH.equals(orderCase.getType())) {
			iconName = "fa-flask";
			tooltip = OrderCase.TYPE_RESEARCH + " case";
		}
		else if (OrderCase.TYPE_CLINICAL_RESEARCH.equals(orderCase.getType())) {
			iconName = "fa-flask";
			tooltip = OrderCase.TYPE_CLINICAL_RESEARCH + " case";
		}
		typeIcons.add(new VuetifyIcon(iconName, "grey", tooltip, 20));
		typeFlags = new FlagValue(typeIcons);
		
		this.caseType = orderCase.getType();
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		int step = 0;
		int totalSteps = OrderCase.getTotalSteps();
		if (orderCase.getCaseHistory() != null && !orderCase.getCaseHistory().isEmpty()) {
			step = orderCase.getCaseHistory().get(orderCase.getCaseHistory().size() - 1).getStep(); //get the last step
		}
		
		for (int i = 0; i < totalSteps; i++) {
			String color = "info";
			String when = "after";
			if (i == step) {
				color = "info";
				when = "during";
			}
			else if (i > step) {
				color = "grey";
				when = "before";
			}
			String icon = "mdi-numeric-" + i + "-box";
			icons.add(new VuetifyIcon(icon, color, OrderCase.getStepTooltip(when, i)));
		}
		
		progressFlags = new FlagValue(icons);
		
		if (orderCase.getCaseHistory() != null) {
			for (CaseHistory hist : orderCase.getCaseHistory()) {
				if (hist.getStep().equals(CaseHistory.STEP_NOT_ASSIGNED)) {
					OffsetDateTime date = OffsetDateTime.parse(hist.getTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).withOffsetSameInstant(TypeUtils.offset);
					uploadedDate = date.format(TypeUtils.monthFormatter);
					break;
				}
			}
			if (uploadedDate == null) { //no uploaded date for old cases
				for (CaseHistory hist : orderCase.getCaseHistory()) {
					if (hist.getStep().equals(CaseHistory.STEP_ASSIGNED)) {
						OffsetDateTime date = OffsetDateTime.parse(hist.getTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).withOffsetSameInstant(TypeUtils.offset);
						uploadedDate = date.format(TypeUtils.monthFormatter);
						break;
					}
				}
			}
		}
	}

	public String getEpicOrderNumber() {
		return epicOrderNumber;
	}

	public String getEpicOrderDate() {
		return epicOrderDate;
	}

	public String getDateReceived() {
		return dateReceived;
	}

	public String getIcd10() {
		return icd10;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public String getCaseId() {
		return caseId;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public List<String> getAssignedToIds() {
		return assignedToIds;
	}

	public String getPatientName() {
		return patientName;
	}


	public FlagValue getTypeFlags() {
		return typeFlags;
	}

	public FlagValue getProgressFlags() {
		return progressFlags;
	}

	public String getCaseType() {
		return caseType;
	}

	public boolean isActive() {
		return active;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}

	public String getUploadedDate() {
		return uploadedDate;
	}

	public String getCaseOwnerId() {
		return caseOwnerId;
	}




}
