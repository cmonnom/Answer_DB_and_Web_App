package utsw.bicf.answer.model.hybrid;

import utsw.bicf.answer.controller.serialization.vuetify.Header;

public class HeaderOrder {
	
	String value;
	Boolean isHidden;
	String text;
	
	
	
	
	public HeaderOrder() {
	}
	
	public HeaderOrder(Header header) {
		this.value = header.getValue();
		this.isHidden = header.getIsHidden();
		this.text = header.getOneLineText();
	}
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean isHidden() {
		return isHidden;
	}
	public void setHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
