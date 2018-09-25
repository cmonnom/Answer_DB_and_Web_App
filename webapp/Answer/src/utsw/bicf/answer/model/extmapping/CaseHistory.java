package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHistory {
	
	public static final Integer STEP_NOT_ASSIGNED = 0;
	public static final Integer STEP_ASSIGNED = 1;
	public static final Integer STEP_UNDER_REVIEW = 2;
	public static final Integer STEP_REPORTING = 3;
	public static final Integer STEP_FINALIZED = 4;
	
	
	String time;
	Integer step;
	
	public CaseHistory() {
		
	}


	public Integer getStep() {
		return step;
	}


	public String getTime() {
		return time;
	}


    public static boolean lastStepMatches(OrderCase orderCase, int step) {
    	return (orderCase.getCaseHistory() != null 
    			&& orderCase.getCaseHistory().get(orderCase.getCaseHistory().size() - 1).getStep() == step);
    }


}
