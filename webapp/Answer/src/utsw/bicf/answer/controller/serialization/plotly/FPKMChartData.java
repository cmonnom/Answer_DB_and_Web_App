package utsw.bicf.answer.controller.serialization.plotly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.FPKMData;
import utsw.bicf.answer.model.extmapping.FPKMPerCaseData;

public class FPKMChartData extends PlotlyChartData {
	
	String oncotreeCode;
	
	Double currentCaseData;
	String currentCaseLabel;
	List<Double> boxData;
	List<Double> outliersData;
	List<String> outliersLabels;
	Double min = 1000000d, max = -1000000d;
	
	List<Double> fpkms = new ArrayList<Double>();
	
	long nbOfCases;
	
	
	public FPKMChartData(FPKMData fpkmData, String caseId, Boolean useLog2) {
		
		this.fpkms = fpkmData.getFpkms().stream().map(i -> i.getFpkmValue()).collect(Collectors.toList());
		this.oncotreeCode = fpkmData.getOncotreeCode();
		
		
		
		//Populate labels
		this.outliersLabels = new ArrayList<String>();
		
		//Initialize Series
		this.outliersData = new ArrayList<Double>(); //4 series (stock, scatter, median, current case scatter) per oncotree root code. Only one at a time for now -> 1 box plot
		
		//need all to calculate boxplot
		List<Double> fpkmsAll = fpkmData.getFpkms().stream().map(d -> d.getFpkmValue()).collect(Collectors.toList());
		
		//extract the current case FPKM data. Need to make sure it's in the data set 
		List<FPKMPerCaseData> currentCases = fpkmData.getFpkms().stream().filter(d -> d.getCaseId().equals(caseId)).collect(Collectors.toList());
		this.nbOfCases = fpkmData.getFpkms().stream().map(d -> d.getCaseId()).collect(Collectors.toSet()).size();
		FPKMPerCaseData currentCase = null;
		if (currentCases != null && !currentCases.isEmpty()) {
			currentCase = currentCases.get(0); 
		}
		
		this.initBoxPlot(fpkmData, fpkmsAll, caseId, useLog2);
		
		//current Case
		if (currentCase != null) {
			this.currentCaseData = currentCase.getFpkmValue().doubleValue();
			this.currentCaseLabel = currentCase.getCaseId() + " " + currentCase.getCaseName();
		}
		
		if (useLog2) {
			this.outliersData = outliersData.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
			this.currentCaseData = convertToLog2(this.currentCaseData);
		}
		this.min = Math.min(this.min, this.currentCaseData);
		this.min = 1.05d * this.min;
		this.max = Math.max(this.max, this.currentCaseData);
		this.max = 1.05d * this.max;
	}
	
	private Double convertToLog2(Double value) {
		if (value == 0d) {
			value = 0.01d;
		}
		value = Math.log(value) / Math.log(2);
		value = Math.round(value * 100)  * 1.0d / 100d; 
		return value;
	}
	
	private void initBoxPlot(FPKMData fpkmData, List<Double> fpkmsAll, String caseId, Boolean useLog2) {
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
		for (FPKMPerCaseData d : fpkmData.getFpkms()) {
			if (!d.getCaseId().equals(caseId) 
				&& (d.getFpkmValue() > boxPlotData.getUpperFence()
				|| d.getFpkmValue() < boxPlotData.getLowerFence())
				) {
				this.outliersLabels.add(d.getCaseId() + " " + d.getCaseName());
				this.outliersData.add(d.getFpkmValue());
				if (this.min == null) {
					this.min = useLog2 ? this.convertToLog2(d.getFpkmValue()) : d.getFpkmValue();
				}
				else {
					this.min = Math.min(this.max, useLog2 ? this.convertToLog2(d.getFpkmValue()) : d.getFpkmValue());
				}
				if (this.max == null) {
					this.max = useLog2 ? this.convertToLog2(d.getFpkmValue()) : d.getFpkmValue();
				}
				else {
					this.max = Math.max(this.max, useLog2 ? this.convertToLog2(d.getFpkmValue()) : d.getFpkmValue());
				}
			}
		}
	}
	
	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
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

}
