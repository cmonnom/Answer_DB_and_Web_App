package utsw.bicf.answer.model.extmapping.pubmed;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
	
	@JsonProperty("ArticleTitle")
	String articleTitle;
	@JsonProperty("Pagination")
	Pagination pagination;
	@JsonProperty("AuthorList")
	List<Author> authorList;
	@JsonProperty("Journal")
	Journal journal;
	
	public Article() {
		super();
	}
	
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public List<Author> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<Author> authorList) {
		this.authorList = authorList;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}





	
}
