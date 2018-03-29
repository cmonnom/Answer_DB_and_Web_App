package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;
import utsw.bicf.answer.reporting.parse.AnnotationCategory;

public class FinalReportTemplate {

	PDDocument mainDocument;

	String ngsLabLogoPath;
	String utswLogoPath;
	String title;
	List<TreatmentOption> summary;
	Patient p;
	File output;
	private float latestYPosition;
	float pageWidthMinusMargins;
	float pageHeight;
	float tableYNewPageStart;
	String finalInterpretation;
	List<GeneVariantDetails> geneDetails;
	String[] comments;
	List<Link> links = new ArrayList<Link>();
	File tempFile;

	public FinalReportTemplate(File output, String title, Patient p, String finalInterpretation,
			List<TreatmentOption> summary, List<GeneVariantDetails> geneDetails, String[] comments) throws IOException {
		this.output = output;
		if (output.exists()) {
			output.delete();
		}
		this.tempFile = new File(output.getParentFile(), "temp.pdf");
		if (tempFile.exists()) {
			tempFile.delete();
		}
		this.title = title;
		this.p = p;
		this.summary = summary;
		this.finalInterpretation = finalInterpretation;
		this.geneDetails = geneDetails;
		this.comments = comments;
		init();
	}

	private void init() throws IOException {

		this.mainDocument = new PDDocument();


		// java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		FinalReportTemplateConstants.MAIN_FONT_TYPE = PDType0Font.load(mainDocument,
				new File("C:/Windows/Fonts/Arial.ttf"));
		FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD = PDType0Font.load(mainDocument,
				new File("C:/Windows/Fonts/Arial.ttf"));
		PDPage page = new PDPage(PDRectangle.LETTER);
		mainDocument.addPage(page);
		this.pageWidthMinusMargins = page.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINLEFT
				- FinalReportTemplateConstants.MARGINRIGHT;
		this.pageHeight = page.getMediaBox().getHeight();
		this.tableYNewPageStart = pageHeight - FinalReportTemplateConstants.MARGINTOP;

		this.addAddress();
		this.addNGSImageElement();
		this.addUTSWImageElement();
		this.addTitle();
		this.createPatientTable();
		this.createTreatmentOptionsSummaryTable();
		this.addFinalInterpretation();
		this.addGeneDetails();
		this.addComments();
		this.addInformationAboutTheTest();

		this.addFooters();
		
