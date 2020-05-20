package utsw.bicf.answer.model.extmapping.interpro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Protein {
	
	@JsonProperty("accession")
	String accession;
	@JsonProperty("entry_protein_locations")
	List<EntryProteinLocation> entryProteinLocations;
	
	public Protein() {
		super();
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public List<EntryProteinLocation> getEntryProteinLocations() {
		return entryProteinLocations;
	}

	public void setEntryProteinLocations(List<EntryProteinLocation> entryProteinLocations) {
		this.entryProteinLocations = entryProteinLocations;
	}








}
