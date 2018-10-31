package utsw.bicf.answer.controller.serialization;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.SearchItem;

/**
 * When needing to populate a list for a select drop down.
 * Create the child class with the appropriate constructor populating List<SearchItem> items;
 * For instance:
 * public ProjectOrderItems(List<SearchItem> orders) {
		super();
		this.items = orders.stream()
				.map(order -> new SearchItem(order.getOrderSampleId(), order.getProjectOrderId()))
				.collect(Collectors.toList());
	}
 * @author Guillaume
 *
 */
public abstract class SearchItems{
	
	protected List<SearchItem> items;
	Boolean isAllowed = true;
	
	public SearchItems() {
	}
	
	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<SearchItem> getItems() {
		return items;
	}

	public void setItems(List<SearchItem> items) {
		this.items = items;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}




}

