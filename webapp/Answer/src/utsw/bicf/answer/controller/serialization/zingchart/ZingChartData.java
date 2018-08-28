package utsw.bicf.answer.controller.serialization.zingchart;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.zingchart.Values;

public class ZingChartData {
	
	List<String> labels;
	List<Values> series;
	Boolean isAllowed = true;
	
	public ZingChartData() {
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<String>  getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public List<Values> getSeries() {
		return series;
	}

	public void setSeries(List<Values> series) {
		this.series = series;
	}


}
