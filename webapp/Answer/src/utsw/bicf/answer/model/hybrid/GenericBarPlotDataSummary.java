package utsw.bicf.answer.model.hybrid;

import utsw.bicf.answer.model.GenieSummary;

/**
 * Horizontal bar where x is a number and y is a category
 * @author Guillaume
 *
 */
public class GenericBarPlotDataSummary {
	
	GenieSummary dAll;
	GenericBarPlotData dGene;
	Float key;
	public GenieSummary getdAll() {
		return dAll;
	}
	public void setdAll(GenieSummary dAll) {
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
	public GenericBarPlotDataSummary(GenieSummary dAll, GenericBarPlotData dGene, Float key) {
		super();
		this.dAll = dAll;
		this.dGene = dGene;
		this.key = key;
	}
	
	
	

}
