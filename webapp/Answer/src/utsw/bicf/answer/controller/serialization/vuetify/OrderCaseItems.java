package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseItems extends SearchItems {
	
	public OrderCaseItems(List<OrderCase> orderCases) {
		super();
		this.items = orderCases.stream()
				.map(orderCase -> new SearchItemString(orderCase.getCaseId() + " " + orderCase.getEpicOrderNumber() + "-" + orderCase.getMedicalRecordNumber() + " " + orderCase.getPatientName(), orderCase.getCaseId() ))
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
				.collect(Collectors.toList());
	}

}



