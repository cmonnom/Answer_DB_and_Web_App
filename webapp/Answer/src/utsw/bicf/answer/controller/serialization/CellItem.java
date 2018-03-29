package utsw.bicf.answer.controller.serialization;

public class CellItem {
	
	String label;
	String value;
	
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

}
