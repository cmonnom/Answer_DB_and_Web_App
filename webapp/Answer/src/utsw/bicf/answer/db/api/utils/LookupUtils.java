package utsw.bicf.answer.db.api.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utsw.bicf.answer.model.extmapping.AminoAcid;

public class LookupUtils {
	
	private static final Map<String, AminoAcid> PROTEIN_3_TO_1_MAP = new HashMap<String, AminoAcid>();
	public static final Map<String, AminoAcid> PROTEIN_1_TO_3_MAP = new HashMap<String, AminoAcid>();
	private static final AminoAcid ALA = new AminoAcid("alanine", "ALA", "A");
	private static final AminoAcid ARGININE = new AminoAcid("arginine", "ARG","R");
	private static final AminoAcid ASPARAGINE = new AminoAcid("asparagine", "ASN","N");
	private static final AminoAcid ASPARTIC_ACID = new AminoAcid("aspartic acid", "ASP","D");
	private static final AminoAcid ASPARAGINE_OR_ASPARTIC_ACID = new AminoAcid("asparagine or aspartic acid", "ASX","B");
	private static final AminoAcid CYSTEINE = new AminoAcid("cysteine", "CYS","C");
	private static final AminoAcid GLUTAMIC_ACID = new AminoAcid("glutamic acid", "GLU","E");
	private static final AminoAcid GLUTAMINE = new AminoAcid("glutamine", "GLN","Q");
	private static final AminoAcid GLUTAMINE_OR_GLUTAMIC_ACID = new AminoAcid("glutamine or glutamic acid", "GLX","Z");
	private static final AminoAcid GLYCINE = new AminoAcid("glycine", "GLY","G");
	private static final AminoAcid HISTIDINE = new AminoAcid("histidine", "HIS","H");
	private static final AminoAcid ISOLEUCINE = new AminoAcid("isoleucine", "ILE","I");
	private static final AminoAcid LEUCINE = new AminoAcid("leucine", "LEU","L");
	private static final AminoAcid LYSINE = new AminoAcid("lysine", "LYS","K");
	private static final AminoAcid METHIONINE = new AminoAcid("methionine", "MET","M");
	private static final AminoAcid PHENYLALANINE = new AminoAcid("phenylalanine", "PHE","F");
	private static final AminoAcid PROLINE = new AminoAcid("proline", "PRO","P");
	private static final AminoAcid SERINE = new AminoAcid("serine", "SER","S");
	private static final AminoAcid THREONINE = new AminoAcid("threonine", "THR","T");
	private static final AminoAcid TRYPTOPHAN = new AminoAcid("tryptophan", "TRP","W");
	private static final AminoAcid TYROSINE = new AminoAcid("tyrosine", "TYR","Y");
	private static final AminoAcid VALINE = new AminoAcid("valine", "VAL","V");


	static {
		PROTEIN_3_TO_1_MAP.put(ALA.getThreeLetter(), ALA);
		PROTEIN_3_TO_1_MAP.put(ARGININE.getThreeLetter(), ARGININE);
		PROTEIN_3_TO_1_MAP.put(ASPARAGINE.getThreeLetter(), ASPARAGINE);
		PROTEIN_3_TO_1_MAP.put(ASPARTIC_ACID.getThreeLetter(), ASPARTIC_ACID);
		PROTEIN_3_TO_1_MAP.put(ASPARAGINE_OR_ASPARTIC_ACID.getThreeLetter(), ASPARAGINE_OR_ASPARTIC_ACID);
		PROTEIN_3_TO_1_MAP.put(CYSTEINE.getThreeLetter(), CYSTEINE);
		PROTEIN_3_TO_1_MAP.put(GLUTAMIC_ACID.getThreeLetter(), GLUTAMIC_ACID);
		PROTEIN_3_TO_1_MAP.put(GLUTAMINE.getThreeLetter(), GLUTAMINE);
		PROTEIN_3_TO_1_MAP.put(GLUTAMINE_OR_GLUTAMIC_ACID.getThreeLetter(), GLUTAMINE_OR_GLUTAMIC_ACID);
		PROTEIN_3_TO_1_MAP.put(GLYCINE.getThreeLetter(), GLYCINE);
		PROTEIN_3_TO_1_MAP.put(HISTIDINE.getThreeLetter(), HISTIDINE);
		PROTEIN_3_TO_1_MAP.put(ISOLEUCINE.getThreeLetter(), ISOLEUCINE);
		PROTEIN_3_TO_1_MAP.put(LEUCINE.getThreeLetter(), LEUCINE);
		PROTEIN_3_TO_1_MAP.put(METHIONINE.getThreeLetter(), METHIONINE);
		PROTEIN_3_TO_1_MAP.put(PHENYLALANINE.getThreeLetter(), PHENYLALANINE);
		PROTEIN_3_TO_1_MAP.put(PROLINE.getThreeLetter(), PROLINE);
		PROTEIN_3_TO_1_MAP.put(SERINE.getThreeLetter(), SERINE);
		PROTEIN_3_TO_1_MAP.put(THREONINE.getThreeLetter(), THREONINE);
		PROTEIN_3_TO_1_MAP.put(TRYPTOPHAN.getThreeLetter(), TRYPTOPHAN);
		PROTEIN_3_TO_1_MAP.put(TYROSINE.getThreeLetter(), TYROSINE);
		PROTEIN_3_TO_1_MAP.put(VALINE.getThreeLetter(), VALINE);
	}
	
