package utsw.bicf.answer.model.hybrid;

/**
 * Horizontal bar where x is a number and y is a category
 * @author Guillaume
 *
 */
public class RatioBarPlotData extends GenericBarPlotData{
	
	Number total;
	Number ratio;
	
	public RatioBarPlotData() {
		super();
	}

	public RatioBarPlotData(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "x": x = (Number) values[i]; break;
			case "y": y = (String) values[i]; break;
			case "total": total = (Number) values[i]; break;
			case "ratio": ratio = (Number) values[i]; break;
			}
		}
	}
	
	public Number getTotal() {
		return total;
	}

	public void setTotal(Number total) {
		this.total = total;
	}

	public Number getRatio() {
		return ratio;
	}

	public void setRatio(Number ratio) {
		this.ratio = ratio;
	}

	

}
