package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.Build;
import utsw.bicf.answer.model.extmapping.Caller;
import utsw.bicf.answer.model.extmapping.Variant;

public class SNPIndelVariantRow {
	
	String oid; //variant id in MongoDB
	String chrom;
	Integer pos;
	String chromPos;
	String geneName;
	String geneVariant;
	String effects;
	String notation;
	String tumorAltFrequency;
	Integer tumorAltDepth;
	Integer tumorTotalDepth;
	String normalAltFrequency;
	Integer normalAltDepth;
	Integer normalTotalDepth;
	String rnaAltFrequency;
	Integer rnaAltDepth;
	Integer rnaTotalDepth;
	List<Caller> callSet;
	String type;
	List<Integer> cosmicPatients;
	Integer maxCosmicPatients;
	List<String> externalIds; //list of external database ids (dbsnp, cosmic, etc)
	String alt;
	String reference;
	List<String> filters; //list of filers
	FlagValue iconFlags;
	Boolean isSelected;
	Boolean mdaAnnotated;
	Boolean utswAnnotated;
	Integer numCasesSeen;
	String numCasesSeenFormatted;
	String exacAlleleFrequency;
	String somaticStatus;
	String gnomadPopmaxAlleleFrequency;
	List<Integer> gnomadHomozygotes;
	Boolean isRepeat;
	Boolean common;
	List<String> repeatTypes;
	Boolean callsetInconsistent;
	Map<String, Build> oldBuilds;
	List<Variant> relatedVariants;
	Boolean inCosmic;
	String oncokbGeneName;
	String oncokbVariantName;
	Boolean hasRelatedVariants;
	String impact;
	String rank;
	Boolean likelyArtifact;
	Boolean gnomadLcr;
	String gnomadHg19Variant;
	String deltaTumorNormal;
	String highestTier;
	
	Map<Integer, AnnotatorSelection> selectionPerAnnotator;
	
	Boolean includeInFilter;
	
	public SNPIndelVariantRow() {
		super();
	}

