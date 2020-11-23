package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINCAAChangeType {
	
	private static final Map<String, String[]> loincCodes = new HashMap<String, String[]>();
	private static final Map<String, String> effectToLoinc = new HashMap<String, String>();
	
	static {
		loincCodes.put("Wild type", new String[] {"Wild type","LA9658-1"});
		loincCodes.put("Deletion", new String[] {"Deletion","LA6692-3"});
		loincCodes.put("Duplication", new String[] {"Duplication","LA6686-5"});
		loincCodes.put("Frameshift", new String[] {"Frameshift","LA6694-9"});
		loincCodes.put("Initiating Methionine", new String[] {"Initiating Methionine","LA6695-6"});
		loincCodes.put("Insertion", new String[] {"Insertion","LA6687-3"});
		loincCodes.put("Insertion and Deletion", new String[] {"Insertion and Deletion","LA9659-9"});
		loincCodes.put("Missense", new String[] {"Missense","LA6698-0"});
		loincCodes.put("Nonsense", new String[] {"Nonsense","LA6699-8"});
		loincCodes.put("Silent", new String[] {"Silent","LA6700-4"});
		loincCodes.put("Stop Codon Mutation", new String[] {"Stop Codon Mutation","LA6701-2"});
		
		effectToLoinc.put("exon_loss_variant", "Deletion");
		effectToLoinc.put("exon_loss_variant", "Deletion");
		effectToLoinc.put("disruptive_inframe_insertion", "Deletion");
		effectToLoinc.put("inframe_deletion", "Deletion");
		effectToLoinc.put("disruptive_inframe_deletion", "Deletion");
		effectToLoinc.put("3_prime_UTR_truncation + exon_loss", "Deletion");
		effectToLoinc.put("5_prime_UTR_truncation + exon_loss_variant", "Deletion");
		
		effectToLoinc.put("exon_loss_variant", "Duplication");
		effectToLoinc.put("frameshift_variant", "Frameshift");
		
		effectToLoinc.put("start_lost", "Initiating Methionine");
		effectToLoinc.put("5_prime_UTR_premature_start_codon_gain_variant", "Initiating Methionine");
		effectToLoinc.put("inframe_insertion", "Insertion");
		effectToLoinc.put("inversion", "Insertion and Deletion");
		effectToLoinc.put("missense_variant", "Missense");
		effectToLoinc.put("coding_sequence_variant", "Missense");
		effectToLoinc.put("stop_gained", "Nonsense");
		effectToLoinc.put("initiator_codon_variant", "Silent");
		effectToLoinc.put("stop_retained_variant", "Silent");
		effectToLoinc.put("synonymous_variant", "Silent");
		effectToLoinc.put("start_retained", "Silent");
		effectToLoinc.put("stop_retained_variant", "Silent");
		effectToLoinc.put("stop_lost", "Stop Codon Mutation");


	}
	
	public static String[] getLoincCode(String key) {
		return loincCodes.get(key);
	}

	public static String getLoincCodeKeyFromEffect(String effect) {
		return effectToLoinc.get(effect);
	}
	
	
}
