package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;
import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.model.ClinicalTest;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.model.hybrid.ReportNavigationRow;
import utsw.bicf.answer.model.hybrid.SampleLowCoverageFromQC;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.EncodingGlyphException;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;

public class FinalReportPDFTemplate {

	Report report;
	PDDocument mainDocument;
	float pageWidthMinusMargins;
	float pageHeight;
	float latestYPosition;
	FileProperties fileProps;
	OrderCase caseSummary;
	File tempFile;
	OtherProperties otherProps;
	List<Link> links = new ArrayList<Link>();
	Map<Integer, List<FooterColor>> colorPerPage = new HashMap<Integer, List<FooterColor>>();
	User signedBy;
	ClinicalTest clinicalTest;

	public FinalReportPDFTemplate(Report report, FileProperties fileProps, OrderCase caseSummary, OtherProperties otherProps, User signedBy, ClinicalTest clinicalTest) throws EncodingGlyphException {
		this.otherProps = otherProps;
		this.report = report;
		this.fileProps = fileProps;
		this.caseSummary = caseSummary;
		this.signedBy = signedBy;
		this.clinicalTest = clinicalTest;
		try {
			this.tempFile = new File(fileProps.getPdfFilesDir(), System.currentTimeMillis() + "_temp.pdf");
			if (tempFile.exists()) {
				tempFile.delete();
			}
			init();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException, URISyntaxException, EncodingGlyphException {
		this.mainDocument = new PDDocument();
		PDPage page = new PDPage(PDRectangle.LETTER);
		mainDocument.addPage(page);
		this.pageWidthMinusMargins = page.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINLEFT
				- FinalReportTemplateConstants.MARGINRIGHT;
		this.pageHeight = page.getMediaBox().getHeight();

		this.addAddress();
		this.addNGSImageElement();
		this.addUTSWImageElement();
		this.addTitle();
		this.createPatientTable();
		this.addNotes();
		this.createNavigationTable();
		this.createIndicatedTherapiesTable();
		this.createClinicalTrialsTable();
		this.createClinicalSignificanceTables();
		this.createCNVTable();
		this.createTranslocationTable();
		this.createPubmedTable();
//		this.createLowCoverageTable();
		this.addInformationAboutTheTest();
		this.addFooters();
		if (report.getFinalized() == null || !report.getFinalized()) {
			this.addWatermark();
		}
		
		this.addLinks();
	}

	private void createLowCoverageTable() throws IOException, EncodingGlyphException {
		List<SampleLowCoverageFromQC> lowCovs = report.getLowCoverages();
		Map<String, List<Integer>> exonsPerGenes = new HashMap<String, List<Integer>>();
		
		for (SampleLowCoverageFromQC lowCov : lowCovs) {
			List<Integer> exons = exonsPerGenes.get(lowCov.getGene());
			if (exons == null) {
				exons = new ArrayList<Integer>();
			}
			exons.add(lowCov.getExonNb());
			exonsPerGenes.put(lowCov.getGene(), exons);
		}
		this.updatePotentialNewPagePosition(exonsPerGenes.size());
		
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.PUBMED_COLOR, FinalReportTemplateConstants.BORDER_PUBMED_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		BaseTable table = createNewTable(currentPage);
		//Title
		Row<PDPage> row = table.createRow(12); 
//				table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.LOW_COV_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setTextColor(Color.WHITE);
		cellHeader.setFillColor(FinalReportTemplateConstants.PUBMED_COLOR);
		
		if (exonsPerGenes.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No exon with low coverage");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(25, "GENE");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(75, "EXONS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			boolean greyBackground = false;
			for (String gene : exonsPerGenes.keySet()) {
				List<Integer> exons = exonsPerGenes.get(gene);
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(25, gene);
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(75, exons.stream().filter(e -> e != null).map(e -> e.toString()).collect(Collectors.joining(", ")));
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Low Cov Table." );
		}
	}

	private void addAddress() throws IOException, EncodingGlyphException {
		FinalReportTemplateConstants.MAIN_FONT_TYPE = PDType0Font.load(mainDocument,
				fileProps.getPdfFontFile());
		FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD = PDType0Font.load(mainDocument,
				fileProps.getPdfFontBoldFile());
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
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in address." );
		}

	}

	private void addNGSImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		File ngsLogoFile = new File(fileProps.getPdfLogoDir(), fileProps.getPdfNGSLogoName());
		Image ngsImage = new Image(ImageIO.read(ngsLogoFile));
		ngsImage = ngsImage.scaleByWidth(100);
		float xPos = (firstPage.getMediaBox().getWidth() - ngsImage.getWidth()) / 2 ;
		ngsImage.draw(mainDocument, contentStream, xPos, yPos);
		contentStream.close();
	}

	private void addUTSWImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP - 2;
		Image ngsImage = new Image(ImageIO.read(new File(fileProps.getPdfLogoDir(), fileProps.getPdfUTSWLogoName())));
		ngsImage = ngsImage.scaleByWidth(100);
		float xPos = firstPage.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINRIGHT - ngsImage.getWidth();
		ngsImage.draw(mainDocument, contentStream, xPos, yPos);
		contentStream.close();
	}

