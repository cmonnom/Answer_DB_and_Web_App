package utsw.bicf.answer.igv;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Resource {

	@JacksonXmlProperty(isAttribute = true)
	String name;
	@JacksonXmlProperty(isAttribute = true)
	String path;
	@JacksonXmlProperty(isAttribute = true)
	String index;
	
	public Resource() {
		super();
	}
	
	public Resource(String name, String path, String index) {
		super();
		this.name = name;
		this.path = path;
		this.index = index;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	
}
