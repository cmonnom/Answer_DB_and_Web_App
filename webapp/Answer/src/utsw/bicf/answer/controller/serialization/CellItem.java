package utsw.bicf.answer.controller.serialization;

public class CellItem {
	
	public static final String TYPE_TEXT = "text";
	
	String label;
	String value;
	String type;
	
	public CellItem() {
	}
	public CellItem(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
