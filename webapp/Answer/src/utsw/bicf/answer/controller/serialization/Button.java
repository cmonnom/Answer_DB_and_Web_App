package utsw.bicf.answer.controller.serialization;

/**
 * To add buttons inside a dataTable cell.
 * 
 * @author Guillaume
 *
 */
public class Button {
	String icon; //name of the icon according to Vuetify specs
	String action; //name of the javascript function to call when button is clicked
	String tooltip;
	String color; //name of the color (could be a Vuetify color like warning or info)
	boolean isLink;
	String href;
	
	public Button() {
		
	}
	
	public Button(String icon, String action, String tooltip, String color) {
		super();
		this.icon = icon;
		this.action = action;
		this.tooltip = tooltip;
		this.color = color;
	}
	public Button(String icon, String href, String tooltip, String color, boolean isLink) {
		super();
		this.icon = icon;
		this.href = href;
		this.tooltip = tooltip;
		this.color = color;
		this.isLink = isLink;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public boolean isLink() {
		return isLink;
	}

	public void setLink(boolean isLink) {
		this.isLink = isLink;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
