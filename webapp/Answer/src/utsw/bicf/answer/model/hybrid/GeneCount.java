package utsw.bicf.answer.model.hybrid;

public class GeneCount implements Comparable<GeneCount> {
	
	String gene;
	Integer count;
	
	
	public GeneCount(String gene, Integer count) {
		super();
		this.gene = gene;
		this.count = count;
	}
	@Override
	public int compareTo(GeneCount o) {
		return o.count.compareTo(this.count);
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}