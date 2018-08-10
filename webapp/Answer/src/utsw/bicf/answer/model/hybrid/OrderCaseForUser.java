package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseForUser {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	String patientName;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public OrderCaseForUser(OrderCase orderCase) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate = orderCase.getEpicOrderDate();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		this.patientName = orderCase.getPatientName();
		buttons.add(new Button("create", "open", "Work on Case", "info"));
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



}
