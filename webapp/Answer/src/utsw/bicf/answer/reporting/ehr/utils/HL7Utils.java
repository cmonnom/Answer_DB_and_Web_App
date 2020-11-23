package utsw.bicf.answer.reporting.ehr.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class HL7Utils {
	
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private AtomicInteger obxId = new AtomicInteger(1);
	private AtomicInteger variantIdInt = new AtomicInteger(1);
	private AtomicInteger variantIdAlpha = new AtomicInteger(1);
	
	public String getNextVariantId() {
		if (variantIdAlpha.get() == ALPHABET.length()) {
			variantIdInt.incrementAndGet();
			variantIdAlpha.set(1);
		}
		String alpha = ALPHABET.substring(variantIdAlpha.get() - 1, variantIdAlpha.get());
		variantIdAlpha.incrementAndGet();
		return variantIdInt + alpha;
	}
	
	public int getObservationId() {
		return obxId.get();
	}
	
	public void incrementObservationCount() {
		obxId.incrementAndGet();
		
	}

}