	private void addTitle() throws IOException {
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		String testName = FinalReportTemplateConstants.DEFAULT_TITLE;
		if (report.getLabTestName() != null) {
			testName = report.getLabTestName();
		}
		PDStreamUtils.write(contentStream, testName, FinalReportTemplateConstants.MAIN_FONT_TYPE, 16,
				FinalReportTemplateConstants.MARGINLEFT, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 20; // position of the subtitle
		
		if (report.getTumorPanel() != null && !report.getTumorPanel().equals("")) {
			String tumorPanel = (report.getTumorPanel() + " Panel Report").toUpperCase(); 
			PDStreamUtils.write(contentStream, tumorPanel, FinalReportTemplateConstants.MAIN_FONT_TYPE, 16,
					FinalReportTemplateConstants.MARGINLEFT, latestYPosition, Color.BLACK);
			latestYPosition = latestYPosition - 30; // position of the patient table title
		}
		else {
			latestYPosition = latestYPosition - 10;
		}
		contentStream.close();
	}

	private void addNotes() throws IOException, EncodingGlyphException {
		// Title
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(0);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE).createCell(100, FinalReportTemplateConstants.CASE_SUMMARY_TITLE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);

		// Content
		Row<PDPage> row = table.createRow(12);
		cell = row.createCell(100, report.getSummary() != null ? report.getSummary().replaceAll("\\n", "<br/>") : ""); //otherwise, "No glyph for U+000A in font Calibri"
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
		try {
			latestYPosition = table.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Case Summary." );
		}
	}
	
	private void createPatientTable() throws IOException, EncodingGlyphException {
		PatientInfo patientDetails = report.getPatientInfo();

		PDPage firstPage = mainDocument.getPage(0);

		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.PATIENT_DETAILS_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 20; // position of the patient table
		contentStream.close();

		float tableWidth = pageWidthMinusMargins / 3;

		boolean cellBorder = false;
		float defaultFont = FinalReportTemplateConstants.SMALLEST_TEXT_FONT_SIZE;

		BaseTable leftTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, cellBorder, true);
		List<CellItem> leftTableItems = patientDetails.getPatientTables().get(0).getItems();
//		//add signed by
//		String signedByName = "Dr. " + signedBy.getFullName();
//		if (report.getFinalized() == null || !report.getFinalized()) {
//			signedByName = "NOT SIGNED";
//		}
//		leftTableItems.add(new CellItem("Report Electronically Signed By", signedByName));
		for (CellItem item : leftTableItems) {
			formatPatientCells(defaultFont, leftTable, item);
		}
//		try {
//			leftTable.draw();
//		} catch (IllegalArgumentException e) {
//			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (left table)." );
//		}
		float maxTableHeight = leftTable.getHeaderAndDataHeight();

		BaseTable middleTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth(), mainDocument, firstPage, cellBorder,
				true);
		List<CellItem> middleTableItems = patientDetails.getPatientTables().get(1).getItems();
		
		for (CellItem item : middleTableItems) {
			formatPatientCells(defaultFont, middleTable, item);
		}
//		try {
//			middleTable.draw();
//		} catch (IllegalArgumentException e) {
//			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (middle table)." );
//		}

		maxTableHeight = Math.max(maxTableHeight, middleTable.getHeaderAndDataHeight());

		BaseTable rightTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth(),
				mainDocument, firstPage, cellBorder, true);
		List<CellItem> rightTableItems = patientDetails.getPatientTables().get(2).getItems();
		//add report date
		String dateModified = report.getDateModified();
		if (dateModified == null || dateModified.equals("")) {
			dateModified = LocalDate.now().format(TypeUtils.monthFormatter);
		}
		if (report.getFinalized() != null && report.getFinalized() && report.getDateFinalized() != null ) {
			dateModified = report.getDateFinalized().split("T")[0];
		}
		rightTableItems.add(3, new CellItem("Report Date", dateModified));
//		//add signed by
//		String signedByName = "Dr. " + signedBy.getFullName();
//		if (report.getFinalized() == null || !report.getFinalized()) {
//			signedByName = "NOT SIGNED";
//		}
//		rightTableItems.add(new CellItem("Report Electronically Signed By", signedByName));
		for (CellItem item : rightTableItems) {
			formatPatientCells(defaultFont, rightTable, item);
		}
