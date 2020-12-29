package utsw.bicf.answer.model.hybrid;

/**
 * Horizontal bar where x is a number and y is a category
 * @author Guillaume
 *
 */
public class GenericBarPlotDataSummary {
	
	GenericBarPlotData dAll;
	GenericBarPlotData dGene;
	Float key;
	public GenericBarPlotData getdAll() {
		return dAll;
	}
	public void setdAll(GenericBarPlotData dAll) {
		this.dAll = dAll;
	}
	public GenericBarPlotData getdGene() {
		return dGene;
	}
	public void setdGene(GenericBarPlotData dGene) {
		this.dGene = dGene;
	}
	public Float getKey() {
		return key;
	}
	public void setKey(Float key) {
		this.key = key;
	}
	public GenericBarPlotDataSummary() {
		super();
	}
	public GenericBarPlotDataSummary(GenericBarPlotData dAll, GenericBarPlotData dGene, Float key) {
		super();
		this.dAll = dAll;
		this.dGene = dGene;
		this.key = key;
	}
	
	
	

}
