package utsw.bicf.answer.controller.serialization.zingchart;
//package utsw.bicf.answer.controller.serialization.zingchart;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import utsw.bicf.answer.model.Sample;
//import utsw.bicf.answer.model.hybrid.SampleCoverage;
//import utsw.bicf.answer.controller.serialization.zingchart.Values;
//import utsw.bicf.answer.controller.serialization.zingchart.ZingChartData;
//
//public class SampleCoverageZingChartData extends ZingChartData {
//	
//	@JsonIgnore
//	Sample sample;
//	String sampleLabName;
//	
//	
//	public SampleCoverageZingChartData(Sample sample, List<SampleCoverage> coverages) {
//		this.sample = sample;
//		this.sampleLabName = sample.getSampleLabName();
//		Integer itemsSize = coverages.size();
//		
//		//Populate labels
//		this.labels = new ArrayList<String>();
//		for (int i = 0; i < itemsSize; i++) {
//			SampleCoverage cov = coverages.get(i);
////			this.labels.add(cov.getExonId().toString()); 
//			this.labels.add(cov.getGeneSymbol());
//		}
//		
//		//Initialize Series
//		this.series = new ArrayList<Values>();
//		
////		Max Depth DataSet
//		this.series.add(createSeries(coverages, itemsSize, "Max. Depth", "getMaxDepth"));
//		
////		Avg Depth DataSet
//		this.series.add(createSeries(coverages, itemsSize, "Avg. Depth", "getAvgDepth"));
//		
////		Median Depth DataSet
//		this.series.add(createSeries(coverages, itemsSize, "Median. Depth", "getMedianDepth"));
//		
////		Min Depth DataSet
//		this.series.add(createSeries(coverages, itemsSize, "Min. Depth", "getMinDepth"));
//		
//		
//	}
//
//
//	private Values createSeries(List<SampleCoverage> coverages, Integer itemsSize, String seriesTitle, String method) {
//		List<Object> data = new ArrayList<Object>();
//		List<String> dataLabels = new ArrayList<String>();
//		Values valuesAvgDepth = new Values(data, seriesTitle, dataLabels);
//		for (int i = 0; i < itemsSize; i++) {
//			SampleCoverage cov = coverages.get(i);
//		     // call computeRentalCost method with parameter int
//		     try {
//		    	 Method oneMethod = SampleCoverage.class.getMethod(method);
//		    	 data.add(oneMethod.invoke(cov, new Object[] {}));
////		    	 dataLabels.add(cov.getExonName().split(";")[0]);
//		    	 dataLabels.add(cov.getGeneSymbol());
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			}
//
//		}
//		return valuesAvgDepth;
//	}
//
//
//	public String getSampleLabName() {
//		return sampleLabName;
//	}
//
//
//	public void setSampleLabName(String sampleLabName) {
//		this.sampleLabName = sampleLabName;
//	}
//
//}
