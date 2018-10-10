package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.utils.PDStreamUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.ClinicalTrialXML;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.hybrid.PatientInfo;
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
		this.createIndicatedTherapiesTable();
		this.createClinicalTrialsTable();
		this.createClinicalSignificanceTables();
		this.createCNVTable();
		this.createTranslocationTable();
//		this.addInformationAboutTheTest();
		
		this.addFooters();
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
		File ngsLogoFile = new File(fileProps.getPdfLogoDir(), FinalReportTemplateConstants.NGS_LOGO_PATH);
		Image ngsImage = new Image(ImageIO.read(ngsLogoFile));
		ngsImage = ngsImage.scaleByWidth(118);
		ngsImage.draw(mainDocument, contentStream, 247, yPos);
		contentStream.close();
	}

	private void addUTSWImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		Image ngsImage = new Image(ImageIO.read(new File(fileProps.getPdfLogoDir(), FinalReportTemplateConstants.UTSW_LOGO_PATH)));
		ngsImage = ngsImage.scaleByWidth(182);
		ngsImage.draw(mainDocument, contentStream, 400, yPos);
		contentStream.close();
	}

	private void addTitle() throws IOException {
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE, 16,
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
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable leftTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, cellBorder, true);
		List<CellItem> leftTableItems = patientDetails.getPatientTables().get(0).getItems();
		for (CellItem item : leftTableItems) {
			this.createRow(leftTable, item.getLabel(), item.getValue(), defaultFont);
		}
		leftTable.draw();
		float maxTableHeight = leftTable.getHeaderAndDataHeight();

		BaseTable middleTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() - 1, mainDocument, firstPage, cellBorder,
				true);
		List<CellItem> middleTableItems = patientDetails.getPatientTables().get(1).getItems();
		for (CellItem item : middleTableItems) {
			this.createRow(middleTable, item.getLabel(), item.getValue(), defaultFont);
		}
		middleTable.draw();
		
		maxTableHeight = Math.max(maxTableHeight, middleTable.getHeaderAndDataHeight());

		BaseTable rightTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth() - 2,
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
		leftTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		leftTableEmpty.draw();
		BaseTable middleTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() - 1, mainDocument, firstPage, true,
				true);
		middleTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		middleTableEmpty.draw();
		BaseTable rightTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth() - 2,
				mainDocument, firstPage, true, true);
		rightTableEmpty.createRow(maxTableHeight).createCell(100, "")
				.setBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER);
		rightTableEmpty.draw();

		latestYPosition -= maxTableHeight + 10;
	}
	
	private void updatePotentialNewPagePosition() {
		if (latestYPosition <= FinalReportTemplateConstants.MARGINBOTTOM * 2) { //start on a new page if to low on the page
			mainDocument.addPage(new PDPage(PDRectangle.LETTER));
			latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
		}
		else {
			latestYPosition -= 20;
		}
	}
	
	private void createIndicatedTherapiesTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		contentStream.close();
		
		latestYPosition -= 20;
		
		float tableWidth = pageWidthMinusMargins;

		boolean cellBorder = true;
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, cellBorder, true);
		List<IndicatedTherapy> items = report.getIndicatedTherapies();
		
		//Headers
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(15, "Gene");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "Variant");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "Level");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(45, "Indication");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		
		
		for (IndicatedTherapy item : items) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(15, item.getGene());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(20, item.getVariant());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(20, item.getLevel());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(45, item.getIndication());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
		}
		table.draw();
