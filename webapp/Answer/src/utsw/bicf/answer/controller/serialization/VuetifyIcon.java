package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VuetifyIcon {
	
	public VuetifyIcon() {
		super();
	}

	String iconName;
	String color;
	String tooltip;
	boolean chip;
	Integer size = null;
	
	public VuetifyIcon(String iconName, String color, String tooltip) {
		super();
		this.iconName = iconName;
		this.color = color;
		this.tooltip = tooltip;
	}
	
	public VuetifyIcon(String iconName, String color, String tooltip, Integer size) {
		super();
		this.iconName = iconName;
		this.color = color;
		this.tooltip = tooltip;
		this.size = size;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public boolean isChip() {
		return chip;
	}

	public void setChip(boolean chip) {
		this.chip = chip;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}


	
	
}
