package utsw.bicf.answer.controller.serialization;

public class VuetifyIcon {
	
	String iconName;
	String color;
	String tooltip;
	
	public VuetifyIcon(String iconName, String color, String tooltip) {
		super();
		this.iconName = iconName;
		this.color = color;
		this.tooltip = tooltip;
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
	
	
}
