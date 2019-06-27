package utsw.bicf.answer.controller.serialization.vuetify;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;
import utsw.bicf.answer.security.QcAPIAuthentication;

public class OpenCaseSummary {
	
	PatientInfo patientInfo;
	String caseId;
	String caseName;
	Map<String, Set<String>> effects; //unique list of effects. Used for filtering by checking which effects should be included
	Set<String> failedFilters; //unique list of failed QC filters. Used for filtering by checking which failed reason should be included
	Integer userId;
	SNPIndelVariantSummary snpIndelVariantSummary;
	CNVSummary cnvSummary;
	TranslocationSummary translocationSummary;
	Boolean isAllowed;
	List<ReportGroupForDisplay> reportGroups;
	String qcUrl;
	String tumorVcf;
	List<String> assignedToIds;
	String type;
	boolean reportReady;
	Map<String, String> checkBoxLabelsByValue;

	public OpenCaseSummary(ModelDAO modelDAO, QcAPIAuthentication qcAPI, OrderCase aCase, String uniqueIdField, User user, List<ReportGroupForDisplay> reportGroups) throws JsonParseException, JsonMappingException, UnsupportedOperationException, URISyntaxException, IOException {
		List<HeaderOrder> snpOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "SNP/Indel Variants");
		List<HeaderOrder> cnvOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "CNVs");
		List<HeaderOrder> ftlOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Fusions / Translocations");
		this.snpIndelVariantSummary = new SNPIndelVariantSummary(modelDAO, aCase, uniqueIdField, reportGroups, snpOrders, user);
		this.cnvSummary = new CNVSummary(modelDAO, aCase, uniqueIdField, cnvOrders, user);
		this.translocationSummary = new TranslocationSummary(modelDAO, aCase, uniqueIdField, ftlOrders, user);
		this.patientInfo = new PatientInfo(aCase);
		this.caseId = aCase.getCaseId();
		this.caseName = aCase.getCaseName();
		this.userId = user.getUserId();
		this.effects = getUniqueEffects(aCase);
		this.failedFilters = getUniqueFailedFilters(aCase);
		this.isAllowed = true;
		this.reportGroups = reportGroups;
		this.qcUrl = qcAPI.getUrl();
		this.tumorVcf = aCase.getTumorVcf();
		this.assignedToIds = aCase.getAssignedTo();
		this.type = aCase.getType();
		
		this.checkBoxLabelsByValue = Variant.CHECKBOX_FILTERS_MAP;
//		for (String formattedValue : Variant.CHECKBOX_FILTERS_MAP.keySet()) {
//			this.checkBoxLabelsByValue.put(Variant.CHECKBOX_FILTERS_MAP.get(formattedValue), formattedValue);
//		}
		
		if (user.getIndividualPermission().getCanView()) {
			reportReady = CaseHistory.lastStepMatches(aCase, CaseHistory.STEP_REPORTING);
		}
	}

	
	/**
	 * Create a unique list of effects in the current case.
	 * Sort alphabetically and tries to make the string prettier
	 * @param aCase
	 * @return
	 */
	private static Map<String, Set<String>> getUniqueEffects(OrderCase aCase) {
		Map<String, Set<String>> effectsByImpact = new HashMap<String, Set<String>>(); 
		for (Variant variant : aCase.getVariants()) {
			Set<String> effects = effectsByImpact.get(variant.getImpact());
			if (effects == null) {
				effects = new HashSet<String>();
			}
			effects.addAll(variant.getEffects());
			effectsByImpact.put(variant.getImpact(), effects);
		}
		Map<String, Set<String>> formattedEffectsByImpact = new HashMap<String, Set<String>>(); 
		for (String impact : effectsByImpact.keySet()) {
			Set<String> effects = effectsByImpact.get(impact);
			Set<String> effectsFormatted = new HashSet<String>();
			for (String effect : effects) {
				String effectFormatted = effect.replaceAll("_", " ");
				effectFormatted = StringUtils.capitalize(effectFormatted);
				Variant.CHECKBOX_FILTERS_MAP.put(effectFormatted, effect);
				effectsFormatted.add(effectFormatted);
			}
			formattedEffectsByImpact.put(impact, effectsFormatted.stream().sorted().collect(Collectors.toSet()));
		}
		return formattedEffectsByImpact;
		
	}
	
	/**
	 * Create a unique list of effects in the current case.
	 * Sort alphabetically and tries to make the string prettier
	 * @param aCase
	 * @return
	 */
	private static Set<String> getUniqueFailedFilters(OrderCase aCase) {
		Set<String> failedFilters = new HashSet<String>(); 
		for (Variant variant : aCase.getVariants()) {
			for (String filter : variant.getFilters()) {
				String filterFormatted = TypeUtils.splitCamelCaseString(filter);
				Variant.CHECKBOX_FILTERS_MAP.put(filterFormatted, filter);
				if (!Variant.VALUE_FAIL.equals(filter) && !Variant.VALUE_PASS.equals(filter)) {
					failedFilters.add(filterFormatted);
				}
			}
		}
		return failedFilters;
		
	}

	
	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public Map<String, Set<String>> getEffects() {
		return effects;
	}

	public void setEffects(Map<String, Set<String>> effects) {
		this.effects = effects;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public SNPIndelVariantSummary getSnpIndelVariantSummary() {
		return snpIndelVariantSummary;
	}


	public void setSnpIndelVariantSummary(SNPIndelVariantSummary snpIndelVariantSummary) {
		this.snpIndelVariantSummary = snpIndelVariantSummary;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public CNVSummary getCnvSummary() {
		return cnvSummary;
	}


	public void setCnvSummary(CNVSummary cnvSummary) {
		this.cnvSummary = cnvSummary;
	}


	public TranslocationSummary getTranslocationSummary() {
		return translocationSummary;
	}


	public void setTranslocationSummary(TranslocationSummary translocationSummary) {
		this.translocationSummary = translocationSummary;
	}


	public List<ReportGroupForDisplay> getReportGroups() {
		return reportGroups;
	}


	public void setReportGroups(List<ReportGroupForDisplay> reportGroups) {
		this.reportGroups = reportGroups;
	}


	public String getQcUrl() {
		return qcUrl;
	}


	public void setQcUrl(String qcUrl) {
		this.qcUrl = qcUrl;
	}


	public List<String> getAssignedToIds() {
		return assignedToIds;
	}


	public void setAssignedToIds(List<String> assignedToIds) {
		this.assignedToIds = assignedToIds;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isReportReady() {
		return reportReady;
	}


	public void setReportReady(boolean reportReady) {
		this.reportReady = reportReady;
	}


	public String getTumorVcf() {
		return tumorVcf;
	}


	public void setTumorVcf(String tumorVcf) {
		this.tumorVcf = tumorVcf;
	}


	public Set<String> getFailedFilters() {
		return failedFilters;
	}


	public void setFailedFilters(Set<String> failedFilters) {
		this.failedFilters = failedFilters;
	}


	public Map<String, String> getCheckBoxLabelsByValue() {
		return checkBoxLabelsByValue;
	}


	public void setCheckBoxLabelsByValue(Map<String, String> checkBoxLabelsByValue) {
		this.checkBoxLabelsByValue = checkBoxLabelsByValue;
	}






//	public String getQcUrl() {
//		return qcUrl;
//	}
//
//
//	public void setQcUrl(String qcUrl) {
//		this.qcUrl = qcUrl;
//	}


}
