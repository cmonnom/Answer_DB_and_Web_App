package utsw.bicf.answer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="clinical_test_disclaimer")
public class ClinicalTestDisclaimer {
	
	public ClinicalTestDisclaimer() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="clinical_test_disclaimer_id")
	Integer clinicalTestDisclaimerId;
	
	@Column(name="text")
	String text;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="clinical_test_id")
	ClinicalTest clinicalTest;

	public Integer getClinicalTestDisclaimerId() {
		return clinicalTestDisclaimerId;
	}

	public void setClinicalTestDisclaimerId(Integer clinicalTestDisclaimerId) {
		this.clinicalTestDisclaimerId = clinicalTestDisclaimerId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ClinicalTest getClinicalTest() {
		return clinicalTest;
	}

	public void setClinicalTest(ClinicalTest clinicalTest) {
		this.clinicalTest = clinicalTest;
	}
	
}
