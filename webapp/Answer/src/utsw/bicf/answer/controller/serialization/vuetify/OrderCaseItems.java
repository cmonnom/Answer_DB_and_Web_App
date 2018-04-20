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
				.map(orderCase -> new SearchItemString(orderCase.getCaseId(), orderCase.getCaseId()))
				.collect(Collectors.toList());
	}

}



