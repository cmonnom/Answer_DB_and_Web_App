package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseForUser {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	String patientName;
	FlagValue typeFlags;
	String caseType;
	boolean active;
	String oncotreeDiagnosis;
	
	List<Button> buttons = new ArrayList<Button>();
	FlagValue progressFlags;
	
	public OrderCaseForUser(OrderCase orderCase, User currentUser) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate = orderCase.getEpicOrderDate();
		this.oncotreeDiagnosis = orderCase.getOncotreeDiagnosis();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		this.patientName = orderCase.getPatientName();
		this.active = orderCase.getActive();
		buttons.add(new Button("create", "open", "Work on Case", "info"));
		if (CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_REPORTING)
				|| CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_FINALIZED)) {
			if (currentUser.getIndividualPermission().getCanReview()) {
				buttons.add(new Button("assignment", "edit-report", "View/Edit Report", "info"));
			}
			else {
				buttons.add(new Button("assignment", "open-report-read-only", "Open Report in View Only Mode", "info"));
			}
		}
		if (orderCase.getActive() != null && orderCase.getActive()) {
			if (CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_FINALIZED)) {
				buttons.add(new Button("mdi-logout", "deactivate-case", "Archive the case / Final Sign Out", "info"));
			}
			else {
				buttons.add(new Button("mdi-archive", "deactivate-case", "Archive the case (no sign out)", "info"));
			}
		}
		
		List<VuetifyIcon> typeIcons = new ArrayList<VuetifyIcon>();
		String iconName = null;
		String tooltip = null;
//		//TODO remove this
//		if (orderCase.getCaseId().equals("ORD528")) {
//			orderCase.setType(OrderCase.TYPE_RESEARCH);
//		}
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
		typeIcons.add(new VuetifyIcon(iconName, "grey", tooltip));
		typeFlags = new FlagValue(typeIcons);
		
		this.caseType = orderCase.getType();
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		int step = 0;
		int totalSteps = OrderCase.getTotalSteps();
		if (orderCase.getCaseHistory() != null && !orderCase.getCaseHistory().isEmpty()) {
			step = orderCase.getCaseHistory().get(orderCase.getCaseHistory().size() - 1).getStep(); //get the last step
		}
		
		for (int i = 0; i < totalSteps; i++) {
			String color = i <= step ? "info" : "grey";
			icons.add(new VuetifyIcon("mdi-numeric-" + i + "-box", color, OrderCase.getStepTooltip(i)));
		}
		
		progressFlags = new FlagValue(icons);
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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}



}
