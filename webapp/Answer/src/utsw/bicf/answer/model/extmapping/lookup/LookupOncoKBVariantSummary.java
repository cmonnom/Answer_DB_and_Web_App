package utsw.bicf.answer.model.extmapping.lookup;

import java.util.ArrayList;
import java.util.List;

public class LookupOncoKBVariantSummary {
	
	String clinicalImplications;
	List<StdOfCareIndication> indications = new ArrayList<StdOfCareIndication>();
	List<StdOfCareIndication> investigationalIndications = new ArrayList<StdOfCareIndication>();
	String drugResistance;
	String oncokbVariantUrl;
	String mutationEffect;
	String oncogenic;
	String mutationEffectPubMedUrl;
	
	public LookupOncoKBVariantSummary() {
		super();
	}

	public String getClinicalImplications() {
		return clinicalImplications;
	}

	public void setClinicalImplications(String clinicalImplications) {
		this.clinicalImplications = clinicalImplications;
	}

	public List<StdOfCareIndication> getIndications() {
		return indications;
	}

	public void setIndications(List<StdOfCareIndication> indications) {
		this.indications = indications;
	}

	public String getDrugResistance() {
		return drugResistance;
	}

	public void setDrugResistance(String drugResistance) {
		this.drugResistance = drugResistance;
	}

	public String getOncokbVariantUrl() {
		return oncokbVariantUrl;
	}

	public void setOncokbVariantUrl(String oncokbVariantUrl) {
		this.oncokbVariantUrl = oncokbVariantUrl;
	}

	public String getMutationEffect() {
		return mutationEffect;
	}

	public void setMutationEffect(String mutationEffect) {
		this.mutationEffect = mutationEffect;
	}

	public String getOncogenic() {
		return oncogenic;
	}

	public void setOncogenic(String oncogenic) {
		this.oncogenic = oncogenic;
	}

	public String getMutationEffectPubMedUrl() {
		return mutationEffectPubMedUrl;
	}

	public void setMutationEffectPubMedUrl(String mutationEffectPubMedUrl) {
		this.mutationEffectPubMedUrl = mutationEffectPubMedUrl;
	}

	public List<StdOfCareIndication> getInvestigationalIndications() {
		return investigationalIndications;
	}

	public void setInvestigationalIndications(List<StdOfCareIndication> investigationalIndications) {
		this.investigationalIndications = investigationalIndications;
	}
	
}
