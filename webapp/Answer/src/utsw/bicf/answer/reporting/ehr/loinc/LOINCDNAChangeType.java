package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCDNAChangeType {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	
	static {
		loincCodes.put("Wild type", new String[] {"Wild type","LA9658-1"});
		loincCodes.put("Deletion", new String[] {"Deletion","LA6692-3"});
		loincCodes.put("Duplication", new String[] {"Duplication","LA6686-5"});
		loincCodes.put("Insertion", new String[] {"Insertion","LA6687-3"});
		loincCodes.put("Insertion/Deletion", new String[] {"Insertion/Deletion","LA6688-1"});
		loincCodes.put("Inversion", new String[] {"Inversion","LA6689-9"});
		loincCodes.put("Substitution", new String[] {"Substitution","LA6690-7"});
	}
	
	public static String[] getLoincCode(String answerChrom) {
		return loincCodes.get(answerChrom);
	}

}
