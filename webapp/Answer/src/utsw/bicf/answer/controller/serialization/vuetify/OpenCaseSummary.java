package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.FinalReport;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.OpenCaseRow;
import utsw.bicf.answer.model.hybrid.PatientInfo;

public class OpenCaseSummary extends Summary<OpenCaseRow> {
	
	PatientInfo patientInfo;
	String caseId;
	String caseName;
	List<String> effects; //unique list of effects. Used for filtering by checking which effects should be included
	Integer userId;

	public OpenCaseSummary(ModelDAO modelDAO, OrderCase aCase, FinalReport finalReport, String uniqueIdField, User user) {
		super(createRows(modelDAO, aCase), uniqueIdField);
		this.patientInfo = new PatientInfo(aCase, finalReport);
		this.caseId = aCase.getCaseId();
		this.caseName = aCase.getCaseName();
		this.userId = user.getUserId();
		effects = getUniqueEffects(aCase);
	}

	private static List<OpenCaseRow> createRows(ModelDAO modelDAO, OrderCase aCase) {
		List<OpenCaseRow> rows = new ArrayList<OpenCaseRow>();
		for (Variant variant : aCase.getVariants()) {
			rows.add(new OpenCaseRow(variant));
			
		}
		return rows;
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

	@Override
	public void initializeHeaders() {
		Header chromPos = new Header("CHR", "chromPos");
		chromPos.setWidth("200px");
		headers.add(chromPos);
		Header geneVariant = new Header("Gene Variant", "geneVariant");
		geneVariant.setWidth("225px");
		headers.add(geneVariant);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("150px");
		iconFlags.setIsFlag(true);
		headers.add(iconFlags);
		Header effects = new Header("Effects", "effects");
		headers.add(effects);
		Header tumorTotalDepth = new Header(new String[] {"Tumor"," Total Depth"}, "tumorTotalDepth", Units.NB);
		headers.add(tumorTotalDepth);
		Header taf = new Header(new String[] {"Tumor Alt", "Percent"}, "tumorAltFrequency", Units.PCT);
		taf.setWidth("100px");
		headers.add(taf);
//		Header tumorAltDepth = new Header(new String[] {"Tumor","Depth"}, "tumorAltDepth", Units.NB);
//		headers.add(tumorAltDepth);
		Header normalTotalDepth = new Header(new String[] {"Normal"," Total Depth"}, "normalTotalDepth", Units.NB);
		headers.add(normalTotalDepth);
		Header naf = new Header(new String[] {"Normal Alt", "Percent"}, "normalAltFrequency", Units.PCT);
		naf.setWidth("100px");
		headers.add(naf);
//		Header normalAltDepth = new Header(new String[] {"Normal","Depth"}, "normalAltDepth", Units.NB);
//		headers.add(normalAltDepth);
		Header rnaTotalDepth = new Header(new String[] {"RNA"," Total Depth"}, "rnaTotalDepth", Units.NB);
		headers.add(rnaTotalDepth);
		Header raf = new Header(new String[] {"RNA Alt", "Percent"}, "rnaAltFrequency", Units.PCT);
		raf.setWidth("100px");
		headers.add(raf);
//		Header rnaAltDepth = new Header(new String[] {"RNA","Depth"}, "rnaAltDepth", Units.NB);
//		headers.add(rnaAltDepth);
		//keep in the same order
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
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

}
