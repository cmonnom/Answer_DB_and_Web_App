package utsw.bicf.answer.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="clinical_test")
public class ClinicalTest {
	
	public ClinicalTest() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="clinical_test_id")
	Integer clinicalTestId;
	
	@Column(name="test_name")
	String testName;
	
	@Column(name="test_link")
	String testLink;
	
	@OneToMany(mappedBy="clinicalTest", fetch=FetchType.EAGER, cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	List<ClinicalTestDisclaimer> disclaimerTexts;

	public Integer getClinicalTestId() {
		return clinicalTestId;
	}

	public void setClinicalTestId(Integer clinicalTestId) {
		this.clinicalTestId = clinicalTestId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestLink() {
		return testLink;
	}

	public void setTestLink(String testLink) {
		this.testLink = testLink;
	}

	public List<ClinicalTestDisclaimer> getDisclaimerTexts() {
		return disclaimerTexts;
	}

	public void setDisclaimerTexts(List<ClinicalTestDisclaimer> disclaimerTexts) {
		this.disclaimerTexts = disclaimerTexts;
	}
	
}
