package utsw.bicf.answer.controller.serialization.zingchart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;

import utsw.bicf.answer.model.extmapping.FPKMData;
import utsw.bicf.answer.model.extmapping.FPKMPerCaseData;

public class FPKMChartData extends ZingChartData {
	
	String oncotreeCode;
	String boxPlotTooltip;
	Double maxValue;
	
	Values stockSerie;
	Values medianSerie;
	Values scatterSerie;
	Values currentCaseSerie;
	Values outliersSerie;
	
	
	public FPKMChartData(FPKMData fpkmData, String caseId, Boolean showOtherPlots) {
		
		this.oncotreeCode = fpkmData.getOncotreeCode();
		
		//Populate labels
		this.labels = new ArrayList<String>();
		
		//Initialize Series
		this.series = new ArrayList<Values>(); //4 series (stock, scatter, median, current case scatter) per oncotree root code. Only one at a time for now -> 1 box plot
		
		//need all to calculate boxplot
		List<Double> fpkmsAll = fpkmData.getFpkms().stream().map(d -> d.getFpkmValue()).collect(Collectors.toList());
		//need to filter out current case for scatter plot
		List<Double> fpkmsWithoutCurrentCase = fpkmData.getFpkms().stream().filter(d -> !d.getCaseId().equals(caseId)).map(d -> d.getFpkmValue()).collect(Collectors.toList());
		
		//extract the current case FPKM data. Need to make sure it's in the data set 
		List<FPKMPerCaseData> currentCases = fpkmData.getFpkms().stream().filter(d -> d.getCaseId().equals(caseId)).collect(Collectors.toList());
		FPKMPerCaseData currentCase = null;
		if (currentCases != null && !currentCases.isEmpty()) {
			currentCase = currentCases.get(0); 
		}
		
		maxValue = fpkmsAll.stream().max(Double::compare).get() * 1.05;
		
		this.initBoxPlot(fpkmData, fpkmsAll, fpkmsWithoutCurrentCase, caseId, showOtherPlots);
		
		//current Case
		if (currentCase != null) {
			List<String> caseName = new ArrayList<String>();
			caseName.add(currentCase.getCaseId() + " " + currentCase.getCaseName());
			List<List<Double>> currentMarkerCoords = new ArrayList<List<Double>>();
			List<Double> marker = new ArrayList<Double>();
			marker.add(0d);
			marker.add(currentCase.getFpkmValue().doubleValue());
			currentMarkerCoords.add(marker);
			this.currentCaseSerie = new Values(currentMarkerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Current Case", caseName);
		}
		
	}
	
	private void initBoxPlot(FPKMData fpkmData, List<Double> fpkmsAll, List<Double> fpkmsWithoutCurrentCase, String caseId, boolean includeOtherCases) {
		BoxPlotData boxData = new BoxPlotData(fpkmsAll);
		List<Object> stockSerie = new ArrayList<Object>();
		stockSerie.add(boxData.getQ1());
		stockSerie.add(boxData.getUpperFence());
		stockSerie.add(boxData.getLowerFence());
		stockSerie.add(boxData.getQ3());
		this.stockSerie = new Values(stockSerie, "Box Plot", null);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Upper: ").append(boxData.getUpperFence()).append("<br/>");
		sb.append("Q3: ").append(boxData.getQ3()).append("<br/>");
		sb.append("Median: ").append(boxData.getMedian()).append("<br/>");
		sb.append("Q1: ").append(boxData.getQ1()).append("<br/>");
		sb.append("Lower: ").append(boxData.getLowerFence());
		this.boxPlotTooltip = sb.toString();
		
		if (includeOtherCases) {
			//scatter plot. Skip current case
			List<String> caseNames = new ArrayList<String>();
			List<Double> fkpmOthers = new ArrayList<Double>();
			for (FPKMPerCaseData d : fpkmData.getFpkms()) {
				if (!d.getCaseId().equals(caseId) 
					&& d.getFpkmValue() <= boxData.getUpperFence()
					&& d.getFpkmValue() >= boxData.getLowerFence()) {
					caseNames.add(d.getCaseId() + " " + d.getCaseName());
					fkpmOthers.add(d.getFpkmValue());
				}
			}
			List<List<Double>> markerCoords = new ArrayList<List<Double>>();
			for (Double fpkm : fkpmOthers) {
				List<Double> marker = this.markerCoordsWithJitter(fpkm);
				markerCoords.add(marker);
			}
			this.scatterSerie = new Values(markerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "All Cases", caseNames);
		}
		
		//outliers
		//scatter plot. Skip current case
		List<String> outliersCaseNames = new ArrayList<String>();
		List<Double> outliersFkpmOthers = new ArrayList<Double>();
		for (FPKMPerCaseData d : fpkmData.getFpkms()) {
			if (!d.getCaseId().equals(caseId) 
				&& (d.getFpkmValue() > boxData.getUpperFence()
				|| d.getFpkmValue() < boxData.getLowerFence())) {
				outliersCaseNames.add(d.getCaseId() + " " + d.getCaseName());
				outliersFkpmOthers.add(d.getFpkmValue());
			}
		}
		List<List<Double>> outliersMarkerCoords = new ArrayList<List<Double>>();
		for (Double fpkm : outliersFkpmOthers) {
			List<Double> marker = new ArrayList<Double>();
			marker.add(0d);
			marker.add(fpkm.doubleValue());
			outliersMarkerCoords.add(marker);
		}
		this.outliersSerie = new Values(outliersMarkerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Outliers", outliersCaseNames);
		
		//median line
		List<List<Double>> lineCoords = new ArrayList<List<Double>>();
		List<Double> left = Arrays.asList(-0.4, boxData.getMedian());
		List<Double> right = Arrays.asList(0.4, boxData.getMedian());
		lineCoords.add(left);
		lineCoords.add(right);
		this.medianSerie = new Values(lineCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Median", null);
		
		
		
	}
	
	/**
	 * Create a marker with random jitter
	 * for scatter plots
	 * @param fpkmValue
	 * @return
	 */
	private List<Double> markerCoordsWithJitter(Double fpkmValue) {
		List<Double> marker = new ArrayList<Double>();
		double jitter = RandomUtils.nextDouble(0, 0.45);
		boolean negative = RandomUtils.nextBoolean();
		if (negative) {
			jitter = -jitter;
		}
		marker.add(jitter); //introduce jitter
		marker.add(fpkmValue.doubleValue());
		return marker;
	}

	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
	}

	public String getBoxPlotTooltip() {
		return boxPlotTooltip;
	}

	public void setBoxPlotTooltip(String boxPlotTooltip) {
		this.boxPlotTooltip = boxPlotTooltip;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Values getStockSerie() {
		return stockSerie;
	}

	public void setStockSerie(Values stockSerie) {
		this.stockSerie = stockSerie;
	}

	public Values getMedianSerie() {
		return medianSerie;
	}

	public void setMedianSerie(Values medianSerie) {
		this.medianSerie = medianSerie;
	}

	public Values getScatterSerie() {
		return scatterSerie;
	}

	public void setScatterSerie(Values scatterSerie) {
		this.scatterSerie = scatterSerie;
	}

	public Values getCurrentCaseSerie() {
		return currentCaseSerie;
	}

	public void setCurrentCaseSerie(Values currentCaseSerie) {
		this.currentCaseSerie = currentCaseSerie;
	}

	public Values getOutliersSerie() {
		return outliersSerie;
	}

	public void setOutliersSerie(Values outliersSerie) {
		this.outliersSerie = outliersSerie;
	}

}
