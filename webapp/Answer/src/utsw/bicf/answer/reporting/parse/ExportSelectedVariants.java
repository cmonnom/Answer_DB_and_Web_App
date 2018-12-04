package utsw.bicf.answer.reporting.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.security.FileProperties;

public class ExportSelectedVariants {

	private static final List<String> HEADERS = new ArrayList<String>();
	private static final List<String> COLUMN_LINKS = new ArrayList<String>();
	static {
		HEADERS.add("Case ID");
		HEADERS.add("LociGRCh38");
		HEADERS.add("ID");
		HEADERS.add("Gene");
		HEADERS.add("AminoAcid");
		HEADERS.add("Effect");
		HEADERS.add("Ref");
		HEADERS.add("Alt");
		HEADERS.add("Tumor DNA AF");
		HEADERS.add("Tumor DNA Depth");
		HEADERS.add("Normal DNA AF");
		HEADERS.add("Normal DNA Depth");
		HEADERS.add("Tumor RNA AF");
		HEADERS.add("Tumor RNA Depth");
		HEADERS.add("Classification");
		HEADERS.add("Tier");
		HEADERS.add("Comments");
		HEADERS.add("Drugs");
//		HEADERS.add("Clinical trials");
		HEADERS.add("PMID");
		HEADERS.add("gnomAD (MAF %, hom)");
		HEADERS.add("ExAC (MAF %, hom)");
		HEADERS.add("ClinVar");
		HEADERS.add("OncoKB");
		HEADERS.add("MCG");
		HEADERS.add("CIVIC");
		HEADERS.add("JAX CKB");

		COLUMN_LINKS.add("Clinical trials"); //Clinical trials
		COLUMN_LINKS.add("PMID"); //PMIds
	}

	List<Variant> selectedVariants;
	OrderCase detailedCase;
	String csv;
	FileProperties fileProperties;

	public ExportSelectedVariants(OrderCase detailedCase, List<Variant> selectedVariants,
			FileProperties fileProperties) {
		this.detailedCase = detailedCase;
		this.selectedVariants = selectedVariants;
		this.fileProperties = fileProperties;
	}

