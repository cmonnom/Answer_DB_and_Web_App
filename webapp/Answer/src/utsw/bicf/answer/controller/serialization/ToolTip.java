package utsw.bicf.answer.controller.serialization;

public class ToolTip {

	public ToolTip() {
		super();
	}

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
