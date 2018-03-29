package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Simple class to hold xml fields that just have a single String value and no attributes
 * @author Guillaume
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JacksonXmlText
	String value;

	public ClarityValue() {
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
