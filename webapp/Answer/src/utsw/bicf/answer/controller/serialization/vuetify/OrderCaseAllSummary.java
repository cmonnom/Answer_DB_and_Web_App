package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.OrderCaseAll;

public class OrderCaseAllSummary extends Summary<OrderCaseAll>{
//	
	public OrderCaseAllSummary(List<OrderCaseAll> orderCases, User user) {
		super(orderCases, "epicOrderNumber");
		
		//only allow to assign if user can edit
		if (user.getIndividualPermission().getAdmin() 
				|| (user.getIndividualPermission().getCanView())) {
		Header actions = new Header("Actions", "actions");
		actions.setButtons(true);
		headers.add(actions);
		
		}
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
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
		
		headers.add(new Header("Patient Name", "patientName"));
		
		Header steps = new Header("Progress", "progressFlags");
		steps.setIsFlag(true);
		steps.setSortable(false);
		steps.setAlign("left");
		headers.add(steps);
		
		headers.add(new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber"));
		headers.add(new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate"));
		Header icd10 = new Header("ICD 10", "icd10");
		icd10.setWidth("300px");
		headers.add(icd10);
		headers.add(new Header(new String[] {"Assigned", "To"}, "assignedTo"));
		headers.add(new Header(new String[] {"Date", "Received"}, "dateReceived"));
		
		
	}
}