	public SNPIndelVariantRow(Variant variant, List<ReportGroupForDisplay> reportGroups, Integer totalCases, Map<Integer, AnnotatorSelection> selectionPerAnnotator) {
		this.oid = variant.getMongoDBId().getOid();
		this.chrom = TypeUtils.formatChromosome(variant.getChrom());
		this.pos = variant.getPos();
		this.chromPos = this.chrom + ":" + variant.getPos();
		this.geneName = variant.getGeneName();
		this.geneVariant = variant.getGeneName() + " " + variant.getNotation();
		this.effects = variant.getEffects() != null ? variant.getEffects().stream().collect(Collectors.joining("<br/>")) : null;
		if (variant.getNotation() != null) { 
			int length = variant.getNotation().length();
			if (length > 100 && alt != null && alt.equals("DUP")) { //notation is too long
				this.notation = variant.getNotation().substring(0, Math.min(length, 50)) + "...";
			}
		}
		else {
			this.notation = variant.getNotation();
		}
		if (variant.getTumorAltFrequency() != null) {
			if (variant.getTumorAltFrequency() > 0 && variant.getTumorAltFrequency() < 0.0001) {
				this.tumorAltFrequency = "< 0.01";
			}
			else {
				this.tumorAltFrequency = String.format("%.2f", variant.getTumorAltFrequency() * 100);
			}
		}
		this.tumorAltDepth = variant.getTumorAltDepth();
		this.tumorTotalDepth = variant.getTumorTotalDepth();
		if (variant.getNormalAltFrequency() != null) {
			if (variant.getNormalAltFrequency() > 0 && variant.getNormalAltFrequency() < 0.0001) {
				this.normalAltFrequency = "< 0.01";
			}
			else {
				this.normalAltFrequency = String.format("%.2f", variant.getNormalAltFrequency() * 100);
			}
		}
		if (variant.getNormalAltFrequency() != null && variant.getTumorAltFrequency() != null) {
			float delta = (variant.getTumorAltFrequency() - variant.getNormalAltFrequency());
			if (delta <= 0) {
				this.deltaTumorNormal = "0";
			}
			else if (delta > 0 && delta < 0.0001) {
				this.deltaTumorNormal = "< 0.01";
			}
			else {
				this.deltaTumorNormal = String.format("%.2f", delta * 100);
			}
		}
		this.normalAltDepth = variant.getNormalAltDepth();
		this.normalTotalDepth = variant.getNormalTotalDepth();
		if (variant.getRnaAltFrequency() != null) {
			if (variant.getRnaAltFrequency() > 0 && variant.getRnaAltFrequency() < 0.0001) {
				this.rnaAltFrequency = "< 0.01";
			}
			else {
				this.rnaAltFrequency = String.format("%.2f", variant.getRnaAltFrequency() * 100);
			}
		}
		this.rnaAltDepth = variant.getRnaAltDepth();
		this.rnaTotalDepth = variant.getRnaTotalDepth();
		this.callSet = variant.getCallSet();
		this.type = variant.getType();
		this.cosmicPatients = variant.getCosmicPatients();
//		if (this.cosmicPatients != null && this.cosmicPatients.size() > 0) {
//			this.maxCosmicPatients = this.cosmicPatients.stream().mapToInt(Integer::intValue).max().getAsInt();
//		}
//		else {
//			this.maxCosmicPatients = 0;
//		}
		this.maxCosmicPatients = variant.getMaxCosmicPatients();
		this.externalIds = variant.getIds();
		this.alt = variant.getAlt();
		this.reference = variant.getReference();
		this.filters = variant.getFilters();
		this.isSelected = variant.getSelected();
		this.mdaAnnotated = variant.getMdaAnnotated();
		this.utswAnnotated = variant.getUtswAnnotated();
		this.numCasesSeen = variant.getNumCasesSeen();
		this.numCasesSeenFormatted = this.numCasesSeen + "/" + totalCases;
		if (variant.getExacAlleleFrequency() != null) {
			if (variant.getExacAlleleFrequency() > 0 && variant.getExacAlleleFrequency() < 0.0001) {
				this.exacAlleleFrequency = "< 0.01";
			}
			else {
				this.exacAlleleFrequency = String.format("%.2f", variant.getExacAlleleFrequency() * 100);
			}
		}
		this.somaticStatus = variant.getSomaticStatus();
		if (variant.getGnomadPopmaxAlleleFrequency() != null) {
			if (variant.getGnomadPopmaxAlleleFrequency() > 0 && variant.getGnomadPopmaxAlleleFrequency() < 0.0001) {
				this.gnomadPopmaxAlleleFrequency = "< 0.01";
			}
			else {
				this.gnomadPopmaxAlleleFrequency = String.format("%.2f", variant.getGnomadPopmaxAlleleFrequency() * 100);
			}
		}
		common = gnomadPopmaxAlleleFrequency != null && variant.getGnomadPopmaxAlleleFrequency() > 0.01;
		this.gnomadHomozygotes = variant.getGnomadHomozygotes();
		isRepeat = variant.getIsRepeat();
		callsetInconsistent = variant.getCallsetInconsistent();
		repeatTypes = variant.getRepeatTypes();
		this.oldBuilds = variant.getOldBuilds();
		this.relatedVariants = variant.getRelatedVariants();
		this.hasRelatedVariants = variant.getHasRelatedVariants();
		this.oncokbGeneName = variant.getOncokbGeneName();
		this.oncokbVariantName = variant.getOncokbVariantName();
		this.inCosmic = variant.getInCosmic();
		this.impact = variant.getImpact();
		this.rank = variant.getRank();
		this.likelyArtifact = variant.getLikelyArtifact();
		this.gnomadLcr = variant.getGnomadLcr();
		this.gnomadHg19Variant = variant.getGnomadHg19Variant();
		
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		boolean failed = filters.contains(Variant.VALUE_FAIL);
		if (failed) {
			String failedReasons = filters.stream()
			.filter(f -> !Variant.VALUE_FAIL.equals(f))
			.map(f -> TypeUtils.splitCamelCaseString(f))
			.collect(Collectors.joining(", "));
			icons.add(new VuetifyIcon("cancel", "red", "Failed QC: " + failedReasons));
		}
		else {
			icons.add(new VuetifyIcon("check_circle", "green", "Passed QC"));
		}
		if (mdaAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted", "green", "MDA Annotations"));
		}
//		else {
//			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No MDA Annotations"));
//		}
//		if (utswAnnotated != null && utswAnnotated) {
//			icons.add(new VuetifyIcon("mdi-message-bulleted", "indigo darken-4", "UTSW Annotations"));
//		}
		if (utswAnnotated == null || !utswAnnotated) {
			icons.add(new VuetifyIcon("mdi-message-bulleted-off", "grey", "No UTSW Annotations"));
		}
		iconFlags = new FlagValue(icons);
		
		this.selectionPerAnnotator = selectionPerAnnotator;
		
		this.highestTier = variant.getHighestTier();
		
	}





///**
// * Determine if the given geneName is in any existing group of genes
// * that should be reported
// * @param geneName
// * @param reportGroups
// * @return
// */
//	private boolean neededInReport(String geneName, List<ReportGroupForDisplay> reportGroups) {
//		for (ReportGroupForDisplay group : reportGroups) {
//			for (String gene : group.getGenesToReport()) {
//				if (gene.equals(geneName)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}





