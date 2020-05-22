package utsw.bicf.answer.controller.serialization.plotly;

import java.util.ArrayList;
import java.util.List;

public class StackedBarPlotData extends PlotlyChartData {
	
	List<Trace> traces = new ArrayList<Trace>();
	String plotId;

	public StackedBarPlotData() {
		super();
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public List<Trace> getTraces() {
		return traces;
	}

	public void setTraces(List<Trace> traces) {
		this.traces = traces;
	}
	

}
