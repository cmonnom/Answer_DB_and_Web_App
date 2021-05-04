package utsw.bicf.answer.reporting.ehr.loinc;

public 	class EffectPriority implements Comparable<EffectPriority>{
	String effect;
	Integer priority;
	
	
	public EffectPriority(String effect, Integer priority) {
		this.effect = effect;
		this.priority = priority;
	}

	@Override
	public int compareTo(EffectPriority o) {
		return this.priority.compareTo(o.priority);
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}