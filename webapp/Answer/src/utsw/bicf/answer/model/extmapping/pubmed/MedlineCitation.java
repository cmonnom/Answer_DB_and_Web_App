package utsw.bicf.answer.model.extmapping.pubmed;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedlineCitation {
	
//	@JsonProperty("PMID")
//	List<Integer> pmIds;
	@JsonProperty("Article")
	Article article;
	@JsonProperty("DateCompleted")
	DateCompleted dateCompleted;
	
	
	public MedlineCitation() {
		
	}
	public Article getArticle() {
		return article;
	}
	public void setArticle(Article article) {
		this.article = article;
	}
	public DateCompleted getDateCompleted() {
		return dateCompleted;
	}
	public void setDateCompleted(DateCompleted dateCompleted) {
		this.dateCompleted = dateCompleted;
	}
//	public List<Integer> getPmIds() {
//		return pmIds;
//	}
//	public void setPmIds(List<Integer> pmIds) {
//		this.pmIds = pmIds;
//	}




	
}
