package utsw.bicf.answer.model.extmapping.lookup;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;

public class LookupGeneSummaries {
	
	List<String> databases;
	Map<String, LookupSummary> summaries = new Hashtable<String, LookupSummary>();
	EnsemblResponse ensembl;
	
	public LookupGeneSummaries(EnsemblResponse ensembl) {
		super();
		this.ensembl = ensembl;
	}

	public LookupGeneSummaries() {
		super();
	}

	public List<String> getDatabases() {
		return databases;
	}

	public void setDatabases(List<String> databases) {
		this.databases = databases;
	}

	public Map<String, LookupSummary> getSummaries() {
		return summaries;
	}

	public void setSummaries(Map<String, LookupSummary> summaries) {
		this.summaries = summaries;
	}

	public EnsemblResponse getEnsembl() {
		return ensembl;
	}

	public void setEnsembl(EnsemblResponse ensembl) {
		this.ensembl = ensembl;
	}
	
	

}
