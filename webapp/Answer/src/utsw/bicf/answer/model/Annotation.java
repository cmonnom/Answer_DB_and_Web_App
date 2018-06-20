//package utsw.bicf.answer.model;
//
//import java.time.LocalDateTime;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import utsw.bicf.answer.clarity.api.utils.TypeUtils;
//
//@Entity
//@Table(name="annotation")
//public class Annotation {
//	
//	public Annotation() {
//		
//	}
//
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(name="annotation_id")
//	Integer annotationId;
//	
//	//Name of the organization this annotation is from (eg. UTSW)
//	@JsonIgnore
//	@Column(name="origin")
//	String origin;
//	
//	@Column(name="text")
//	String text;
//
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="answer_user_id")
//	User user;
//	
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="gene_id")
//	Gene gene;
//	
//	@Column(name="created_date")
//	LocalDateTime createdDate;
//	
//	
//	@Column(name="modified_date")
//	LocalDateTime modifiedDate;
//	
//	@Column(name="deleted")
//	Boolean deleted;
//	
//	@Transient
//	String createdDateFormatted;
//	@Transient
//	String modifiedDateFormatted;
//	@Transient
//	Boolean isVisible = true;
//	@Transient
//	Boolean markedForDeletion = false;
//
//	public Integer getAnnotationId() {
//		return annotationId;
//	}
//
//	public void setAnnotationId(Integer annotationId) {
//		this.annotationId = annotationId;
//	}
//
//	public String getOrigin() {
//		return origin;
//	}
//
//	public void setOrigin(String origin) {
//		this.origin = origin;
//	}
//
//	public String getText() {
//		return text;
//	}
//
//	public void setText(String text) {
//		this.text = text;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public Gene getGene() {
//		return gene;
//	}
//
//	public void setGene(Gene gene) {
//		this.gene = gene;
//	}
//
//	public LocalDateTime getCreatedDate() {
//		return createdDate;
//	}
//
//	public void setCreatedDate(LocalDateTime createdDate) {
//		this.createdDate = createdDate;
//	}
//
//	public LocalDateTime getModifiedDate() {
//		return modifiedDate;
//	}
//
//	public void setModifiedDate(LocalDateTime modifiedDate) {
//		this.modifiedDate = modifiedDate;
//	}
//
//	public String getCreatedDateFormatted() {
//		return TypeUtils.localDateFormatter.format(createdDate);
//	}
//
//	public String getModifiedDateFormatted() {
//		if (modifiedDate != null) {
//			return TypeUtils.localDateFormatter.format(modifiedDate);
//		}
//		return null;
//	}
//
//	public Boolean getIsVisible() {
//		return isVisible;
//	}
//
//	public Boolean getMarkedForDeletion() {
//		return markedForDeletion;
//	}
//
//	public Boolean getDeleted() {
//		return deleted;
//	}
//
//	public void setDeleted(Boolean deleted) {
//		this.deleted = deleted;
//	}
//	
//	
//}
