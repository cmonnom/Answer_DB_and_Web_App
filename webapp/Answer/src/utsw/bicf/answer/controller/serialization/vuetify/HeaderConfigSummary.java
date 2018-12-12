package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class HeaderConfigSummary {
	
	List<HeaderOrder> headerOrders;
	String tableTitle;
	

	public HeaderConfigSummary() {
	}

	public HeaderConfigSummary(List<HeaderOrder> headerOrders, String tableTitle) {
		super();
		this.headerOrders = headerOrders;
		this.tableTitle = tableTitle;
	}

	public List<HeaderOrder> getHeaderOrders() {
		return headerOrders;
	}

	public void setHeaderOrders(List<HeaderOrder> headerOrders) {
		this.headerOrders = headerOrders;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}


}
