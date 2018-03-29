package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Units {
	
	PCT("%"), NB(""), NANO_GRAM("ng"), NANO_MOL("nM"), BASE_PAIR("bp"),
	NANO_GRAM_BY_MICRO_L("ng/&micro;l");
	
	String value;
	
	
	Units(String value) {
		this.value = value;
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
