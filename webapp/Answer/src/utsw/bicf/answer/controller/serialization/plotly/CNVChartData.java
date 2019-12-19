package utsw.bicf.answer.controller.serialization.plotly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.CNRData;
import utsw.bicf.answer.model.extmapping.CNSData;

public class CNVChartData extends PlotlyChartData {
	
	public static final Double CR_MAX = 5d;
	public static final Double CR_MIN = -5d;
	
	Trace cnr2 = new Trace();
	Trace cnrOthers = new Trace();
//	List<Object> cnrData2X = new ArrayList<Object>();
//	List<Object> cnrData2Y = new ArrayList<Object>();
//	List<String> cnrLabels2 = new ArrayList<String>();
//	List<Object> cnrDataOthersX = new ArrayList<Object>();
//	List<Object> cnrDataOthersY = new ArrayList<Object>();
//	List<String> cnrLabelsOthers = new ArrayList<String>();
	
	//for values outside of the expected range [-5, 5]
	Trace cnr2Outliers = new Trace();
	Trace cnrOtherOutliers = new Trace();
//	List<Object> cnrData2OutliersX = new ArrayList<Object>();
//	List<Object> cnrData2OutliersY = new ArrayList<Object>();
//	List<String> cnrLabels2Outliers = new ArrayList<String>();
//	List<Object> cnrDataOtherOutliersX = new ArrayList<Object>();
//	List<Object> cnrDataOtherOutliersY = new ArrayList<Object>();
//	List<String> cnrLabelsOtherOutliers = new ArrayList<String>();
	
	Trace chr = new Trace();
//	List<Long> chrEnd = new ArrayList<Long>();
//	List<Long> chrStart = new ArrayList<Long>();
//	List<String> chrLabels = new ArrayList<String>();
	long minChrom = -1;
	
	Trace cns = new Trace();
//	List<Object[]> cnsDataX = new ArrayList<Object[]>();
//	List<Object[]> cnsDataY = new ArrayList<Object[]>();
//	List<String> cnsLabels = new ArrayList<String>();
	List<String> cnsTitles = new ArrayList<String>();
	
	List<Trace> genesSelected = new ArrayList<Trace>();
	List<Trace> genesSelectedOutliers = new ArrayList<Trace>();
	
	Trace genes = new Trace();
	List<String> geneLabels = new ArrayList<String>();
	
	public CNVChartData(List<CNSData> cnsData, List<CNRData> cnrData, List<String> selectedGenes) {
		
		this.updateStartEnd(cnsData, cnrData);
		
		if (!selectedGenes.isEmpty()) {
			//separate series by genes
			createCNRSeriesByGene(selectedGenes, cnrData);
			this.cnr2.setName("CNR (CN=2)");
			this.cnrOthers.setName("CNR (others)");
			this.cnr2Outliers.setName("CNR Outliers (CN=2)");
			this.cnrOtherOutliers.setName("CNR Outliers (others)");
		}
		else {
			createCNRSeriesByCN(cnsData, cnrData); //separate series for CN == 2 and Others
			this.cnr2.setName("CNR (CN=2)");
			this.cnrOthers.setName("CNR (others)");
			this.cnr2Outliers.setName("CNR Outliers (CN=2)");
			this.cnrOtherOutliers.setName("CNR Outliers (others)");
		}
		for (CNSData c : cnsData) {
			createCNSSeries(c);
		}
		
		getGeneBoudaries(cnrData);
		
	}

