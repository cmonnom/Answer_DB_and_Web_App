package utsw.bicf.answer.controller.serialization.vuetify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.HeaderConfig;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

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
	
	ModelDAO modelDAO; //can be null if not used
	
	List<T> items;
	List<Header> headers;
	List<String> headerOrder;
	String uniqueIdField;
	Boolean isAllowed;
	
	String csvContent;
	
	boolean actionable;
	
	List<HeaderOrder> headerOrdersPOJO;
	
	public Summary() {
	}
	
	public Summary(List<T> items, String uniqueIdField, List<HeaderOrder> headerOrders) {
		this.items = items;
		this.headers = new ArrayList<Header>();
		if (headerOrders != null) {
			this.headerOrder = headerOrders.stream().map(h -> h.getValue()).collect(Collectors.toList());
			this.headerOrdersPOJO = headerOrders;
		}
		else {
			this.headerOrder = new ArrayList<String>();
		}
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		initializeHeaders();
		this.updateHeaderOrder();
		this.setHiddenStatus();
	}
	
	public Summary(List<T> items, String uniqueIdField, boolean actionable, List<HeaderOrder> headerOrders) {
		this.items = items;
		this.headers = new ArrayList<Header>();
		if (headerOrders != null) {
			this.headerOrder = headerOrders.stream().map(h -> h.getValue()).collect(Collectors.toList());
			this.headerOrdersPOJO = headerOrders;
		}
		else {
			this.headerOrder = new ArrayList<String>();
		}
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		this.actionable = actionable;
		initializeHeaders();
		this.updateHeaderOrder();
		this.setHiddenStatus();
	}
	
	public Summary(List<T> items, String uniqueIdField, List<HeaderOrder> headerOrders,
			ModelDAO modelDAO) {
		this.modelDAO = modelDAO;
		this.items = items;
		this.headers = new ArrayList<Header>();
		if (headerOrders != null) {
			this.headerOrder = headerOrders.stream().map(h -> h.getValue()).collect(Collectors.toList());
			this.headerOrdersPOJO = headerOrders;
		}
		else {
			this.headerOrder = new ArrayList<String>();
		}
		this.uniqueIdField = uniqueIdField;
		this.isAllowed = true;
		initializeHeaders();
		this.updateHeaderOrder();
		this.setHiddenStatus();
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

	public void setHiddenStatus() {
		if (headerOrdersPOJO == null) {
			return;
		}
		for (Header header : headers) {
			for (HeaderOrder order : headerOrdersPOJO) {
				if (header.getValue().equals(order.getValue())) {
					header.setIsHidden(order.isHidden());
					break;
				}
			}
		}
	}
	
	public void updateHeaderOrder() {
		//don't override the order if it exists as a preference
		if (headerOrdersPOJO == null || headerOrdersPOJO.size() != headers.size()) {
			//keep in the same order
			headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		}
	}
	
	public static List<HeaderOrder> getHeaderOrdersForUserAndTable(ModelDAO modelDAO, User user, String title) throws JsonParseException, JsonMappingException, IOException {
		List<HeaderConfig> existingConfigs = modelDAO.getHeaderConfigForUserAndTable(user.getUserId(), title);
		HeaderConfig uniqueConfigForTable = null;
		List<HeaderOrder> headerOrders = new ArrayList<HeaderOrder>();
		if (existingConfigs != null && !existingConfigs.isEmpty()) {
			uniqueConfigForTable = existingConfigs.get(0);
			ObjectMapper mapper = new ObjectMapper();
			HeaderOrder[] headerOrdersArray = mapper.readValue(uniqueConfigForTable.getHeaderOrder(), HeaderOrder[].class);
			for (HeaderOrder h : headerOrdersArray) {
				headerOrders.add(h);
			}
		}
		return headerOrders;
	}
}
