package utsw.bicf.answer.model.extmapping.oncokb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvidenceResponse {
	
	Integer id;
	String evidenceType;
	OncoKBGene gene;
	List<OncoKBAlteration> alterations;
	String description;
	List<OncoKBTreatment> treatments;
	List<OncoKBArticle> articles;
	String knownEffect;
	
	public EvidenceResponse() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEvidenceType() {
		return evidenceType;
	}

	public void setEvidenceType(String evidenceType) {
		this.evidenceType = evidenceType;
	}

	public OncoKBGene getGene() {
		return gene;
	}

	public void setGene(OncoKBGene gene) {
		this.gene = gene;
	}

	public List<OncoKBAlteration> getAlterations() {
		return alterations;
	}

	public void setAlterations(List<OncoKBAlteration> alterations) {
		this.alterations = alterations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<OncoKBTreatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<OncoKBTreatment> treatments) {
		this.treatments = treatments;
	}

	public List<OncoKBArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<OncoKBArticle> articles) {
		this.articles = articles;
	}

	public String getKnownEffect() {
		return knownEffect;
	}

	public void setKnownEffect(String knownEffect) {
		this.knownEffect = knownEffect;
	}





}
