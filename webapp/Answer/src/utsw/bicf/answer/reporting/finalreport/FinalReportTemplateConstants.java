package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDType0Font;

import be.quodlibet.boxable.line.LineStyle;

public class FinalReportTemplateConstants {

	public static final int MARGINTOP = 36, MARGINLEFT = 36, MARGINBOTTOM = 36, MARGINRIGHT = 36, LOGO_MARGIN_TOP = 36;
	public static final int PARAGRAPH_PADDING_BOTTOM = 20;
	public static final int ADDRESS_FONT_SIZE = 8;
	public static final int DEFAULT_TEXT_FONT_SIZE = 12;
	public static final int SMALLER_TEXT_FONT_SIZE = 11;
	public static final int SMALLEST_TEXT_FONT_SIZE = 10;
	public static final int TITLE_TEXT_FONT_SIZE = 14;
//	public static final PDType1Font MAIN_FONT_TYPE = PDType1Font.HELVETICA;
//	public static final PDType1Font MAIN_FONT_TYPE_BOLD = PDType1Font.HELVETICA_BOLD;
	public static PDType0Font MAIN_FONT_TYPE;
	public static PDType0Font MAIN_FONT_TYPE_BOLD;
	public static final Color LIGHT_BLUE = new Color(238, 245, 251);
	public static final Color GENE_GREEN_1 = new Color(236, 242, 172);
	public static final Color GENE_GREEN_2 = new Color(236, 250, 195);
	public static final Color GENE_GREEN_3 = new Color(247, 250, 212);
	public static final Color GENE_TEXT_GREEN = new Color(78, 152, 0);
	public static final Color GENE_BLUE = new Color(156, 194, 229);
	public static final Color GENE_TEXT_BLUE = new Color(26, 73, 132);
	public static final Color LIGHT_GRAY = new Color(128, 128, 128);
	public static final Color GRAY = new Color(178, 178, 178);
	public static final Color BACKGROUND_GRAY = new Color(245, 245, 245);
//	public static final Color BACKGROUND_LIGHT_GRAY = new Color(250, 250, 250);
	public static final Color BACKGROUND_LIGHT_GRAY = new Color(245, 245, 245);
	public static final Color LINK_BLUE = new Color(77, 144, 206);
	public static final Color LINK_ANSWER_GREEN = new Color(77, 182, 172);
	public static final LineStyle THINLINE_OUTTER = new LineStyle(new Color(204, 204, 204), 1f);
	public static final LineStyle THINLINE_OUTTER_ANSWER_GREEN = new LineStyle(new Color(77, 182, 172), 1f);

	public static final LineStyle NO_BORDER = new LineStyle(Color.WHITE, 2f);
	public static final LineStyle NO_BORDER_THIN = new LineStyle(Color.WHITE, 1.5f);
	public static final LineStyle NO_BORDER_ZERO = new LineStyle(Color.WHITE, 0.01f);
	public static final LineStyle LIGHT_GRAY_BORDER_THIN = new LineStyle(BACKGROUND_LIGHT_GRAY, 1.5f);

	public static final Color GENE_COLOR = new Color(255, 171, 64);
	public static final Color THERAPY_COLOR = new Color(77, 182, 172);
	public static final Color CNV_COLOR = new Color(97, 184, 101);
	public static final Color CLIN_SIGNIFICANCE_COLOR = new Color(244, 143, 177); //new Color(51, 121, 199);
	public static final Color TRIAL_COLOR = new Color(255, 200, 36);
	public static final Color FTL_COLOR = new Color(206, 147, 216); //new Color(59, 162, 244);
	public static final Color PUBMED_COLOR = new Color(51, 102, 153);
	public static final Color ABOUT_THE_TEST_COLOR = new Color(200, 200, 200);
	
	public static final LineStyle BORDER_GENE_COLOR = new LineStyle(GENE_COLOR, 1.5f);
	public static final LineStyle BORDER_THERAPY_COLOR = new LineStyle(THERAPY_COLOR, 1.5f);
	public static final LineStyle BORDER_TRIAL_COLOR = new LineStyle(TRIAL_COLOR, 1.5f);
	public static final LineStyle BORDER_CLIN_SIGNIFICANCE_COLOR = new LineStyle(CLIN_SIGNIFICANCE_COLOR, 1.5f);
	public static final LineStyle BORDER_CNV_COLOR = new LineStyle(CNV_COLOR, 1.5f);
	public static final LineStyle BORDER_FTL_COLOR = new LineStyle(FTL_COLOR, 1.5f);
	public static final LineStyle BORDER_PUBMED_COLOR = new LineStyle(PUBMED_COLOR, 1.5f);
	public static final LineStyle BORDER_ABOUT_THE_TEST_COLOR = new LineStyle(ABOUT_THE_TEST_COLOR, 1.5f);
	
	
	public static final List<String> ADDRESS = new ArrayList<String>();
	public static final String TRIAL_URL = "https://clinicaltrials.gov/ct2/show/";
	public static final String PUBMED_URL = "https://www.ncbi.nlm.nih.gov/pubmed/?term=";

	public static final List<Header> TREATMENT_OPTIONS_SUMMARY_HEADERS = new ArrayList<Header>();
	
	//_NAV titles are meant to be slightly different than the regular title
	//This will allow a unique "link" search term when adding links
	public static final String DEFAULT_TITLE = "1385-Gene Pan-Cancer Mutation Test";
	public static final String PATIENT_DETAILS_TITLE = "PATIENT RECORD";
	public static final String CASE_SUMMARY_TITLE = "CASE SUMMARY";
	
	public static final String GENE_TITLE = "BIOMARKERS";
	public static final String INDICATED_THERAPIES_TITLE = "INDICATED THERAPIES";
	public static final String INDICATED_THERAPIES_TITLE_NAV = " INDICATED THERAPIES ";
	public static final String CLINICAL_TRIALS_TITLE = "CLINICAL TRIALS";
	public static final String CLINICAL_TRIALS_TITLE_NAV = " CLINICAL TRIALS ";
	public static final String CNV_TITLE = "COPY NUMBER ALTERATIONS";
	public static final String CNV_TITLE_SHORT = "CNVs";
	public static final String TRANSLOCATION_TITLE = "GENE FUSIONS";
	public static final String TRANSLOCATION_TITLE_SHORT = " FUSIONS ";
	public static final String DISCLAMER_TITLE = "INFORMATION ABOUT THE TEST";
	public static final String CLINICAL_SIGNIFICANCE = "CLINICAL SIGNIFICANCE";
	public static final String CLINICAL_SIGNIFICANCE_NAV = "VARIANT TIERS";
	public static final String PUBMED_REFERENCE_TITLE = "PUBMED REFERENCES";
	public static final String LOW_COV_TITLE = "LOW COVERAGE EXONS";
	
	
	static {
		// widths should add up to 100
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Gene", 13));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Sequence Change", 20));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Aberration", 17));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("FDA approved within indication", 16));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("FDA approved outside of indication", 20));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Clinical Trials", 14));
		
		ADDRESS.add("UTSW Clinical NGS Laboratory");
		ADDRESS.add("Room EB3.302");
		ADDRESS.add("BioCenter At Southwestern Medical District");
		ADDRESS.add("2330 Inwood Road");
		ADDRESS.add("Dallas, TX 75390");
		ADDRESS.add("CLIA ID 45D0861764");
		ADDRESS.add("Director: Ravi Sarode, MD");
	}
	
}
