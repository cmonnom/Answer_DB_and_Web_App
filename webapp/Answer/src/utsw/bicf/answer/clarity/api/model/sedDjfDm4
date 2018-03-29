package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JacksonXmlRootElement(localName = "field", namespace= "udf")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityUDFField implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String type;
	String name;
	@JacksonXmlText
	String value;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(getName()).append(" | Type: ").append(getType())
		.append(" | Value: ").append(getValue());
		return sb.toString();
	}

}
