package utsw.bicf.answer.controller.serialization;

public class OrderCaseSearchItemString extends SearchItemString {
	
	String iconAvatar;

	public OrderCaseSearchItemString() {
		super();
	}

	public OrderCaseSearchItemString(String name, String value) {
		super(name, value);
	}

	public String getIconAvatar() {
		return iconAvatar;
	}

	public void setIconAvatar(String iconAvatar) {
		this.iconAvatar = iconAvatar;
	}
}
