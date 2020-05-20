package utsw.bicf.answer.model.hybrid;

/**
 * Horizontal bar where x is a number and y is a category
 * @author Guillaume
 *
 */
public class GenericBarPlotData {
	
	Number x;
	String y;
	public GenericBarPlotData() {
		super();
	}
	public GenericBarPlotData(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "x": x = (Number) values[i]; break;
			case "y": y = (String) values[i]; break;
			}
		}
	}
	public Number getX() {
		return x;
	}
	public void setX(Number x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}

	

}
