package utsw.bicf.answer.controller.serialization.zingchart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.CNRData;
import utsw.bicf.answer.model.extmapping.CNSData;

public class CNVChartData extends ZingChartData {
	
	List<String> sortedChrs = new ArrayList<String>();
	List<Long> maxChroms = new ArrayList<Long>();
	long dataPointCount = 0;
	long minChrom = -1;
	
	public CNVChartData(List<CNSData> cnsData, List<CNRData> cnrData) {
		
		this.updateStartEnd(cnsData, cnrData);
		
		
		//Populate labels
		this.labels = new ArrayList<String>();
		Set<Long> uniqXAxis = new HashSet<Long>();
		for (CNSData c : cnsData) {
			uniqXAxis.add(c.getStart());
			uniqXAxis.add(c.getEnd());
		}
		for (CNRData c : cnrData) {
			uniqXAxis.add(c.getStart());
		}
//		Integer itemsSize = uniqXAxis.size();
//		this.labels = uniqXAxis.stream().sorted().map(x -> x + "").collect(Collectors.toList());
		
		//Initialize Series
		this.series = new ArrayList<Values>();
		
//		this.series.add(createCNRSeries(cnrData)); //just one series for all CNS
		this.series.addAll(createCNRSeriesByCN(cnsData, cnrData)); //separate series for CN == 2 and Others
		for (CNSData c : cnsData) {
			this.series.add(createCNSSeries(c));
			dataPointCount++;
		}
		
	}

	private Values createCNSSeries(CNSData cns) {
		List<Object> data = new ArrayList<Object>();
		List<String> dataLabels = new ArrayList<String>();
		Values values = new Values(data, "CNS", dataLabels);
		values.setType("line");
		data.add(new Object[] {cns.getStart(), cns.getLog2()});
		data.add(new Object[] {cns.getEnd(), cns.getLog2()});
		dataLabels.add(cns.getLog2() + "");
		return values;
	}
	
	//To make each gene distinct. Too slow and not very useful
	private List<Values> createCNRSeriesByGene(List<CNRData> cnrData) {
		Map<String, List<CNRData>> cnrByGene = new HashMap<String, List<CNRData>>();
		for (CNRData cnr : cnrData) {
			List<CNRData> list = new ArrayList<CNRData>();
			if (cnrByGene.containsKey(cnr.getGene())) {
				list = cnrByGene.get(cnr.getGene());
			}
			list.add(cnr);
			cnrByGene.put(cnr.getGene(), list);
		}
		List<Values> valuesByGene = new ArrayList<Values>();
		for (String gene : cnrByGene.keySet()) {
			List<CNRData> list = cnrByGene.get(gene);
			List<Object> data = new ArrayList<Object>();
			List<String> dataLabels = new ArrayList<String>();
			Values values = new Values(data, "CNR", dataLabels);
			values.setType("scatter");
			for (CNRData cnr : list) {
				data.add(new Object[] {cnr.getStart(), cnr.getLog2()});
				dataLabels.add(gene);
			}
			valuesByGene.add(values);
		}
		return valuesByGene;
	}
	
