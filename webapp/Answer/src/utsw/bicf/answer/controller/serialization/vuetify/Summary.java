package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.vuetify.Header;

/**
 * Any Controller that needs to return the data as a Vuetify.data-table
 * should use this class to format it to a JSON String.
 * Jackson does the formatting automatically when using createVuetifyDataTableJSON
 * which returns a JSON String.
 * Any class extending Summary only needs to implements the constructor with the proper type
 * and initializeHeaders() with the headers to display and in which order.
 * Values in headerOrder should match Header.value
 * @author Guillaume
 *
 * @param <T>
 */
public abstract class Summary<T> {
	
	List<T> items;
	List<Header> headers;
	List<String> headerOrder;
	String uniqueIdField;
	Boolean isAllowed;
	
	public Summary(List<T> items, String uniqueIdField) {
		this.items = items;
		this.headers = new ArrayList<Header>();
		this.headerOrder = new ArrayList<String>();
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		initializeHeaders();
	}
	
	public abstract void initializeHeaders();

	public String createVuetifyDataTableJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public List<String> getHeaderOrder() {
		return headerOrder;
	}

	public void setHeaderOrder(List<String> headerOrder) {
		this.headerOrder = headerOrder;
	}

	public String getUniqueIdField() {
		return uniqueIdField;
	}

	public void setUniqueIdField(String uniqueIdField) {
		this.uniqueIdField = uniqueIdField;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	
	
	
}
