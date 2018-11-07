package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
	public static final Color BACKGROUND_LIGHT_GRAY = new Color(250, 250, 250);
	public static final Color LINK_BLUE = new Color(77, 144, 206);
	public static final Color LINK_ANSWER_GREEN = new Color(77, 182, 172);
	public static final LineStyle THINLINE_OUTTER = new LineStyle(new Color(204, 204, 204), 1f);
	public static final LineStyle THINLINE_OUTTER_ANSWER_GREEN = new LineStyle(new Color(77, 182, 172), 1f);

	public static final LineStyle NO_BORDER = new LineStyle(Color.WHITE, 2f);
	public static final LineStyle NO_BORDER_THIN = new LineStyle(Color.WHITE, 1.5f);
	public static final LineStyle LIGHT_GRAY_BORDER_THIN = new LineStyle(BACKGROUND_LIGHT_GRAY, 1.5f);

	public static final Color GENE_COLOR = new Color(255, 171, 64);
	public static final Color THERAPY_COLOR = new Color(77, 182, 172);
	public static final Color TRIAL_COLOR = new Color(97, 184, 101);
	public static final Color CLIN_SIGNIFICANCE_COLOR = new Color(51, 121, 199);
	public static final Color CNV_COLOR = new Color(255, 200, 36);
	public static final Color FTL_COLOR = new Color(59, 162, 244);
	
	public static final LineStyle BORDER_GENE_COLOR = new LineStyle(new Color(255, 171, 64), 1.5f);
	public static final LineStyle BORDER_THERAPY_COLOR = new LineStyle(new Color(77, 182, 172), 1.5f);
	public static final LineStyle BORDER_TRIAL_COLOR = new LineStyle(new Color(97, 184, 101), 1.5f);
	public static final LineStyle BORDER_CLIN_SIGNIFICANCE_COLOR = new LineStyle(new Color(51, 121, 199), 1.5f);
	public static final LineStyle BORDER_CNV_COLOR = new LineStyle(new Color(255, 200, 36), 1.5f);
	public static final LineStyle BORDER_FTL_COLOR = new LineStyle(new Color(59, 162, 244), 1.5f);
	
	
	public static final List<String> ADDRESS = new ArrayList<String>();
	public static final String TRIAL_URL = "https://clinicaltrials.gov/ct2/show/";

	public static final List<Header> TREATMENT_OPTIONS_SUMMARY_HEADERS = new ArrayList<Header>();
	
	//_NAV titles are meant to be slightly different than the regular title
	//This will allow a unique "link" search term when adding links
	public static final String TITLE = "1385-Gene Pan-Cancer Mutation Test";
	public static final String PATIENT_DETAILS_TITLE = "PATIENT RECORD";
	
	public static final String GENE_TITLE = "GENES";
	public static final String INDICATED_THERAPIES_TITLE = "INDICATED THERAPIES";
	public static final String INDICATED_THERAPIES_TITLE_NAV = " INDICATED THERAPIES ";
	public static final String CLINICAL_TRIALS_TITLE = "CLINICAL TRIALS";
	public static final String CLINICAL_TRIALS_TITLE_NAV = " CLINICAL TRIALS ";
	public static final String CNV_TITLE = "COPY NUMBER ALTERATIONS";
	public static final String CNV_TITLE_SHORT = "CNVs";
	public static final String TRANSLOCATION_TITLE = "GENE FUSIONS";
	public static final String TRANSLOCATION_TITLE_SHORT = "FUSIONS";
	public static final String DISCLAMER_TITLE = "INFORMATION ABOUT THE TEST";
	public static final String CLINICAL_SIGNIFICANCE = "CLINICAL SIGNIFICANCE";
	public static final String CLINICAL_SIGNIFICANCE_NAV = "VARIANT DETAILS";
	
	
	static {
		// widths should add up to 100
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Gene", 13));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Sequence Change", 20));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Aberration", 17));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("FDA approved within indication", 16));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("FDA approved outside of indication", 20));
		TREATMENT_OPTIONS_SUMMARY_HEADERS.add(new Header("Clinical Trials", 14));
		
		ADDRESS.add("UTSW NGS Clinical Laboratory");
		ADDRESS.add("Room EB3.302");
		ADDRESS.add("BioCenter At Southwestern Medical District");
		ADDRESS.add("2330 Inwood Road");
		ADDRESS.add("Dallas, TX 75390");
		ADDRESS.add("CLIA ID 45D0861764");
		ADDRESS.add("Director: Ravi Sarode, MD");
	}

	public static final String[] ABOUT_THE_TEST = new String[] { "Test Characteristics and Performance:"
			+ " DNA and RNA are isolated from fresh or formalin-fixed, paraffin-embedded tissues. Sequencing libraries are "
			+ "generated using Kapa Biosystems and Illumina chemistry. A custom panel of DNA probes is used to produce an"
			+ "enriched library containing all exons from over 1.385 cancer-related genes, which are sequenced on Illumina "
			+ "HiSeq 4000, NextSeq 550 or MiSeq instruments. DNA and RNA sequence analyses are done using custom "
			+ "germline, somatic and mRNA bioinformatics pipelines run on the UTSW Bio-High Performance Computer "
			+ "cluster and optimized for detection of single nucleotide variants, indels and known gene fusions. Reports are "
			+ "generated in the Philips IntelliSpace Genomics system (Philips Healthcare, 2 Canal Park, Cambridge, MA)."
			+ "Median target exon coverage for the assay is 900X with 94% of exons at >100X. The minor allele frequency "
			+ "limit of detection is 5% for single nucleotide variants and 10% for indels and known gene fusions. The assay is "
			+ "not informative for mutations outside the 1.385 cancer-related genes or for those regions for which the assay "
			+ "achieves limited coverage. Full details of the genes tested, exon coverage and the bioinformatics pipeline are "
			+ "available at http://www.utsouthwestern.edu/sites/genomics-molecular-pathology/.", "Disclaimer:",
			"This is a laboratory developed test, and its performance characteristics have been determined by the Next "
					+ "Generation Sequencing Clinical Lab, Department of Pathology, UTSW.  It has not been cleared or approved by "
					+ "the U.S. Food and Drug Administration. The U.S. Food and Drug Administration does not require this test to go "
					+ "through premarket review. This test is used for clinical purposes. It should not be regarded as investigational or"
					+ "for research. This laboratory is certified under the Clinical Laboratory Improvement Amendments (1988) as "
					+ "qualified to perform high complexity testing.",
			"N-of-One has provided Philips with the research, analysis, and interpretation on a patient specific basis, of "
					+ "peer-reviewed studies and publically available databases. N-of-One does not provide medical services, nor is "
					+ "any N-of-One employee engaged in the practice of medicine for or on behalf of N-of-One. Some tests, drugs "
					+ "and biomarkers identified in this report may not be approved by the FDA for a particular use or validated for "
					+ "that use. The Content is compiled from sources believed to be reliable. Extensive efforts have been made to "
					+ "make the Content as accurate and as up-to-date as possible. The Content may contain typographical errors "
					+ "and omissions. The Content is for research, professional medical and scientific use only. Copyright N-of-One, "
					+ "Inc. 2011-2017: Not for Distribution, Publication or Re-publication.",
			"Builds and Versions", 
			"Ensembl-vep: v.89.4", 
			"UCSC: v.20130630", 
			"dbNSFP: v.3.2a", 
			"COSMIC: v81",
			"CLINVAR: v.20161201", "VARDB: v.20170901", "COSMIC_FUSION: v81", "THERAPY_AVAILABILITY: v.20170614"
			};

}