	/**
	 * Creates 2 series:
	 * - a series for CN == 2
	 * - a series for others
	 * If CNS has CN == 2 and a CNR is in its range [start,end],
	 * then those CNR belong to the same series and should be colored the same
	 * @param cnsData
	 * @param cnrData
	 * @return
	 */
	private List<Values> createCNRSeriesByCN(List<CNSData> cnsData, List<CNRData> cnrData) {
		List<CNSData> cn2 = cnsData.stream().filter(c -> c.getCn() == 2).collect(Collectors.toList());
//		List<CNSData> cnOther = cnsData.stream().filter(c -> c.getCn() != 2).collect(Collectors.toList());
		
		List<CNRData> cnr2 = new ArrayList<CNRData>();
		List<CNRData> cnrOther = new ArrayList<CNRData>();
		
		for (CNRData cnr : cnrData) {
			boolean hasCN = false;
			long start = cnr.getStart();
			for (CNSData cns: cn2) {
				if (cns.getStart() <= start && start <= cns.getEnd()) {
					hasCN = true;
					break;
				}
			}
			if (hasCN) {
				cnr2.add(cnr);
			}
			else {
				cnrOther.add(cnr);
			}
		}
		
		List<Values> valuesByCN = new ArrayList<Values>();
		
		List<Object> data2 = new ArrayList<Object>();
		List<String> dataLabels2 = new ArrayList<String>();
		Values values2 = new Values(data2, "CNR (CN=2)", dataLabels2);
		values2.setType("scatter");
		for (CNRData cnr : cnr2) {
			data2.add(new Object[] {cnr.getStart(), cnr.getLog2()});
			dataLabels2.add(cnr.getGene());
			dataPointCount++;
		}
		valuesByCN.add(values2);
		
		List<Object> dataOther = new ArrayList<Object>();
		List<String> dataLabelsOther = new ArrayList<String>();
		Values valuesOther = new Values(dataOther, "CNR", dataLabelsOther);
		valuesOther.setType("scatter");
		for (CNRData cnr : cnrOther) {
			dataOther.add(new Object[] {cnr.getStart(), cnr.getLog2()});
			dataLabelsOther.add(cnr.getGene());
			dataPointCount++;
		}
		valuesByCN.add(valuesOther);
		
		return valuesByCN;
	}
	
	private Values createCNRSeries(List<CNRData> cnrData) {
		List<Object> data = new ArrayList<Object>();
		List<String> dataLabels = new ArrayList<String>();
		Values values = new Values(data, "CNR", dataLabels);
		values.setType("scatter");
		for (CNRData cnr : cnrData) {
			data.add(new Object[] {cnr.getStart(), cnr.getLog2()});
			dataLabels.add(cnr.getGene());
		}
		
		return values;
	}

	private void updateStartEnd(List<CNSData> cnsData, List<CNRData> cnrData) {
		Map<String, Long> chrMax = new HashMap<String, Long>();
		Map<String, List<CNRData>> byChr = new HashMap<String, List<CNRData>>();
		for (CNRData cnr : cnrData) {
			List<CNRData> list = null;
			if (byChr.containsKey(cnr.getChr())) {
				list = byChr.get(cnr.getChr());
			}
			else {
				list = new ArrayList<CNRData>();
			}
			list.add(cnr);
			byChr.put(cnr.getChr(), list);
		}
		
		sortedChrs = byChr.keySet().stream().sorted().collect(Collectors.toList());
		Long max = 0L;
		for (String chr : sortedChrs) {
			List<CNRData> list = byChr.get(chr);
			for (CNRData item : list) {
				if (minChrom == -1) {
					minChrom = item.getStart();
				}
				item.setStart(item.getStart() + max);
				item.setEnd(item.getEnd() + max);
			}
			chrMax.put(chr, max);
			max = list.stream().map(c -> c.getStart()).max(Comparator.comparing(Long::valueOf)).get();
			maxChroms.add(max);
			
		}
		
		//adjust start and end for CNS
		for (CNSData cns : cnsData) {
			max = chrMax.get(cns.getChr());
			cns.setStart(cns.getStart() + max);
			cns.setEnd(cns.getEnd() + max);
		}
	}

	public List<String> getSortedChrs() {
		return sortedChrs;
	}

	public void setSortedChrs(List<String> sortedChrs) {
		this.sortedChrs = sortedChrs;
	}

	public List<Long> getMaxChroms() {
		return maxChroms;
	}

	public void setMaxChroms(List<Long> maxChroms) {
		this.maxChroms = maxChroms;
	}

	public long getDataPointCount() {
		return dataPointCount;
	}

	public void setDataPointCount(long dataPointCount) {
		this.dataPointCount = dataPointCount;
	}

	public long getMinChrom() {
		return minChrom;
	}

	public void setMinChrom(long minChrom) {
		this.minChrom = minChrom;
	}
}
