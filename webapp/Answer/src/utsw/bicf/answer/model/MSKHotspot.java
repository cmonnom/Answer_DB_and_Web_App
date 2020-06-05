package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="msk_hotspot")
public class MSKHotspot {
	
	public MSKHotspot() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="msk_hotspot_id")
	Integer mskHotspotId;
	
	@Column(name="hugo_symbol")
	String hugoSymbol;
	
	@Column(name="residue")
	String residue;
	
	@Column(name="mutation_type")
	String mutationType;
	
	@Column(name="is_hotspot")
	Boolean isHotspot;
	
	@Column(name="is_three_d")
	Boolean isThreeD;

	public Integer getMskHotspotId() {
		return mskHotspotId;
	}

	public void setMskHotspotId(Integer mskHotspotId) {
		this.mskHotspotId = mskHotspotId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
	}

	public String getResidue() {
		return residue;
	}

	public void setResidue(String residue) {
		this.residue = residue;
	}

	public String getMutationType() {
		return mutationType;
	}

	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}

	public Boolean getIsHotspot() {
		return isHotspot;
	}

	public void setIsHotspot(Boolean isHotspot) {
		this.isHotspot = isHotspot;
	}

	public Boolean getIsThreeD() {
		return isThreeD;
	}

	public void setIsThreeD(Boolean isThreeD) {
		this.isThreeD = isThreeD;
	}


}
