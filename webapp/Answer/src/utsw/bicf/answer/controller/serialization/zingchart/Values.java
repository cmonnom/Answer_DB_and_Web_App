package utsw.bicf.answer.controller.serialization.zingchart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Values {

	List<Object> values;
	String text;
	@JsonProperty("data-labels")
	List<String> dataLabels;
	String type;
	String color;
	Float alpha;
	Marker marker;
	
	public Values() {
		super();
	}
	
	public Values(List<Object> values, String text, List<String> dataLabels) {
		super();
		this.values = values;
		this.text = text;
		this.dataLabels = dataLabels;
	}
	
	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getDataLabels() {
		return dataLabels;
	}

	public void setDataLabels(List<String> dataLabels) {
		this.dataLabels = dataLabels;
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

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}




	
	
}
