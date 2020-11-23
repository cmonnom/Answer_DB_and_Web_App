package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCVariantCategory {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("snp", new String[] {"Simple variant","LA26801-3"});
		loincCodes.put("cnv", new String[] {"Structural variant","	LA26802-1"});
		loincCodes.put("translocation", new String[] {"Structural variant","	LA26802-1"});
	}
	
	public static String[] getLoincCode(String answerChrom) {
		return loincCodes.get(answerChrom);
	}

}
