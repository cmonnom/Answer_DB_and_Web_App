package utsw.bicf.answer.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="order_case")
public class OrderCase {
	
	public OrderCase() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="case_id")
	Integer caseId;
	
	@Column(name="patient_mrn")
	String patientMrn;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="mda_email_id")
	MDAEmail mdaEmail;
	
	@Column(name="ready_for_review")
	Boolean readyForReview;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="final_interpretation_id")
	FreeText finalInterpretation;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="final_comment_id")
	FreeText finalComment;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="curator_comment_id")
	FreeText curatorComment;
	
	@OneToMany(mappedBy="orderCase", fetch=FetchType.LAZY, cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	List<VariantSelected> variantSelected;
	

	
	@Column(name="locked")
	Boolean locked;
	
	@Column(name="in_use")
	Boolean inUse;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="created_by_id")
	User createdBy;
	
	@Column(name="date_created")
	LocalDateTime dateCreated;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="last_modified_by_id")
	User lastModifiedBy;
	
	@Column(name="date_modified")
	LocalDateTime dateModified;

	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

	public String getPatientMrn() {
		return patientMrn;
	}

	public void setPatientMrn(String patientMrn) {
		this.patientMrn = patientMrn;
	}

	public MDAEmail getMdaEmail() {
		return mdaEmail;
	}

	public void setMdaEmail(MDAEmail mdaEmail) {
		this.mdaEmail = mdaEmail;
	}


	public Boolean getReadyForReview() {
		return readyForReview;
	}

	public void setReadyForReview(Boolean readyForReview) {
		this.readyForReview = readyForReview;
	}

	public FreeText getFinalInterpretation() {
		return finalInterpretation;
	}

	public void setFinalInterpretation(FreeText finalInterpretation) {
		this.finalInterpretation = finalInterpretation;
	}

	public FreeText getFinalComment() {
		return finalComment;
	}

	public void setFinalComment(FreeText finalComment) {
		this.finalComment = finalComment;
	}

	public FreeText getCuratorComment() {
		return curatorComment;
	}

	public void setCuratorComment(FreeText curatorComment) {
		this.curatorComment = curatorComment;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getInUse() {
		return inUse;
	}

	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(LocalDateTime dateModified) {
		this.dateModified = dateModified;
	}

	public List<VariantSelected> getVariantSelected() {
		return variantSelected;
	}

	public void setVariantSelected(List<VariantSelected> variantSelected) {
		this.variantSelected = variantSelected;
	}

}
