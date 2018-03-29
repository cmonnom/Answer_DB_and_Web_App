package utsw.bicf.answer.clarity.api.utils;

import utsw.bicf.answer.clarity.api.model.ClarityUDFField;

public class UDFUtils {
	
	/**
	 * Iterate through all udf:field in the current object to find the fieldName value
	 * @param udfFields
	 * @param fieldName
	 * @return
	 */
	public static String getUDFValue(ClarityUDFField[] udfFields, String fieldName) {
		if (udfFields != null) {
			for (ClarityUDFField udf : udfFields) {
				if (udf.getName().equals(fieldName)) {
					return udf.getValue();
				}
			}
		}
		return null; // couldn't find it.
	}
	
	public static Double getUDFDoubleValue(ClarityUDFField[] udfFields, String fieldName) {
		String value = getUDFValue(udfFields, fieldName);
		if (value != null) {
			return Double.parseDouble(value);
		}
		return null;
	}
	
	public static Integer getUDFIntegerValue(ClarityUDFField[] udfFields, String fieldName) {
		String value = getUDFValue(udfFields, fieldName);
		if (value != null) {
			return Integer.parseInt(value);
		}
		return null;
	}

}
