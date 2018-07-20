package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.OrderCaseAssigned;

public class OrderCaseAssignedSummary extends Summary<OrderCaseAssigned>{
//	
	public OrderCaseAssignedSummary(List<OrderCaseAssigned> casesAssigned, User user) {
		super(casesAssigned, "epicOrderNumber");
		
		//only allow to reassign if user can edit
		if (user.getIndividualPermission().getAdmin() 
				|| (user.getIndividualPermission().getCanAssign())) {
			Header actions = new Header("Reassign", "actions");
			actions.setButtons(true);
			headers.add(actions);
		}
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
	
	


	@Override
	public void initializeHeaders() {
		headers.add(new Header(new String[] {"Epic","Order Nb"}, "epicOrderNumber"));
		headers.add(new Header(new String[] {"Epic", "Order Date"}, "epicOrderDate"));
		headers.add(new Header(new String[] {"Date", "Received"}, "dateReceived"));
		headers.add(new Header(new String[] {"Assigned", "To"}, "assignedTo"));
	
	}
}
