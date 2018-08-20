package utsw.bicf.answer.model.extmapping;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.reporting.parse.AnnotationRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {
	
	//keep the name of fields in the JSON sent by MongoDB static
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
	public static final String FIELD_IN_COSMIC = "inCosmic";
	public static final String FIELD_OLD_BUILDS = "oldBuilds";
	
	//Some values like filter pass/fail need to be translated into boolean
	//keep the values expected in the JSON string here
	public static final String VALUE_PASS = "PASS";
	public static final String VALUE_FAIL = "FailedQC";
	public static final String FIELD_HAS_REPEATS = "isRepeat";
	public static final String FIELD_IMPACT = "impact";
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String chrom;
	String geneName;
	List<String> effects;
	String notation;
	Float tumorAltFrequency;
	String tumorAltFrequencyFormatted;
	Integer tumorAltDepth;
	Integer tumorTotalDepth;
	Float normalAltFrequency;
	String normalAltFrequencyFormatted;
	Integer normalAltDepth;
	Integer normalTotalDepth;
	Float rnaAltFrequency;
	String rnaAltFrequencyFormatted;
	Integer rnaAltDepth;
	Integer rnaTotalDepth;
	Integer pos;
	List<Caller> callSet;
	String type;
	List<Integer> cosmicPatients;
	List<String> ids; //list of external database ids (dbsnp, cosmic, etc)
	String alt;
	String reference;
	List<String> filters; //list of filers
	Boolean selected;
	Integer numCasesSeen;
	Float exacAlleleFrequency;
	String exacAlleleFrequencyFormatted;
	String somaticStatus;
	Float gnomadPopmaxAlleleFrequency;
	String gnomadPopmaxAlleleFrequencyFormatted;
	Boolean isRepeat;
	List<String> repeatTypes;
	Boolean callsetInconsistent;
	Boolean inCosmic;
	
	List<VCFAnnotation> vcfAnnotations;
	AnnotationRow mdaAnnotation;
	ReferenceVariant referenceVariant;
	Boolean mdaAnnotated;
	Boolean utswAnnotated;
	Map<String, Build> oldBuilds;
	List<Variant> relatedVariants;
	
	String oncokbGeneName;
	String oncokbVariantName;
	Boolean hasRelatedVariants;
	
	String tier;
	
	Boolean isOncokbVariant;
	String impact;
	
	List<MongoDBId> annotationIdsForReporting;
	
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
		if (tumorAltFrequencyFormatted == null) {
			tumorAltFrequencyFormatted = tumorAltFrequency != null ? String.format("%.2f", tumorAltFrequency * 100) : null;
		}
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


	public List<Caller> getCallSet() {
		return callSet;
	}


	public void setCallSet(List<Caller> callSet) {
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
		if (normalAltFrequencyFormatted == null) {
			normalAltFrequencyFormatted = normalAltFrequency != null ? String.format("%.2f", normalAltFrequency * 100) : null;
		}
		return normalAltFrequency;
	}


	public Integer getNormalAltDepth() {
		return normalAltDepth;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public Float getRnaAltFrequency() {
		if (rnaAltFrequencyFormatted == null) {
			rnaAltFrequencyFormatted = rnaAltFrequency != null ? String.format("%.2f", rnaAltFrequency * 100) : null;
		}
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


	public MongoDBId getMongoDBId() {
		return mongoDBId;
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




	public Integer getNumCasesSeen() {
		return numCasesSeen;
	}


	public Float getExacAlleleFrequency() {
		if (exacAlleleFrequencyFormatted == null) {
			exacAlleleFrequencyFormatted = exacAlleleFrequency != null ? String.format("%.2f", exacAlleleFrequency * 100) : null;
		}
		return exacAlleleFrequency;
	}


	public String getSomaticStatus() {
		return somaticStatus;
	}


	public Float getGnomadPopmaxAlleleFrequency() {
		if (gnomadPopmaxAlleleFrequencyFormatted == null) {
			gnomadPopmaxAlleleFrequencyFormatted = gnomadPopmaxAlleleFrequency != null ? String.format("%.2f", gnomadPopmaxAlleleFrequency * 100) : null;
		}
		return gnomadPopmaxAlleleFrequency;
	}


	public Map<String, Build> getOldBuilds() {
		return oldBuilds;
	}


	public List<Variant> getRelatedVariants() {
		return relatedVariants;
	}


	public String getReference() {
		return reference;
	}


	public Boolean getInCosmic() {
		return inCosmic;
	}


	public String getOncokbGeneName() {
		return oncokbGeneName;
	}


	public String getOncokbVariantName() {
		return oncokbVariantName;
	}


	public Boolean getHasRelatedVariants() {
		return hasRelatedVariants;
	}


	public String getTier() {
		return tier;
	}


	public String getTumorAltFrequencyFormatted() {
		return tumorAltFrequencyFormatted;
	}


	public String getNormalAltFrequencyFormatted() {
		return normalAltFrequencyFormatted;
	}


	public String getRnaAltFrequencyFormatted() {
		return rnaAltFrequencyFormatted;
	}


	public String getExacAlleleFrequencyFormatted() {
		return exacAlleleFrequencyFormatted;
	}


	public String getGnomadPopmaxAlleleFrequencyFormatted() {
		return gnomadPopmaxAlleleFrequencyFormatted;
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


	public static String getFieldExacAlleleFrequency() {
		return FIELD_EXAC_ALLELE_FREQUENCY;
	}


	public static String getFieldGnomadAlleleFrequency() {
		return FIELD_GNOMAD_ALLELE_FREQUENCY;
	}


	public static String getFieldNumCasesSeen() {
		return FIELD_NUM_CASES_SEEN;
	}


	public static String getFieldInCosmic() {
		return FIELD_IN_COSMIC;
	}


	public static String getFieldOldBuilds() {
		return FIELD_OLD_BUILDS;
	}


	public static String getValuePass() {
		return VALUE_PASS;
	}


	public static String getValueFail() {
		return VALUE_FAIL;
	}


	public Boolean getIsRepeat() {
		return isRepeat;
	}


	public List<String> getRepeatTypes() {
		return repeatTypes;
	}


	public Boolean getCallsetInconsistent() {
		return callsetInconsistent;
	}


	public static String getFieldHasRepeats() {
		return FIELD_HAS_REPEATS;
	}


	public Boolean getIsOncokbVariant() {
		return isOncokbVariant;
	}


	public String getImpact() {
		return impact;
	}


	public List<MongoDBId> getAnnotationIdsForReporting() {
		return annotationIdsForReporting;
	}


	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}


	public void setEffects(List<String> effects) {
		this.effects = effects;
	}


	public void setTumorAltFrequencyFormatted(String tumorAltFrequencyFormatted) {
		this.tumorAltFrequencyFormatted = tumorAltFrequencyFormatted;
	}


	public void setTumorTotalDepth(Integer tumorTotalDepth) {
		this.tumorTotalDepth = tumorTotalDepth;
	}


	public void setNormalAltFrequency(Float normalAltFrequency) {
		this.normalAltFrequency = normalAltFrequency;
	}


	public void setNormalAltFrequencyFormatted(String normalAltFrequencyFormatted) {
		this.normalAltFrequencyFormatted = normalAltFrequencyFormatted;
	}


	public void setNormalAltDepth(Integer normalAltDepth) {
		this.normalAltDepth = normalAltDepth;
	}


	public void setNormalTotalDepth(Integer normalTotalDepth) {
		this.normalTotalDepth = normalTotalDepth;
	}


	public void setRnaAltFrequency(Float rnaAltFrequency) {
		this.rnaAltFrequency = rnaAltFrequency;
	}


	public void setRnaAltFrequencyFormatted(String rnaAltFrequencyFormatted) {
		this.rnaAltFrequencyFormatted = rnaAltFrequencyFormatted;
	}


	public void setRnaAltDepth(Integer rnaAltDepth) {
		this.rnaAltDepth = rnaAltDepth;
	}


	public void setRnaTotalDepth(Integer rnaTotalDepth) {
		this.rnaTotalDepth = rnaTotalDepth;
	}


	public void setReference(String reference) {
		this.reference = reference;
	}


	public void setFilters(List<String> filters) {
		this.filters = filters;
	}


	public void setSelected(Boolean selected) {
		this.selected = selected;
	}


	public void setNumCasesSeen(Integer numCasesSeen) {
		this.numCasesSeen = numCasesSeen;
	}


	public void setExacAlleleFrequency(Float exacAlleleFrequency) {
		this.exacAlleleFrequency = exacAlleleFrequency;
	}


	public void setExacAlleleFrequencyFormatted(String exacAlleleFrequencyFormatted) {
		this.exacAlleleFrequencyFormatted = exacAlleleFrequencyFormatted;
	}


	public void setSomaticStatus(String somaticStatus) {
		this.somaticStatus = somaticStatus;
	}


	public void setGnomadPopmaxAlleleFrequency(Float gnomadPopmaxAlleleFrequency) {
		this.gnomadPopmaxAlleleFrequency = gnomadPopmaxAlleleFrequency;
	}


	public void setGnomadPopmaxAlleleFrequencyFormatted(String gnomadPopmaxAlleleFrequencyFormatted) {
		this.gnomadPopmaxAlleleFrequencyFormatted = gnomadPopmaxAlleleFrequencyFormatted;
	}


	public void setIsRepeat(Boolean isRepeat) {
		this.isRepeat = isRepeat;
	}


	public void setRepeatTypes(List<String> repeatTypes) {
		this.repeatTypes = repeatTypes;
	}


	public void setCallsetInconsistent(Boolean callsetInconsistent) {
		this.callsetInconsistent = callsetInconsistent;
	}


	public void setInCosmic(Boolean inCosmic) {
		this.inCosmic = inCosmic;
	}


	public void setVcfAnnotations(List<VCFAnnotation> vcfAnnotations) {
		this.vcfAnnotations = vcfAnnotations;
	}


	public void setMdaAnnotation(AnnotationRow mdaAnnotation) {
		this.mdaAnnotation = mdaAnnotation;
	}


	public void setReferenceVariant(ReferenceVariant referenceVariant) {
		this.referenceVariant = referenceVariant;
	}


	public void setMdaAnnotated(Boolean mdaAnnotated) {
		this.mdaAnnotated = mdaAnnotated;
	}


	public void setUtswAnnotated(Boolean utswAnnotated) {
		this.utswAnnotated = utswAnnotated;
	}


	public void setOldBuilds(Map<String, Build> oldBuilds) {
		this.oldBuilds = oldBuilds;
	}


	public void setRelatedVariants(List<Variant> relatedVariants) {
		this.relatedVariants = relatedVariants;
	}


	public void setOncokbGeneName(String oncokbGeneName) {
		this.oncokbGeneName = oncokbGeneName;
	}


	public void setOncokbVariantName(String oncokbVariantName) {
		this.oncokbVariantName = oncokbVariantName;
	}


	public void setHasRelatedVariants(Boolean hasRelatedVariants) {
		this.hasRelatedVariants = hasRelatedVariants;
	}


	public void setTier(String tier) {
		this.tier = tier;
	}


	public void setIsOncokbVariant(Boolean isOncokbVariant) {
		this.isOncokbVariant = isOncokbVariant;
	}


	public void setImpact(String impact) {
		this.impact = impact;
	}


	public void setAnnotationIdsForReporting(List<MongoDBId> annotationIdsForReporting) {
		this.annotationIdsForReporting = annotationIdsForReporting;
	}





}
