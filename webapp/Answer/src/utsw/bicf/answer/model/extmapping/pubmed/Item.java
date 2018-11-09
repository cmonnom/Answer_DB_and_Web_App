package utsw.bicf.answer.model.extmapping.pubmed;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
	
//	@XmlAttribute
//	String name;
//	@XmlAttribute
//	String type;
	String value;
	List<Item> items = new ArrayList<Item>();
	
	
	public Item() {
	}


//	public String getName() {
//		return name;
//	}
//
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//
//	public String getType() {
//		return type;
//	}
//
//
//	public void setType(String type) {
//		this.type = type;
//	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}

	


}
