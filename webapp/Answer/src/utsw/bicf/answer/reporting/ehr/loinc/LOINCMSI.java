package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCMSI {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("Stable", new String[] {"Stable","LA14122-8"});
		loincCodes.put("MSI-L", new String[] {"MSI-L","LA26202-4"});
		loincCodes.put("MSI-H", new String[] {"MSI-H","LA26203-2"});
		loincCodes.put("Indeterminate", new String[] {"Indeterminate","LA11884-6"});
	}
	
	public static String[] getLoincCode(String code) {
		return loincCodes.get(code);
	}

}
