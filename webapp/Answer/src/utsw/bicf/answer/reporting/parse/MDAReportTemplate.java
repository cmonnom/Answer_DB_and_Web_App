package utsw.bicf.answer.reporting.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MDAReportTemplate {

	File sourceHTML;
	String mrn;
	Map<String, AnnotationRow> annotationRows = null;
	Map<String, FrequencyRow> frequencyRows = null;
	List<BiomarkerTrialsRow> selectedBiomarkers = null;
	List<BiomarkerTrialsRow> relevantBiomarkers = null;

	public MDAReportTemplate(File sourceHTML) throws IOException {
		this.sourceHTML = sourceHTML;
		init();
	}

	private void init() throws IOException {
		if (!sourceHTML.exists()) {
			throw new FileNotFoundException();
		}
		Document htmlDoc = Jsoup.parse(sourceHTML, "UTF-8");

		this.mrn = htmlDoc.select("b:contains(MRN:)").first().nextElementSibling().nextSibling().outerHtml();

		Elements tables = htmlDoc.select("table");
		for (Element table : tables) {
			if (table.select("td:contains(Tested Panel)").first() != null) {
				// annotation table
				annotationRows = parseAnnotationTable(table);
			} else if (table.select("td:contains(CMS50)").first() != null) {
				// frequency table
				frequencyRows = parseFrequencyTable(table);
			} else if (table.select("td:contains(Biomarker-Selected Trials)").first() != null) {
				selectedBiomarkers = parseBiomarkerTable(table, "selected");
			} else if (table.select("td:contains(Biomarker-Relevant Trials)").first() != null) {
				relevantBiomarkers = parseBiomarkerTable(table, "relevant");
			}
		}
	}

	private Map<String, AnnotationRow> parseAnnotationTable(Element table) {
		Elements rows = table.select("tr");
		Element headerRow = rows.first();
		List<String> headers = headerRow.select("b").eachText();
		Map<String, Integer> headerMap = buildHeaderMap(headers);
		Map<String, AnnotationRow> parsedRows = new LinkedHashMap<String, AnnotationRow>();
		String lastGeneVariant = null;
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements items = row.select("td");
			if (items.size() == headers.size()) { // regular row
				AnnotationRow values = new AnnotationRow();
				values.setTestedPanel(items.get(headerMap.get(AnnotationRow.HEADER_TESTED_PANEL)).text());
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
//		for (AnnotationRow row : parsedRows.values()) {
//			row.prettyPrint();
//		}
		return parsedRows;
	}

	private Map<String, FrequencyRow> parseFrequencyTable(Element table) {
		Elements rows = table.select("tr");
		Element headerRow = rows.get(1); // "Frequency in all tumor types" is a row for some reason. Need to skip it
		List<String> headers = headerRow.select("b").eachText();
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
//		for (FrequencyRow row : parsedRows.values()) {
//			row.prettyPrint();
//		}
		return parsedRows;
	}

	/**
	 * Should handle both table or selected and relevant biomarker tables
	 * @param table
	 * @param selectedOrRelevant
	 * @return
	 */
	private List<BiomarkerTrialsRow> parseBiomarkerTable(Element table, String selectedOrRelevant) {
		Elements rows = table.select("tr");
		Element headerRow = rows.get(1); // "Frequency in all tumor types" is a row for some reason. Need to skip it
		List<String> headers = headerRow.select("b").eachText();
		Map<String, Integer> headerMap = buildHeaderMap(headers);
		List<BiomarkerTrialsRow> parsedRows = new ArrayList<BiomarkerTrialsRow>();
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements items = row.select("td");
			BiomarkerTrialsRow values = new BiomarkerTrialsRow();
			if (selectedOrRelevant.equals("selected")) {
				values.setSelectedBiomarker(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_SELECTED_BIOMARKER)).text());
			}
			else {
				values.setSelectedBiomarker(items.get(headerMap.get(BiomarkerTrialsRow.HEADER_RELEVANT_BIOMARKER)).text());
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
//		for (BiomarkerTrialsRow row : parsedRows) {
//			row.prettyPrint();
//		}
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

	public File getSourceHTML() {
		return sourceHTML;
	}

	public void setSourceHTML(File sourceHTML) {
		this.sourceHTML = sourceHTML;
	}

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

}
