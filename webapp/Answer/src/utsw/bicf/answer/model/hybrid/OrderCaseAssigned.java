package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseAssigned {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String assignedTo;
	List<String> assignedToIds;
	String caseId;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public OrderCaseAssigned(OrderCase orderCase, List<User> users) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate = orderCase.getEpicOrderDate();
		this.caseId = orderCase.getCaseId();
		this.assignedToIds = orderCase.getAssignedTo();
		List<String> userNames = new ArrayList<String>();
		for (String userId : orderCase.getAssignedTo()) {
			for (User user : users) {
				if (userId.equals(user.getUserId().toString())) {
					userNames.add(user.getFullName());
				}
			}
		}
		this.dateReceived = orderCase.getReceivedDate();
		this.assignedTo = userNames.stream().collect(Collectors.joining("<br/>"));
		
		buttons.add(new Button("assignment_ind", "assignToUser", "Reassign", "info"));
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

	public String getAssignedTo() {
		return assignedTo;
	}

	public String getCaseId() {
		return caseId;
	}
	
	public List<Button> getButtons() {
		return buttons;
	}

	public List<String> getAssignedToIds() {
		return assignedToIds;
	}

	public void setAssignedToIds(List<String> assignedToIds) {
		this.assignedToIds = assignedToIds;
	}


}
