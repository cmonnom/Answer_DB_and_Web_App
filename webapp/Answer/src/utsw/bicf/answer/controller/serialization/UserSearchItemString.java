package utsw.bicf.answer.controller.serialization;

public class UserSearchItemString extends SearchItemString {
	
	String value;
	boolean canReview;
	
	public UserSearchItemString() {
	}
	
	public UserSearchItemString(String name, String value, boolean canReview) {
		super();
		this.name = name;
		this.value = value;
		this.canReview = canReview;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(SearchItemString o) {
		return this.name.compareTo(o.name);
	}

	public boolean isCanReview() {
		return canReview;
	}

	public void setCanReview(boolean canReview) {
		this.canReview = canReview;
	}


}
