package utsw.bicf.answer.igv;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Resource {

	@JacksonXmlProperty(isAttribute = true)
	String name;
	@JacksonXmlProperty(isAttribute = true)
	String path;
	
	public Resource() {
		super();
	}
	
	public Resource(String name, String path) {
		super();
		this.name = name;
		this.path = path;
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
	
	
}
