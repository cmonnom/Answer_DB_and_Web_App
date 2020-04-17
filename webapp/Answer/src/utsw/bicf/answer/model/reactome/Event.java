package utsw.bicf.answer.model.reactome;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
	
	String stId;
	String name;
	String species;
	String type;
	Boolean diagram;
	String url;
	List<Event> children;
	
	public Event() {
		super();
	}

	public String getStId() {
		return stId;
	}

	public void setStId(String stId) {
		this.stId = stId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getDiagram() {
		return diagram;
	}

	public void setDiagram(Boolean diagram) {
		this.diagram = diagram;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Event> getChildren() {
		return children;
	}

	public void setChildren(List<Event> children) {
		this.children = children;
	}



}
