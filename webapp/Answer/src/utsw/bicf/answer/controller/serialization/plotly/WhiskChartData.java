package utsw.bicf.answer.controller.serialization.plotly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.WhiskerData;
import utsw.bicf.answer.model.extmapping.WhiskerPerCaseData;

public class WhiskChartData extends PlotlyChartData {
	
	String label;
	
	Double currentCaseData;
	String currentCaseLabel;
	List<Double> boxData;
	List<Double> outliersData;
	List<String> outliersLabels;
	Double min = 1000000d, max = -1000000d;
	
	List<Double> fpkms = new ArrayList<Double>();
	
	long nbOfCases;
	
	
	public WhiskChartData(WhiskerData whiskData, String caseId, Boolean useLog2) {
		
		this.fpkms = whiskData.getPerCaseList().stream().map(i -> i.getWhiskValue()).collect(Collectors.toList());
		this.label = whiskData.getLabel();
		
		
		
		//Populate labels
		this.outliersLabels = new ArrayList<String>();
		
		//Initialize Series
		this.outliersData = new ArrayList<Double>(); //4 series (stock, scatter, median, current case scatter) per oncotree root code. Only one at a time for now -> 1 box plot
		
		//need all to calculate boxplot
		List<Double> fpkmsAll = whiskData.getPerCaseList().stream().map(d -> d.getWhiskValue()).collect(Collectors.toList());
		
		//extract the current case FPKM data. Need to make sure it's in the data set 
		List<WhiskerPerCaseData> currentCases = whiskData.getPerCaseList().stream().filter(d -> d.getCaseId().equals(caseId)).collect(Collectors.toList());
		this.nbOfCases = whiskData.getPerCaseList().stream().map(d -> d.getCaseId()).collect(Collectors.toSet()).size();
		WhiskerPerCaseData currentCase = null;
		if (currentCases != null && !currentCases.isEmpty()) {
			currentCase = currentCases.get(0); 
		}
		
		this.initBoxPlot(whiskData, fpkmsAll, caseId, useLog2);
		
		//current Case
		if (currentCase != null) {
			this.currentCaseData = currentCase.getWhiskValue().doubleValue();
			this.currentCaseLabel = currentCase.getCaseId() + " " + currentCase.getCaseName();
		}
		
		if (useLog2) {
			this.outliersData = outliersData.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
			this.currentCaseData = convertToLog2(this.currentCaseData);
		}
		if (this.currentCaseData != null) {
			this.min = Math.min(this.min, this.currentCaseData);
			this.max = Math.max(this.max, this.currentCaseData);
		}
		this.min = 1.05d * this.min;
		this.max = 1.05d * this.max;
	}
	
	private Double convertToLog2(Double value) {
		if (value == null) {
			return null;
		}
		if (value == 0d) {
			value = 0.01d;
		}
		value = Math.log(value) / Math.log(2);
		value = Math.round(value * 100)  * 1.0d / 100d; 
		return value;
	}
	
	private void initBoxPlot(WhiskerData fpkmData, List<Double> fpkmsAll, String caseId, Boolean useLog2) {
		BoxPlotData boxPlotData = new BoxPlotData(fpkmsAll);
		this.boxData = new ArrayList<Double>();
		
		if (useLog2) {
			this.boxData.add(this.convertToLog2(boxPlotData.getLowerFence()));
			this.boxData.add(this.convertToLog2(boxPlotData.getQ1()));
			this.boxData.add(this.convertToLog2(boxPlotData.getQ1()));
			this.boxData.add(this.convertToLog2(boxPlotData.getMedian()));
			this.boxData.add(this.convertToLog2(boxPlotData.getQ3()));
			this.boxData.add(this.convertToLog2(boxPlotData.getQ3()));
			this.boxData.add(this.convertToLog2(boxPlotData.getUpperFence()));
		}
		else{
			this.boxData.add(boxPlotData.getLowerFence());
			this.boxData.add(boxPlotData.getQ1());
			this.boxData.add(boxPlotData.getQ1());
			this.boxData.add(boxPlotData.getMedian());
			this.boxData.add(boxPlotData.getQ3());
			this.boxData.add(boxPlotData.getQ3());
			this.boxData.add(boxPlotData.getUpperFence());
		}
		//outliers
		//scatter plot. Skip current case
		this.outliersLabels = new ArrayList<String>();
		this.outliersData = new ArrayList<Double>();
		for (WhiskerPerCaseData d : fpkmData.getPerCaseList()) {
			if (!d.getCaseId().equals(caseId) 
				&& (d.getWhiskValue() > boxPlotData.getUpperFence()
				|| d.getWhiskValue() < boxPlotData.getLowerFence())
				) {
				this.outliersLabels.add(d.getCaseId() + " " + d.getCaseName());
				this.outliersData.add(d.getWhiskValue());
				if (this.min == null) {
					this.min = useLog2 ? this.convertToLog2(d.getWhiskValue()) : d.getWhiskValue();
				}
				else {
					this.min = Math.min(this.max, useLog2 ? this.convertToLog2(d.getWhiskValue()) : d.getWhiskValue());
				}
				if (this.max == null) {
					this.max = useLog2 ? this.convertToLog2(d.getWhiskValue()) : d.getWhiskValue();
				}
				else {
					this.max = Math.max(this.max, useLog2 ? this.convertToLog2(d.getWhiskValue()) : d.getWhiskValue());
				}
			}
		}
	}
	

	public List<Double> getFpkms() {
		return fpkms;
	}

	public void setFpkms(List<Double> fpkms) {
		this.fpkms = fpkms;
	}

	public long getNbOfCases() {
		return nbOfCases;
	}

	public void setNbOfCases(long nbOfCases) {
		this.nbOfCases = nbOfCases;
	}

	public Double getCurrentCaseData() {
		return currentCaseData;
	}

	public void setCurrentCaseData(Double currentCaseData) {
		this.currentCaseData = currentCaseData;
	}

	public String getCurrentCaseLabel() {
		return currentCaseLabel;
	}

	public void setCurrentCaseLabel(String currentCaseLabel) {
		this.currentCaseLabel = currentCaseLabel;
	}

	public List<Double> getBoxData() {
		return boxData;
	}

	public void setBoxData(List<Double> boxData) {
		this.boxData = boxData;
	}

	public List<Double> getOutliersData() {
		return outliersData;
	}

	public void setOutliersData(List<Double> outliersData) {
		this.outliersData = outliersData;
	}

	public List<String> getOutliersLabels() {
		return outliersLabels;
	}

	public void setOutliersLabels(List<String> outliersLabels) {
		this.outliersLabels = outliersLabels;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
