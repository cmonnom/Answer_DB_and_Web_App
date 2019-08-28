package utsw.bicf.answer.controller.serialization.zingchart;

public class Marker {

	String type; //shape of the marker
	String color;
	Float alpha;
	
	public Marker() {
		super();
	}
	
	public Marker(String type) {
		super();
		this.type = type;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Float getAlpha() {
		return alpha;
	}

	public void setAlpha(Float alpha) {
		this.alpha = alpha;
	}
	
}