//		try {
//			rightTable.draw();
//		} catch (IllegalArgumentException e) {
//			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (right table)." );
//		}
		maxTableHeight = Math.max(maxTableHeight, rightTable.getHeaderAndDataHeight());
		
		List<BaseTable> tables = new ArrayList<BaseTable>();
		tables.add(leftTable);
		tables.add(middleTable);
		tables.add(rightTable);
		tables.sort(new Comparator<BaseTable>() {

			@Override
			public int compare(BaseTable o1, BaseTable o2) {
				if (o1.getHeaderAndDataHeight() < o2.getHeaderAndDataHeight()) {
					return 1;
				}
				else if (o1.getHeaderAndDataHeight() > o2.getHeaderAndDataHeight()) {
					return -1;
				}
				return 0;
			}
		});
		maxTableHeight = tables.get(0).getHeaderAndDataHeight();
		float avgRowHeight = maxTableHeight / tables.get(1).getRows().size();
		for (Row<PDPage> row : tables.get(1).getRows()) {
			row.setHeight(avgRowHeight);
		}
		avgRowHeight = maxTableHeight / tables.get(2).getRows().size();
		for (Row<PDPage> row : tables.get(2).getRows()) {
			row.setHeight(avgRowHeight);
		}
		
		maxTableHeight += 5;
		
		//signature
		BaseTable signatureTable = new BaseTable(latestYPosition - maxTableHeight, FinalReportTemplateConstants.MARGINTOP, 0, pageWidthMinusMargins * 0.5f,
				FinalReportTemplateConstants.MARGINLEFT + pageWidthMinusMargins * 0.5f, mainDocument, firstPage, cellBorder, true);
		String signedByName = "Dr. " + signedBy.getFullName();
		if (report.getFinalized() == null || !report.getFinalized()) {
			signedByName = "NOT SIGNED";
		}
		this.createRow(signatureTable, "Report Electronically Signed By", signedByName, defaultFont);
		try {
			signatureTable.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (right table)." );
		}
		maxTableHeight += signatureTable.getHeaderAndDataHeight();
		
		//now draw the adjusted tables
		try {
			leftTable.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (left table)." );
		}
		try {
			middleTable.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (middle table)." );
		}
		try {
			rightTable.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Patient Table (right table)." );
		}
		
		// draw borders
		BaseTable allWidthEmptyTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, pageWidthMinusMargins,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, true, true);
		Row<PDPage> row = allWidthEmptyTable.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		allWidthEmptyTable.draw();
		
		latestYPosition -= maxTableHeight + 10;
		
	}

	public void formatPatientCells(float defaultFont, BaseTable rightTable, CellItem item) throws IOException {
		if (item.getField() != null && item.getField().equals("dedupPctOver100X")) {
			if (item.getValue() != null) {
				item.setValue(item.getValue() + "%");
			}
			else {
				item.setValue("");
			}
		}
		else if (item.getField() != null && item.getField().equals("tumorPercent")) {
			if (item.getValue() != null) {
				item.setValue(item.getValue() + "%");
			}
			else {
				item.setValue("");
			}
		}
		else if (item.getField() != null && item.getField().equals("msi")) {
			String value = item.getValue() == null || item.getValue().equals("Not calculated") ? "" : item.getValue() + "%";
			item.setValue(value);
		}
		if (item.getValue2() != null && !item.getValue2().equals("")) {
			item.setValue(item.getValue2() + "<br/>" + item.getValue());
		}
	
		this.createRow(rightTable, item.getLabel(), item.getValue(), defaultFont);
	}

	private void applyPatientRecordTableBorderFormatting(Cell<PDPage> cell) {
		cell.setTopBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
	}

	private void updatePotentialNewPagePosition(long rows) {
		if ((latestYPosition <= pageHeight * 0.4 && rows >= 2) //passed 60% of the page and table has more than 2 rows
//				|| rows > 5 // too many rows
				|| latestYPosition <= 150 //too close to the end of the page
			) {
			mainDocument.addPage(new PDPage(PDRectangle.LETTER));
			latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
//			System.out.println(latestYPosition + " pageheight:" + pageHeight + " " + rows + "rows page break");
		}
		else {
			latestYPosition -= 20;
//			System.out.println(latestYPosition + " pageheight:" + pageHeight + " " + rows + "rows no break");
		}
	}
	
	private void updatePageBreakPosition() {
		mainDocument.addPage(new PDPage(PDRectangle.LETTER));
		latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
	}

	private void createNavigationTable() throws IOException, EncodingGlyphException {
		this.updatePotentialNewPagePosition(0);
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = createNewTable(currentPage);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(Color.WHITE, FinalReportTemplateConstants.NO_BORDER_ZERO);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		

		Row<PDPage> headerRow = table.createRow(12);
		table.addHeaderRow(headerRow);
		Cell<PDPage> cell = headerRow.createCell(20, FinalReportTemplateConstants.GENE_TITLE);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.GENE_COLOR);
		cell = headerRow.createCell(20, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.THERAPY_COLOR);
		cell = headerRow.createCell(20, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.TRIAL_COLOR);
		cell = headerRow.createCell(20, FinalReportTemplateConstants.CLINICAL_SIGNIFICANCE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		cell = headerRow.createCell(9, FinalReportTemplateConstants.CNV_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CNV_COLOR);
		cell = headerRow.createCell(11, FinalReportTemplateConstants.TRANSLOCATION_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.FTL_COLOR);
		
		
		report.updateClinicalTrialCount(); //update now in case the trial selection changed
		boolean grayBackground = false;
//		List<String> sortedKeys = report.getNavigationRowsPerGene().keySet().stream().sorted().collect(Collectors.toList());
		List<ReportNavigationRow> sortedValues = report.getNavigationRowsPerGene().values().stream().sorted().collect(Collectors.toList());
		//concatenate VUS into one row
		//on hold for now
//		String vusGenes = report.getNavigationRowsPerGeneVUS().keySet().stream().collect(Collectors.joining(" "));
//		if (vusGenes != null && !vusGenes.equals("")) {
//			ReportNavigationRow vusRow = report.getNavigationRowsPerGeneVUS().values().stream().findFirst().get();
//			vusRow.setGene(vusGenes);
//			vusRow.setLabel(vusGenes);
//			sortedValues.add(vusRow);
//		}
		//count and extract Tier 3 if too many
		long tier3Count = sortedValues.stream()
				.filter(r -> r.getUnknownClinicalSignificanceCount() > 0
						&& r.getIndicatedTherapyCount() == 0)
				.collect(Collectors.counting());
		List<ReportNavigationRow> sortedValuesFiltered = new ArrayList<ReportNavigationRow>();
		if (tier3Count > 10) {
			//remove all tier 3 and replace with a single row
			for (ReportNavigationRow navigationRow : sortedValues) {
				if (navigationRow.getUnknownClinicalSignificanceCount() == 0
						|| navigationRow.getIndicatedTherapyCount() > 0) {
					sortedValuesFiltered.add(navigationRow);
				}
			}
			ReportNavigationRow tier3Row = new ReportNavigationRow(tier3Count + " Additional Mutated Genes", tier3Count + " Additional Mutated Genes");
			tier3Row.setUnknownClinicalSignificanceCount((int) tier3Count);
			sortedValuesFiltered.add(tier3Row);
		}
		else {
			sortedValuesFiltered = sortedValues;
		}
		
		
		for (ReportNavigationRow navigationRow : sortedValuesFiltered) {
			String gene = navigationRow.getLabel();
			Color defaultColor = Color.WHITE;
			if (grayBackground) {
				defaultColor = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			Row<PDPage> row = table.createRow(12);
			cell = row.createCell(20, gene);
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell = row.createCell(20, navigationRow.getIndicatedTherapyCount() == 0 ? "" : navigationRow.getIndicatedTherapyCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(20, navigationRow.getClinicalTrialCount() == 0 ? "" :  navigationRow.getClinicalTrialCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			List<String> csList = new ArrayList<String>();
			if (navigationRow.getStrongClinicalSignificanceCount() > 0) {
				csList.add("Tier 1");
			}
			if (navigationRow.getPossibleClinicalSignificanceCount() > 0) {
				csList.add("Tier 2");
			}
			if (navigationRow.getUnknownClinicalSignificanceCount() > 0) {
				csList.add("Tier 3");
			}
			cell = row.createCell(20, csList.stream().collect(Collectors.joining(", ")));
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			String aberrationType = "";
			if (navigationRow.getCnvCount() == -1) {
				aberrationType = "Loss";
			}
			else if (navigationRow.getCnvCount() == 1) {
				aberrationType = "Gain";
			}
			cell = row.createCell(10, aberrationType);
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(10, navigationRow.getFusionCount() == 0 ? "" : navigationRow.getFusionCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			grayBackground = !grayBackground;
		}
		

		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Navigation Table." );
		}
	}

	private void applyNavigationCountCellFormatting(Cell<PDPage> cell, Color color) {
		this.applyCellFormatting(cell, FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE, color);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setBottomPadding(4);
	}
	
	private BaseTable createNewTable(PDPage currentPage) throws IOException {
		return new BaseTable(latestYPosition, pageHeight - FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM, pageWidthMinusMargins,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
	}

	private void createIndicatedTherapiesTable() throws IOException, EncodingGlyphException {
		List<IndicatedTherapy> items = report.getIndicatedTherapies();
		this.updatePotentialNewPagePosition(items.size() + 1);
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.THERAPY_COLOR, FinalReportTemplateConstants.BORDER_THERAPY_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		
		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.THERAPY_COLOR);

		boolean greyBackground = false;
		List<IndicatedTherapy> sortedItems = items.stream().sorted(new Comparator<IndicatedTherapy>() {
			@Override
			public int compare(IndicatedTherapy o1, IndicatedTherapy o2) {
				if (o1.getTier() != null && o2.getTier() != null) {
					int compared = o1.getTier().compareTo(o2.getTier());
					//sort by drugs after tier
					if (compared == 0) {
						return o1.getDrugs().compareTo(o2.getDrugs());
					}
					return compared;
				}
				if (o1.getTier() == null && o2.getTier() != null) {
					return 1;
				}
				if (o1.getTier() != null && o2.getTier() == null) {
					return -1;
				}
				return 0; //both null
			}
		}).collect(Collectors.toList());
		if (!sortedItems.isEmpty()) {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(18, "DRUGS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(18, "VARIANT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(18, "LEVEL");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(46, "INDICATION");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			
			for (IndicatedTherapy item : sortedItems) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(18, item.getDrugs());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(18, item.getVariant());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(18, item.getLevel());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(46, item.getIndication());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}
		else {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No indicated therapy for this report.");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		links.add(new Link(FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition - 20));
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Indicated Therapy Table." );
		}
	}

	private void createClinicalTrialsTable() throws IOException, URISyntaxException, EncodingGlyphException {
		if (report.getClinicalTrials() == null) {
			return;
		}
		long rows = report.getClinicalTrials()
		.stream().filter(item -> item.getIsSelected() != null && item.getIsSelected())
		.collect(Collectors.counting());
		this.updatePotentialNewPagePosition(rows);
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.TRIAL_COLOR, FinalReportTemplateConstants.BORDER_TRIAL_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		
		cellHeader.setFillColor(FinalReportTemplateConstants.TRIAL_COLOR);
		links.add(new Link(FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE_NAV, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition));

		boolean trialsSelected = false;
		for (BiomarkerTrialsRow item : report.getClinicalTrials()) {
			if (item.getIsSelected() != null && item.getIsSelected()) {
				trialsSelected = true;
				break;
			}
		}
		
		if (trialsSelected) {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(30, "TITLE");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "PHASE");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "TARGETS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(25, "LOCATIONS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(15, "NCT ID");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			
		}
		boolean greyBackground = false;
		for (BiomarkerTrialsRow item : report.getClinicalTrials()) {
			if (item.getIsSelected() != null && item.getIsSelected()) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(30, item.getTitle());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getPhase());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(20, item.getBiomarker());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(25, item.getPi() + "<br/>" + item.getDept());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(15, item.getNctid());
				links.add(new Link(item.getNctid(), "https://clinicaltrials.gov/ct2/show/" + item.getNctid()));
				this.applyCellFormatting(cell, defaultFont, color);
				this.applyLinkCellFormatting(cell);
				greyBackground = !greyBackground;
			}
		}
		if (!trialsSelected) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No trial selected for this report.");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Clinical Trials Table." );
		}
	}

	private void applyHeaderFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setFillColor(FinalReportTemplateConstants.BACKGROUND_GRAY);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
	}

	private void applyTitleHeaderFormatting(Cell<PDPage> cell) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontBold(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
	}

	private void applyCellFormatting(Cell<PDPage> cell, float defaultFont, Color color) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontBold(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(4);
		if (color.equals(Color.WHITE)) {
			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		}
		else {
			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		}
		cell.setFillColor(color);
	}

	private void applyNavigationCellFormatting(Cell<PDPage> cell, Color fillColor) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontBold(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setFillColor(fillColor);
	}

	private void applyLinkCellFormatting(Cell<PDPage> cell) {
		cell.setTextColor(FinalReportTemplateConstants.LINK_ANSWER_GREEN);
	}

	private void createClinicalSignificanceTables() throws IOException, EncodingGlyphException {
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignifanceTables = new ArrayList<Map<String, GeneVariantAndAnnotation>>();
		clinicalSignifanceTables.add(report.getSnpVariantsStrongClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsPossibleClinicalSignificance());

		String[] tableTitles = new String[] {"TIER 1 - VARIANTS OF STRONG CLINICAL SIGNIFICANCE",
				"TIER 2 - VARIANTS OF POSSIBLE CLINICAL SIGNIFICANCE"};
		int counter = 0;
		for (Map<String, GeneVariantAndAnnotation> table : clinicalSignifanceTables) {
			boolean addLink = counter == 0;
			this.createAClinicalSignificanceTable(table, tableTitles[counter], addLink);
			counter++;
		}
		this.createAnUnkwnownClinicalSignificanceTable(report.getSnpVariantsUnknownClinicalSignificance());
	}


	private void createAClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems, String tableTitle, boolean addLink) throws IOException, EncodingGlyphException{
		this.updatePotentialNewPagePosition(tableItems.size() + 1);
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR, FinalReportTemplateConstants.BORDER_CLIN_SIGNIFICANCE_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		if (addLink) {
			links.add(new Link(FinalReportTemplateConstants.CLINICAL_SIGNIFICANCE_NAV, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition));
		}

		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, tableTitle);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);

		if (tableItems == null || tableItems.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No variant to report at this level.");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(30, "VARIANT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(70, "COMMENT");
			this.applyHeaderFormatting(cellHeader, defaultFont);

			boolean greyBackground = false;
			for (String variant : tableItems.keySet()) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				GeneVariantAndAnnotation item = tableItems.get(variant);
				row = table.createRow(12);
				StringBuilder cellContent = new StringBuilder();
				String geneVariant = item.getGeneVariant();
				if (geneVariant.length() > 25) {
					geneVariant = geneVariant.substring(0, 20) + "<br/>" + geneVariant.substring(20); 
				}
				cellContent.append("<b>").append(geneVariant).append("</b><br/>")
				.append("<b>Pos: </b>").append(item.getPosition()).append("<br/>");
				if (item.getType().equals("snp")) {
					cellContent
					.append("<b>Ref: </b>").append(item.getRef()).append("<br/>")
					.append("<b>Alt: </b>").append(item.getAlt()).append("<br/>")
					.append("<b>ENST: </b>")
					.append(item.getTranscript()).append("<br/>")
					.append("<b>VAF: </b>").append(item.getTaf()).append("<br/>")
					.append("<b>Depth: </b>").append(item.gettDepth()).append("<br/>");
				}
				else if (item.getType().equals("cnv")) {
					cellContent.append("<b>Copy Number: </b>").append(item.getCopyNumber()).append("<br/>")
					.append("<b>Aberration Type: </b>").append(item.getAberrationType()).append("<br/>");
				}
				
				
				Cell<PDPage> cell = row.createCell(30, cellContent.toString());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(70, this.createAnnotationText(item.getAnnotationsByCategory()));
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Clinical Significance Table: " + tableTitle );
		}

	}
	
	private String createAnnotationText(Map<String, String> annotationsByCategory) {
		StringBuilder sb = new StringBuilder();
		List<String> sortedCategories = annotationsByCategory.keySet().stream().sorted().collect(Collectors.toList());
		for (String cat : sortedCategories) {
			sb.append("<b>").append(cat).append(":</b> ").append(annotationsByCategory.get(cat));
			sb.append("<br/>");
		}
		return sb.toString();
	}
	
	private void createAnUnkwnownClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems) throws IOException, EncodingGlyphException{
		this.updatePotentialNewPagePosition(tableItems.size());
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR, FinalReportTemplateConstants.BORDER_CLIN_SIGNIFICANCE_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);

		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);

		//Title
		Row<PDPage> row = table.createRow(12); 
		Cell<PDPage> cellHeader = row.createCell(100, "TIER 3 - VARIANTS OF UNKNOWN CLINICAL SIGNIFICANCE");
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);

		if (tableItems == null || tableItems.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No variant to report at this level.");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
//			boolean greyBackground = false;
//			int counter = 0;
//			for (String variant : tableItems.keySet()) {
//				if (counter % 3 == 0) { //new row every 3 VUS
//					row = table.createRow(12);
//				}
//				counter++;
//				Color color = Color.WHITE;
//				if (greyBackground) {
//					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
//				}
//				GeneVariantAndAnnotation item = tableItems.get(variant);
//				Cell<PDPage> cell = row.createCell(33.33f, item.getGeneVariant());
//				this.applyCellFormatting(cell, defaultFont, color);
//				greyBackground = !greyBackground;
//			}
//			while(counter % 3 != 0) {
//				Color color = Color.WHITE;
//				if (greyBackground) {
//					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
//				}
//				Cell<PDPage> cell = row.createCell(33.33f, "");
//				this.applyCellFormatting(cell, defaultFont, color);
//				greyBackground = !greyBackground;
//				counter++;
//			}
			/////TODO
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(25, "VARIANT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "POSITION");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(25, "ENST");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(15, "VAF");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(15, "DEPTH");
			this.applyHeaderFormatting(cellHeader, defaultFont);

			boolean greyBackground = false;
			for (String variant : tableItems.keySet()) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				GeneVariantAndAnnotation item = tableItems.get(variant);
				row = table.createRow(12);
				
				Cell<PDPage> cell = row.createCell(25, item.getGeneVariant());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(20, item.getPosition());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(25, item.getTranscript());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(15, item.getTaf());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(15, item.gettDepth());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				greyBackground = !greyBackground;
				
			}
			/////TODO
		}

		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Unknown Clinical Significance Table." );
		}

	}

	private void createCNVTable() throws IOException, EncodingGlyphException {
		List<CNVReport> items = report.getCnvs();
		this.updatePotentialNewPagePosition(items.size());
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.CNV_COLOR, FinalReportTemplateConstants.BORDER_CNV_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.CNV_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CNV_COLOR);
		links.add(new Link(FinalReportTemplateConstants.CNV_TITLE_SHORT, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition));


		if (items == null || items.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No additional CNV");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(25, "CHR:START-END");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "COPY #");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "CYTOBAND");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(45, "COMMENT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			boolean greyBackground = false;
			for (CNVReport item : items) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(25, item.getChrom() + ":" +item.getStartFormatted() + "-" + item.getEndFormatted());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getCopyNumber() + "");
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(20, item.getCytoband());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(45, item.getComment());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in CNV Table." );
		}
	}

	private List<FooterColor> getExistingColorsForCurrentPage() {
		List<FooterColor> colors = colorPerPage.get(this.mainDocument.getNumberOfPages() - 1);
		if (colors == null) {
			colors = new ArrayList<FooterColor>();
		}
		return colors;
	}
	
	private void createTranslocationTable() throws IOException, EncodingGlyphException {
		List<TranslocationReport> items = report.getTranslocations();
		this.updatePotentialNewPagePosition(items.size());
//		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.FTL_COLOR, FinalReportTemplateConstants.BORDER_FTL_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.TRANSLOCATION_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.FTL_COLOR);
		links.add(new Link(FinalReportTemplateConstants.TRANSLOCATION_TITLE_SHORT, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition));

		if (items == null || items.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No additional fusion");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(20, "FUSION");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "GENE1");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "LAST EXON");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "GENE2");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "FIRST EXON");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(40, "COMMENT");
			this.applyHeaderFormatting(cellHeader, defaultFont);

			boolean greyBackground = false;
			for (TranslocationReport item : items) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(20, item.getFusionName());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getLeftGene());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getLastExon());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(10, item.getRightGene());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getFirstExon());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(40, item.getComment());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Fusion Table." );
		}
	}
	
	private void createPubmedTable() throws IOException, EncodingGlyphException {
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.PUBMED_COLOR, FinalReportTemplateConstants.BORDER_PUBMED_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		List<PubMed> items = report.getPubmeds();

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.PUBMED_REFERENCE_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setTextColor(Color.WHITE);
		cellHeader.setFillColor(FinalReportTemplateConstants.PUBMED_COLOR);

		if (items == null || items.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No PubMed references.");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
//			row = table.createRow(12); 
//			table.addHeaderRow(row);
//			cellHeader = row.createCell(20, "FUSION NAME");
//			this.applyHeaderFormatting(cellHeader, defaultFont);
//			cellHeader = row.createCell(10, "GENE1");
//			this.applyHeaderFormatting(cellHeader, defaultFont);
//			cellHeader = row.createCell(10, "LAST EXON");
//			this.applyHeaderFormatting(cellHeader, defaultFont);
//			cellHeader = row.createCell(10, "GENE2");
//			this.applyHeaderFormatting(cellHeader, defaultFont);
//			cellHeader = row.createCell(10, "FIRST EXON");
//			this.applyHeaderFormatting(cellHeader, defaultFont);
//			cellHeader = row.createCell(40, "COMMENT");
//			this.applyHeaderFormatting(cellHeader, defaultFont);

			boolean greyBackground = false;
			for (PubMed item : items) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				row = table.createRow(12);
				StringBuilder sb = new StringBuilder();
//				sb.append("<b>").append(item.getTitle()).append("</b>").append("<br/>"); //can't use tags, it reverts to Helvetica
				sb.append("").append(item.getTitle()).append("");
//				Cell<PDPage> cell = row.createCell(100, sanitizeString(sb.toString()));
				Cell<PDPage> cell = row.createCell(100, sb.toString());
				cell.setBottomPadding(0);
				this.applyCellFormatting(cell, FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE, color);
				cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD); //override the normal font to make it bold
				
				//Date
				row = table.createRow(12);
				cell = row.createCell(100, item.getDescription());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setTopPadding(-3);
				
				//PMID link
				row = table.createRow(12);
				sb = new StringBuilder();
				sb.append("PMID: ").append(item.getPmid());
				links.add(new Link("PMID: " + item.getPmid(), FinalReportTemplateConstants.PUBMED_URL + item.getPmid()));
				cell = row.createCell(100, sb.toString());
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setTextColor(FinalReportTemplateConstants.LINK_ANSWER_GREEN);
				cell.setTopPadding(-3);
				greyBackground = !greyBackground;
			}
		}
		try {
			latestYPosition = table.draw() - 20;
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in PubMed Table." );
		}
	}

