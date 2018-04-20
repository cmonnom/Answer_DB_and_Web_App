//package utsw.bicf.answer.model;
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
//
//@Entity
//@Table(name="variant_selected")
//public class VariantSelected {
//	
//	public VariantSelected() {
//		
//	}
//
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(name="variant_selected_id")
//	Integer variantSelectedId;
//	
//	@Column(name="gene_and_variant")
//	String geneAndVariant;
//
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//	@JoinColumn(name="order_case_id")
//	OrderCase orderCase;
//
//	public OrderCase getOrderCase() {
//		return orderCase;
//	}
//
//	public void setOrderCase(OrderCase orderCase) {
//		this.orderCase = orderCase;
//	}
//
//	public Integer getVariantSelectedId() {
//		return variantSelectedId;
//	}
//
//	public void setVariantSelectedId(Integer variantSelectedId) {
//		this.variantSelectedId = variantSelectedId;
//	}
//
//	public String getGeneAndVariant() {
//		return geneAndVariant;
//	}
//
//	public void setGeneAndVariant(String geneAndVariant) {
//		this.geneAndVariant = geneAndVariant;
//	}
//	
//	
//
//	
//	
//}