	private void createCNRSeriesByGene(List<String> selectedGenes, List<CNRData> cnrData) {
		Map<String, List<CNRData>> cnrByGene = cnrData.stream().collect(Collectors.groupingBy(c -> c.getGene()));
		List<CNRData> cnrNotInSelectedGenes = cnrData.stream().filter(c -> !selectedGenes.contains(c.getGene())).collect(Collectors.toList());
		
		//unselected genes first so they are not hiding the selected genes
		for (CNRData cnr : cnrNotInSelectedGenes) {
			if (cnr.getLog2() > CR_MAX || cnr.getLog2() < CR_MIN) {
				Double ceilingValue = null;
				if (cnr.getLog2() > CR_MAX) {
					ceilingValue = CR_MAX;
				}
				else {
					ceilingValue =  CR_MIN;
				}
				cnrOtherOutliers.addX(cnr.getStart());
				cnrOtherOutliers.addY(ceilingValue);
				cnrOtherOutliers.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
			}
			cnrOthers.addX(cnr.getStart());
			cnrOthers.addY(cnr.getLog2());
			cnrOthers.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
		}
		
		//selected genes
		List<String> sortedGenes = cnrByGene.keySet().stream().sorted().collect(Collectors.toList());
		for (String gene : sortedGenes) {
			if (selectedGenes.contains(gene)) {
				Trace trace = new Trace();
				trace.setName(gene);
				Trace outlierTrace = new Trace();
				outlierTrace.setName(gene);
				
				List<CNRData> cnrList = cnrByGene.get(gene);
				for (CNRData cnr : cnrList) {
					if (cnr.getLog2() > CR_MAX || cnr.getLog2() < CR_MIN) {
						Double ceilingValue = null;
						if (cnr.getLog2() > CR_MAX) {
							ceilingValue = CR_MAX;
						}
						else {
							ceilingValue =  CR_MIN;
						}
						outlierTrace.addX(cnr.getStart());
						outlierTrace.addY(ceilingValue);
						outlierTrace.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
					}
					trace.addX(cnr.getStart());
					trace.addY(cnr.getLog2());
					trace.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
				}
				this.genesSelectedOutliers.add(outlierTrace);
				this.genesSelected.add(trace);
			}
		}
	}

	private void createCNSSeries(CNSData cns) {
		this.cns.addX(new Object[] {cns.getStart(), cns.getEnd()});
		this.cns.addY(new Object[] {cns.getLog2(), cns.getLog2()});
		this.cns.addLabel("Log2: " + cns.getLog2() + " CN:" + cns.getCn());
		this.cnsTitles.add("CNS " + cns.getChr());
	}
	
	/**
	 * Creates 2 series:
	 * - a series for CN == 2
	 * - a series for others
	 * If CNS has CN == 2 and a CNR is in its range [start,end],
	 * then those CNR belong to the same series and should be colored the same
	 * @param cnsData
	 * @param cnrData
	 */
	private void createCNRSeriesByCN(List<CNSData> cnsData, List<CNRData> cnrData) {
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
		

		
		
		for (CNRData cnr : cnr2) {
			if (cnr.getLog2() > CR_MAX || cnr.getLog2() < CR_MIN) {
				Double ceilingValue = null;
				if (cnr.getLog2() > CR_MAX) {
					ceilingValue = CR_MAX;
				}
				else {
					ceilingValue =  CR_MIN;
				}
				this.cnr2Outliers.addX(cnr.getStart());
				this.cnr2Outliers.addY(ceilingValue);
				this.cnr2Outliers.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
			}
			this.cnr2.addX(cnr.getStart());
			this.cnr2.addY(cnr.getLog2());
			this.cnr2.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
		}
		//add fake outliers
//			this.cnr2Outliers.addX(1000000000);
//			this.cnr2Outliers.addY(CR_MAX);
//			this.cnr2Outliers.addLabel("Gene: " + "CRAZY" + " Log2: " + 8.5);
		
		for (CNRData cnr : cnrOther) {
			if (cnr.getLog2() > CR_MAX || cnr.getLog2() < CR_MIN) {
				Double ceilingValue = null;
				if (cnr.getLog2() > CR_MAX) {
					ceilingValue = CR_MAX;
				}
				else {
					ceilingValue =  CR_MIN;
				}
				this.cnrOtherOutliers.addX(cnr.getStart());
				this.cnrOtherOutliers.addY(ceilingValue);
				this.cnrOtherOutliers.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
			}
			this.cnrOthers.addX(cnr.getStart());
			this.cnrOthers.addY(cnr.getLog2());
			this.cnrOthers.addLabel("Gene: " + cnr.getGene() + " Log2: " + cnr.getLog2());
		}
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

		this.chr.setLabels(byChr.keySet().stream().sorted().collect(Collectors.toList()));
		this.chr.addStart(0L);
		Long max = 0L;
		for (String chr : this.chr.getLabels()) {
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
			this.chr.addEnd(max);
			this.chr.addStart(max);
		}
		this.chr.removeLastStart(); //remove the last one
		
		//adjust start and end for CNS
		for (CNSData cns : cnsData) {
			max = chrMax.get(cns.getChr());
			if (max == null) {
				continue; //skip this CNS
//				max = missingMax + 1;
//				missingMax += cns.getEnd();
			}
			cns.setStart(cns.getStart() + max);
			cns.setEnd(cns.getEnd() + max);
		}
	}
	
