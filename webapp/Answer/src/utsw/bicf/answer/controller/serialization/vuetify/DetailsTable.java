package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.controller.serialization.vuetify.Header;

/**
 * Wrapper class used by SampleDetailsSummary 
 * to store a title and headers of a table (displayed as a v-flex v-list)
 * @author Guillaume
 *
 */
public class DetailsTable {
	
	String title;
	List<Header> headers;
	
	public DetailsTable(String title, List<Header> headers) {
		super();
		this.title = title;
		this.headers = headers;
	}

	public String getTitle() {
		return title;
	}

	public List<Header> getHeaders() {
		return headers;
	}
	

	
	
}
