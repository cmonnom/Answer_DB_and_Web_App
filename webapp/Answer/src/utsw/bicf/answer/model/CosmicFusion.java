package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cosmic_fusion")
public class CosmicFusion {
	
	public CosmicFusion() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cosmic_fusion_id")
	Integer cosmicFusionId;
	
	
	@Column(name="translocation_name")
	String translocationName;
	
	@Column(name="five_chr")
	String fiveChr;
	
	@Column(name="five_gene")
	String fiveGene;
	
	@Column(name="five_start")
	Integer fiveStart;
	
	@Column(name="five_end")
	Integer fiveEnd;
	
	@Column(name="five_enst")
	String fiveENST;

	@Column(name="three_chr")
	String threeChr;
	
	@Column(name="three_gene")
	String threeGene;
	
	@Column(name="three_start")
	Integer threeStart;
	
	@Column(name="three_end")
	Integer threeEnd;
	
	@Column(name="three_enst")
	String threeENST;
	
	@Column(name="five_exon")
	String fiveExon;
	
	@Column(name="three_exon")
	String threeExon;
	
	@Column(name="fusion_id")
	String fusionId;
	
	public Integer getCosmicFusionId() {
		return cosmicFusionId;
	}

	public void setCosmicFusionId(Integer cosmicFusionId) {
		this.cosmicFusionId = cosmicFusionId;
	}

	public String getTranslocationName() {
		return translocationName;
	}

	public void setTranslocationName(String translocationName) {
		this.translocationName = translocationName;
	}

	public String getFiveChr() {
		return fiveChr;
	}

	public void setFiveChr(String fiveChr) {
		this.fiveChr = fiveChr;
	}

	public String getFiveGene() {
		return fiveGene;
	}

	public void setFiveGene(String fiveGene) {
		this.fiveGene = fiveGene;
	}

	public Integer getFiveStart() {
		return fiveStart;
	}

	public void setFiveStart(Integer fiveStart) {
		this.fiveStart = fiveStart;
	}

	public String getFiveENST() {
		return fiveENST;
	}

	public void setFiveENST(String fiveENST) {
		this.fiveENST = fiveENST;
	}

	public String getThreeChr() {
		return threeChr;
	}

	public void setThreeChr(String threeChr) {
		this.threeChr = threeChr;
	}

	public String getThreeGene() {
		return threeGene;
	}

	public void setThreeGene(String threeGene) {
		this.threeGene = threeGene;
	}

	public Integer getThreeStart() {
		return threeStart;
	}

	public void setThreeStart(Integer threeStart) {
		this.threeStart = threeStart;
	}

	public String getThreeENST() {
		return threeENST;
	}

	public void setThreeENST(String threeENST) {
		this.threeENST = threeENST;
	}

	public Integer getFiveEnd() {
		return fiveEnd;
	}

	public void setFiveEnd(Integer fiveEnd) {
		this.fiveEnd = fiveEnd;
	}

	public Integer getThreeEnd() {
		return threeEnd;
	}

	public void setThreeEnd(Integer threeEnd) {
		this.threeEnd = threeEnd;
	}

	public String getFusionId() {
		return fusionId;
	}

	public void setFusionId(String fusionId) {
		this.fusionId = fusionId;
	}

	public String getFiveExon() {
		return fiveExon;
	}

	public void setFiveExon(String fiveExon) {
		this.fiveExon = fiveExon;
	}

	public String getThreeExon() {
		return threeExon;
	}

	public void setThreeExon(String threeExon) {
		this.threeExon = threeExon;
	}

}
