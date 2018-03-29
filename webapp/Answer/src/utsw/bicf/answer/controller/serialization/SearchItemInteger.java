package utsw.bicf.answer.controller.serialization;

import utsw.bicf.answer.controller.serialization.SearchItem;

public class SearchItemInteger extends SearchItem {
	
	Integer value;
	
	public SearchItemInteger() {
	}
	
	public SearchItemInteger(String name, Integer value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}


}
