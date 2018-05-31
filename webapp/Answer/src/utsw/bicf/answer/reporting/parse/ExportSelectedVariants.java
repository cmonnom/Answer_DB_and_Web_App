package utsw.bicf.answer.reporting.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;

public class ExportSelectedVariants {
	
	private static final List<String> HEADERS = new ArrayList<String>();
	static {
		HEADERS.add("Case ID");
			HEADERS.add("LociGRCh38");
			HEADERS.add("ID");
			HEADERS.add("Gene");
			HEADERS.add("AminoAcid");
			HEADERS.add("Effect");
			HEADERS.add("Ref");
			HEADERS.add("Alt");
			HEADERS.add("Tumor DNA AF");
			HEADERS.add("Tumor DNA Depth");
			HEADERS.add("Normal DNA AF");
			HEADERS.add("Normal DNA Depth");
			HEADERS.add("Tumor RNA AF");
			HEADERS.add("Tumor RNA Depth");
			HEADERS.add("Classification");
			HEADERS.add("Tier");
			HEADERS.add("Comments");
			HEADERS.add("Drugs");
			HEADERS.add("Clinical trials");
			HEADERS.add("PMID");
			HEADERS.add("gnomAD (MAF %, hom)");
			HEADERS.add("ExAC (MAF %, hom)");
			HEADERS.add("ClinVar");
			HEADERS.add("OncoKB");
			HEADERS.add("MCG");
			HEADERS.add("CIVIC");
			HEADERS.add("JAX CKB");
	}
	
	List<Variant> selectedVariants;
	OrderCase detailedCase;
	String csv;

	public ExportSelectedVariants(OrderCase detailedCase, List<Variant> selectedVariants) {
		this.detailedCase = detailedCase;
		this.selectedVariants = selectedVariants;
	}
	
	public String createCSV() {
		StringBuilder csvContent = new StringBuilder();
		//write the headers
		csvContent.append(HEADERS.stream().collect(Collectors.joining(","))).append("\n");
		
		for (Variant v : selectedVariants) {
			
			//TODO finish the row
			
			if (v.getUtswAnnotated() != null && v.getUtswAnnotated()) {
				List<Annotation> annotations = v.getReferenceVariant().getUtswAnnotations();
				for (Annotation a : annotations) {
					List<String> items = createVariantItemsBeforeAnnotation(v); //duplicate lines for each comment
					items.add(a.getClassification());
					items.add(a.getTier());
					items.add(a.getCategory() + ": " + a.getText());
					items.add(a.getPmids() != null ? a.getPmids().stream()
							.map(id -> "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id)
							.collect(Collectors.joining(";")) : "");
					items.add(a.getNctids() != null ? a.getNctids().stream()
							.map(id -> "https://clinicaltrials.gov/ct2/show/" + id)
							.collect(Collectors.joining(";")) : "");
					items.addAll(createVariantItemsAfterAnnotation(v));
					//write the row
					csvContent.append(createCSVRow(items)).append("\n");
				}
				
			}
			else { //a row with empty annotations
				List<String> items = createVariantItemsBeforeAnnotation(v);
				items.add(""); //Classification
				items.add(""); //Tier
				items.add(""); //Category
				items.add(""); // PMIDs
				items.addAll(createVariantItemsAfterAnnotation(v));
				//write the row
				csvContent.append(createCSVRow(items)).append("\n");
			}
			
			
		
		}
		
		return csvContent.toString();
	}
	
	private String createCSVRow(List<String> items) {
		return items.stream().collect(Collectors.joining(","));
	}
	
	private List<String> createVariantItemsBeforeAnnotation(Variant v) {
		List<String> items = new ArrayList<String>();
		items.add(detailedCase.getCaseId());
		items.add(v.getChrom() + ":" + v.getPos());
		items.add(v.getIds().stream().collect(Collectors.joining(";")));
		items.add(v.getGeneName());
		items.add(v.getNotation());
		items.add(v.getEffects().stream().collect(Collectors.joining(";")));
		items.add(v.getReferenceVariant().getReference());
		items.add(v.getReferenceVariant().getAlt());
		items.add(v.getTumorAltFrequency());
		items.add(v.getTumorTotalDepth() + "");
		items.add(v.getNormalAltFrequency());
		items.add(v.getNormalTotalDepth() + "");
		items.add(v.getRnaAltFrequency());
		items.add(v.getRnaTotalDepth() + "");
		return items;
	}
	
	private List<String> createVariantItemsAfterAnnotation(Variant v) {
		List<String> items = new ArrayList<String>();
		items.add(""); //gnomAD
		items.add(""); //ExAC
		items.add(""); //ClinVar
		items.add(""); //OncoKB
		items.add(""); //CIVIC
		items.add(""); //JAX CKB
		
		return items;
	}

}
