package utsw.bicf.answer.controller.serialization;

public class OrderCaseSearchItemString extends SearchItemString {
	
	String iconAvatar;
	String href;

	public OrderCaseSearchItemString() {
		super();
	}

	public OrderCaseSearchItemString(String name, String value) {
		super(name, value);
	}
	public OrderCaseSearchItemString(String name, String value, String href) {
		super(name, value);
		this.href = href;
	}

	public String getIconAvatar() {
		return iconAvatar;
	}

	public void setIconAvatar(String iconAvatar) {
		this.iconAvatar = iconAvatar;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
