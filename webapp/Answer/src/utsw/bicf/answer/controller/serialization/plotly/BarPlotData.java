package utsw.bicf.answer.controller.serialization.plotly;

public class BarPlotData extends PlotlyChartData {
	
	Trace trace;
	String plotId;

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	public BarPlotData() {
		super();
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}
	

}
