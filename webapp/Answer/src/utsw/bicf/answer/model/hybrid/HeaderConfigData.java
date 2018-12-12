package utsw.bicf.answer.model.hybrid;

import java.util.List;

import utsw.bicf.answer.controller.serialization.vuetify.Header;

public class HeaderConfigData {
	
	List<Header> headers;
	String tableTitle;
	
	public List<Header> getHeaders() {
		return headers;
	}
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	public String getTableTitle() {
		return tableTitle;
	}
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

}
