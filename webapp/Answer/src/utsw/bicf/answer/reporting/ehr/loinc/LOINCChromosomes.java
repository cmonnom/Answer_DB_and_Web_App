package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCChromosomes {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("chr1", new String[] {"Chromosome 1","LA21254-0"});
		loincCodes.put("chr2", new String[] {"Chromosome 2","LA21255-7"});
		loincCodes.put("chr3", new String[] {"Chromosome 3","LA21256-5"});
		loincCodes.put("chr4", new String[] {"Chromosome 4","LA21257-3"});
		loincCodes.put("chr5", new String[] {"Chromosome 5","LA21258-1"});
		loincCodes.put("chr6", new String[] {"Chromosome 6","LA21259-9"});
		loincCodes.put("chr7", new String[] {"Chromosome 7","LA21260-7"});
		loincCodes.put("chr8", new String[] {"Chromosome 8","LA21261-5"});
		loincCodes.put("chr9", new String[] {"Chromosome 9","LA21262-3"});
		loincCodes.put("chr10", new String[] {"Chromosome 10","LA21263-1"});
		loincCodes.put("chr11", new String[] {"Chromosome 11","LA21264-9"});
		loincCodes.put("chr12", new String[] {"Chromosome 12","LA21265-6"});
		loincCodes.put("chr13", new String[] {"Chromosome 13","LA21266-4"});
		loincCodes.put("chr14", new String[] {"Chromosome 14","LA21267-2"});
		loincCodes.put("chr15", new String[] {"Chromosome 15","LA21268-0"});
		loincCodes.put("chr16", new String[] {"Chromosome 16","LA21269-8"});
		loincCodes.put("chr17", new String[] {"Chromosome 17","LA21270-6"});
		loincCodes.put("chr18", new String[] {"Chromosome 18","LA21271-4"});
		loincCodes.put("chr19", new String[] {"Chromosome 19","LA21272-2"});
		loincCodes.put("chr20", new String[] {"Chromosome 20","LA21273-0"});
		loincCodes.put("chr21", new String[] {"Chromosome 21","LA21274-8"});
		loincCodes.put("chr22", new String[] {"Chromosome 22","LA21275-5"});
		loincCodes.put("chrX", new String[] {"Chromosome X","LA21276-3"});
		loincCodes.put("chrY", new String[] {"Chromosome Y","LA21277-1"});
	}
	
	public static String[] getLoincCode(String answerChrom) {
		return loincCodes.get(answerChrom);
	}

}
