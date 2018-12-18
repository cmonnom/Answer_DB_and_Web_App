package utsw.bicf.answer.reporting.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

public class MDAReportTemplate {

	File sourceHTML;
	String mrn;
	String patient;
	Map<String, AnnotationRow> annotationRows = null;
	Map<String, FrequencyRow> frequencyRows = null;
	List<BiomarkerTrialsRow> selectedBiomarkers = null;
	List<BiomarkerTrialsRow> relevantBiomarkers = null;
	List<BiomarkerTrialsRow> selectedAdditionalBiomarkers = null;
	List<BiomarkerTrialsRow> relevantAdditionalBiomarkers = null;
	
	public MDAReportTemplate() {
		
	}

	public MDAReportTemplate(File sourceHTML) throws IOException {
		this.sourceHTML = sourceHTML;
		init();
	}

	public MDAReportTemplate(String emailContent) {
		if (emailContent != null && !emailContent.equals("")) {
			Document htmlDoc = Jsoup.parse(emailContent);
			parseEmail(htmlDoc);
		}
	}

	private void parseEmail(Document htmlDoc) {
		this.mrn = htmlDoc.select("b:contains(MRN:)").first().nextSibling().outerHtml().trim();
		this.patient = htmlDoc.select("b:contains(Patient:)").first().nextSibling().outerHtml().trim();

		Elements tables = htmlDoc.select("table");
		for (Element table : tables) {
			if (table.select("th:contains(Tested Panel)").first() != null) {
				// annotation table
				annotationRows = parseAnnotationTable(table);
			} else if (table.select("th:contains(CMS50)").first() != null) {
				// frequency table
				frequencyRows = parseFrequencyTable(table);
			} else if (this.isSelectedAdditionalBiomarkers(table)) {
				selectedAdditionalBiomarkers = parseBiomarkerTable(table, "selected");
			}
			else if (this.isSelectedBiomarkers(table)) {
				selectedBiomarkers = parseBiomarkerTable(table, "selected");
			} else if (this.isRelevantAdditionalBiomarkers(table)) {
				relevantAdditionalBiomarkers = parseBiomarkerTable(table, "relevant");
			}
			else if (this.isRelevantBiomarkers(table)) {
				relevantBiomarkers = parseBiomarkerTable(table, "relevant");

			}
		}
	}

	//Future versions of the Moclia report might have different ways required for identifying
	//the correct table. Add or statements if needed here.
	private boolean isSelectedBiomarkers(Element table) {
		return table.select("th:contains(Selected Biomarker(s)*)").first() != null
				&& table.select("th:contains(Additional Required Biomarker(s))").first() == null;
	}
	
	private boolean isSelectedAdditionalBiomarkers(Element table) {
		return table.select("th:contains(Selected Biomarker(s)*)").first() != null
				&& table.select("th:contains(Additional Required Biomarker(s))").first() != null;
	}
	
	private boolean isRelevantBiomarkers(Element table) {
		return table.select("th:contains(Relevant Biomarker(s)*)").first() != null
				&& table.select("th:contains(Additional Required Biomarker(s))").first() == null;
	}
	
	private boolean isRelevantAdditionalBiomarkers(Element table) {
		return table.select("th:contains(Relevant Biomarker(s)*)").first() != null
				&& table.select("th:contains(Additional Required Biomarker(s))").first() != null;
	}

	private void init() throws IOException {
		if (!sourceHTML.exists()) {
			throw new FileNotFoundException();
		}
		Document htmlDoc = Jsoup.parse(sourceHTML, "UTF-8");
		parseEmail(htmlDoc);
	}

