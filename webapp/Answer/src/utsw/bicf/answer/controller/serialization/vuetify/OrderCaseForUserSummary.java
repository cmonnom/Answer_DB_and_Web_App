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
		headers.add(type);
		
		Header hiddenType = new Header("Case Type", "caseType");
		hiddenType.setIsHidden(true);
		headers.add(hiddenType);
		
		Header hiddenCaseId = new Header("Case ID", "caseId");
		hiddenCaseId.setIsHidden(true);
		headers.add(hiddenCaseId);
		
		headers.add(new Header("Patient Name", "patientName"));
		Header steps = new Header("Progress", "progressFlags");
		steps.setIsFlag(true);
		steps.setSortable(false);
		steps.setAlign("left");
		headers.add(steps);  //keep hidden until ready for prime time.
		headers.add(new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber"));
		headers.add(new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate"));
		Header icd10 = new Header("ICD 10", "icd10");
		icd10.setWidth("300px");
		icd10.setWidthValue(300);
		headers.add(icd10);
		headers.add(new Header(new String[] {"Date", "Received"}, "dateReceived"));
		Header actions = new Header("Open", "actions");
		actions.setButtons(true);
		actions.setAlign("left");
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
