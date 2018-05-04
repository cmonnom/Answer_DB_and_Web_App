package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.hybrid.OrderCaseAvailable;

public class OrderCaseAvailableSummary extends Summary<OrderCaseAvailable>{
//	
	public OrderCaseAvailableSummary(List<OrderCaseAvailable> orderCases) {
		super(orderCases, "epicOrderNumber");
	}
	
	

	@Override
	public void initializeHeaders() {
		headers.add(new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber"));
		headers.add(new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate"));
		Header icd10 = new Header("ICD 10", "icd10");
		icd10.setWidth("200px");
		headers.add(icd10);
		headers.add(new Header(new String[] {"Date", "Received"}, "dateReceived"));
		Header actions = new Header(new String[] {"Assign", "To"}, "actions");
		actions.setButtons(true);
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