	public File createExcel() throws IOException {
		Workbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		//styles
		CellStyle cs = workbook.createCellStyle();
//		cs.setWrapText(true);
		CellStyle linkStyle = workbook.createCellStyle();
		Font linkFont = workbook.createFont();
		linkFont.setUnderline(Font.U_SINGLE);
		linkFont.setColor(IndexedColors.BLUE.getIndex());
		linkStyle.setFont(linkFont);
		
		Sheet sheet = workbook.createSheet("Variants");
		
		Row header = sheet.createRow(0);
		for (int i = 0; i < HEADERS.size(); i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(HEADERS.get(i));
		}
		
		int currentRowNb = 1;
		for (Variant v : selectedVariants) {
			if (v.getUtswAnnotated() != null && v.getUtswAnnotated()) {
				List<Annotation> annotations = v.getReferenceVariant().getUtswAnnotations();
				for (Annotation a : annotations) {
					Row row = sheet.createRow(currentRowNb);
					currentRowNb++;
					List<String> items = createVariantItemsBeforeAnnotation(v); //duplicate lines for each comment
					items.add(a.getClassification());
					items.add(a.getTier());
					items.add(a.getCategory() + ": " + a.getText());
					items.add(""); //drugs
//					items.add(a.getNctids() != null ? a.getNctids().stream()
//							.map(id -> "https://clinicaltrials.gov/ct2/show/" + id)
//							.collect(Collectors.joining(";")) : "");
					items.add(a.getPmids() != null ? a.getPmids().stream()
							.map(id -> "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id)
							.collect(Collectors.joining(";")) : "");
					items.addAll(createVariantItemsAfterAnnotation(v));
					//write the row
					for (int i = 0; i < items.size(); i++) {
						Cell cell  = row.createCell(i);
						if (COLUMN_LINKS.contains(HEADERS.get(i))) {
							cell.setCellStyle(linkStyle);
							Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
							link.setAddress(items.get(i));
							cell.setHyperlink(link);
						}
						else {
							cell.setCellStyle(cs);
						}
						cell.setCellValue(items.get(i));
					}
				}
				
			}
			else { //a row with empty annotations
				Row row = sheet.createRow(currentRowNb);
				currentRowNb++;
				List<String> items = createVariantItemsBeforeAnnotation(v);
				items.add(""); //Classification
				items.add(""); //Tier
				items.add(""); //Comment
				items.add(""); //Drugs
				items.add(""); //NCTIDs
				items.add(""); // PMIDs
				items.addAll(createVariantItemsAfterAnnotation(v));
				//write the row
				for (int i = 0; i < items.size(); i++) {
					Cell cell  = row.createCell(i);
					cell.setCellValue(items.get(i));
				}
			}
			
		}
		
		for (int i = 0; i < HEADERS.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		File outputFile = new File(fileProperties.getExcelFilesDir(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "variants.xlsx");
		
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		workbook.write(outputStream);
		workbook.close();
		
		return outputFile;
	}

	public String createCSV() {
		StringBuilder csvContent = new StringBuilder();
		// write the headers
		csvContent.append(HEADERS.stream().collect(Collectors.joining(","))).append("\n");

		for (Variant v : selectedVariants) {

			// TODO finish the row

			if (v.getUtswAnnotated() != null && v.getUtswAnnotated()) {
				List<Annotation> annotations = v.getReferenceVariant().getUtswAnnotations();
				for (Annotation a : annotations) {
					List<String> items = createVariantItemsBeforeAnnotation(v); // duplicate lines for each comment
					items.add(a.getClassification());
					items.add(a.getTier());
					String category = a.getCategory() != null ? a.getCategory() : "";
					items.add(category + ": " + a.getText());
					items.add(
							a.getPmids() != null
									? a.getPmids().stream().map(id -> "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id)
											.collect(Collectors.joining(";"))
									: "");
//					items.add(
//							a.getNctids() != null
//									? a.getNctids().stream().map(id -> "https://clinicaltrials.gov/ct2/show/" + id)
//											.collect(Collectors.joining(";"))
//									: "");
					items.addAll(createVariantItemsAfterAnnotation(v));
					// write the row
					csvContent.append(createCSVRow(items)).append("\n");
				}

			} else { // a row with empty annotations
				List<String> items = createVariantItemsBeforeAnnotation(v);
				items.add(""); // Classification
				items.add(""); // Tier
				items.add(""); // Category
				items.add(""); // PMIDs
				items.addAll(createVariantItemsAfterAnnotation(v));
				// write the row
				csvContent.append(createCSVRow(items)).append("\n");
			}

		}

		return csvContent.toString();
	}

	private String createCSVRow(List<String> items) {
		return items.stream().collect(Collectors.joining(","));
	}

	private List<String> createVariantItemsBeforeAnnotation(Variant v) {
		List<String> items = new ArrayList<String>();
		items.add(detailedCase.getCaseId());
		items.add(v.getChrom() + ":" + v.getPos());
		items.add(v.getIds().stream().collect(Collectors.joining(";")));
		items.add(v.getGeneName());
		items.add(v.getNotation());
		items.add(v.getEffects().stream().collect(Collectors.joining(";")));
		items.add(v.getReferenceVariant().getReference());
		items.add(v.getReferenceVariant().getAlt());
		items.add(v.getTumorAltFrequency() != null ? v.getTumorAltFrequency().toString() : "");
		items.add(v.getTumorTotalDepth() != null ? v.getTumorTotalDepth() + "" : "");
		items.add(v.getNormalAltFrequency() != null ? v.getNormalAltFrequency().toString() : "");
		items.add(v.getNormalTotalDepth() != null ? v.getNormalTotalDepth() + "" : "");
		items.add(v.getRnaAltFrequency() != null ? v.getRnaAltFrequency() + "" : "");
		items.add(v.getRnaTotalDepth() != null ? v.getRnaTotalDepth() + "" : "");
		return items;
	}

	private List<String> createVariantItemsAfterAnnotation(Variant v) {
		List<String> items = new ArrayList<String>();
		items.add(v.getGnomadPopmaxAlleleFrequency() != null ? v.getGnomadPopmaxAlleleFrequency() + "" : ""); // gnomAD
		items.add(v.getExacAlleleFrequency() != null ? v.getExacAlleleFrequency() + "" : ""); // ExAC
		items.add(""); // ClinVar
		items.add(""); // OncoKB
		items.add(""); // CIVIC
		items.add(""); // JAX CKB

		return items;
	}

}
