package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.controller.serialization.SearchItemString;

public class Effects {
	
	List<SearchItemString> lof = new ArrayList<SearchItemString>();
	List<SearchItemString> coding = new ArrayList<SearchItemString>();
	String lofTitle = "LOF";
	String codingTitle = "Coding";
	@JsonIgnore
	Map<String, String> lookupTable = new HashMap<String, String>();
	
	
	
	public Effects() {
		this.createEffects();
	}

	private void createEffects() {
		lof.add(new SearchItemString("Exon Loss", "exon_loss_variant"));
		lof.add(new SearchItemString("Exon Duplication", "exon_duplication"));
		lof.add(new SearchItemString("Frameshift Variant", "frameshift_variant"));
		lof.add(new SearchItemString("Gene Fusion", "gene_fusion"));
		lof.add(new SearchItemString("Bidirectional Gene Fusion", "bidirectional_gene_fusion"));
		lof.add(new SearchItemString("Rearranged At DNA Level", "rearranged_at_DNA_level"));
		lof.add(new SearchItemString("Protein Protein Contact", "protein_protein_contact"));
		lof.add(new SearchItemString("Structural Interaction Variant", "structural_interaction_variant"));
		lof.add(new SearchItemString("Rare Amino Acid Variant", "rare_amino_acid_variant"));
		lof.add(new SearchItemString("Splice Acceptor Variant", "splice_acceptor_variant"));
		lof.add(new SearchItemString("Splice Donor Variant", "splice_donor_variant"));
		lof.add(new SearchItemString("Start Lost", "start_lost"));
		lof.add(new SearchItemString("Stop Gained", "stop_gained"));
		lof.add(new SearchItemString("Stop Lost", "stop_lost"));

		coding.add(new SearchItemString("Disruptive Inframe Deletion", "disruptive_inframe_deletion"));
		coding.add(new SearchItemString("Disruptive Inframe Insertion", "disruptive_inframe_insertion"));
		coding.add(new SearchItemString("Inframe Deletion", "inframe_deletion"));
		coding.add(new SearchItemString("Inframe Insertion", "inframe_insertion"));
		coding.add(new SearchItemString("Duplication", "duplication"));
		coding.add(new SearchItemString("Missense Variant", "missense_variant"));
		coding.add(new SearchItemString("Splice Region Variant", "splice_region_variant"));
		coding.add(new SearchItemString("3 Prime UTR Truncation", "3_prime_UTR_truncation"));
		coding.add(new SearchItemString("5 Prime UTR Truncation", "5_prime_UTR_truncation"));

		lookupTable.put("Exon Loss", "exon_loss_variant");
		lookupTable.put("Exon Duplication", "exon_duplication");
		lookupTable.put("Frameshift Variant", "frameshift_variant");
		lookupTable.put("Gene Fusion", "gene_fusion");
		lookupTable.put("Bidirectional Gene Fusion", "bidirectional_gene_fusion");
		lookupTable.put("Rearranged At DNA Level", "rearranged_at_DNA_level");
		lookupTable.put("Protein Protein Contact", "protein_protein_contact");
		lookupTable.put("Structural Interaction Variant", "structural_interaction_variant");
		lookupTable.put("Rare Amino Acid Variant", "rare_amino_acid_variant");
		lookupTable.put("Splice Acceptor Variant", "splice_acceptor_variant");
		lookupTable.put("Splice Donor Variant", "splice_donor_variant");
		lookupTable.put("Start Lost", "start_lost");
		lookupTable.put("Stop Gained", "stop_gained");
		lookupTable.put("Stop Lost", "stop_lost");
		lookupTable.put("Disruptive Inframe Deletion", "disruptive_inframe_deletion");
		lookupTable.put("Disruptive Inframe Insertion", "disruptive_inframe_insertion");
		lookupTable.put("Inframe Deletion", "inframe_deletion");
		lookupTable.put("Inframe Insertion", "inframe_insertion");
		lookupTable.put("Duplication", "duplication");
		lookupTable.put("Missense Variant", "missense_variant");
		lookupTable.put("Splice Region Variant", "splice_region_variant");
		lookupTable.put("3 Prime UTR Truncation", "3_prime_UTR_truncation");
		lookupTable.put("5 Prime UTR Truncation", "5_prime_UTR_truncation");
		
	}

	public List<SearchItemString> getLof() {
		return lof;
	}

	public List<SearchItemString> getCoding() {
		return coding;
	}

	public String getLofTitle() {
		return lofTitle;
	}

	public String getCodingTitle() {
		return codingTitle;
	}

	public Map<String, String> getLookupTable() {
		return lookupTable;
	}



}
