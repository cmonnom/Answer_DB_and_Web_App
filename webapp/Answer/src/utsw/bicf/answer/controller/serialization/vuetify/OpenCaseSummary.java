package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.FinalReport;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;

public class OpenCaseSummary {
	
	PatientInfo patientInfo;
	String caseId;
	String caseName;
	Map<String, Set<String>> effects; //unique list of effects. Used for filtering by checking which effects should be included
	Integer userId;
	SNPIndelVariantSummary snpIndelVariantSummary;
	CNVSummary cnvSummary;
	TranslocationSummary translocationSummary;
	Boolean isAllowed;
	List<ReportGroupForDisplay> reportGroups;

	public OpenCaseSummary(ModelDAO modelDAO, OrderCase aCase, FinalReport finalReport, String uniqueIdField, User user, List<ReportGroupForDisplay> reportGroups) {
		this.snpIndelVariantSummary = new SNPIndelVariantSummary(modelDAO, aCase, uniqueIdField, reportGroups);
		this.cnvSummary = new CNVSummary(modelDAO, aCase, uniqueIdField);
		this.translocationSummary = new TranslocationSummary(modelDAO, aCase, uniqueIdField);
		this.patientInfo = new PatientInfo(aCase, finalReport);
		this.caseId = aCase.getCaseId();
		this.caseName = aCase.getCaseName();
		this.userId = user.getUserId();
		this.effects = getUniqueEffects(aCase);
		this.isAllowed = true;
		this.reportGroups = reportGroups;
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
				effect = effect.replaceAll("_", " ");
				effect = StringUtils.capitalize(effect);
				effectsFormatted.add(effect);
			}
			formattedEffectsByImpact.put(impact, effectsFormatted.stream().sorted().collect(Collectors.toSet()));
		}
		return formattedEffectsByImpact;
		
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


}