//	private String sanitizeString(String text) throws IOException {
//		StringBuilder nonSymbolBuffer = new StringBuilder();
//	    for (char character : text.toCharArray()) {
//	        if (WinAnsiEncoding.INSTANCE.contains(character)) {
//	            nonSymbolBuffer.append(character);
//	        } else {
//	            //handle writing line with symbols...
//	        	if ((int) character == 954 ) { //kappa symbol
//	        		nonSymbolBuffer.append("k");
//	        	}
//	        	else {
//	        		nonSymbolBuffer.append("?");
//	        	}
//	        	
//	        }
//	    }
//		return nonSymbolBuffer.toString();
//	}
	
	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize) throws IOException {
		return createRow(table, title, value, fontSize, FinalReportTemplateConstants.MAIN_FONT_TYPE);
	}

	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize, PDFont font) throws IOException {
		if (title == null) {
			title = "";
		}
		if (value == null) {
			value = "";
		}
		float titleWidth = font.getStringWidth(title);
		float valueWidth = font.getStringWidth(value);
		float titleRatio = (float) titleWidth / (float) (valueWidth + titleWidth);
		if (value.split("-").length == 3 && titleRatio > 0.6) {
			titleRatio = 60; //leave 40% of space for dates to avoid line returns with long titles
		}
		else {
			titleRatio = Math.min(75, Math.max(25, 100 * titleRatio)); //avoid too small or too large
		}
		float valueRatio = 100 - titleRatio;
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(titleRatio, title, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.GRAY);
		cell.setFontSize(fontSize);
		cell.setRightPadding(0);
		cell = row.createCell(valueRatio, value, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.BLACK);
		cell.setFontSize(fontSize);
		return row;
	}

	private Cell<PDPage> createFooterCell(Row<PDPage> row, String text, HorizontalAlignment align, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(align);
		cell.setFontSize(FinalReportTemplateConstants.ADDRESS_FONT_SIZE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
		return cell;
	}
	
	private Cell<PDPage> createFooterCellColor(Row<PDPage> row, String text, HorizontalAlignment align, float widthPct, Color color, LineStyle border) {
		Cell<PDPage> cell = createFooterCell(row, text, align, widthPct);
		cell.setFillColor(color);
		cell.setTopBorderStyle(border);
		cell.setBottomBorderStyle(border);
		cell.setLeftBorderStyle(border);
		cell.setRightBorderStyle(border);
		return cell;
	}

	private void addFooters() throws IOException, EncodingGlyphException {
		int pageTotal = this.mainDocument.getNumberOfPages();
		float tableYPos = FinalReportTemplateConstants.MARGINBOTTOM;
		float tableWidth = pageWidthMinusMargins;
		Color fillColor = Color.WHITE;
		LineStyle borderColor = FinalReportTemplateConstants.NO_BORDER_ZERO;
		for (int i = 0; i < pageTotal; i++) {
			PDPage currentPage = this.mainDocument.getPage(i);
			BaseTable table = new BaseTable(tableYPos, tableYPos, 0, tableWidth,
					FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
			Row<PDPage> row = table.createRow(6);
			String testName = FinalReportTemplateConstants.DEFAULT_TITLE;
			if (report.getLabTestName() != null) {
				testName = report.getLabTestName();
			}
			List<FooterColor> colors = colorPerPage.get(i);
			if (colors == null) { //don't change color until the next page has an entry
				colors = colorPerPage.get(i - 1);
			}
			int pageNb = i - 1;
			while (colors == null && pageNb > 0) {
				colors = colorPerPage.get(pageNb -1);
				pageNb--;
			}
			//when the navigation table is pushed to page 2 (because of text above too big)
			//the colors for page 1 might be null
			//add a white color for page 1
			if (colors == null) {
				colors = new ArrayList<FooterColor>();
				FooterColor fColor = new FooterColor(Color.WHITE, FinalReportTemplateConstants.NO_BORDER_ZERO);
				if (!colors.contains(fColor)) {
					colors.add(fColor);
				}
			}
			
//			this.createFooterCellColor(row, " ", HorizontalAlignment.LEFT, 2f, fillColor, borderColor);
//			this.createFooterCell(row, testName, HorizontalAlignment.LEFT, 34f);
//			this.createFooterCell(row, "MRN " + caseSummary.getMedicalRecordNumber() + " " + caseSummary.getPatientName(), HorizontalAlignment.CENTER, 32f);
//			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 30f);
			this.createFooterCell(row, testName, HorizontalAlignment.LEFT, 42f);
			this.createFooterCell(row, "MRN " + caseSummary.getMedicalRecordNumber() + " " + caseSummary.getPatientName(), HorizontalAlignment.CENTER, 29f);
			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 25f);
			float width = 2f;
			if (colors.size() > 1) {
				width = 4f / colors.size();
			}
			for (FooterColor fColor : colors) {
				this.createFooterCellColor(row, " ", HorizontalAlignment.LEFT, width, fColor.getColor(), fColor.getLineStyle());
			}
			if (colors.size() == 1) {
				this.createFooterCellColor(row, " ", HorizontalAlignment.LEFT, width, fillColor, borderColor);
			}
//			//draw color cell
//			row = table.createRow(1);
//			Cell<PDPage> cell = row.createCell(100, " ");
//			cell.setFontSize(1);
//			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
//			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
//			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
//			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_ZERO);
//			cell.setFillColor(FinalReportTemplateConstants.CNV_COLOR);
//			cell.set
			try {
				table.draw();
			} catch (IllegalArgumentException e) {
				throw new EncodingGlyphException(e.getMessage() + " in footers." );
			}
		}
	}

	private void addInformationAboutTheTest() throws IOException, EncodingGlyphException {
		// Title
		this.updatePageBreakPosition();
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<FooterColor> colors = this.getExistingColorsForCurrentPage();
		FooterColor fColor = new FooterColor(FinalReportTemplateConstants.ABOUT_THE_TEST_COLOR, FinalReportTemplateConstants.BORDER_ABOUT_THE_TEST_COLOR);
		if (!colors.contains(fColor)) {
			colors.add(fColor);
		}
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
		Cell<PDPage> cellHeader = table.createRow(12).createCell(100, FinalReportTemplateConstants.DISCLAMER_TITLE);
//		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
//		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);
//		cell.setAlign(HorizontalAlignment.LEFT);
		applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.ABOUT_THE_TEST_COLOR);

		// Content
		//Fetch the correct disclamer based on the test name:
		List<String> aboutTheTest = clinicalTest.getDisclaimerTexts().stream().map(c -> c.getText()).collect(Collectors.toList());
		String aboutTheTestLink = clinicalTest.getTestLink();
		for (String info : aboutTheTest) {
			Row<PDPage> row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, info);
			applyCellFormatting(cell, FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE, Color.WHITE);
//			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
//			cell.setAlign(HorizontalAlignment.LEFT);
//			cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
			cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
//			cell.setBottomPadding(10);
			if (info.startsWith("http")) {
				cell.setTopPadding(-3);
				cell.setTextColor(FinalReportTemplateConstants.LINK_ANSWER_GREEN);
			}
		}
		try {
			latestYPosition = table.draw();
		} catch (IllegalArgumentException e) {
			throw new EncodingGlyphException(e.getMessage() + " in Information About the Test." );
		}
		this.links.add(new Link(aboutTheTestLink, aboutTheTestLink));
	}

	public void saveTemp() throws IOException {
		mainDocument.save(tempFile);
		mainDocument.close();
	}
	
	public void saveFinalized() throws IOException {
		File finalizedFile = new File(fileProps.getPdfFinalizedFilesDir(), System.currentTimeMillis() + "_" + report.getMongoDBId().getOid() + ".pdf");
		mainDocument.save(finalizedFile);
		mainDocument.close();
	}

	public String createPDFLink(FileProperties fileProps) throws IOException {
		File target = new File(fileProps.getPdfFilesDir(), tempFile.getName());
		if (!target.exists()) {
			return null;
		}
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".pdf";
		File link = new File(fileProps.getPdfLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}
	
	public static String createPDFLinkWithoutReport(FileProperties fileProps, String reportOid) throws IOException {
		List<File> finalizedPDFs = Files.list((fileProps.getPdfFinalizedFilesDir().toPath())).map(p -> p.toFile()).filter(f -> f.getName().endsWith(reportOid + ".pdf"))
				.collect(Collectors.toList());
		if (finalizedPDFs != null && !finalizedPDFs.isEmpty()) {
			Collections.sort(finalizedPDFs, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return new Long(o2.lastModified()).compareTo(new Long(o1.lastModified()));
				}
			});
			File target = finalizedPDFs.get(0);
			if (!target.exists()) {
				return null;
			}
			String random = RandomStringUtils.random(25, true, true);
			String linkName = random + ".pdf";
			File link = new File(fileProps.getPdfLinksDir(), linkName);
			Files.createSymbolicLink(link.toPath(), target.toPath());
			return linkName;
		}
		return null;
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
		borderULine.setWidth(0);

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

			if (link.getUrl() != null) {
				PDActionURI action = new PDActionURI();
				action.setURI(link.getUrl());
				annotation.setAction(action);

			}
			else if (link.getDestinationPageNb() != null) {
				PDActionGoTo gotoAction = new PDActionGoTo();
				PDPageXYZDestination dest = new PDPageXYZDestination();
				dest.setPage(mainDocument.getPage(link.getDestinationPageNb()));
				dest.setTop(link.getTop() + 20);
				dest.setZoom(0);
				dest.setLeft(0);
				gotoAction.setDestination(dest);
				annotation.setAction(gotoAction);
			}
		}
	}
	
	private void addWatermark() throws IOException {
		File watermark = new File(fileProps.getPdfLogoDir(), fileProps.getPdfDraftWatermarkName());
		Image watermarkImage = new Image(ImageIO.read(watermark));
		watermarkImage = watermarkImage.scaleByWidth(mainDocument.getPage(0).getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINLEFT - FinalReportTemplateConstants.MARGINRIGHT - 10);
		for (int i = 0; i < mainDocument.getNumberOfPages(); i++) {
			PDPage page = mainDocument.getPage(i);
			PDPageContentStream contentStream = new PDPageContentStream(mainDocument, page,
					PDPageContentStream.AppendMode.APPEND, true);
			float yPos = pageHeight - FinalReportTemplateConstants.MARGINTOP * 3;
			float xPos = FinalReportTemplateConstants.MARGINLEFT + 5;
			watermarkImage.draw(mainDocument, contentStream, xPos, yPos);
			contentStream.close();
			
		}
	}
}
