package utsw.bicf.answer.model.extmapping.pubmed;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PubmedArticle {
	
	@JsonProperty("MedlineCitation")
	MedlineCitation medlineCitation;
	
	

	public PubmedArticle() {
	}

	public MedlineCitation getMedlineCitation() {
		return medlineCitation;
	}

	public void setMedlineCitation(MedlineCitation medlineCitation) {
		this.medlineCitation = medlineCitation;
	}


	public String prettyPrint() throws ParseException {
		StringBuilder sb = new StringBuilder();
		List<String> authors = new ArrayList<String>();
		for (Author author : medlineCitation.getArticle().getAuthorList()) {
			authors.add(author.getPrettyPrint());
		}
		sb.append(authors.stream().collect(Collectors.joining(","))).append(" "); //authors
		sb.append(medlineCitation.getArticle().getArticleTitle()).append(" "); //title
		sb.append(medlineCitation.getArticle().getJournal().getIsoAbbreviation()).append(". "); //journal name
		sb.append(medlineCitation.getDateCompleted().getPrettyPrint()).append(";"); //date
		sb.append(medlineCitation.getArticle().getJournal().getJournalIssue().getVolume()) //journal info
			.append("(")
			.append(medlineCitation.getArticle().getJournal().getJournalIssue().getIssue())
			.append(")").append(":"); 
		sb.append(medlineCitation.getArticle().getPagination().getMedlinePgn()).append("."); //pagination
		return sb.toString();
	}

	
}
