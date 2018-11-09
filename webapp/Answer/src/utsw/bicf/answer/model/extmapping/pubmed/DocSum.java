package utsw.bicf.answer.model.extmapping.pubmed;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocSum {
	
	@JsonProperty("Item")
	List<Item> items = new ArrayList<Item>();
	@JsonProperty("Id")
	String id;
	
	public DocSum() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}








	
}
