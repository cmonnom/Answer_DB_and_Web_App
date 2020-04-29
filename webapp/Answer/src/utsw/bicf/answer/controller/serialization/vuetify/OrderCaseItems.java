package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.serialization.OrderCaseSearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class OrderCaseItems extends SearchItems {
	
	public OrderCaseItems(List<OrderCase> orderCases, User user, String urlEdit, String urlReadOnly) {
		super();
		this.items = orderCases.stream()
				.map(orderCase -> buildSearchItem(orderCase, user, urlEdit, urlReadOnly))
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
				.collect(Collectors.toList());
	}

	private OrderCaseSearchItemString buildSearchItem(OrderCase orderCase, User user, String urlEdit, String urlReadOnly){
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(orderCase, user);
		String href = (isAssigned ? urlEdit : urlReadOnly) + "/" + orderCase.getCaseId();
		OrderCaseSearchItemString item = new OrderCaseSearchItemString(orderCase.getCaseId() + " " + orderCase.getEpicOrderNumber() + "-" + orderCase.getMedicalRecordNumber() + " " + orderCase.getPatientName(), orderCase.getCaseId(), href);
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



