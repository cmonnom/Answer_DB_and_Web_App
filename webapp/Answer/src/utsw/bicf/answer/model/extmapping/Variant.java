package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.reporting.parse.AnnotationRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {
	
	//keep the name of fields in the JSON sent by MangoDB static
	//so they can all be centralized here
	public static final String FIELD_CHROM = "chrom";
	public static final String FIELD_GENE_NAME = "geneName";
	public static final String FIELD_TUMOR_ALT_FREQUENCY = "tumorAltFrequency";
	public static final String FIELD_TUMOR_TOTAL_DEPTH = "tumorTotalDepth";
	public static final String FIELD_NORMAL_ALT_FREQUENCY = "normalAltFrequency";
	public static final String FIELD_NORMAL_TOTAL_DEPTH = "normalTotalDepth";
	public static final String FIELD_RNA_ALT_FREQUENCY = "rnaAltFrequency";
	public static final String FIELD_RNA_TOTAL_DEPTH = "rnaTotalDepth";
	public static final String FIELD_EFFECTS = "effects";
	public static final String FIELD_ANNOTATIONS = "annotations"; //TODO
	public static final String FIELD_FILTERS = "filters";
	public static final String FIELD_EXAC_ALLELE_FREQUENCY = "exacAlleleFrequency";
	public static final String FIELD_GNOMAD_ALLELE_FREQUENCY = "gnomadPopmaxAlleleFrequency";
	public static final String FIELD_NUM_CASES_SEEN = "numCasesSeen";
	
	//Some values like filter pass/fail need to be translated into boolean
	//keep the values expected in the JSON string here
	public static final String VALUE_PASS = "PASS";
	public static final String VALUE_FAIL = "FailedQC";
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MangoDBId mangoDBId;
	String chrom;
	String geneName;
	List<String> effects;
	String notation;
	Float tumorAltFrequency;
	Integer tumorAltDepth;
	Integer tumorTotalDepth;
	Float normalAltFrequency;
	Integer normalAltDepth;
	Integer normalTotalDepth;
	Float rnaAltFrequency;
	Integer rnaAltDepth;
	Integer rnaTotalDepth;
	Integer pos;
	List<String> callSet;
	String type;
	List<Integer> cosmicPatients;
	List<String> ids; //list of external database ids (dbsnp, cosmic, etc)
	String alt;
	List<String> filters; //list of filers
	Boolean selected;
	Integer numCasesSeen;
	Float exacAlleleFrequency;
	String somaticStatus;
	Float gnomadPopmaxAlleleFrequency;
	Boolean repeat;
	Boolean inconsistent;
	
	List<VCFAnnotation> vcfAnnotations;
	AnnotationRow mdaAnnotation;
	ReferenceVariant referenceVariant;
	Boolean mdaAnnotated;
	Boolean utswAnnotated;
	
	
	public Variant() {
		
	}


	public String getChrom() {
		return chrom;
	}


	public void setChrom(String chrom) {
		this.chrom = chrom;
	}


	public String getGeneName() {
		return geneName;
	}


	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}


	public List<String> getEffects() {
		return effects;
	}


	public void setEffect(List<String> effects) {
		this.effects = effects;
	}


	public String getNotation() {
		return notation;
	}


	public void setNotation(String notation) {
		this.notation = notation;
	}


	public Float getTumorAltFrequency() {
		return tumorAltFrequency;
	}


	public void setTumorAltFrequency(Float tumorAltFrequency) {
		this.tumorAltFrequency = tumorAltFrequency;
	}


	public Integer getPos() {
		return pos;
	}


	public void setPos(Integer pos) {
		this.pos = pos;
	}


	public Integer getTumorAltDepth() {
		return tumorAltDepth;
	}


	public void setTumorAltDepth(Integer tumorAltDepth) {
		this.tumorAltDepth = tumorAltDepth;
	}


	public List<String> getCallSet() {
		return callSet;
	}


	public void setCallSet(List<String> callSet) {
		this.callSet = callSet;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public List<Integer> getCosmicPatients() {
		return cosmicPatients;
	}


	public void setCosmicPatients(List<Integer> cosmicPatients) {
		this.cosmicPatients = cosmicPatients;
	}


	public List<String> getIds() {
		return ids;
	}


	public void setIds(List<String> ids) {
		this.ids = ids;
	}


	public String getAlt() {
		return alt;
	}


	public void setAlt(String alt) {
		this.alt = alt;
	}


	public List<String> getFilters() {
		return filters;
	}


	public void setFilter(List<String> filters) {
		this.filters = filters;
	}


	public Integer getTumorTotalDepth() {
		return tumorTotalDepth;
	}


	public Float getNormalAltFrequency() {
		return normalAltFrequency;
	}


	public Integer getNormalAltDepth() {
		return normalAltDepth;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public Float getRnaAltFrequency() {
		return rnaAltFrequency;
	}


	public Integer getRnaAltDepth() {
		return rnaAltDepth;
	}


	public Integer getRnaTotalDepth() {
		return rnaTotalDepth;
	}


	public List<VCFAnnotation> getVcfAnnotations() {
		return vcfAnnotations;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}


	public AnnotationRow getMdaAnnotation() {
		return mdaAnnotation;
	}


	public Boolean getSelected() {
		return selected;
	}


	public ReferenceVariant getReferenceVariant() {
		return referenceVariant;
	}


	public Boolean getMdaAnnotated() {
		return mdaAnnotated;
	}


	public Boolean getUtswAnnotated() {
		return utswAnnotated;
	}


	public static String getFieldChrom() {
		return FIELD_CHROM;
	}


	public static String getFieldGeneName() {
		return FIELD_GENE_NAME;
	}


	public static String getFieldTumorAltFrequency() {
		return FIELD_TUMOR_ALT_FREQUENCY;
	}


	public static String getFieldTumorTotalDepth() {
		return FIELD_TUMOR_TOTAL_DEPTH;
	}


	public static String getFieldNormalAltFrequency() {
		return FIELD_NORMAL_ALT_FREQUENCY;
	}


	public static String getFieldNormalTotalDepth() {
		return FIELD_NORMAL_TOTAL_DEPTH;
	}


	public static String getFieldRnaAltFrequency() {
		return FIELD_RNA_ALT_FREQUENCY;
	}


	public static String getFieldRnaTotalDepth() {
		return FIELD_RNA_TOTAL_DEPTH;
	}


	public static String getFieldEffects() {
		return FIELD_EFFECTS;
	}


	public static String getFieldAnnotations() {
		return FIELD_ANNOTATIONS;
	}


	public static String getFieldFilters() {
		return FIELD_FILTERS;
	}


	public static String getValuePass() {
		return VALUE_PASS;
	}


	public static String getValueFail() {
		return VALUE_FAIL;
	}


	public Integer getNumCasesSeen() {
		return numCasesSeen;
	}


	public Float getExacAlleleFrequency() {
		return exacAlleleFrequency;
	}


	public String getSomaticStatus() {
		return somaticStatus;
	}


	public Float getGnomadPopmaxAlleleFrequency() {
		return gnomadPopmaxAlleleFrequency;
	}


	public Boolean getRepeat() {
		return repeat;
	}


	public Boolean getInconsistent() {
		return inconsistent;
	}


}
