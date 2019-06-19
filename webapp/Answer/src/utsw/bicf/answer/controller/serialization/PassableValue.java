package utsw.bicf.answer.controller.serialization;

public class PassableValue {
	
	public PassableValue() {
		super();
	}
	String field;
	Object value;
	Boolean pass;
	
	public PassableValue(String field, Object value, Boolean pass) {
		super();
		this.field = field;
		this.value = value;
		this.pass = pass;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Boolean getPass() {
		return pass;
	}
	public void setPass(Boolean pass) {
		this.pass = pass;
	}
	
	
}
