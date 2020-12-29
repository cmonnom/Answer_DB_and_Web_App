package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCGenomicSource {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("Somatic", new String[] {"Somatic","LA6684-0"});
		loincCodes.put("Germline", new String[] {"Germline","LA6683-2"});
		loincCodes.put("Unknown", new String[] {"Unknown genomic origin","LA18197-6"});
	}
	
	public static String[] getLoincCode(String answerChrom) {
		return loincCodes.get(answerChrom);
	}

}
