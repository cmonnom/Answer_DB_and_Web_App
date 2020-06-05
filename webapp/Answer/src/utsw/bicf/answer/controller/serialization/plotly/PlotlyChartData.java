package utsw.bicf.answer.controller.serialization.plotly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlotlyChartData {
	
	Boolean isAllowed = true;
	boolean success = true;
	String plotTitle;
	Boolean hideLegendMarkers = false;
	
	public PlotlyChartData() {
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPlotTitle() {
		return plotTitle;
	}

	public void setPlotTitle(String plotTitle) {
		this.plotTitle = plotTitle;
	}

	public Boolean getHideLegendMarkers() {
		return hideLegendMarkers;
	}

	public void setHideLegendMarkers(Boolean hideLegendMarkers) {
		this.hideLegendMarkers = hideLegendMarkers;
	}


}
