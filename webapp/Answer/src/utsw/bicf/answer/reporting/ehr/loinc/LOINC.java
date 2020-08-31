package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINC {
	
	private static final Map<String, LOINCItem> codes = new HashMap<String, LOINCItem>();
	static {
		codes.put("Gene studied", new LOINCItem("48018-6", "Gene studied"));
		codes.put("Transcript ref sequence ID", new LOINCItem("51958-7", "Transcript ref sequence ID"));
		codes.put("Amino acid change p.HGVS", new LOINCItem("48005-3", "Amino acid change p.HGVS"));
		codes.put("Chromosome", new LOINCItem("91272006", "Chromosome", "SCT"));
		codes.put("Genomic ref allele", new LOINCItem("69547-8", "Genomic ref allele"));
		codes.put("Genomic allele start-end", new LOINCItem("81254-5", "Genomic allele start-end"));
		codes.put("Genomic alt allele", new LOINCItem("69551-0", "Genomic alt allele"));
		codes.put("Genetic sequence variation clinical significance [Imp]", new LOINCItem("53037-8", "Genetic sequence variation clinical significance [Imp]"));
		codes.put("Allelic freq NFr", new LOINCItem("81258-6", "Allelic freq NFr"));
		
		codes.put("Pathogenic", new LOINCItem("LA6703-8", "Pathogenic"));
		codes.put("Likely pathogenic", new LOINCItem("LA6704-6", "Likely pathogenic"));
		codes.put("Uncertain significance", new LOINCItem("LA6705-3", "Uncertain significance"));
		
		codes.put("Annotation comment [Interpretation] Narrative", new LOINCItem("48767-8", "Annotation comment [Interpretation] Narrative"));
		
		codes.put("Tumor Mutational Burden (Gene Mut Tested Bld/T)", new LOINCItem("48025-1", "Tumor Mutational Burden (Gene Mut Tested Bld/T)"));
		
		codes.put("Therapy count", new LOINCItem("THERAPYCOUNT", "Therapy count", "")); //no LOINC code?
		
		codes.put("THERAPYGENE", new LOINCItem("THERAPYGENE", "Therapy ", ": Gene", "")); //no LOINC code?
		codes.put("THERAPYVARIANT", new LOINCItem("THERAPYVARIANT", "Therapy ", ": Variant", "")); //no LOINC code?
		codes.put("THERAPYAGENT", new LOINCItem("THERAPYAGENT", "Therapy ", ": Agent", "")); //no LOINC code?
		codes.put("THERAPYPUBMEDID", new LOINCItem("THERAPYPUBMEDID", "Therapy ", ": Evidence ID", "")); //no LOINC code?
		
		codes.put("Trial count", new LOINCItem("TRIALCOUNT", "Trial count", "")); //no LOINC code?
		
		
		codes.put("TRIALNCTID", new LOINCItem("TRIALNCTID", "Trial ", ": Clinical Trial NCT ID", "")); //no LOINC code?
		codes.put("TRIALMATCHES", new LOINCItem("TRIALMATCHES", "Trial ", ": Matched criteria", "")); //no LOINC code?
		
		//Possible NCTID: https://loinc.org/82786-5/
		//https://search.loinc.org/searchLOINC/search.zul?query=CLINTRIAL
		
		//Possible pubmed https://loinc.org/75608-0/
		//75608-0 Citation [Bibliographic Citation] in Reference lab test Narrative
	}

	public static LOINCItem getCode(String name) {
		return codes.get(name);
	}
}


