//package utsw.bicf.answer.controller.serialization.zingchart;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.apache.commons.lang3.RandomUtils;
//
//import utsw.bicf.answer.model.extmapping.FPKMData;
//import utsw.bicf.answer.model.extmapping.FPKMPerCaseData;
//
//public class FPKMChartData extends ZingChartData {
//	
//	String oncotreeCode;
//	String boxPlotTooltip;
//	Double maxValue;
//	
//	Values stockSerie;
//	Values medianSerie;
//	Values scatterSerie;
//	Values currentCaseSerie;
//	Values outliersSerie;
//	
//	List<Double> fpkms = new ArrayList<Double>();
//	
//	int nbOfCases;
//	
//	
//	public FPKMChartData(FPKMData fpkmData, String caseId, Boolean showOtherPlots, Boolean useLog2) {
//		
//		this.fpkms = fpkmData.getFpkms().stream().map(i -> i.getFpkmValue()).collect(Collectors.toList());
//		this.oncotreeCode = fpkmData.getOncotreeCode();
//		
//		
//		
//		//Populate labels
//		this.labels = new ArrayList<String>();
//		
//		//Initialize Series
//		this.series = new ArrayList<Values>(); //4 series (stock, scatter, median, current case scatter) per oncotree root code. Only one at a time for now -> 1 box plot
//		
//		//need all to calculate boxplot
//		List<Double> fpkmsAll = fpkmData.getFpkms().stream().map(d -> d.getFpkmValue()).collect(Collectors.toList());
//		//need to filter out current case for scatter plot
//		List<Double> fpkmsWithoutCurrentCase = fpkmData.getFpkms().stream().filter(d -> !d.getCaseId().equals(caseId)).map(d -> d.getFpkmValue()).collect(Collectors.toList());
//		
//		//extract the current case FPKM data. Need to make sure it's in the data set 
//		List<FPKMPerCaseData> currentCases = fpkmData.getFpkms().stream().filter(d -> d.getCaseId().equals(caseId)).collect(Collectors.toList());
//		this.nbOfCases = currentCases.size();
//		FPKMPerCaseData currentCase = null;
//		if (currentCases != null && !currentCases.isEmpty()) {
//			currentCase = currentCases.get(0); 
//		}
//		
//		maxValue = fpkmsAll.stream().max(Double::compare).get() * 1.05;
//		
//		this.initBoxPlot(fpkmData, fpkmsAll, fpkmsWithoutCurrentCase, caseId, showOtherPlots, useLog2);
//		
//		//current Case
//		if (currentCase != null) {
//			List<String> caseName = new ArrayList<String>();
//			caseName.add(currentCase.getCaseId() + " " + currentCase.getCaseName());
//			List<List<Double>> currentMarkerCoords = new ArrayList<List<Double>>();
//			List<Double> marker = new ArrayList<Double>();
//			marker.add(0d);
//			marker.add(currentCase.getFpkmValue().doubleValue());
//			currentMarkerCoords.add(marker);
//			this.currentCaseSerie = new Values(currentMarkerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Current Case", caseName);
//		}
//		
//		
////		for (FPKMPerCaseData fpkm : fpkmData.getFpkms()) {
////			Double fpkmValue = fpkm.getFpkmValue();
////			if (useLog2) {
////				if (fpkmValue == 0d) {
////					fpkmValue = 0.01d;
////				}
////				fpkmValue = Math.log(fpkmValue) / Math.log(2);
////			}
////			fpkmValue = Math.round(fpkmValue * 100)  * 1.0d / 100d;
////			fpkm.setFpkmValue(fpkmValue);
////		}
//		
//		if (useLog2) {
//			this.stockSerie.values = this.stockSerie.values.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
//			this.medianSerie.values = this.medianSerie.values.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
//			if (this.scatterSerie != null) {
//				this.scatterSerie.values = this.scatterSerie.values.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
//			}
//			this.currentCaseSerie.values = this.currentCaseSerie.values.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
//			this.outliersSerie.values = this.outliersSerie.values.stream().map(v -> convertToLog2(v)).collect(Collectors.toList());
//			this.maxValue = (Double) this.convertToLog2(this.maxValue);
//		}
//	
//	}
//	
//	@SuppressWarnings("unchecked")
//	private Object convertToLog2(Object value) {
//		Double valueDouble = null;
//		if (value instanceof Double) {
//			valueDouble = (Double) value;
//		}
//		else {
//			valueDouble = ((List<Double>) value).get(1);
//		}
//		if (valueDouble == 0d) {
//			valueDouble = 0.01d;
//		}
//		valueDouble = Math.log(valueDouble) / Math.log(2);
//		valueDouble = Math.round(valueDouble * 100)  * 1.0d / 100d; 
//		
//		if (value instanceof Double) {
//			return valueDouble;
//		}
//		else {
//			List<Double> array = (List<Double>) value;
//			array.set(1, valueDouble);
//			return array;
//		}
//	}
//	
//	private void initBoxPlot(FPKMData fpkmData, List<Double> fpkmsAll, List<Double> fpkmsWithoutCurrentCase, String caseId, boolean includeOtherCases,
//			Boolean useLog2) {
//		BoxPlotData boxData = new BoxPlotData(fpkmsAll);
//		List<Object> stockSerie = new ArrayList<Object>();
//		stockSerie.add(boxData.getQ1());
//		stockSerie.add(boxData.getUpperFence());
//		stockSerie.add(boxData.getLowerFence());
//		stockSerie.add(boxData.getQ3());
//		this.stockSerie = new Values(stockSerie, "Box Plot", null);
//		
//		StringBuilder sb = new StringBuilder();
//		if (useLog2) {
//			sb.append("Upper: ").append(this.convertToLog2(boxData.getUpperFence())).append("<br/>");
//			sb.append("Q3: ").append(this.convertToLog2(boxData.getQ3())).append("<br/>");
//			sb.append("Median: ").append(this.convertToLog2(boxData.getMedian())).append("<br/>");
//			sb.append("Q1: ").append(this.convertToLog2(boxData.getQ1())).append("<br/>");
//			sb.append("Lower: ").append(this.convertToLog2(boxData.getLowerFence()));
//		}
//		else{
//			sb.append("Upper: ").append(boxData.getUpperFence()).append("<br/>");
//			sb.append("Q3: ").append(boxData.getQ3()).append("<br/>");
//			sb.append("Median: ").append(boxData.getMedian()).append("<br/>");
//			sb.append("Q1: ").append(boxData.getQ1()).append("<br/>");
//			sb.append("Lower: ").append(boxData.getLowerFence());
//		}
//		
//		this.boxPlotTooltip = sb.toString();
//		
//		if (includeOtherCases) {
//			//scatter plot. Skip current case
//			List<String> caseNames = new ArrayList<String>();
//			List<Double> fkpmOthers = new ArrayList<Double>();
//			for (FPKMPerCaseData d : fpkmData.getFpkms()) {
//				if (!d.getCaseId().equals(caseId) 
//					&& d.getFpkmValue() <= boxData.getUpperFence()
//					&& d.getFpkmValue() >= boxData.getLowerFence()) {
//					caseNames.add(d.getCaseId() + " " + d.getCaseName());
//					fkpmOthers.add(d.getFpkmValue());
//				}
//			}
//			List<List<Double>> markerCoords = new ArrayList<List<Double>>();
//			for (Double fpkm : fkpmOthers) {
//				List<Double> marker = this.markerCoordsWithJitter(fpkm);
//				markerCoords.add(marker);
//			}
//			this.scatterSerie = new Values(markerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "All Cases", caseNames);
//		}
//		
//		//outliers
//		//scatter plot. Skip current case
//		List<String> outliersCaseNames = new ArrayList<String>();
//		List<Double> outliersFkpmOthers = new ArrayList<Double>();
//		for (FPKMPerCaseData d : fpkmData.getFpkms()) {
//			if (!d.getCaseId().equals(caseId) 
//				&& (d.getFpkmValue() > boxData.getUpperFence()
//				|| d.getFpkmValue() < boxData.getLowerFence())
//				) {
//				outliersCaseNames.add(d.getCaseId() + " " + d.getCaseName());
//				outliersFkpmOthers.add(d.getFpkmValue());
//			}
//		}
//		List<List<Double>> outliersMarkerCoords = new ArrayList<List<Double>>();
//		for (Double fpkm : outliersFkpmOthers) {
//			List<Double> marker = new ArrayList<Double>();
//			marker.add(0d);
//			marker.add(fpkm.doubleValue());
//			outliersMarkerCoords.add(marker);
//		}
//		this.outliersSerie = new Values(outliersMarkerCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Outliers", outliersCaseNames);
//		
//		//median line
//		List<List<Double>> lineCoords = new ArrayList<List<Double>>();
//		List<Double> left = Arrays.asList(-0.4, boxData.getMedian());
//		List<Double> right = Arrays.asList(0.4, boxData.getMedian());
//		lineCoords.add(left);
//		lineCoords.add(right);
//		this.medianSerie = new Values(lineCoords.stream().map(Object.class::cast).collect(Collectors.toList()), "Median", null);
//		
//		
//		if (boxData.getUpperFence() >= this.maxValue) {
//			this.maxValue = boxData.getUpperFence() * 1.05;
//		}
//	}
//	
//	/**
//	 * Create a marker with random jitter
//	 * for scatter plots
//	 * @param fpkmValue
//	 * @return
//	 */
//	private List<Double> markerCoordsWithJitter(Double fpkmValue) {
//		List<Double> marker = new ArrayList<Double>();
//		double jitter = RandomUtils.nextDouble(0, 0.45);
//		boolean negative = RandomUtils.nextBoolean();
//		if (negative) {
//			jitter = -jitter;
//		}
//		marker.add(jitter); //introduce jitter
//		marker.add(fpkmValue.doubleValue());
//		return marker;
//	}
//
//	public String getOncotreeCode() {
//		return oncotreeCode;
//	}
//
//	public void setOncotreeCode(String oncotreeCode) {
//		this.oncotreeCode = oncotreeCode;
//	}
//
//	public String getBoxPlotTooltip() {
//		return boxPlotTooltip;
//	}
//
//	public void setBoxPlotTooltip(String boxPlotTooltip) {
//		this.boxPlotTooltip = boxPlotTooltip;
//	}
//
//	public Double getMaxValue() {
//		return maxValue;
//	}
//
//	public void setMaxValue(Double maxValue) {
//		this.maxValue = maxValue;
//	}
//
//	public Values getStockSerie() {
//		return stockSerie;
//	}
//
//	public void setStockSerie(Values stockSerie) {
//		this.stockSerie = stockSerie;
//	}
//
//	public Values getMedianSerie() {
//		return medianSerie;
//	}
//
//	public void setMedianSerie(Values medianSerie) {
//		this.medianSerie = medianSerie;
//	}
//
//	public Values getScatterSerie() {
//		return scatterSerie;
//	}
//
//	public void setScatterSerie(Values scatterSerie) {
//		this.scatterSerie = scatterSerie;
//	}
//
//	public Values getCurrentCaseSerie() {
//		return currentCaseSerie;
//	}
//
//	public void setCurrentCaseSerie(Values currentCaseSerie) {
//		this.currentCaseSerie = currentCaseSerie;
//	}
//
//	public Values getOutliersSerie() {
//		return outliersSerie;
//	}
//
//	public void setOutliersSerie(Values outliersSerie) {
//		this.outliersSerie = outliersSerie;
//	}
//
//	public List<Double> getFpkms() {
//		return fpkms;
//	}
//
//	public void setFpkms(List<Double> fpkms) {
//		this.fpkms = fpkms;
//	}
//
//	public int getNbOfCases() {
//		return nbOfCases;
//	}
//
//	public void setNbOfCases(int nbOfCases) {
//		this.nbOfCases = nbOfCases;
//	}
//
//}
