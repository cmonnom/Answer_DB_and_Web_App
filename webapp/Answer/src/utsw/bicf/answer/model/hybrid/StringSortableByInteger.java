package utsw.bicf.answer.model.hybrid;

public class StringSortableByInteger implements Comparable<StringSortableByInteger> {
	
	String stringValue;
	Integer intValue;
	boolean descending = false;
	public StringSortableByInteger(String stringValue, Integer intValue, boolean descending) {
		super();
		this.stringValue = stringValue;
		this.intValue = intValue;
		this.descending = descending;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Integer getIntValue() {
		return intValue;
	}
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	@Override
	public int compareTo(StringSortableByInteger o) {
		if (descending) {
			return o.intValue.compareTo(this.intValue);
		}
		return this.intValue.compareTo(o.intValue);
	}

}
