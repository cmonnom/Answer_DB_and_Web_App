package utsw.bicf.answer.controller.serialization.vuetify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
	
	String csvContent;
	
	boolean actionable;
	
	public Summary() {
	}
	
	public Summary(List<T> items, String uniqueIdField) {
		this.items = items;
		this.headers = new ArrayList<Header>();
		this.headerOrder = new ArrayList<String>();
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		initializeHeaders();
	}
	
	public Summary(List<T> items, String uniqueIdField, boolean actionable) {
		this.items = items;
		this.headers = new ArrayList<Header>();
		this.headerOrder = new ArrayList<String>();
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		this.actionable = actionable;
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
	
	public String getCsvContent() {
		return csvContent;
	}

	public void setCsvContent(String csvContent) {
		this.csvContent = csvContent;
	}
	
	public void createCSVContent() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder csvContent = new StringBuilder(
				this.getHeaders().stream().filter(header -> header.buttons == null || !header.buttons).map(header -> header.getOneLineText()).collect(Collectors.joining(",")))
				.append("\n");
		List<String> rows = new ArrayList<String>();
		String json = this.createVuetifyObjectJSON();
		if (json != null && !json.equals("")) {
			JsonNode jsonNode = mapper.readTree(json);
			if (jsonNode != null) {
				JsonNode items = jsonNode.get("items");
				for (JsonNode item : items) {
					List<String> row = new ArrayList<String>();
					for (Header header : this.getHeaders()) {
						if (header.buttons == null || !header.buttons) {
							String value = item.get(header.getValue()).asText(); // get the item value matching the header
							// could be a PassableValue
							if ((value == null || value.equals("")) && item.get(header.getValue()) != null
									&& item.get(header.getValue()).get("value") != null) {
								value = item.get(header.getValue()).get("value").asText();
							}
							row.add((value == null || value.equals("null")) ? "" : value.replaceAll(",", ""));
						}
					}
					rows.add(row.stream().collect(Collectors.joining(",")));
				}
				csvContent.append(rows.stream().collect(Collectors.joining("\n")));
			}
		}
		this.setCsvContent(csvContent.toString());
	}

	public boolean isActionable() {
		return actionable;
	}

	public void setActionable(boolean actionable) {
		this.actionable = actionable;
	}
}
