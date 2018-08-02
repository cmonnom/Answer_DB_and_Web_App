package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseAvailable {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public OrderCaseAvailable(OrderCase orderCase, User user) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate =orderCase.getEpicOrderDate();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		if (user.getIndividualPermission().getCanAssign()) {
			buttons.add(new Button("assignment_ind", "assignToUser", "Assign To", "info"));
		}
		if (user.getIndividualPermission().getCanView()) {
			buttons.add(new Button("visibility", "open-read-only", "Open in View Only Mode", "info"));
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



}
