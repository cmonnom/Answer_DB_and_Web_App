package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_summary")
public class GenieSummary {
	
	public static final String CATEGORY_CANCER_COUNT = "cat_cancer_count";
	
	public GenieSummary() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_summary_id")
	Integer genieSummaryId;
	
	@Column(name="tally")
	Integer tally;
	
	@Column(name="label")
	String label;
	
	@Column(name="category")
	String category;

	public Integer getGenieSummaryId() {
		return genieSummaryId;
	}

	public void setGenieSummaryId(Integer genieSummaryId) {
		this.genieSummaryId = genieSummaryId;
	}

	public GenieSummary(Integer tally, String label, String category) {
		super();
		this.tally = tally;
		this.label = label;
		this.category = category;
	}

	public Integer getTally() {
		return tally;
	}

	public void setTally(Integer tally) {
		this.tally = tally;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	

}
