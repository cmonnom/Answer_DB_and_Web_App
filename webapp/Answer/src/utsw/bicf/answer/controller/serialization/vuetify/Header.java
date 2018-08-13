package utsw.bicf.answer.controller.serialization.vuetify;

import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.controller.serialization.Units;

/**
 * Any Summary object should create its own list of Header
 * using this class. Jackson will use it to output a JSON string
 * @author Guillaume
 *
 */
public class Header {
	
	String value;
	String text;
	//in case of long title, you can break it to have a line return
	String textPart1;
	String textPart2;
//	String unit;
	ToolTip toolTip;
	Units unit;
	Boolean isPassable;
	Boolean isActionable;
	Boolean isFlag;
	String width;
	Integer widthValue;
	Boolean buttons; //to allow buttons instead of text in a cell
	Boolean isHidden;
	String align;
	Boolean sortable = true;
	Boolean isLink;
	
	public Header(String text, String value, Units unit, ToolTip toolTip, Boolean isPassable) {
		this.text = text;
		this.value = value;
		this.unit = unit;
		this.toolTip = toolTip;
		this.isPassable = isPassable;
	}
	
	public Header(String text, String value) {
		super();
		this.value = value;
		this.text = text;
	}
	
	public Header(String text, String value, ToolTip toolTip, Boolean isPassable) {
		super();
		this.value = value;
		this.text = text;
		this.toolTip = toolTip;
		this.isPassable = isPassable;
	}
	
	public Header(String text, String value, Units unit) {
		super();
		this.value = value;
		this.text = text;
		this.unit = unit;
	}
	
	public Header(String[] textParts, String value) {
		super();
		this.value = value;
		if (textParts != null) {
			this.textPart1 = textParts[0];
			if (textParts.length == 2) {
				this.textPart2 = textParts[1];
			}
		}
	}
	
	public Header(String[] textParts, String value, Units unit) {
		super();
		this.value = value;
		if (textParts != null) {
			this.textPart1 = textParts[0];
			if (textParts.length == 2) {
				this.textPart2 = textParts[1];
			}
		}
		this.unit = unit;
	}
	
	public Header(String[] textParts, String value, Units unit, ToolTip toolTip, Boolean isPassable) {
		super();
		this.value = value;
		if (textParts != null) {
			this.textPart1 = textParts[0];
			if (textParts.length == 2) {
				this.textPart2 = textParts[1];
			}
		}
		this.unit = unit;
		this.toolTip = toolTip;
		this.isPassable = isPassable;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTextPart1() {
		return textPart1;
	}

	public void setTextPart1(String textPart1) {
		this.textPart1 = textPart1;
	}

	public String getTextPart2() {
		return textPart2;
	}

	public void setTextPart2(String textPart2) {
		this.textPart2 = textPart2;
	}

	public Units getUnit() {
		return unit;
	}

	public void setUnit(Units unit) {
		this.unit = unit;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ToolTip getToolTip() {
		return toolTip;
	}

	public void setToolTip(ToolTip toolTip) {
		this.toolTip = toolTip;
	}

	public Boolean getIsPassable() {
		return isPassable;
	}

	public void setIsPassable(Boolean isPassable) {
		this.isPassable = isPassable;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public Boolean getIsActionable() {
		return isActionable;
	}

	public void setIsActionable(Boolean isActionable) {
		this.isActionable = isActionable;
	}

	public Boolean getButtons() {
		return buttons;
	}

	public void setButtons(Boolean buttons) {
		this.buttons = buttons;
	}
	
	public String getOneLineText() {
		if (text != null) {
			return text;
		}
		if (textPart2 != null) {
			return textPart1 + " " + textPart2;
		}
		return textPart1;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	public Boolean getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(Boolean isFlag) {
		this.isFlag = isFlag;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public Boolean getSortable() {
		return sortable;
	}

	public void setSortable(Boolean sortable) {
		this.sortable = sortable;
	}

	public Integer getWidthValue() {
		return widthValue;
	}

	public void setWidthValue(Integer widthValue) {
		this.widthValue = widthValue;
	}

	public Boolean getIsLink() {
		return isLink;
	}

	public void setIsLink(Boolean isLink) {
		this.isLink = isLink;
	}

}
