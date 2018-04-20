package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.hybrid.OrderCaseForUser;

public class OrderCaseForUserSummary extends Summary<OrderCaseForUser>{
//	
	public OrderCaseForUserSummary(List<OrderCaseForUser> casesForUser) {
		super(casesForUser, "epicOrderNumber");
	}
	
	

	@Override
	public void initializeHeaders() {
		headers.add(new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber"));
		headers.add(new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate"));
		headers.add(new Header("ICD 10", "icd10"));
		headers.add(new Header(new String[] {"Date", "Received"}, "dateReceived"));
		Header actions = new Header("Open", "actions");
		actions.setButtons(true);
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
