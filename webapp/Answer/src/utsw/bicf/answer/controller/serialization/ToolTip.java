package utsw.bicf.answer.controller.serialization;

public class ToolTip {

	String text;
	
	public ToolTip(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
	
	
}