	static {
		PROTEIN_1_TO_3_MAP.put(ALA.getOneLetter(), ALA);
		PROTEIN_1_TO_3_MAP.put(ARGININE.getOneLetter(), ARGININE);
		PROTEIN_1_TO_3_MAP.put(ASPARAGINE.getOneLetter(), ASPARAGINE);
		PROTEIN_1_TO_3_MAP.put(ASPARTIC_ACID.getOneLetter(), ASPARTIC_ACID);
		PROTEIN_1_TO_3_MAP.put(ASPARAGINE_OR_ASPARTIC_ACID.getOneLetter(), ASPARAGINE_OR_ASPARTIC_ACID);
		PROTEIN_1_TO_3_MAP.put(CYSTEINE.getOneLetter(), CYSTEINE);
		PROTEIN_1_TO_3_MAP.put(GLUTAMIC_ACID.getOneLetter(), GLUTAMIC_ACID);
		PROTEIN_1_TO_3_MAP.put(GLUTAMINE.getOneLetter(), GLUTAMINE);
		PROTEIN_1_TO_3_MAP.put(GLUTAMINE_OR_GLUTAMIC_ACID.getOneLetter(), GLUTAMINE_OR_GLUTAMIC_ACID);
		PROTEIN_1_TO_3_MAP.put(GLYCINE.getOneLetter(), GLYCINE);
		PROTEIN_1_TO_3_MAP.put(HISTIDINE.getOneLetter(), HISTIDINE);
		PROTEIN_1_TO_3_MAP.put(ISOLEUCINE.getOneLetter(), ISOLEUCINE);
		PROTEIN_1_TO_3_MAP.put(LEUCINE.getOneLetter(), LEUCINE);
		PROTEIN_1_TO_3_MAP.put(METHIONINE.getOneLetter(), METHIONINE);
		PROTEIN_1_TO_3_MAP.put(PHENYLALANINE.getOneLetter(), PHENYLALANINE);
		PROTEIN_1_TO_3_MAP.put(PROLINE.getOneLetter(), PROLINE);
		PROTEIN_1_TO_3_MAP.put(SERINE.getOneLetter(), SERINE);
		PROTEIN_1_TO_3_MAP.put(THREONINE.getOneLetter(), THREONINE);
		PROTEIN_1_TO_3_MAP.put(TRYPTOPHAN.getOneLetter(), TRYPTOPHAN);
		PROTEIN_1_TO_3_MAP.put(TYROSINE.getOneLetter(), TYROSINE);
		PROTEIN_1_TO_3_MAP.put(VALINE.getOneLetter(), VALINE);
	}
	
	private static final Pattern AA_PATTERN = Pattern.compile("p.([A-Za-z]{3})([0-9]+)([A-Za-z]{3})");
	
	public static String aminoAcid3To1(String threeLetterProteinChange) {
		Matcher matcher = AA_PATTERN.matcher(threeLetterProteinChange.trim());
		while (matcher.find()) {
			AminoAcid orig = PROTEIN_3_TO_1_MAP.get(matcher.group(1).toUpperCase());
			String pos = matcher.group(2);
			AminoAcid change = PROTEIN_3_TO_1_MAP.get(matcher.group(3).toUpperCase());
			return orig.getOneLetter() + pos + change.getOneLetter();
		}
		return threeLetterProteinChange.replace("p.", "");
	}
	
	public static String[] aminoAcid3To1Array(String threeLetterProteinChange) {
		Matcher matcher = AA_PATTERN.matcher(threeLetterProteinChange.trim());
		while (matcher.find()) {
			AminoAcid orig = PROTEIN_3_TO_1_MAP.get(matcher.group(1).toUpperCase());
			String pos = matcher.group(2);
			AminoAcid change = PROTEIN_3_TO_1_MAP.get(matcher.group(3).toUpperCase());
			return new String[] {orig.getOneLetter(), pos, change.getOneLetter()};
		}
		String cleaned = threeLetterProteinChange.replace("p.", "");
		String orig = cleaned.substring(0,1);
		String change = cleaned.substring(cleaned.length() - 1, cleaned.length());
		String pos = cleaned.replaceAll("[A-Za-z]+", "");
		return new String[] {orig, pos, change};
	}

}
