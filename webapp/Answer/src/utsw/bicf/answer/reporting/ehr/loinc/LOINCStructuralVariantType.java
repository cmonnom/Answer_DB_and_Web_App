package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCStructuralVariantType {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("Translocation", new String[] {"Translocation","LA26331-1"});
	}
	
	public static String[] getLoincCode(String code) {
		return loincCodes.get(code);
	}

}