	private Map<String, AnnotationRow> parseAnnotationTable(Element table) {
		Elements rows = table.select("tr");
		Element headerRow = rows.first();
		List<String> headers = headerRow.select("th").eachText();
		Map<String, Integer> headerMap = buildHeaderMap(headers);
		Map<String, AnnotationRow> parsedRows = new LinkedHashMap<String, AnnotationRow>();
		String lastGeneVariant = null;
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements items = row.select("td");
			if (items.size() == headers.size()) { // regular row
				AnnotationRow values = new AnnotationRow();
				values.setTestedPanel(items.get(headerMap.get(AnnotationRow.HEADER_TESTED_PANEL)).text());
				values.setReportDate(items.get(headerMap.get(AnnotationRow.HEADER_REPORT_DATE)).text());
				if (values.getReportDate() != null) {
					LocalDate reportDate = LocalDate.parse(values.getReportDate(), TypeUtils.localDateFormatter);
					values.setCreatedSince(TypeUtils.dateSince(reportDate));
				}
				values.setReportNb(items.get(headerMap.get(AnnotationRow.HEADER_REPORT_NB)).text());
				values.setGene(items.get(headerMap.get(AnnotationRow.HEADER_GENE)).text());
				values.setAlteration(items.get(headerMap.get(AnnotationRow.HEADER_ALTERATION)).text());
				lastGeneVariant = values.getGene() + values.getAlteration();
				values.setAllelicFrequency(items.get(headerMap.get(AnnotationRow.HEADER_ALLELIC_FREQUENCY)).text());
				values.setCnv(items.get(headerMap.get(AnnotationRow.HEADER_CNV)).text());
				values.setFunctionalSignificance(
						items.get(headerMap.get(AnnotationRow.HEADER_FUNCTIONAL_SIGNIFICANCE)).text());
				Elements annotationCategories = items.get(headerMap.get(AnnotationRow.HEADER_ANNOTATION)).children();
				List<String> annotations = annotationCategories.eachText();
				values.setAnnotations(annotations);
				values.setActionableGene(items.get(headerMap.get(AnnotationRow.HEADER_ACTIONABLE_GENE)).text());
				// deal with unmerged cells
				addUnmergedCells(values, items.get(headerMap.get(AnnotationRow.HEADER_ACTIONABLE_VARIANT)).text(),
						items.get(headerMap.get(AnnotationRow.HEADER_ACTIONABLE_FOR)).text());
				parsedRows.put(lastGeneVariant, values);
			} else { // rest of the row with unmerged cells (actionable variants and actionable fors)
				AnnotationRow values = parsedRows.get(lastGeneVariant);
				if (values != null) {
					String actionableVariant = items.get(0).text();
					String actionableFor = items.get(1).text();
					addUnmergedCells(values, actionableVariant, actionableFor);
				}
				// else there was something wrong
			}
		}
		// for (AnnotationRow row : parsedRows.values()) {
		// row.prettyPrint();
		// }
		return parsedRows;
	}

	private Map<String, FrequencyRow> parseFrequencyTable(Element table) {
		Elements rows = table.select("tr");
		Element headerRow = rows.first();
		List<String> headers = headerRow.select("th").eachText();
		Map<String, Integer> headerMap = buildHeaderMap(headers);
		Map<String, FrequencyRow> parsedRows = new LinkedHashMap<String, FrequencyRow>();
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements items = row.select("td");
			FrequencyRow values = new FrequencyRow();
			values.setGene(items.get(headerMap.get(FrequencyRow.HEADER_GENE)).text());
			values.setAlteration(items.get(headerMap.get(FrequencyRow.HEADER_ALTERATION)).text());
			values.setcBio(items.get(headerMap.get(FrequencyRow.HEADER_CBIO)).text());
			values.setCosmic(items.get(headerMap.get(FrequencyRow.HEADER_COSMIC)).text());
			values.setCms50(items.get(headerMap.get(FrequencyRow.HEADER_CMS50)).text());
			values.setT200(items.get(headerMap.get(FrequencyRow.HEADER_T200)).text());
			values.setGermlineInT200Dataset(items.get(headerMap.get(FrequencyRow.HEADER_GERMLINE)).text());
			parsedRows.put(values.getGene() + values.getAlteration(), values);
		}
		// for (FrequencyRow row : parsedRows.values()) {
		// row.prettyPrint();
		// }
		return parsedRows;
	}

	/**
	 * Should handle both table or selected and relevant biomarker tables
	 * 
	 * @param table
	 * @param selectedOrRelevant
	 * @return
	 */
	private List<BiomarkerTrialsRow> parseBiomarkerTable(Element table, String selectedOrRelevant) {
		Elements rows = table.select("tr");
		Element headerRow = rows.first();
		List<String> headers = headerRow.select("th").eachText();
		Map<String, Integer> headerMap = buildHeaderMap(headers);
		List<BiomarkerTrialsRow> parsedRows = new ArrayList<BiomarkerTrialsRow>();
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements items = row.select("td");
			BiomarkerTrialsRow values = new BiomarkerTrialsRow();
			if (selectedOrRelevant.equals("selected")) {
				values.setSelectedBiomarker(
						items.get(headerMap.get(BiomarkerTrialsRow.HEADER_SELECTED_BIOMARKER)).text());
			} else {
				values.setSelectedBiomarker(
						items.get(headerMap.get(BiomarkerTrialsRow.HEADER_RELEVANT_BIOMARKER)).text());
			}
			
			Element elt = headerMap.get(BiomarkerTrialsRow.HEADER_ADD_REQUIRED_BIOMARKERS) != null ? items.get(headerMap.get(BiomarkerTrialsRow.HEADER_ADD_REQUIRED_BIOMARKERS)) : null;
			if (elt != null) {
				values.setAdditionalRequiredBiomarkers(elt.text());
			}
			values.setDrugs(parseUnderlineDrugs(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_DRUGS))));
			values.setTitle(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_TITLE)).text());
			values.setNctid(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_NCTID)).text());
			values.setMdaddProtocolId(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_MDACC_PROTOCOL_ID)).text());
			values.setPhase(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_PHASE)).text());
			values.setPi(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_PI)).text());
			values.setDept(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_DEPT)).text());
			parsedRows.add(values);
		}
		// for (BiomarkerTrialsRow row : parsedRows) {
		// row.prettyPrint();
		// }
		return parsedRows;
	}
	
	private String parseUnderlineDrugs(Element item) {
		return item.select("u").eachText().stream().collect(Collectors.joining(","));
	}

	private void addUnmergedCells(AnnotationRow values, String actionableVariant, String actionableFor) {
		List<String> actionableVariants = values.getActionableVariants();
		actionableVariants.add(actionableVariant);
		values.setActionableVariants(actionableVariants);
		List<String> actionableFors = values.getActionableFors();
		actionableFors.add(actionableFor);
		values.setActionableFors(actionableFors);
	}

	private static Map<String, Integer> buildHeaderMap(List<String> headers) {
		Map<String, Integer> headerMap = new HashMap<String, Integer>();
		for (int i = 0; i < headers.size(); i++) {
			headerMap.put(headers.get(i), i);
		}
		return headerMap;
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	// public File getSourceHTML() {
	// return sourceHTML;
	// }
	//
	// public void setSourceHTML(File sourceHTML) {
	// this.sourceHTML = sourceHTML;
	// }

	public String getMrn() {
		return mrn;
	}

	public void setMrn(String mrn) {
		this.mrn = mrn;
	}

	public Map<String, AnnotationRow> getAnnotationRows() {
		return annotationRows;
	}

	public void setAnnotationRows(Map<String, AnnotationRow> annotationRows) {
		this.annotationRows = annotationRows;
	}

	public Map<String, FrequencyRow> getFrequencyRows() {
		return frequencyRows;
	}

	public void setFrequencyRows(Map<String, FrequencyRow> frequencyRows) {
		this.frequencyRows = frequencyRows;
	}

	public List<BiomarkerTrialsRow> getSelectedBiomarkers() {
		return selectedBiomarkers;
	}

	public void setSelectedBiomarkers(List<BiomarkerTrialsRow> selectedBiomarkers) {
		this.selectedBiomarkers = selectedBiomarkers;
	}

	public List<BiomarkerTrialsRow> getRelevantBiomarkers() {
		return relevantBiomarkers;
	}

	public void setRelevantBiomarkers(List<BiomarkerTrialsRow> relevantBiomarkers) {
		this.relevantBiomarkers = relevantBiomarkers;
	}

	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public List<BiomarkerTrialsRow> getSelectedAdditionalBiomarkers() {
		return selectedAdditionalBiomarkers;
	}

	public void setSelectedAdditionalBiomarkers(List<BiomarkerTrialsRow> selectedAdditionalBiomarkers) {
		this.selectedAdditionalBiomarkers = selectedAdditionalBiomarkers;
	}

	public List<BiomarkerTrialsRow> getRelevantAdditionalBiomarkers() {
		return relevantAdditionalBiomarkers;
	}

	public void setRelevantAdditionalBiomarkers(List<BiomarkerTrialsRow> relevantAdditionalBiomarkers) {
		this.relevantAdditionalBiomarkers = relevantAdditionalBiomarkers;
	}

}
