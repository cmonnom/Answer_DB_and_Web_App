package utsw.bicf.answer.model.hybrid;

/**
 * Basic Lollipop plot with no label predefined (2 slots)
 * @author Guillaume
 *
 */
public class GenericLollipopPlotData {
	
	Number x;
	Number y;
	String label1;
	String label2;
	public GenericLollipopPlotData() {
		super();
	}
	public GenericLollipopPlotData(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "x": x = (Number) values[i]; break;
			case "y": y = (Number) values[i]; break;
			case "label1": label1 = (String) values[i]; break;
			case "label2": label2 = (String) values[i]; break;
			}
		}
	}
	public Number getX() {
		return x;
	}
	public void setX(Number x) {
		this.x = x;
	}
	public Number getY() {
		return y;
	}
	public void setY(Number y) {
		this.y = y;
	}
	public String getLabel1() {
		return label1;
	}
	public void setLabel1(String label1) {
		this.label1 = label1;
	}
	public String getLabel2() {
		return label2;
	}
	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	

}
