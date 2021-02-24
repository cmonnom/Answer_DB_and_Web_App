package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCVariantCategory {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("snp", new String[] {"Simple","LA26801-3", "LN"});
		loincCodes.put("cnv", new String[] {"Structural","LA26802-1", "LN"});
		loincCodes.put("translocation", new String[] {"Fusion","", ""});
	}
	
	public static String[] getLoincCode(String answerChrom) {
		return loincCodes.get(answerChrom);
	}

}
