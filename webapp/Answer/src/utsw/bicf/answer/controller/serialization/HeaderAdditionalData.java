package utsw.bicf.answer.controller.serialization;

public class HeaderAdditionalData {
	ToolTip tooltip;
	public HeaderAdditionalData() {
		super();
	}
	Integer userId;
	String firstName;
	String lastName;
	public ToolTip getTooltip() {
		return tooltip;
	}
	public void setTooltip(ToolTip tooltip) {
		this.tooltip = tooltip;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
