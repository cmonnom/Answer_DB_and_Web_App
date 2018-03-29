package utsw.bicf.answer.controller.serialization.zingchart;
//package utsw.bicf.answer.controller.serialization.zingchart;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import utsw.bicf.answer.model.hybrid.MonthlySampleTAT;
//import utsw.bicf.answer.controller.serialization.zingchart.Values;
//import utsw.bicf.answer.controller.serialization.zingchart.ZingChartData;
//
//public class TATZingChartData extends ZingChartData {
//	
//	public TATZingChartData(List<MonthlySampleTAT> tats) {
//		Integer itemsSize = tats.size();
//		
//		//Populate labels
//		this.labels = new ArrayList<String>();
//		for (int i = 0; i < itemsSize; i++) {
//			MonthlySampleTAT tat = tats.get(i);
//			this.labels.add(tat.getMonthLabel());
//		}
//		
//		//Initialize Series
//		this.series = new ArrayList<Values>();
//		//Sample Count
//		this.series.add(createSeries(tats, itemsSize, "Order Count", "getSubjectCount"));
//		//TAT Lab
//		this.series.add(createSeries(tats, itemsSize, "Avg. Lab TAT (days)", "getAvgTATLabRounded"));
//		//TAT Analysis
//		this.series.add(createSeries(tats, itemsSize, "Avg. Analysis TAT (days)", "getAvgTATAnalysisRounded"));
//	}
//
//
//	private Values createSeries(List<MonthlySampleTAT> tats, Integer itemsSize, String seriesTitle, String method) {
//		List<Object> data = new ArrayList<Object>();
//		List<String> dataLabels = new ArrayList<String>();
//		Values valuesAvgDepth = new Values(data, seriesTitle, dataLabels);
//		for (int i = 0; i < itemsSize; i++) {
//			MonthlySampleTAT tat = tats.get(i);
//		     try {
//		    	 Method oneMethod = MonthlySampleTAT.class.getMethod(method);
//		    	 data.add(oneMethod.invoke(tat, new Object[] {}));
//		    	 dataLabels.add(tat.getMonthLabel());
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
//}