	private void getGeneBoudaries(List<CNRData> cnrData) {
		Map<String, List<CNRData>> cnrByGene = cnrData.stream().collect(Collectors.groupingBy(c -> c.getGene()));
		List<Object[]> genesToSort = new ArrayList<Object[]>();
		for (String gene : cnrByGene.keySet()) {
			List<CNRData> dataPoints = cnrByGene.get(gene);
			Long start = dataPoints.stream().map(d -> d.getStart()).sorted().findFirst().get();
			Long end = dataPoints.stream().map(d -> d.getEnd()).sorted(Comparator.reverseOrder()).findFirst().get();
			genesToSort.add(new Object[]{start - 50, end + 50, gene});
//			this.genes.addStart(start - 50); //expand gene's edges a bit
//			this.genes.addEnd(end + 50);
		}
		genesToSort = genesToSort.stream().sorted(Comparator.comparing(se -> (long)se[0])).collect(Collectors.toList());
		for (Object[] startEnd : genesToSort) {
			this.genes.addStart((long) startEnd[0] - 50);
			this.genes.addEnd((long) startEnd[1] + 50);
			this.geneLabels.add((String) startEnd[2]);
		}
	}


	public List<String> getCnsTitles() {
		return cnsTitles;
	}

	public void setCnsTitles(List<String> cnsTitles) {
		this.cnsTitles = cnsTitles;
	}

	public Trace getCnr2() {
		return cnr2;
	}

	public void setCnr2(Trace cnr2) {
		this.cnr2 = cnr2;
	}

	public Trace getCnrOthers() {
		return cnrOthers;
	}

	public void setCnrOthers(Trace cnrOthers) {
		this.cnrOthers = cnrOthers;
	}

	public Trace getCnr2Outliers() {
		return cnr2Outliers;
	}

	public void setCnr2Outliers(Trace cnr2Outliers) {
		this.cnr2Outliers = cnr2Outliers;
	}

	public Trace getCnrOtherOutliers() {
		return cnrOtherOutliers;
	}

	public void setCnrOtherOutliers(Trace cnrOtherOutliers) {
		this.cnrOtherOutliers = cnrOtherOutliers;
	}

	public Trace getChr() {
		return chr;
	}

	public void setChr(Trace chr) {
		this.chr = chr;
	}

	public Trace getCns() {
		return cns;
	}

	public void setCns(Trace cns) {
		this.cns = cns;
	}

	public List<Trace> getGenesSelected() {
		return genesSelected;
	}

	public void setGenesSelected(List<Trace> genesSelected) {
		this.genesSelected = genesSelected;
	}

	public List<Trace> getGenesSelectedOutliers() {
		return genesSelectedOutliers;
	}

	public void setGenesSelectedOutliers(List<Trace> genesSelectedOutliers) {
		this.genesSelectedOutliers = genesSelectedOutliers;
	}

	public Trace getGenes() {
		return genes;
	}

	public void setGenes(Trace genes) {
		this.genes = genes;
	}

	public List<String> getGeneLabels() {
		return geneLabels;
	}

	public void setGeneLabels(List<String> geneLabels) {
		this.geneLabels = geneLabels;
	}

}
