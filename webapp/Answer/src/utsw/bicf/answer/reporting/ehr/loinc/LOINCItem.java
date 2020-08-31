package utsw.bicf.answer.reporting.ehr.loinc;

public class LOINCItem {
	public String id;
	public String text;
	public String system = "LN";
	public String textPart1;
	public String textPart2;
	
	public LOINCItem(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}
	
	public LOINCItem(String id, String textPart1, String textPart2, String system) {
		super();
		this.id = id;
		this.system = system;
		this.textPart1 = textPart1;
		this.textPart2 = textPart2;
	}

	public LOINCItem(String id, String text, String system) {
		super();
		this.id = id;
		this.text = text;
		this.system = system;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSystem() {
		return system;
	}

	public String getTextPart1() {
		return textPart1;
	}

	public String getTextPart2() {
		return textPart2;
	}

}