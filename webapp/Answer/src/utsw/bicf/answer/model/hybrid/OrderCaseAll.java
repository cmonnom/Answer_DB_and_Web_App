package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseAll {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	String assignedTo;
	List<String> assignedToIds;
	String patientName;
	
	List<Button> buttons = new ArrayList<Button>();
	FlagValue iconFlags;
	
	public OrderCaseAll(OrderCase orderCase, List<User> users, User curentUser) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate =orderCase.getEpicOrderDate();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		this.assignedToIds = orderCase.getAssignedTo();
		this.patientName = orderCase.getPatientName();
		List<String> userNames = new ArrayList<String>();
		for (String userId : orderCase.getAssignedTo()) {
			for (User user : users) {
				if (userId.equals(user.getUserId().toString())) {
					userNames.add(user.getFullName());
				}
			}
		}
		this.assignedTo = userNames.stream().collect(Collectors.joining("<br/>"));
		if (curentUser.getIndividualPermission().getCanAssign()) {
			buttons.add(new Button("assignment_ind", "assignToUser", "Assign To", "info"));
		}
		if (curentUser.getIndividualPermission().getCanView()) {
			buttons.add(new Button("visibility", "open-read-only", "Open in View Only Mode", "info"));
		}
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		int step = 0;
		int totalSteps = OrderCase.getTotalSteps();
		if (orderCase.getHistory() != null && !orderCase.getHistory().isEmpty()) {
			step = orderCase.getHistory().get(orderCase.getHistory().size() - 1).getStep(); //get the last step
		}
		
		for (int i = 0; i < totalSteps; i++) {
			String color = i <= step ? "info" : "grey";
			icons.add(new VuetifyIcon("mdi-numeric-" + i + "-box", color, OrderCase.getStepTooltip(i)));
		}
		
		iconFlags = new FlagValue(icons);
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

	public FlagValue getIconFlags() {
		return iconFlags;
	}



}
