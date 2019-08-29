package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.OrderCaseSearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseItems extends SearchItems {
	
	public OrderCaseItems(List<OrderCase> orderCases) {
		super();
		this.items = orderCases.stream()
				.map(orderCase -> buildSearchItem(orderCase))
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
				.collect(Collectors.toList());
	}

	private OrderCaseSearchItemString buildSearchItem(OrderCase orderCase) {
		OrderCaseSearchItemString item = new OrderCaseSearchItemString(orderCase.getCaseId() + " " + orderCase.getEpicOrderNumber() + "-" + orderCase.getMedicalRecordNumber() + " " + orderCase.getPatientName(), orderCase.getCaseId());
		String iconAvatar = null;
		if (OrderCase.TYPE_CLINICAL.equals(orderCase.getType())) {
			iconAvatar = "fa-user-md";
		}
		else if (OrderCase.TYPE_RESEARCH.equals(orderCase.getType())) {
			iconAvatar = "fa-flask";
		}
		else if (OrderCase.TYPE_CLINICAL_RESEARCH.equals(orderCase.getType())) {
			iconAvatar = "fa-flask";
		}
		item.setIconAvatar(iconAvatar);
		return item;
	}
	
}



