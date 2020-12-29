package utsw.bicf.answer.reporting.ehr.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class HL7Utils {
	
	private AtomicInteger obxId = new AtomicInteger(1);
	private int variantIdInt = 2;
	private AtomicInteger variantIdAlpha = new AtomicInteger(1);
	
	public String getNextVariantId() {
		int dividend = variantIdAlpha.get();
	    String columnName = this.getNextVariantIdSub(dividend);

	    variantIdAlpha.incrementAndGet();		
		return variantIdInt + columnName;
	}
	
	public String getNextVariantIdSub(int dividend) {
		String columnName = "";
		int modulo;

		while (dividend > 0)
		{
			modulo = (dividend - 1) % 26;
			columnName = (char)(65 + modulo) + columnName;
			dividend = (int)((dividend - modulo) / 26);
		} 
		return columnName.toLowerCase();
	}

	public int getObservationId() {
		return obxId.get();
	}
	
	public void incrementObservationCount() {
		obxId.incrementAndGet();
		
	}

}
