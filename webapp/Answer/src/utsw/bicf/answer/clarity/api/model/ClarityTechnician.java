package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "technician")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityTechnician implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uri;
	@JacksonXmlProperty(localName = "first-name")
	ClarityValue firstName;
	@JacksonXmlProperty(localName = "last-name")
	ClarityValue lastName;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ClarityTechnician() {
		super();
	}
	
	@Override
	public String toString() {
		return firstName.getValue() + " " + lastName.getValue();
	}


	public ClarityValue getFirstName() {
		return firstName;
	}
	public void setFistName(ClarityValue firstName) {
		this.firstName = firstName;
	}
	public ClarityValue getLastName() {
		return lastName;
	}
	public void setLastName(ClarityValue lastName) {
		this.lastName = lastName;
	}

}
