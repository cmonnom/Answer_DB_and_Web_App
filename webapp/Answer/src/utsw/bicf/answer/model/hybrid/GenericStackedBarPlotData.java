package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Horizontal bar where x is a number and y is a category
 * @author Guillaume
 *
 */
public class GenericStackedBarPlotData {
	
	String[] barXItems;
	String[] catXItems;
	List<Integer> barX = new ArrayList<Integer>();
	List<String> catX = new ArrayList<String>();
	Map<String, Integer> xByCat = new HashMap<String, Integer>();
	String y;
	public GenericStackedBarPlotData() {
		super();
	}
	public GenericStackedBarPlotData(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "bar_x": barXItems = ((String) values[i]).split(","); break;
			case "cat_x": catXItems = ((String) values[i]).split(","); break;
			case "y": y = (String) values[i]; break;
			}
		}
		for (String x : barXItems) {
			barX.add(Integer.parseInt(x));
		}
		//build a map of the sum of x values for each category
		for (int i = 0 ; i < catXItems.length; i++) {
			String x = catXItems[i];
			Integer xValue = Integer.parseInt(barXItems[i]);
			String key = null;
			if (x.equals("SNP") || x.equals("DNP")
					|| x.equals("TNP") || x.equals("ONP")) {
				key = "SNP";
			}
			else {
				key = x;
			}
			Integer xSum = xByCat.get(key);
			if (xSum == null) {
				xSum = 0;
			}
			xSum += xValue;
			xByCat.put(key, xSum);
		}
		
		
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	public Map<String, Integer> getxByCat() {
		return xByCat;
	}
	public void setxByCat(Map<String, Integer> xByCat) {
		this.xByCat = xByCat;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}

}
