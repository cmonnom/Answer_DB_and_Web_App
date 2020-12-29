package utsw.bicf.answer.reporting.ehr;

public class UTSWProps {
	
	/**
	 * 
	 * 
	 *  
		MSH.#1	Field Separator - Required 	 	|
		MSH.#2	Encoding Characters  Required	 	^~\&
		MSH.#3	Sending Application	 	Answer UTSW
		MSH.#4	Sending Facility	 	103
		MSH.#5	Receiving Application	 	Epic
		MSH.#6	Receiving Facility	 	UTSW
		MSH.#7	Date/Time of Message	 	CCYYMMDDHHMMSS
		MSH.#9	Message Type  Required	 	ORU^R01
		MSH.#10	Message Control ID  Required	 	Unique numerical values
		MSH.#11	Processing ID	 	P - Production , T- Test
		MSH.#12	Version ID  Required	 	2.5
		 
		PID.#3	PATIENT ID - INTERNAL	 	UT MRN
		PID.#5	PATIENT NAME	 	Last^First^Middle
		PID.#7	PATIENT DATE OF BIRTH	 	CCYYMMDD
		PID.#8	SEX	 	M- Male, F- Female, U- Unknown

		OBR.#1	Set -ID	 	1
		OBR.#2	Placer Order Number	 	Epic order ID
		OBR.#4	Universal Service ID (Test Code)	 	NGSPCT^COMPREHENSIVE PAN-CANCER NEXT GENERATION SEQUENCING
		OBR.#7	Observation Date/Time	 	CCYYMMDDHHMMSS
		OBR.#16	Ordering Provider	 	NPI ID^Last^First^Middle
		OBR.#22	Results Rpt/Status Change - Date/Time	 	CCYYMMDDHHMMSS
		OBR.#25	Result Status	 	P - Preliminary, F - Final

	 */

	public static String SENDING_APPLICATION = "Answer UTSW";
	public static String SENDING_FACILITY = "103";
	public static String RECEIVING_APPLICATION = "Epic";
	public static String RECEIVING_FACILITY = "UTSW";
	public static String DATE_TIME_DEGREE_OF_PRECISION = "S"; //seconds
	
	
	public static String DIAGNOSIS_CODING_METHOD = "ICD-10-CM"; //valid code?
	
	//PDF Base64
	public static String PDF_IDENTIFIER = "REPORT"; 
	public static String PDF_TEXT = "Answer Report"; 
	
	public static String[] GRCh38 = new String[] {"GRCh38", "GRCh38", "LA26806-2"}; 
	public static String[] VARIANT_ANALYSIS_METHOD_SEQUENCING = new String[] {"Sequencing", "Sequencing", "LA26398-0"}; 
	public static String[] GENETIC_VARIANT_ASSESSMENT_PRESENT = new String[] {"Present", "Present", "LA9633-4"}; 
	
	public static String COSMIC_M = "COSMIC-SMPL"; 
	public static String COSMIC_V = "COSMIC-SMPL"; 
	public static String DB_SNP = "dbSNP"; 
	public static String CLINVAR = "ClinVar-V"; 
}