	public String getEffects() {
		return effects;
	}


	public String getNotation() {
		return notation;
	}


	public String getTumorAltFrequency() {
		return tumorAltFrequency;
	}


	public String getChromPos() {
		return chromPos;
	}

	public Integer getTumorAltDepth() {
		return tumorAltDepth;
	}


	public String getGeneVariant() {
		return geneVariant;
	}


	public String getChrom() {
		return chrom;
	}


	public Integer getPos() {
		return pos;
	}


	public List<Caller> getCallSet() {
		return callSet;
	}


	public String getType() {
		return type;
	}


	public List<Integer> getCosmicPatients() {
		return cosmicPatients;
	}


	public List<String> getExternalIds() {
		return externalIds;
	}


	public String getAlt() {
		return alt;
	}


	public List<String> getFilters() {
		return filters;
	}


	public FlagValue getIconFlags() {
		return iconFlags;
	}


	public Integer getTumorTotalDepth() {
		return tumorTotalDepth;
	}


	public String getNormalAltFrequency() {
		return normalAltFrequency;
	}


	public Integer getNormalAltDepth() {
		return normalAltDepth;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public String getRnaAltFrequency() {
		return rnaAltFrequency;
	}


	public Integer getRnaAltDepth() {
		return rnaAltDepth;
	}


	public Integer getRnaTotalDepth() {
		return rnaTotalDepth;
	}


	public String getGeneName() {
		return geneName;
	}


	public String getOid() {
		return oid;
	}


	public Boolean getIsSelected() {
		return isSelected;
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





	public String getExacAlleleFrequency() {
		return exacAlleleFrequency;
	}





	public String getSomaticStatus() {
		return somaticStatus;
	}





	public String getGnomadPopmaxAlleleFrequency() {
		return gnomadPopmaxAlleleFrequency;
	}










	public Boolean getCommon() {
		return common;
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





	public Boolean getIsRepeat() {
		return isRepeat;
	}





	public List<String> getRepeatTypes() {
		return repeatTypes;
	}





	public Boolean getCallsetInconsistent() {
		return callsetInconsistent;
	}





	public String getImpact() {
		return impact;
	}





	public String getNumCasesSeenFormatted() {
		return numCasesSeenFormatted;
	}





	public String getRank() {
		return rank;
	}


	public List<Integer> getGnomadHomozygotes() {
		return gnomadHomozygotes;
	}





	public Integer getMaxCosmicPatients() {
		return maxCosmicPatients;
	}

	public Boolean getGnomadLcr() {
		return gnomadLcr;
	}

	public Boolean getLikelyArtifact() {
		return likelyArtifact;
	}





	public String getGnomadHg19Variant() {
		return gnomadHg19Variant;
	}





	public Map<Integer, AnnotatorSelection> getSelectionPerAnnotator() {
		return selectionPerAnnotator;
	}

	public String getDeltaTumorNormal() {
		return deltaTumorNormal;
	}

	public String getHighestTier() {
		return highestTier;
	}

	public Boolean getIncludeInFilter() {
		return includeInFilter;
	}





}
