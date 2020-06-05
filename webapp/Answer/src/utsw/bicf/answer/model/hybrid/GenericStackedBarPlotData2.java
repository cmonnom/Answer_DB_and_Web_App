package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericStackedBarPlotData2 {
	
	Map<String, Number> snpIndels = new HashMap<String, Number>();
	Map<String, Number> amplifications = new HashMap<String, Number>();
	Map<String, Number> deletions = new HashMap<String, Number>();
	
	List<GeneCount> geneCounts = new ArrayList<GeneCount>();
	
	public GenericStackedBarPlotData2() {
		super();
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public Map<String, Number> getSnpIndels() {
		return snpIndels;
	}

	public void setSnpIndels(Map<String, Number> snpIndels) {
		this.snpIndels = snpIndels;
	}

	public GenericStackedBarPlotData2(Map<String, Number> snpIndels, Map<String, Number> amplifications,
			Map<String, Number> deletions) {
		super();
		this.snpIndels = snpIndels;
		this.amplifications = amplifications;
		this.deletions = deletions;
		init();
	}
	
	private void init() {
		Set<String> allGenes = new HashSet<String>();
		allGenes.addAll(snpIndels.keySet());
		allGenes.addAll(amplifications.keySet());
		allGenes.addAll(deletions.keySet());
		for (String gene: allGenes) {
			Number snpCount = snpIndels.get(gene);
			Number ampCount = amplifications.get(gene);
			Number delCount = deletions.get(gene);
			Integer count = (snpCount != null ? snpCount.intValue() : 0) 
					+ (ampCount != null ? ampCount.intValue() : 0)
					+ (delCount != null ? delCount.intValue() : 0);
			GeneCount  gc = new GeneCount(gene, count);
			geneCounts.add(gc);
		}
		Collections.sort(geneCounts);
	}
	
	public Map<String, Number> getAmplifications() {
		return amplifications;
	}

	public void setAmplifications(Map<String, Number> amplifications) {
		this.amplifications = amplifications;
	}

	public Map<String, Number> getDeletions() {
		return deletions;
	}

	public void setDeletions(Map<String, Number> deletions) {
		this.deletions = deletions;
	}

	public List<GeneCount> getGeneCounts() {
		return geneCounts;
	}

	public void setGeneCounts(List<GeneCount> geneCounts) {
		this.geneCounts = geneCounts;
	}

}
