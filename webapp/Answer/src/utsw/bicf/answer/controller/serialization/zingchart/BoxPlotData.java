package utsw.bicf.answer.controller.serialization.zingchart;

import java.util.Collections;
import java.util.List;

public class BoxPlotData {

	Double min;
	Double max;
	double median;
	double q1;
	double q3;
	double iqr;
	double lowerFence;
	double upperFence;

	List<Double> values;

	public BoxPlotData(List<Double> values) {
		super();
		this.values = values;
		if (values == null || values.isEmpty()) {
			return;
		}
		init();
	}

	private void init() {
		Collections.sort(values);
		min = values.get(0);
		max = values.get(values.size() - 1);
		median = calcMedian();
		q1 = calcQ(25);
		q3 = calcQ(75);
		iqr = q3 - q1;
		lowerFence = Math.max(0,Math.round((q1 - 1.5 * iqr) * 100) * 1.0d / 100); 
		upperFence = Math.max(0,Math.round((q3 + 1.5 * iqr) * 100) * 1.0d / 100); 
	}

	private double calcMedian() {
		int middle = values.size() / 2;
		if (values.size() % 2 == 1 || values.size() < 3) {
			return values.get(middle);
		}
		return (values.get(middle - 1) + values.get(middle + 1)) / 2;
	}

	private double calcQ(double lowerPercent) {
		int n = (int) Math.round(values.size() * lowerPercent / 100);
		if (n >= values.size()) {
			return values.get(values.size() - 1);
		}
		return values.get(n);
	}

	public Double getMin() {
		return min;
	}

	public Double getMax() {
		return max;
	}


	public List<Double> getValues() {
		return values;
	}

	public double getMedian() {
		return median;
	}

	public double getQ1() {
		return q1;
	}

	public double getQ3() {
		return q3;
	}

	public double getIqr() {
		return iqr;
	}

	public double getLowerFence() {
		return lowerFence;
	}

	public double getUpperFence() {
		return upperFence;
	}
}