//		float maxTableHeight = table.getHeaderAndDataHeight();

		latestYPosition = table.draw() + 20;
	}
	
	private void createClinicalTrialsTable() throws IOException, URISyntaxException {
		if (report.getClinicalTrials() == null) {
			return;
		}
//		table.addHeaderRow(row);
//		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE);
//		this.applyIndicatedTherapyHeaderFormatting(cellHeader, FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);
		//write the section title
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		contentStream.close();
		
		latestYPosition -= 20;
//		HttpGet requestGet = null;
//		HttpClient client = null;
//		XmlMapper mapper = new XmlMapper();
//		if (otherProps.getProxyHostname() != null) {
//			HttpHost proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
//			client = HttpClientBuilder.create().setProxy(proxy).build();
//			
//		}
//		else {
//			client = HttpClientBuilder.create().build();
//		}
		for (BiomarkerTrialsRow trow : report.getClinicalTrials()) {
			this.updatePotentialNewPagePosition();
			float tableWidth = pageWidthMinusMargins;
			currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
			BaseTable table = new BaseTable(latestYPosition, this.pageHeight - FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM, tableWidth,
					FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
			StringBuilder sb = new StringBuilder();
			sb.append("<b>").append(trow.getNctid()).append("</b>:");
			this.createClinicalTrialLineOfText(sb.toString(), table);
			sb = new StringBuilder();
			sb.append(trow.getTitle()).append("<br/>");
			this.createClinicalTrialLineOfText(sb.toString(), table);
			sb = new StringBuilder();
			sb.append("Biomarker(s): ").append(trow.getBiomarker());
			this.createClinicalTrialLineOfText(sb.toString(), table);
//			URI uri = new URI("https://clinicaltrials.gov/ct2/show/" + trow.getNctid() + "?displayxml=true");
//			requestGet = new HttpGet(uri);
//			HttpResponse response = client.execute(requestGet);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode == HttpStatus.SC_OK) {
//				sb = new StringBuilder();
//				ClinicalTrialXML xmlTrial = mapper.readValue(response.getEntity().getContent(), ClinicalTrialXML.class);
//				String summary = xmlTrial.getBriefSummary().getTextblock().replaceAll("\\n", "");
//				sb.append(summary);
//				this.createClinicalTrialLineOfText(sb.toString(), table);
//			}
			
			this.latestYPosition = table.draw() - 20;
		}
	}
	
	private void createClinicalTrialLineOfText(String content, BaseTable table) throws IOException {
		Row<PDPage> tableRow = table.createRow(12); 
		Cell<PDPage> cell = tableRow.createCell(100, content);
		this.applyClinicalTrialCellFormatting(cell, FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
	}
	
	private void applyIndicatedTherapyHeaderFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.GRAY);
		cell.setBottomPadding(10);
	}
	
	private void applyIndicatedTherapyCellFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
	}
	
	private void applyClinicalSignificanceCellFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
	}
	
	private void applyClinicalTrialCellFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(0);
	}
	
	private void createClinicalSignificanceTables() throws IOException {
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignifanceTables = new ArrayList<Map<String, GeneVariantAndAnnotation>>();
		clinicalSignifanceTables.add(report.getSnpVariantsStrongClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsPossibleClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsUnknownClinicalSignificance());
		
		String[] tableTitles = new String[] {"Variants of Strong Clinical Significance",
		                                  "Variants of Possible Clinical Significance",
		                                  "Variants of Unknown Clinical Significance"};
		int counter = 0;
		for (Map<String, GeneVariantAndAnnotation> table : clinicalSignifanceTables) {
			this.updatePotentialNewPagePosition();
			PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
			PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
					PDPageContentStream.AppendMode.APPEND, true);
			PDStreamUtils.write(contentStream, tableTitles[counter], FinalReportTemplateConstants.MAIN_FONT_TYPE,
					FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
					FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
			contentStream.close();
			latestYPosition -= 20;
			this.createAClinicalSignificanceTable(table, currentPage);
			counter++;
			
			
		}
	}
	
	
	private void createAClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems, PDPage currentPage) throws IOException{
		float tableWidth = pageWidthMinusMargins;
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		for (String variant : tableItems.keySet()) {
			GeneVariantAndAnnotation item = tableItems.get(variant);
			Row<PDPage> row = table.createRow(12); 
			StringBuilder sb = new StringBuilder();
			sb.append("<b>").append(item.getGeneVariant()).append(":</b> ").append(item.getAnnotation());
			Cell<PDPage> cell = row.createCell(100, sb.toString());
			this.applyClinicalSignificanceCellFormatting(cell, FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		}
		this.latestYPosition = table.draw() - 20;
	}
	
	private void createCNVTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.CNV_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		contentStream.close();
		
		latestYPosition -= 20;
		
		float tableWidth = pageWidthMinusMargins;

		boolean cellBorder = true;
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = new BaseTable(latestYPosition, this.pageHeight - FinalReportTemplateConstants.MARGINTOP,
				FinalReportTemplateConstants.MARGINBOTTOM, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, cellBorder, true);
		List<CNVReport> items = report.getCnvs();
		
		//Headers
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(10, "CHR");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "Start");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "End");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "Copy Number");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(30, "Genes");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(30, "Comment");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		
		
		for (CNVReport item : items) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(10, item.getChrom());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, item.getStartFormatted());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, item.getEndFormatted());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, item.getCopyNumber() + "");
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(30, item.getGenes());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(30, item.getComment());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
		}

		latestYPosition = table.draw();
	}
	
	private void createTranslocationTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.TRANSLOCATION_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		contentStream.close();
		
		latestYPosition -= 20;
		
		float tableWidth = pageWidthMinusMargins;

		boolean cellBorder = true;
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, cellBorder, true);
		List<TranslocationReport> items = report.getTranslocations();
		
		//Headers
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(20, "Fusion Name");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "Gene1");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "Last Exon");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "Gene2");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "First Exon");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(40, "Comment");
		this.applyIndicatedTherapyHeaderFormatting(cellHeader, defaultFont);
		
		
		for (TranslocationReport item : items) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(20, item.getFusionName());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, item.getLeftGene());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, "0");
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, item.getRightGene());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(10, "0");
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
			cell = row.createCell(40, item.getComment());
			this.applyIndicatedTherapyCellFormatting(cell, defaultFont);
		}

		latestYPosition = table.draw();
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
			this.createFooterCell(row, "MRN " + caseSummary.getMedicalRecordNumber(), HorizontalAlignment.CENTER, 60);
			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 20);
			table.draw();
		}
	}
	
	private void addInformationAboutTheTest() throws IOException {
		// Title
		this.updatePotentialNewPagePosition();
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
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
	
}
