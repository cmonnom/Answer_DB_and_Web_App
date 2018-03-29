package utsw.bicf.answer.controller.serialization;

import utsw.bicf.answer.controller.serialization.SearchItem;

public class SearchItemString extends SearchItem implements Comparable<SearchItemString> {
	
	String value;
	
	public SearchItemString() {
	}
	
	public SearchItemString(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(SearchItemString o) {
		return this.name.compareTo(o.name);
	}


}