		//annotate the document at the end because it needs to be reloaded
		//to avoid font conversion issues
		this.addLinks(); 
	}

	public void addLinks() throws IOException {
		this.saveTemp(); // save and close to a temp file
		mainDocument = PDDocument.load(tempFile);
		for (Link link : links) {
			this.addLink(link);
		}
	}

	/**
	 * Create a link as an annotation after the document is done being written. Each
	 * link object is added to links and then all links are created at the end
	 * 
	 * FIXME there could be an issue with finding the same string multiple times.
	 * All instances would have a link.
	 * 
	 * @param link
	 * @throws IOException
	 */
	public void addLink(Link link) throws IOException {

		PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
		borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
		borderULine.setWidth(1);

		HyperLinkReplacer linker = new HyperLinkReplacer(link, link.getUrlLabel());
		// for now, scan all pages
		linker.setStartPage(0);
		linker.setEndPage(mainDocument.getNumberOfPages());

		Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
		linker.writeText(mainDocument, dummy);

		//now we should have a list of coordinates for each link
		for (LinkCoordinates coords : linker.getLinkCoords()) {
			PDAnnotationLink annotation = new PDAnnotationLink();
			annotation.setBorderStyle(borderULine);
			
			List<PDAnnotation> annotations = mainDocument.getPage(coords.getCurrentPageNb()).getAnnotations();
			annotations.add(annotation);
			
			PDRectangle position = new PDRectangle();
			position.setLowerLeftX(coords.getLowerLeftX());
			position.setLowerLeftY(this.pageHeight - coords.getLowerLeftY());
			position.setUpperRightX(coords.getUpperRightX());
			position.setUpperRightY(this.pageHeight - coords.getUpperRightY());
			annotation.setRectangle(position);
			
			PDActionURI action = new PDActionURI();
			action.setURI(link.getUrl() + "?pageNb=" + coords.getCurrentPageNb());
			annotation.setAction(action);
			
		}
		

	}

	private void addAddress() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;

		float tableWidth = pageWidthMinusMargins / 3;

		BaseTable table = new BaseTable(yPos, yPos, 0, tableWidth, FinalReportTemplateConstants.MARGINLEFT,
				mainDocument, firstPage, false, true);
		for (String line : FinalReportTemplateConstants.ADDRESS) {
			Cell<PDPage> cell = table.createRow(FinalReportTemplateConstants.ADDRESS_FONT_SIZE).createCell(100, line);
			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
			cell.setFontSize(FinalReportTemplateConstants.ADDRESS_FONT_SIZE);
			cell.setBottomPadding(0);
			cell.setTopPadding(0);
			cell.setLeftPadding(0);
		}
		latestYPosition = table.draw();

	}

	private void addNGSImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		Image ngsImage = new Image(ImageIO.read(new File(FinalReportTemplateConstants.NGS_LOGO_PATH)));
		ngsImage = ngsImage.scaleByWidth(118);
		ngsImage.draw(mainDocument, contentStream, 247, yPos);
		contentStream.close();
	}

	private void addUTSWImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		Image ngsImage = new Image(ImageIO.read(new File(FinalReportTemplateConstants.UTSW_LOGO_PATH)));
		ngsImage = ngsImage.scaleByWidth(182);
		ngsImage.draw(mainDocument, contentStream, 400, yPos);
		contentStream.close();
	}

	private void addTitle() throws IOException {
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, title, FinalReportTemplateConstants.MAIN_FONT_TYPE, 18,
				FinalReportTemplateConstants.MARGINLEFT, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 20; // position of the patient table
		contentStream.close();
	}

	private void createPatientTable() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		float tableWidth = pageWidthMinusMargins / 3;

		boolean cellBorder = false;
		float defaultFont = FinalReportTemplateConstants.ADDRESS_FONT_SIZE;
		BaseTable leftTable = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, cellBorder, true);

		this.createRow(leftTable, "Name", p.getFirstName() + " " + p.getLastName(), 10,
				FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		this.createRow(leftTable, "MRN", p.getMRN(), 10, FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		this.createRow(leftTable, "DOB", p.getDateOfBirth(), defaultFont);
		this.createRow(leftTable, "Sex", p.getSex(), defaultFont);
		this.createRow(leftTable, "Order #", p.getOrderNb(), defaultFont);
		this.createRow(leftTable, "Lab Accession #", p.getLabAccessionNb(), defaultFont);
		this.createRow(leftTable, "Report Accession #", p.getReportAccessionNb(), defaultFont);
		this.createRow(leftTable, "Tumor Specimen #", p.getTumorSpecimenNb(), defaultFont);
		this.createRow(leftTable, "Germline Specimen #", p.getGermlineSpecimenNb(), defaultFont);
		leftTable.draw();
		float maxTableHeight = leftTable.getHeaderAndDataHeight();

		BaseTable middleTable = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() - 1, mainDocument, firstPage, cellBorder,
				true);
		this.createRow(middleTable, "Ordered by", p.getOrderedBy(), defaultFont);
		this.createRow(middleTable, "Institution", p.getInstitution(), defaultFont);
		this.createRow(middleTable, "Tumor Tissue", p.getTumorTissue(), defaultFont);
		this.createRow(middleTable, "Germline Tissue", p.getGermlineTissue(), defaultFont);
		this.createRow(middleTable, "ICD-10-CM", p.getIcd10(), defaultFont);
		this.createRow(middleTable, "Clinical Stage", p.getClinicalStage(), defaultFont);
		this.createRow(middleTable, "Treatment Status", p.getTreatmentStatus(), defaultFont);
		middleTable.draw();
		maxTableHeight = Math.max(maxTableHeight, middleTable.getHeaderAndDataHeight());

		BaseTable rightTable = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth() - 2,
				mainDocument, firstPage, cellBorder, true);
		this.createRow(rightTable, "Order Date", p.getOrderDate(), defaultFont);
		this.createRow(rightTable, "Tumor Collection Date", p.getTumorCollectionDate(), defaultFont);
		this.createRow(rightTable, "Lab Received Date", p.getLabReceivedDate(), defaultFont);
		this.createRow(rightTable, "Report Date", p.getReportDate(), defaultFont);
		this.createRow(rightTable, "Report electronically signed by", p.getReportSignedBy(), defaultFont);
		rightTable.draw();
		maxTableHeight = Math.max(maxTableHeight, rightTable.getHeaderAndDataHeight());

		// draw borders
		BaseTable leftTableEmpty = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, true, true);
		leftTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		leftTableEmpty.draw();
		BaseTable middleTableEmpty = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() - 1, mainDocument, firstPage, true,
				true);
		middleTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		middleTableEmpty.draw();
		BaseTable rightTableEmpty = new BaseTable(latestYPosition, latestYPosition, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth() - 2,
				mainDocument, firstPage, true, true);
		rightTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		rightTableEmpty.draw();

		latestYPosition -= maxTableHeight;
	}

	private void createTreatmentOptionsSummaryTable() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		float tableWidth = pageWidthMinusMargins;

		boolean cellBorder = true;
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		BaseTable table = new BaseTable(latestYPosition, this.tableYNewPageStart,
				FinalReportTemplateConstants.MARGINBOTTOM, tableWidth, FinalReportTemplateConstants.MARGINLEFT,
				mainDocument, firstPage, cellBorder, true);

		Cell<PDPage> cell = table.createRow(12).createCell(100, "TREATMENT OPTIONS SUMMARY");
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setFillColor(FinalReportTemplateConstants.BACKGROUND_GRAY);
		cell.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);

		this.createHeaderRow(table, FinalReportTemplateConstants.TREATMENT_OPTIONS_SUMMARY_HEADERS,
				FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE, FinalReportTemplateConstants.MAIN_FONT_TYPE);

		int counter = 0;
		for (TreatmentOption option : summary) {
			counter++;
			String firstOrLast = null;
			if (counter == 0) {
				firstOrLast = "first";
			} else if (counter == summary.size()) {
				firstOrLast = "last";
			}
			this.createTreatmentOptionsRow(table, option, FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE,
					firstOrLast);
		}
		float yStart = table.draw();
		latestYPosition = yStart - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
	}

	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize) {
		return createRow(table, title, value, fontSize, FinalReportTemplateConstants.MAIN_FONT_TYPE);
	}

	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize, PDFont font) {
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(50f, title, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.GRAY);
		cell.setFontSize(fontSize);
		cell = row.createCell(50f, value, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.BLACK);
		cell.setFontSize(fontSize);
		return row;
	}

	/**
	 * Create a row of the Treatment Options Summary Table
	 * 
	 * @param table
	 * @param option
	 * @param fontSize
	 * @return
	 */
	private Row<PDPage> createTreatmentOptionsRow(BaseTable table, TreatmentOption option, float fontSize,
			String rowNb) {
		Row<PDPage> row = table.createRow(12);
		this.createTreatmentCell(row, option.getGene(), fontSize, "left", rowNb);
		this.createTreatmentCell(row, option.getSequenceChange(), fontSize, null, rowNb);
		this.createTreatmentCell(row, option.getAberration(), fontSize, null, rowNb);
		this.createTreatmentCell(row, option.getFdaApprovedWithIndication(), fontSize, null, rowNb);
		this.createTreatmentCell(row, option.getFdaApprovedOutsideOfIndication(), fontSize, null, rowNb);
		this.createTreatmentCell(row, option.getClinicalTrials(), fontSize, "right", rowNb);
		return row;
	}

	/**
	 * All cells in Treatment Options Summary Table should have the same format
	 * except for the header
	 * 
	 * @param row
	 * @param text
	 * @param fontSize
	 * @return
	 */
	private Cell<PDPage> createTreatmentCell(Row<PDPage> row, String text, float fontSize, String leftOrRight,
			String rowNb) {
		LineStyle thinlineOutter = new LineStyle(new Color(204, 204, 204), 1f);
		LineStyle thinlineInner = new LineStyle(new Color(204, 204, 204), 0.5f);
		Cell<PDPage> cell = row.createCell(text);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setTextColor(Color.BLACK);
		cell.setFontSize(fontSize);
		cell.setFillColor(FinalReportTemplateConstants.LIGHT_BLUE);
		cell.setBottomPadding(10);

		cell.setRightBorderStyle(null); // should be null except for the "right" one

		if ("first".equals(rowNb)) {
			cell.setTopBorderStyle(thinlineInner);
		} else {
			cell.setTopBorderStyle(null);
		}

		if ("left".equals(leftOrRight)) {
			cell.setLeftBorderStyle(thinlineOutter);
		} else {
			cell.setLeftBorderStyle(thinlineInner);
		}

		if ("right".equals(leftOrRight)) {
			cell.setRightBorderStyle(thinlineOutter);
		}

		if ("last".equals(rowNb)) {
			cell.setBottomBorderStyle(thinlineOutter);
		} else {
			cell.setBottomBorderStyle(null);
		}
		return cell;
	}

	/**
	 * Header Row of the Treatment Options Summary table
	 * 
	 * @param table
	 * @param headers
	 * @param fontSize
	 * @param font
	 * @return
	 */
	private Row<PDPage> createHeaderRow(BaseTable table, List<Header> headers, float fontSize, PDFont font) {
		Row<PDPage> row = table.createRow(12);
		LineStyle thinlineOutter = new LineStyle(new Color(204, 204, 204), 1f);
		LineStyle thinlineInner = new LineStyle(new Color(204, 204, 204), 0.5f);
		Color backgroundColor = new Color(31, 45, 68);
		int counter = 0;
		for (Header header : headers) {
			counter++;
			Cell<PDPage> cell = row.createCell(header.getWidth(), header.getText());
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setValign(VerticalAlignment.TOP);
			cell.setFont(font);
			cell.setTextColor(Color.WHITE);
			cell.setFontSize(fontSize);
			cell.setFillColor(backgroundColor);
			cell.setTopBorderStyle(null);
			cell.setBottomBorderStyle(thinlineInner);
			if (counter == 1) { // left most cell
				cell.setLeftBorderStyle(thinlineOutter);
				cell.setRightBorderStyle(null);
			} else if (counter == headers.size()) { // right most cell
				cell.setLeftBorderStyle(thinlineInner);
				cell.setRightBorderStyle(thinlineOutter);
			} else {
				cell.setLeftBorderStyle(thinlineInner);
				cell.setRightBorderStyle(null);
			}

		}
		table.addHeaderRow(row);
		return row;
	}

	private void addFinalInterpretation() throws IOException {
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		// this.setMargins(currentPage);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, "FINAL INTERPRETATION", FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD,
				FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE, FinalReportTemplateConstants.MARGINLEFT,
				latestYPosition, Color.BLACK);
		contentStream.close();

		float tableYPos = latestYPosition - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		float tableWidth = pageWidthMinusMargins;

		BaseTable table = new BaseTable(tableYPos, this.tableYNewPageStart, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(100, finalInterpretation);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.LIGHT_BLUE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		float yStart = table.draw();
		latestYPosition = yStart - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;

	}

	private void addGeneDetails() throws IOException {
		for (GeneVariantDetails geneDetail : this.geneDetails) {
			// int pageNbBefore = this.mainDocument.getNumberOfPages() - 1;
			BaseTable table = this.createGeneDetailsTable(geneDetail);
			this.createClinicalTrialMatch(table, geneDetail);
			this.createInformationTable(table, geneDetail);
			latestYPosition = table.draw();
			// int pageNbAfter = this.mainDocument.getNumberOfPages() - 1;
			for (ClinicalTrialMatch trial : geneDetail.getClinicalTrialMatches()) {
				if (trial.getUrl() != null) {
					Link link = new Link(trial.getUrlLabel(), trial.getUrl());
					if (!links.contains(link)) {
						links.add(link);
					}
				}
			}
		}
	}

	private BaseTable createGeneDetailsTable(GeneVariantDetails geneDetail) throws IOException {
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		// this.setMargins(currentPage);
		float tableYPos = latestYPosition - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		float tableWidth = pageWidthMinusMargins;

		// FIRST ROW
		BaseTable table = new BaseTable(tableYPos, tableYNewPageStart, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
		Row<PDPage> row = table.createRow(12);
		// headers
		this.createGeneHeaderCell(row, "Gene", 25);
		this.createGeneHeaderCell(row, "Aberration", 25);
		this.createGeneHeaderCell(row, "Tumor Allele Frequency", 25);
		this.createGeneHeaderCell(row, "CNV", 25);
		// values
		row = table.createRow(12);
		this.createGeneValueCell(row, geneDetail.getGene(), 25);
		this.createGeneValueCell(row, geneDetail.getAberration(), 25);
		this.createGeneValueCell(row, geneDetail.getTaf(), 25);
		this.createGeneValueCell(row, geneDetail.getCnv(), 25);

		// SECOND ROW
		row = table.createRow(12);
		// headers
		this.createGeneHeaderCell(row, "Coordinate", 25);
		this.createGeneHeaderCell(row, "Transcript ID", 25);
		this.createGeneHeaderCell(row, "NP_Number", 25);
		this.createGeneHeaderCell(row, "Variant Qualifier", 25);
		// values
		row = table.createRow(12);
		this.createGeneValueCell(row, geneDetail.getCoord(), 25);
		this.createGeneValueCell(row, geneDetail.getTranscriptId(), 25);
		this.createGeneValueCell(row, geneDetail.getNpNumber(), 25);
		this.createGeneValueCell(row, geneDetail.getVariantQualifier(), 25);

		// THIRD ROW
		row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(100, "THERAPY MATCH");
		cell.setBorderStyle(null);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GENE_GREEN_2);
		cell.setTextColor(FinalReportTemplateConstants.GENE_TEXT_GREEN);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		// REST OF THE GREEN TABLE
		this.createTherapyMatchHeaderRow(table, "FDA Approved therapy within indication");
		this.createTherapyMatchRow(table, geneDetail.getFdaWithinIndication());
		this.createTherapyMatchHeaderRow(table, "FDA Approved therapy outside this indication");
		this.createTherapyMatchRow(table, geneDetail.getFdaOutsideIndication());
		this.createTherapyMatchHeaderRow(table, "Treatment approach");
		String treatmentApproach = "";
		if (geneDetail.getTreatmentApproach() != null) {
			treatmentApproach = geneDetail.getTreatmentApproach().toPlainText();
		}
		this.createTherapyMatchRow(table, treatmentApproach);
		this.createTherapyMatchHeaderRow(table, "Resistance and Interactions ");
		this.createTherapyMatchRow(table, geneDetail.getResistanceAndInteractions());
		// empty cell for padding
		this.createTherapyMatchRow(table, "");

		return table;
	}

	// Green header for the gene detail table
	private Cell<PDPage> createGeneHeaderCell(Row<PDPage> row, String text, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GENE_GREEN_1);
		cell.setTextColor(FinalReportTemplateConstants.GENE_TEXT_GREEN);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setBottomPadding(2); // make it closer to the value cell
		return cell;
	}

	private Cell<PDPage> createGeneValueCell(Row<PDPage> row, String text, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GENE_GREEN_1);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setTopPadding(2); // make it closer to the header cell
		return cell;
	}

	private Cell<PDPage> createTherapyMatchHeaderRow(BaseTable table, String text) {
		Cell<PDPage> cell = createTherapyMatchRow(table, text);
		cell.setTextColor(FinalReportTemplateConstants.GENE_TEXT_GREEN);
		return cell;
	}

	private Cell<PDPage> createTherapyMatchRow(BaseTable table, String text) {
		Cell<PDPage> cell = this.createTherapyMatchCell(table.createRow(10), text);
		return cell;
	}

	private List<Cell<PDPage>> createTherapyMatchRow(BaseTable table, List<String> texts) {
		List<Cell<PDPage>> cells = new ArrayList<Cell<PDPage>>();
		for (String text : texts) {
			Cell<PDPage> cell = this.createTherapyMatchCell(table.createRow(10), text);
			cells.add(cell);
		}
		return cells;
	}

	private Cell<PDPage> createTherapyMatchCell(Row<PDPage> row, String text) {
		Cell<PDPage> cell = row.createCell(100, text);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GENE_GREEN_3);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		cell.setTopPadding(2);
		cell.setBottomPadding(2);
		return cell;
	}

	private Cell<PDPage> createClinicalTrialMatchCell(Row<PDPage> row, String text, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.LIGHT_BLUE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		return cell;
	}

	private void createClinicalTrialMatch(BaseTable table, GeneVariantDetails geneDetail) throws IOException {
		// FIRST ROW
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(100, "CLINICAL TRIAL MATCH");
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GENE_BLUE);
		cell.setTextColor(FinalReportTemplateConstants.GENE_TEXT_BLUE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		for (ClinicalTrialMatch clinicalTrialMatch : geneDetail.getClinicalTrialMatches()) {
			if (clinicalTrialMatch != null) {
				row = table.createRow(12);
				cell = this.createClinicalTrialMatchCell(row, clinicalTrialMatch.getDescription(), 100);
				row = table.createRow(12);
				cell = this.createClinicalTrialMatchCell(row, "Location", 25);
				cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
				cell = this.createClinicalTrialMatchCell(row, clinicalTrialMatch.getLocation(), 50);
				cell = this.createClinicalTrialMatchCell(row, "Recruting", 25);
				cell.setTextColor(FinalReportTemplateConstants.GENE_TEXT_BLUE);
				cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);

				row = table.createRow(12);
				cell = this.createClinicalTrialMatchCell(row, "Contact", 25);
				cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
				cell = this.createClinicalTrialMatchCell(row, clinicalTrialMatch.getContact(), 50);
				cell = this.createClinicalTrialMatchCell(row, clinicalTrialMatch.getUrlLabel(), 25);
				cell.setTextColor(FinalReportTemplateConstants.LINK_BLUE);

				row = table.createRow(12);
				cell = this.createClinicalTrialMatchCell(row, clinicalTrialMatch.getBiomarker(), 100);

				row = table.createRow(12);
				cell = this.createClinicalTrialMatchCell(row, "Drugs: " + clinicalTrialMatch.getDrugs(), 100);
			}
		}
	}

	private void createInformationTable(BaseTable table, GeneVariantDetails geneDetail) {
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(100,
				"INFORMATION ABOUT " + geneDetail.getGene() + " " + geneDetail.getAberration());
		cell.setBorderStyle(null);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFillColor(FinalReportTemplateConstants.GRAY);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		for (AnnotationCategory c : geneDetail.getAnnotationCategories()) {
			if (c != null) {
				cell = this.createTherapyMatchRow(table, c.toPlainText());
				cell.setFillColor(FinalReportTemplateConstants.BACKGROUND_GRAY);
			}
		}
	}

	private void addComments() throws IOException {
		// Title
		float tableYPos = latestYPosition - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = new BaseTable(tableYPos, this.tableYNewPageStart, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(12).createCell(100, "COMMENTS");
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		// Comments
		for (String comment : comments) {
			Row<PDPage> row = table.createRow(12);
			cell = row.createCell(100, comment);
			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		}
		latestYPosition = table.draw();
	}

	private void addInformationAboutTheTest() throws IOException {
		// Title
		float tableYPos = latestYPosition - FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = new BaseTable(tableYPos, this.tableYNewPageStart, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(12).createCell(100, "INFORMATION ABOUT THE TEST");
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		// Content
		for (String info : FinalReportTemplateConstants.ABOUT_THE_TEST) {
			Row<PDPage> row = table.createRow(12);
			cell = row.createCell(100, info);
			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
			cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
			cell.setBottomPadding(10);
		}
		latestYPosition = table.draw();
	}

	private Cell<PDPage> createFooterCell(Row<PDPage> row, String text, HorizontalAlignment align, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(align);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
		return cell;
	}

	private void addFooters() throws IOException {
		int pageTotal = this.mainDocument.getNumberOfPages();
		float tableYPos = FinalReportTemplateConstants.MARGINBOTTOM;
		float tableWidth = pageWidthMinusMargins;
		for (int i = 0; i < pageTotal; i++) {
			PDPage currentPage = this.mainDocument.getPage(i);
			BaseTable table = new BaseTable(tableYPos, tableYPos, 0, tableWidth,
					FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
			Row<PDPage> row = table.createRow(12);
			this.createFooterCell(row, "BICF Custom", HorizontalAlignment.LEFT, 20);
			this.createFooterCell(row, "MRN " + p.getMRN(), HorizontalAlignment.CENTER, 60);
			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 20);
			table.draw();
		}
	}

	public void saveTemp() throws IOException {
		mainDocument.save(tempFile);
		mainDocument.close();
	}

	public void save(File output) throws IOException {
		mainDocument.save(output);
		mainDocument.close();
	}

	public String getNgsLabLogoPath() {
		return ngsLabLogoPath;
	}

	public void setNgsLabLogoPath(String ngsLabLogoPath) {
		this.ngsLabLogoPath = ngsLabLogoPath;
	}

	public String getUtswLogoPath() {
		return utswLogoPath;
	}

	public void setUtswLogoPath(String utswLogoPath) {
		this.utswLogoPath = utswLogoPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public float getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(float pageHeight) {
		this.pageHeight = pageHeight;
	}

}