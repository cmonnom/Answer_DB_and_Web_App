package utsw.bicf.answer.reporting.ehr;

public class UTSWProps {
	
	/**
	 * 
	 * MSH Header 
		MSH-2. Encoding Characters - ​^~\& 
		MSH-3. Sending Application - ​EPICEMR 
		MSH-4. Sending Facility - ​UNC 
		MSH-5. Receiving Application - ​TEMPUS LABS 
		MSH-6. Receiving Facility - ​TEMPUS 
		MSH-7. Date/Time of Message [YYYYMMDDHHmmss] 
		MSH-9. Message Type 
		MSH-9.1 ​ORM 
		MSH-9.2 ​O01 
		MSH-11. Processing ID - ​T - Test​ / ​P - Production 
		MSH-12. HL7 version - 2.5.1 
	 */

	public static String SENDING_APPLICATION = "Answer UTSW";
	public static String SENDING_FACILITY = "UTSW";
	public static String RECEIVING_APPLICATION = "EPIC";
	public static String RECEIVING_FACILITY = "UTSW";
	public static String DATE_TIME_DEGREE_OF_PRECISION = "S"; //seconds
	
	
	public static String DIAGNOSIS_CODING_METHOD = "ICD-10-CM"; //valid code?
	
	//PDF Base64
	public static String PDF_IDENTIFIER = "REPORT"; 
	public static String PDF_TEXT = "Answer Report"; 
}
