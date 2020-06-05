package utsw.bicf.answer.model.hybrid;

public class CondonDistribution {
	
	Number count;
	String aa;
	String cancerType;
	
	
	public CondonDistribution() {
		super();
	}
	
	public CondonDistribution(Object[] values, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			String label = (String) labels[i];
			switch(label) {
			case "x": count = (Number) values[i]; break;
			case "aa": aa = (String) values[i]; break;
			case "cancerType": cancerType = (String) values[i]; break;
			}
		}
	}

	public Number getCount() {
		return count;
	}

	public void setCount(Number count) {
		this.count = count;
	}

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public String getCancerType() {
		return cancerType;
	}

	public void setCancerType(String cancerType) {
		this.cancerType = cancerType;
	}
	

}
