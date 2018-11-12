package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
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
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.model.hybrid.ReportNavigationRow;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
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
	Map<Integer, List<Object>> colorPerPage = new HashMap<Integer, List<Object>>();

	public FinalReportPDFTemplate(Report report, FileProperties fileProps, OrderCase caseSummary, OtherProperties otherProps) {
		this.otherProps = otherProps;
		this.report = report;
		this.fileProps = fileProps;
		this.caseSummary = caseSummary;
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

	private void init() throws IOException, URISyntaxException {
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
		//		this.addInformationAboutTheTest();

		this.addFooters();
		this.addLinks();
	}

	private void addAddress() throws IOException {
		FinalReportTemplateConstants.MAIN_FONT_TYPE = PDType0Font.load(mainDocument,
				fileProps.getPdfFontFile());
		FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD = PDType0Font.load(mainDocument,
				fileProps.getPdfFontFile());
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
		File ngsLogoFile = new File(fileProps.getPdfLogoDir(), fileProps.getPdfNGSLogoName());
		Image ngsImage = new Image(ImageIO.read(ngsLogoFile));
		ngsImage = ngsImage.scaleByWidth(100);
		float xPos = firstPage.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINRIGHT - ngsImage.getWidth();
		ngsImage.draw(mainDocument, contentStream, xPos, yPos);
		contentStream.close();
	}

	private void addUTSWImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		Image ngsImage = new Image(ImageIO.read(new File(fileProps.getPdfLogoDir(), fileProps.getPdfUTSWLogoName())));
		ngsImage = ngsImage.scaleByWidth(100);
		float xPos = firstPage.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINRIGHT - ngsImage.getWidth();
		ngsImage.draw(mainDocument, contentStream, xPos, yPos - ngsImage.getHeight() - 10);
		contentStream.close();
	}

	private void addTitle() throws IOException {
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		String testName = FinalReportTemplateConstants.TITLE;
		if (report.getLabTestName() != null) {
			testName = report.getLabTestName();
		}
		PDStreamUtils.write(contentStream, testName, FinalReportTemplateConstants.MAIN_FONT_TYPE, 16,
				FinalReportTemplateConstants.MARGINLEFT, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 30; // position of the patient table title
		contentStream.close();
	}

	private void addNotes() throws IOException {
		// Title
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(0);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE).createCell(100, "Case Summary");
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);

		// Content
		Row<PDPage> row = table.createRow(12);
		cell = row.createCell(100, report.getSummary());
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
		latestYPosition = table.draw();
	}

	private void createPatientTable() throws IOException {
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
		for (CellItem item : leftTableItems) {
			this.createRow(leftTable, item.getLabel(), item.getValue(), defaultFont);
		}
		leftTable.draw();
		float maxTableHeight = leftTable.getHeaderAndDataHeight();

		BaseTable middleTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth(), mainDocument, firstPage, cellBorder,
				true);
		List<CellItem> middleTableItems = patientDetails.getPatientTables().get(1).getItems();
		for (CellItem item : middleTableItems) {
			this.createRow(middleTable, item.getLabel(), item.getValue(), defaultFont);
		}
		middleTable.draw();

		maxTableHeight = Math.max(maxTableHeight, middleTable.getHeaderAndDataHeight());

		BaseTable rightTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth(),
				mainDocument, firstPage, cellBorder, true);
		List<CellItem> rightTableItems = patientDetails.getPatientTables().get(2).getItems();
		for (CellItem item : rightTableItems) {
			this.createRow(rightTable, item.getLabel(), item.getValue(), defaultFont);
		}
		rightTable.draw();
		maxTableHeight = Math.max(maxTableHeight, rightTable.getHeaderAndDataHeight());

		// draw borders
		BaseTable leftTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, true, true);
		Row<PDPage> row = leftTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		leftTableEmpty.draw();
		BaseTable middleTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth(), mainDocument, firstPage, true,
				true);
		row = middleTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		middleTableEmpty.draw();
		BaseTable rightTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth(),
				mainDocument, firstPage, true, true);
		row = rightTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		rightTableEmpty.draw();

		latestYPosition -= maxTableHeight + 10;
	}

	private void applyPatientRecordTableBorderFormatting(Cell<PDPage> cell) {
		cell.setTopBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER);
	}

	private void updatePotentialNewPagePosition() {
		if (latestYPosition <= FinalReportTemplateConstants.MARGINBOTTOM * 3) { //start on a new page if to low on the page
			mainDocument.addPage(new PDPage(PDRectangle.LETTER));
			latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
		}
		else {
			latestYPosition -= 20;
		}
	}
	
	private void updatePageBreakPosition() {
		mainDocument.addPage(new PDPage(PDRectangle.LETTER));
		latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
	}

	private void createNavigationTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = createNewTable(currentPage);
		List<Object> colors = new ArrayList<Object>();
		colors.add(Color.WHITE);
		colors.add(FinalReportTemplateConstants.NO_BORDER_THIN);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		

		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(20, FinalReportTemplateConstants.GENE_TITLE);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.GENE_COLOR);
		cell = row.createCell(20, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.THERAPY_COLOR);
		cell = row.createCell(20, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.TRIAL_COLOR);
		cell = row.createCell(20, FinalReportTemplateConstants.CLINICAL_SIGNIFICANCE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		cell = row.createCell(10, FinalReportTemplateConstants.CNV_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CNV_COLOR);
		cell = row.createCell(10, FinalReportTemplateConstants.TRANSLOCATION_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.FTL_COLOR);
		
		
		report.updateClinicalTrialCount(); //update now in case the trial selection changed
		boolean grayBackground = false;
		List<String> sortedKeys = report.getNavigationRowsPerGene().keySet().stream().sorted().collect(Collectors.toList());
		for (String gene : sortedKeys) {
			Color defaultColor = Color.WHITE;
			if (grayBackground) {
				defaultColor = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			ReportNavigationRow navigationRow = report.getNavigationRowsPerGene().get(gene);
			row = table.createRow(12);
			cell = row.createCell(20, gene);
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell = row.createCell(20, navigationRow.getIndicatedTherapyCount() == 0 ? "" : navigationRow.getIndicatedTherapyCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(20, navigationRow.getClinicalTrialCount() == 0 ? "" :  navigationRow.getClinicalTrialCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(20, navigationRow.getStrongClinicalSignificanceCount() + " / " + navigationRow.getPossibleClinicalSignificanceCount() + " / " + navigationRow.getUnknownClinicalSignificanceCount());
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(10, navigationRow.getCnvCount() == 0 ? "" : navigationRow.getCnvCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			cell = row.createCell(10, navigationRow.getFusionCount() == 0 ? "" : navigationRow.getFusionCount() + "");
			this.applyNavigationCountCellFormatting(cell, defaultColor);
			grayBackground = !grayBackground;
		}
		

		latestYPosition = table.draw() - 20;
	}

	private void applyNavigationCountCellFormatting(Cell<PDPage> cell, Color color) {
		this.applyCellFormatting(cell, FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE, color);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setBottomPadding(0);
	}
	
	private BaseTable createNewTable(PDPage currentPage) throws IOException {
		return new BaseTable(latestYPosition, pageHeight - FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM, pageWidthMinusMargins,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
	}

	private void createIndicatedTherapiesTable() throws IOException {
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.THERAPY_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_THERAPY_COLOR);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		List<IndicatedTherapy> items = report.getIndicatedTherapies();

		
		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.THERAPY_COLOR);

		//Headers
		row = table.createRow(12); 
		table.addHeaderRow(row);
		cellHeader = row.createCell(25, "VARIANT");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "LEVEL");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(55, "INDICATION");
		this.applyHeaderFormatting(cellHeader, defaultFont);

		boolean greyBackground = false;
		List<IndicatedTherapy> sortedItems = items.stream().sorted(new Comparator<IndicatedTherapy>() {
			@Override
			public int compare(IndicatedTherapy o1, IndicatedTherapy o2) {
				if (o1.getTier() != null && o2.getTier() != null) {
					return o1.getTier().compareTo(o2.getTier());
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
		for (IndicatedTherapy item : sortedItems) {
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(25, item.getVariant());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(20, item.getLevel());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(55, item.getIndication());
			this.applyCellFormatting(cell, defaultFont, color);
			greyBackground = !greyBackground;
		}
		latestYPosition = table.draw() - 20;
		links.add(new Link(FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV, this.mainDocument.getNumberOfPages() - 1, (int) this.latestYPosition));
	}

	private void createClinicalTrialsTable() throws IOException, URISyntaxException {
		if (report.getClinicalTrials() == null) {
			return;
		}
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.TRIAL_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_TRIAL_COLOR);
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
			cellHeader = row.createCell(25, "TITLE");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(10, "PHASE");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "TARGETS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(25, "LOCATIONS");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "NCT ID");
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
				Cell<PDPage> cell = row.createCell(25, item.getTitle());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getPhase());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(20, item.getBiomarker());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(25, item.getPi() + "<br/>" + item.getDept());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(25, item.getNctid());
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
		latestYPosition = table.draw() - 20;
	}

	private void applyHeaderFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setFillColor(FinalReportTemplateConstants.BACKGROUND_GRAY);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
	}

	private void applyTitleHeaderFormatting(Cell<PDPage> cell) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontBold(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
	}

	private void applyCellFormatting(Cell<PDPage> cell, float defaultFont, Color color) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
		if (color.equals(Color.WHITE)) {
			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		}
		else {
			cell.setTopBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setRightBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
		}
		cell.setFillColor(color);
	}

	private void applyNavigationCellFormatting(Cell<PDPage> cell, Color fillColor) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setFillColor(fillColor);
	}

	private void applyLinkCellFormatting(Cell<PDPage> cell) {
		cell.setTextColor(FinalReportTemplateConstants.LINK_ANSWER_GREEN);
	}

	private void createClinicalSignificanceTables() throws IOException {
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignifanceTables = new ArrayList<Map<String, GeneVariantAndAnnotation>>();
		clinicalSignifanceTables.add(report.getSnpVariantsStrongClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsPossibleClinicalSignificance());

		String[] tableTitles = new String[] {"VARIANTS OF STRONG CLINICAL SIGNIFICANCE",
				"VARIANTS OF POSSIBLE CLINICAL SIGNIFICANCE"};
		int counter = 0;
		for (Map<String, GeneVariantAndAnnotation> table : clinicalSignifanceTables) {
			boolean addLink = counter == 0;
			this.createAClinicalSignificanceTable(table, tableTitles[counter], addLink);
			counter++;
		}
		this.createAnUnkwnownClinicalSignificanceTable(report.getSnpVariantsUnknownClinicalSignificance());
	}


	private void createAClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems, String tableTitle, boolean addLink) throws IOException{
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_CLIN_SIGNIFICANCE_COLOR);
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
			Cell<PDPage> cell = row.createCell(100, "No variant to report at this level");
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
				Cell<PDPage> cell = row.createCell(30, item.getGeneVariant());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(70, this.createAnnotationText(item.getAnnotationsByCategory()));
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		latestYPosition = table.draw() - 20;

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
	
	private void createAnUnkwnownClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems) throws IOException{
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_CLIN_SIGNIFICANCE_COLOR);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);

		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);

		//Title
		Row<PDPage> row = table.createRow(12); 
		Cell<PDPage> cellHeader = row.createCell(100, "VARIANTS OF UNKNOWN CLINICAL SIGNIFICANCE");
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);

		if (tableItems == null || tableItems.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No variant to report at this level");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
//			String variants = tableItems.keySet().stream().collect(Collectors.joining(", "));
//			row = table.createRow(12);
//			Color color = Color.WHITE;
//			Cell<PDPage> cell = row.createCell(100, variants);
//			this.applyCellFormatting(cell, defaultFont, color);
			boolean greyBackground = false;
			for (String variant : tableItems.keySet()) {
				Color color = Color.WHITE;
				if (greyBackground) {
					color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
				}
				GeneVariantAndAnnotation item = tableItems.get(variant);
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(100, item.getGeneVariant());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		latestYPosition = table.draw() - 20;

	}

	private void createCNVTable() throws IOException {
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.CNV_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_CNV_COLOR);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		List<CNVReport> items = report.getCnvs();

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
			cellHeader = row.createCell(10, "COPY NB.");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(20, "CYTOBAND");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(45, "COMMENT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			boolean greyBackground = false;
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			for (CNVReport item : items) {
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

		latestYPosition = table.draw() - 20;
	}

	private void createTranslocationTable() throws IOException {
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.FTL_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_FTL_COLOR);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		List<TranslocationReport> items = report.getTranslocations();

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
			cellHeader = row.createCell(20, "FUSION NAME");
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
				cell = row.createCell(10, "0");
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(10, item.getRightGene());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, "0");
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(40, item.getComment());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}
		latestYPosition = table.draw() - 20;
	}
	
	private void createPubmedTable() throws IOException {
//		this.updatePotentialNewPagePosition();
		updatePageBreakPosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(FinalReportTemplateConstants.PUBMED_COLOR);
		colors.add(FinalReportTemplateConstants.BORDER_PUBMED_COLOR);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;

		BaseTable table = createNewTable(currentPage);
		List<PubMed> items = report.getPubmeds();

		//Title
		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.PUBMED_REFERENCE_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.PUBMED_COLOR);

		if (items == null || items.isEmpty()) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No Pubmed references.");
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
				sb.append("<b>").append(item.getTitle()).append("</b>").append("<br/>");
				sb.append(item.getDescription()).append("<br/>");
				sb.append("PMID: ").append(item.getPmid());
				links.add(new Link("PMID: " + item.getPmid(), FinalReportTemplateConstants.PUBMED_URL + item.getPmid()));
				Cell<PDPage> cell = row.createCell(100, sb.toString());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}
		latestYPosition = table.draw() - 20;
	}

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
			titleRatio = Math.min(80, Math.max(20, 100 * titleRatio)); //avoid too small or too large
		}
		float valueRatio = 100 - titleRatio;
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(titleRatio, title, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.GRAY);
		cell.setFontSize(fontSize);
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
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
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

	private void addFooters() throws IOException {
		int pageTotal = this.mainDocument.getNumberOfPages();
		float tableYPos = FinalReportTemplateConstants.MARGINBOTTOM;
		float tableWidth = pageWidthMinusMargins;
		for (int i = 0; i < pageTotal; i++) {
			PDPage currentPage = this.mainDocument.getPage(i);
			BaseTable table = new BaseTable(tableYPos, tableYPos, 0, tableWidth,
					FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
			Row<PDPage> row = table.createRow(6);
			String testName = FinalReportTemplateConstants.TITLE;
			if (report.getLabTestName() != null) {
				testName = report.getLabTestName();
			}
			
			Color fillColor = Color.WHITE;
			LineStyle borderColor = FinalReportTemplateConstants.NO_BORDER_THIN;
			List<Object> colors = colorPerPage.get(i);
			if (colors != null) { //don't change color until the next page has an entry
				fillColor = (Color) colors.get(0); 
				borderColor = (LineStyle) colors.get(1);
			}
			
//			this.createFooterCellColor(row, " ", HorizontalAlignment.LEFT, 2f, fillColor, borderColor);
			this.createFooterCell(row, testName, HorizontalAlignment.LEFT, 34f);
			this.createFooterCell(row, "MRN " + caseSummary.getMedicalRecordNumber() + " " + caseSummary.getPatientName(), HorizontalAlignment.CENTER, 32f);
			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 32f);
			this.createFooterCellColor(row, " ", HorizontalAlignment.LEFT, 2f, fillColor, borderColor);
//			//draw color cell
//			row = table.createRow(1);
//			Cell<PDPage> cell = row.createCell(100, " ");
//			cell.setFontSize(1);
//			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
//			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
//			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
//			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
//			cell.setFillColor(FinalReportTemplateConstants.CNV_COLOR);
//			cell.set
			table.draw();
		}
	}

	private void addInformationAboutTheTest() throws IOException {
		// Title
		this.updatePotentialNewPagePosition();
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		List<Object> colors = new ArrayList<Object>();
		colors.add(Color.WHITE);
		colors.add(FinalReportTemplateConstants.NO_BORDER_THIN);
		colorPerPage.put(this.mainDocument.getNumberOfPages() - 1, colors);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(12).createCell(100, FinalReportTemplateConstants.DISCLAMER_TITLE);
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
			cell.setTextColor(Color.BLACK);
			cell.setBottomPadding(10);
		}
		latestYPosition = table.draw();
	}

	public void saveTemp() throws IOException {
		mainDocument.save(tempFile);
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

			if (link.getUrl() != null) {
				PDActionURI action = new PDActionURI();
				action.setURI(link.getUrl());
				annotation.setAction(action);

				//				PDActionJavaScript javascriptAction = 
				//						new PDActionJavaScript("app.launchURL('" + link.getUrl() + ", true);");
				////				PDAnnotationAdditionalActions actions = new PDAnnotationAdditionalActions();
				////				actions.setU(javascriptAction);
				//				annotation.setAction(javascriptAction);

			}
			else if (link.getDestinationPageNb() != null) {
				PDActionGoTo gotoAction = new PDActionGoTo();
				PDPageFitDestination dest = new PDPageFitDestination();
				dest.setPage(mainDocument.getPage(link.getDestinationPageNb()));
//				dest.setBottom(link.getTop());
				gotoAction.setDestination(dest);
				annotation.setAction(gotoAction);
			}


		}


	}
}
