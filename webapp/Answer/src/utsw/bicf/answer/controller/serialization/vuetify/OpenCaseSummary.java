package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.FinalReport;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;
import utsw.bicf.answer.model.hybrid.PatientInfo;

public class OpenCaseSummary {
	
	PatientInfo patientInfo;
	String caseId;
	String caseName;
	List<String> effects; //unique list of effects. Used for filtering by checking which effects should be included
	Integer userId;
	SNPIndelVariantSummary snpIndelVariantSummary;
	CNVSummary cnvSummary;
	FusionSummary fusionSummary;
	Boolean isAllowed;

	public OpenCaseSummary(ModelDAO modelDAO, OrderCase aCase, FinalReport finalReport, String uniqueIdField, User user) {
		this.snpIndelVariantSummary = new SNPIndelVariantSummary(modelDAO, aCase, uniqueIdField);
		this.cnvSummary = new CNVSummary(modelDAO, aCase, uniqueIdField);
		this.fusionSummary = new FusionSummary(modelDAO, aCase, uniqueIdField);
		this.patientInfo = new PatientInfo(aCase, finalReport);
		this.caseId = aCase.getCaseId();
		this.caseName = aCase.getCaseName();
		this.userId = user.getUserId();
		effects = getUniqueEffects(aCase);
		this.isAllowed = true;
	}

	
	/**
	 * Create a unique list of effects in the current case.
	 * Sort alphabetically and tries to make the string prettier
	 * @param aCase
	 * @return
	 */
	private static List<String> getUniqueEffects(OrderCase aCase) {
		Set<String> effects = new HashSet<String>();
		for (Variant variant : aCase.getVariants()) {
			effects.addAll(variant.getEffects());
		}
		List<String> effectsFormatted = new ArrayList<String>();
		for (String effect : effects) {
			effect = effect.replaceAll("_", " ");
			effect = StringUtils.capitalize(effect);
			effectsFormatted.add(effect);
		}
		return effectsFormatted.stream().sorted().collect(Collectors.toList());
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

	public List<String> getEffects() {
		return effects;
	}

	public void setEffects(List<String> effects) {
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


	public FusionSummary getFusionSummary() {
		return fusionSummary;
	}


	public void setFusionSummary(FusionSummary fusionSummary) {
		this.fusionSummary = fusionSummary;
	}

}
