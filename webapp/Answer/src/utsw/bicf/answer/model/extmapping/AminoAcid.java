package utsw.bicf.answer.model.extmapping;

public class AminoAcid {
	
	String fullName;
	String threeLetter;
	String oneLetter;
	
	public AminoAcid(String fullName, String threeLetter, String oneLetter) {
		super();
		this.fullName = fullName;
		this.threeLetter = threeLetter;
		this.oneLetter = oneLetter;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getThreeLetter() {
		return threeLetter;
	}

	public void setThreeLetter(String threeLetter) {
		this.threeLetter = threeLetter;
	}

	public String getOneLetter() {
		return oneLetter;
	}

	public void setOneLetter(String oneLetter) {
		this.oneLetter = oneLetter;
	}
	
	

}
