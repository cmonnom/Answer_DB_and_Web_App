package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.OrderCaseForUser;

public class OrderCaseForUserSummary extends Summary<OrderCaseForUser>{
//	
	public OrderCaseForUserSummary(List<OrderCaseForUser> casesForUser, List<HeaderOrder> headerOrders) {
		super(casesForUser, "epicOrderNumber", headerOrders);
	}
	
	

	@Override
	public void initializeHeaders() {
		Header type = new Header("Type", "typeFlags");
		type.setIsFlag(true);
		type.setToolTip(new ToolTip("Clinical or Research Case"));
		type.setIsSafe(true);
		headers.add(type);
		
		Header hiddenType = new Header("Case Type", "caseType");
		hiddenType.setIsHidden(true);
		hiddenType.setIsSafe(true);
		headers.add(hiddenType);
		
		Header hiddenCaseId = new Header("Case ID", "caseId");
		hiddenCaseId.setIsHidden(true);
		hiddenCaseId.setIsSafe(true);
		headers.add(hiddenCaseId);
		
		Header patientName = new Header("Patient Name", "patientName");
		patientName.setIsSafe(true);
		headers.add(patientName);
		Header steps = new Header("Progress", "progressFlags");
		steps.setIsFlag(true);
		steps.setSortable(false);
		steps.setIsSafe(true);
		steps.setAlign("left");
		headers.add(steps);  //keep hidden until ready for prime time.
		Header epicOrderNumber = new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber");
		epicOrderNumber.setWidth("95px");
		epicOrderNumber.setIsSafe(true);
		headers.add(epicOrderNumber);
		Header epicOrderDate = new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate");
		epicOrderDate.setWidth("105px");
		epicOrderDate.setIsSafe(true);
		headers.add(epicOrderDate);
		Header oncotreeDiagnosis = new Header(new String[] {"Oncotree", "Code"}, "oncotreeDiagnosis");
		oncotreeDiagnosis.setWidth("90px");
		oncotreeDiagnosis.setIsSafe(true);
		headers.add(oncotreeDiagnosis);
		Header icd10 = new Header("ICD 10", "icd10");
		icd10.setWidth("300px");
		icd10.setWidthValue(300);
		icd10.setIsSafe(true);
		headers.add(icd10);
		Header dateReceived = new Header(new String[] {"Date", "Received"}, "dateReceived");
		dateReceived.setIsSafe(true);
		headers.add(dateReceived);
		Header actions = new Header("Actions", "actions");
		actions.setButtons(true);
		actions.setIsSafe(true);
		actions.setAlign("left");
		
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
