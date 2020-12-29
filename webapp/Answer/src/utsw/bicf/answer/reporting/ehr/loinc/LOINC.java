package utsw.bicf.answer.reporting.ehr.loinc;

import java.util.HashMap;
import java.util.Map;

public class LOINC {
	
	private static final Map<String, LOINCItem> codes = new HashMap<String, LOINCItem>();
	static {
		codes.put("Gene studied", new LOINCItem("48018-6", "Gene studied"));
		codes.put("Transcript ref sequence ID", new LOINCItem("51958-7", "Transcript ref sequence ID"));
		codes.put("Amino acid change p.HGVS", new LOINCItem("48005-3", "Amino acid change p.HGVS"));
//		codes.put("Chromosome", new LOINCItem("91272006", "Chromosome", "SCT"));
		codes.put("Chromosome", new LOINCItem("48000-4", "Chromosome"));
		codes.put("Genomic ref allele", new LOINCItem("69547-8", "Genomic ref allele"));
		codes.put("Genomic allele start-end", new LOINCItem("81254-5", "Genomic allele start-end"));
		codes.put("Genomic alt allele", new LOINCItem("69551-0", "Genomic alt allele"));
		codes.put("Genetic sequence variation clinical significance [Imp]", new LOINCItem("53037-8", "Genetic sequence variation clinical significance [Imp]"));
		codes.put("Sample variant allelic frequency [NFr]", new LOINCItem("81258-6", "Sample variant allelic frequency [NFr]"));
		
		codes.put("Pathogenic", new LOINCItem("LA6703-8", "Pathogenic"));
		codes.put("Likely pathogenic", new LOINCItem("LA6704-6", "Likely pathogenic"));
		codes.put("Uncertain significance", new LOINCItem("LA6705-3", "Uncertain significance"));
		codes.put("Tier 1", new LOINCItem("Tier 1", "Tier 1", ""));
		codes.put("Tier 2", new LOINCItem("Tier 2", "Tier 2", ""));
		codes.put("Tier 3", new LOINCItem("Tier 3", "Tier 3", ""));
		
		codes.put("Annotation comment [Interpretation] Narrative", new LOINCItem("48767-8", "Annotation comment [Interpretation] Narrative"));
		
		codes.put("Gene mutations tested for [#] in Blood or Tissue by Molecular genetics method", new LOINCItem("48025-1", "Gene mutations tested for [#] in Blood or Tissue by Molecular genetics method"));
		
		codes.put("dbSNP version [ID]", new LOINCItem("82115-7", "dbSNP version [ID]"));
		codes.put("Variant category", new LOINCItem("83005-9", "Variant category"));
		codes.put("DNA change (c.HGVS)", new LOINCItem("48004-6", "DNA change (c.HGVS)"));
		codes.put("DNA change type", new LOINCItem("48019-4", "DNA change type"));
		codes.put("Amino acid change [Type]", new LOINCItem("48006-1", "Amino acid change [Type]"));
		codes.put("Variant analysis method [Type]", new LOINCItem("81304-8", "Variant analysis method [Type]"));
		codes.put("Genomic source class [Type]", new LOINCItem("48002-0", "Genomic source class [Type]"));
		codes.put("Human reference sequence assembly version", new LOINCItem("62374-4", "Human reference sequence assembly version"));
		codes.put("Allelic read depth", new LOINCItem("82121-5", "Allelic read depth"));
		codes.put("Genomic structural variant copy number", new LOINCItem("82155-3", "Genomic structural variant copy number"));
		codes.put("Structural variant [Length]", new LOINCItem("81300-6", "Structural variant [Length]"));
		codes.put("Structural variant inner start and end", new LOINCItem("81302-2", "Structural variant inner start and end"));
		codes.put("DNA region name [Identifier]", new LOINCItem("47999-8", "DNA region name [Identifier]"));
		codes.put("Cytogenetic (chromosome) location", new LOINCItem("48001-2", "Cytogenetic (chromosome) location"));
		codes.put("Microsatellite instability [Interpretation] in Cancer specimen Qualitative", new LOINCItem("81695-9", "Microsatellite instability [Interpretation] in Cancer specimen Qualitative"));
		codes.put("Structural variant [Type]", new LOINCItem("81289-1", "Structural variant [Type]"));
		codes.put("Description of ranges of DNA sequences examined", new LOINCItem("81293-3", "Description of ranges of DNA sequences examined"));
		codes.put("Genetic variant assessment", new LOINCItem("69548-6", "Genetic variant assessment"));
		codes.put("DNA sequence variation display name [Text] Narrative", new LOINCItem("47998-0", "DNA sequence variation display name [Text] Narrative"));
		codes.put("Discrete genetic variant", new LOINCItem("81252-9", "Discrete genetic variant"));
		
		
		codes.put("Therapy count", new LOINCItem("THERAPYCOUNT", "Therapy count", "")); //no LOINC code?
		
		codes.put("THERAPYGENE", new LOINCItem("THERAPYGENE", "Therapy ", ": Gene", "")); //no LOINC code?
		codes.put("THERAPYVARIANT", new LOINCItem("THERAPYVARIANT", "Therapy ", ": Variant", "")); //no LOINC code?
		codes.put("THERAPYAGENT", new LOINCItem("THERAPYAGENT", "Therapy ", ": Agent", "")); //no LOINC code?
		codes.put("THERAPYLEVEL", new LOINCItem("THERAPYLEVEL", "Therapy ", ": Level", "")); //no LOINC code?
		codes.put("THERAPYINDICATION", new LOINCItem("THERAPYINDICATION", "Therapy ", ": Indication", "")); //no LOINC code?
//		codes.put("THERAPYPUBMEDID", new LOINCItem("THERAPYPUBMEDID", "Therapy ", ": Evidence ID", "")); //no LOINC code?
		
		codes.put("Trial count", new LOINCItem("TRIALCOUNT", "Trial count", "")); //no LOINC code?
		codes.put("TRIALNCTID", new LOINCItem("TRIALNCTID", "Trial ", ": Clinical Trial NCT ID", "")); //no LOINC code?
		codes.put("TRIALMATCHES", new LOINCItem("TRIALMATCHES", "Trial ", ": Matched criteria", "")); //no LOINC code?
		codes.put("TRIALTITLE", new LOINCItem("TRIALTITLE", "Trial ", ": Title", "")); //no LOINC code?
		
		//Possible NCTID: https://loinc.org/82786-5/
		//https://search.loinc.org/searchLOINC/search.zul?query=CLINTRIAL
		
		codes.put("Citation [Bibliographic Citation] in Reference lab test Narrative", new LOINCItem("75608-0", "Citation [Bibliographic Citation] in Reference lab test Narrative"));
		
		codes.put("Fused Genes", new LOINCItem("Fused Genes", "", "")); //no LOINC code?
		
		codes.put("LINEBREAK", new LOINCItem("LINEBREAK", "", "")); //no LOINC code?
	}

	public static LOINCItem getCode(String name) {
		return codes.get(name);
	}
}


