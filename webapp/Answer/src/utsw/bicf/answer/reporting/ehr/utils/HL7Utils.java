package utsw.bicf.answer.reporting.ehr.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HL7Utils {
	
	private AtomicInteger obxId = new AtomicInteger(1);
	private int variantIdInt = 2;
	private AtomicInteger variantIdAlpha = new AtomicInteger(1);
	
	//replace all characters here
	private static final String EN_DASH = String.valueOf('\u2013');
	private static final String EM_DASH = String.valueOf('\u2014');
	private static final String HORIZONTAL_BAR = String.valueOf('\u2015');
	private static final String DOUBLE_LOW_LINE = String.valueOf('\u2017');
	private static final String LEFT_SINGLE_QUOTATION_MARK = String.valueOf('\u2018');
	private static final String RIGHT_SINGLE_QUOTATION_MARK = String.valueOf('\u2019');
	private static final String SINGLE_LOW_9_QUOTATION_MARK = String.valueOf('\u201A');
	private static final String SINGLE_HIGH_REVERSED_9_QUOTATION_MARK = String.valueOf('\u201B');
	private static final String LEFT_DOUBLE_QUOTATION_MARK = String.valueOf('\u201C');
	private static final String RIGHT_DOUBLE_QUOTATION_MARK = String.valueOf('\u201D');
	private static final String DOUBLE_LOW_9_QUOTATION_MARK = String.valueOf('\u201E');
	private static final String DAGGER = String.valueOf('\u2020');
	private static final String DOUBLE_DAGGER = String.valueOf('\u2021');
	private static final String BULLET = String.valueOf('\u2022');
	private static final String HORIZONTAL_ELLIPSIS = String.valueOf('\u2026');
	private static final String PER_MILLE_SIGN = String.valueOf('\u2030');
	private static final String PRIME = String.valueOf('\u2032');
	private static final String DOUBLE_PRIME = String.valueOf('\u2033');
	private static final String SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK = String.valueOf('\u2039');
	private static final String SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK = String.valueOf('\u203A');
	private static final String DOUBLE_EXCLAMATION_MARK = String.valueOf('\u203C');
	private static final String OVERLINE = String.valueOf('\u203E');
	private static final String FRACTION_SLASH = String.valueOf('\u2044');
	private static final String TIRONIAN_ET_SIGN = String.valueOf('\u204A');
	
	private static final String EN_DASH_SUB = "-";
	private static final String EM_DASH_SUB = "-";
	private static final String HORIZONTAL_BAR_SUB = "-";
	private static final String DOUBLE_LOW_LINE_SUB = "-";
	private static final String LEFT_SINGLE_QUOTATION_MARK_SUB = "'";
	private static final String RIGHT_SINGLE_QUOTATION_MARK_SUB = "'";
	private static final String SINGLE_LOW_9_QUOTATION_MARK_SUB = "'";
	private static final String SINGLE_HIGH_REVERSED_9_QUOTATION_MARK_SUB = "'";
	private static final String LEFT_DOUBLE_QUOTATION_MARK_SUB = " ";
	private static final String RIGHT_DOUBLE_QUOTATION_MARK_SUB = " ";
	private static final String DOUBLE_LOW_9_QUOTATION_MARK_SUB = " ";
	private static final String DAGGER_SUB = " ";
	private static final String DOUBLE_DAGGER_SUB = " ";
	private static final String BULLET_SUB = "*";
	private static final String HORIZONTAL_ELLIPSIS_SUB = "...";
	private static final String PER_MILLE_SIGN_SUB = "0/00";
	private static final String PRIME_SUB = "'";
	private static final String DOUBLE_PRIME_SUB = "''";
	private static final String SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK_SUB = "<";
	private static final String SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK_SUB = ">";
	private static final String DOUBLE_EXCLAMATION_MARK_SUB = "!!";
	private static final String OVERLINE_SUB = "-";
	private static final String FRACTION_SLASH_SUB = "/";
	private static final String TIRONIAN_ET_SIGN_SUB = "7";
	
	private static final String GREEK_SMALL_LETTER_ALPHA = String.valueOf('\u03B1');
	private static final String GREEK_SMALL_LETTER_BETA = String.valueOf('\u03B2');
	private static final String GREEK_SMALL_LETTER_GAMMA = String.valueOf('\u03B3');
	private static final String GREEK_SMALL_LETTER_DELTA = String.valueOf('\u03B4');
	private static final String GREEK_SMALL_LETTER_EPSILON = String.valueOf('\u03B5');
	private static final String GREEK_CAPITAL_LETTER_ALPHA = String.valueOf('\u0391');
	private static final String GREEK_CAPITAL_LETTER_BETA = String.valueOf('\u0392');
	private static final String GREEK_CAPITAL_LETTER_GAMMA = String.valueOf('\u0393');
	private static final String GREEK_CAPITAL_LETTER_DELTA = String.valueOf('\u0394');
	private static final String GREEK_CAPITAL_LETTER_EPSILON = String.valueOf('\u0395');

	
	private static final String GREEK_SMALL_LETTER_ALPHA_SUB = "alpha";
	private static final String GREEK_SMALL_LETTER_BETA_SUB = "beta";
	private static final String GREEK_SMALL_LETTER_GAMMA_SUB = "gamma";
	private static final String GREEK_SMALL_LETTER_DELTA_SUB = "delta";
	private static final String GREEK_SMALL_LETTER_EPSILON_SUB = "epsilon";


	public static final Map<String, String> UTF8_CHARSET = new HashMap<String, String>();
	public static final Map<String, String> GREEK_CHARSET = new HashMap<String, String>();
	
	static {
		UTF8_CHARSET.put(EN_DASH, EN_DASH_SUB);
		UTF8_CHARSET.put(EM_DASH, EM_DASH_SUB);
		UTF8_CHARSET.put(HORIZONTAL_BAR, HORIZONTAL_BAR_SUB);
		UTF8_CHARSET.put(DOUBLE_LOW_LINE, DOUBLE_LOW_LINE_SUB);
		UTF8_CHARSET.put(LEFT_SINGLE_QUOTATION_MARK, LEFT_SINGLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(RIGHT_SINGLE_QUOTATION_MARK, RIGHT_SINGLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(SINGLE_LOW_9_QUOTATION_MARK, SINGLE_LOW_9_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(SINGLE_HIGH_REVERSED_9_QUOTATION_MARK, SINGLE_HIGH_REVERSED_9_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(LEFT_DOUBLE_QUOTATION_MARK, LEFT_DOUBLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(RIGHT_DOUBLE_QUOTATION_MARK, RIGHT_DOUBLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(DOUBLE_LOW_9_QUOTATION_MARK, DOUBLE_LOW_9_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(DAGGER, DAGGER_SUB);
		UTF8_CHARSET.put(DOUBLE_DAGGER, DOUBLE_DAGGER_SUB);
		UTF8_CHARSET.put(BULLET, BULLET_SUB);
		UTF8_CHARSET.put(HORIZONTAL_ELLIPSIS, HORIZONTAL_ELLIPSIS_SUB);
		UTF8_CHARSET.put(PER_MILLE_SIGN, PER_MILLE_SIGN_SUB);
		UTF8_CHARSET.put(PRIME, PRIME_SUB);
		UTF8_CHARSET.put(DOUBLE_PRIME, DOUBLE_PRIME_SUB);
		UTF8_CHARSET.put(SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK, SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK, SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK_SUB);
		UTF8_CHARSET.put(DOUBLE_EXCLAMATION_MARK, DOUBLE_EXCLAMATION_MARK_SUB);
		UTF8_CHARSET.put(OVERLINE, OVERLINE_SUB);
		UTF8_CHARSET.put(FRACTION_SLASH, FRACTION_SLASH_SUB);
		UTF8_CHARSET.put(TIRONIAN_ET_SIGN, TIRONIAN_ET_SIGN_SUB);
		
		GREEK_CHARSET.put(GREEK_SMALL_LETTER_ALPHA, GREEK_SMALL_LETTER_ALPHA_SUB);
		GREEK_CHARSET.put(GREEK_SMALL_LETTER_BETA, GREEK_SMALL_LETTER_BETA_SUB);
		GREEK_CHARSET.put(GREEK_SMALL_LETTER_GAMMA, GREEK_SMALL_LETTER_GAMMA_SUB);
		GREEK_CHARSET.put(GREEK_SMALL_LETTER_DELTA, GREEK_SMALL_LETTER_DELTA_SUB);
		GREEK_CHARSET.put(GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_EPSILON_SUB);
		GREEK_CHARSET.put(GREEK_CAPITAL_LETTER_ALPHA, GREEK_SMALL_LETTER_ALPHA_SUB);
		GREEK_CHARSET.put(GREEK_CAPITAL_LETTER_BETA, GREEK_SMALL_LETTER_BETA_SUB);
		GREEK_CHARSET.put(GREEK_CAPITAL_LETTER_GAMMA, GREEK_SMALL_LETTER_GAMMA_SUB);
		GREEK_CHARSET.put(GREEK_CAPITAL_LETTER_DELTA, GREEK_SMALL_LETTER_DELTA_SUB);
		GREEK_CHARSET.put(GREEK_CAPITAL_LETTER_EPSILON, GREEK_SMALL_LETTER_EPSILON_SUB);
	}

	
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
