package utsw.bicf.answer.controller.serialization.plotly;

import java.util.List;

public class LollipopPlotData extends PlotlyChartData {
	
	Trace trace;
	List<Trace> underlineTraces;
	List<String> annotations;
	Number maxY;
	
	String plotId;
	public Trace getTrace() {
		return trace;
	}
	public void setTrace(Trace trace) {
		this.trace = trace;
	}
	public String getPlotId() {
		return plotId;
	}
	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}
	public List<Trace> getUnderlineTraces() {
		return underlineTraces;
	}
	public void setUnderlineTraces(List<Trace> underlineTraces) {
		this.underlineTraces = underlineTraces;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	public Number getMaxY() {
		return maxY;
	}
	public void setMaxY(Number maxY) {
		this.maxY = maxY;
	}
	

}
