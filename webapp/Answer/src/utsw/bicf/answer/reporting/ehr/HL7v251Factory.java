package utsw.bicf.answer.reporting.ehr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v251.datatype.CWE;
import ca.uhn.hl7v2.model.v251.datatype.ED;
import ca.uhn.hl7v2.model.v251.datatype.NM;
import ca.uhn.hl7v2.model.v251.datatype.NR;
import ca.uhn.hl7v2.model.v251.datatype.ST;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.NTE;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.DefaultEscaping;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.Parser;
import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.EnsemblRequestUtils;
import utsw.bicf.answer.db.api.utils.ReportBuilder;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.reporting.ehr.loinc.LOINC;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCAAChangeType;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCDNAChangeType;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCGenomicSource;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCItem;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCMSI;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCVariantCategory;
import utsw.bicf.answer.reporting.ehr.model.HL7Therapy;
import utsw.bicf.answer.reporting.ehr.model.HL7Variant;
import utsw.bicf.answer.reporting.ehr.model.TempusTrial;
import utsw.bicf.answer.reporting.ehr.utils.HL7Utils;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.security.EnsemblProperties;
import utsw.bicf.answer.security.OtherProperties;

public class HL7v251Factory {
	
	private DefaultEscaping hl7Escaping = new DefaultEscaping();
	private HL7Utils utils = new HL7Utils();
	Report report;
	OrderCase caseSummary;
	RequestUtils requestUtils;
	File pdfFile;
	EnsemblProperties ensemblProps;
	OtherProperties otherProps;
	ModelDAO modelDAO;
	
	String overridePatientName;
	String overrideMRN;
	String overrideDOB;
	String overrideGender;
	String overrideOrder;
	String overrideProviderIdName;
	boolean humanReadable;
	String beakerId;
	String overrideTestName;
	String overrideReportDate;
	EnsemblRequestUtils ensemblUtils;
	
	
	
	public HL7v251Factory(Report report, OrderCase caseSummary, RequestUtils requestUtils, File pdfFile, 
			EnsemblProperties ensemblProps, OtherProperties otherProps,
			String overridePatientName, String overrideMRN, String overrideDOB, 
			String overrideGender, String overrideOrder, 
			String overrideProviderIdName,
			String beakerId, String overrideTestName,
			String overrideReportDate, 
			ModelDAO modelDAO) {
		super();
		this.report = report;
		this.caseSummary = caseSummary;
		this.requestUtils = requestUtils;
		this.pdfFile = pdfFile;
		this.ensemblProps = ensemblProps;
		this.otherProps = otherProps;
		
		this.overridePatientName = overridePatientName;
		this.overrideMRN = overrideMRN;
		this.overrideDOB = overrideDOB;
		this.overrideGender = overrideGender;
		this.overrideOrder = overrideOrder;
		this.overrideProviderIdName = overrideProviderIdName;
		
		this.beakerId = beakerId;
		this.overrideTestName = overrideTestName;
		this.overrideReportDate = overrideReportDate;
		this.ensemblUtils = new EnsemblRequestUtils(ensemblProps, otherProps);
		this.modelDAO = modelDAO;
	}
	
	public String reportToHL7(boolean humanReadable) throws HL7Exception, IOException, URISyntaxException {
		this.humanReadable = humanReadable;
		ORU_R01 oru = new ORU_R01();
		oru.initQuickstart("ORU", "R01", "T");
		
		generateData(oru);
		
		// Now, let's encode the message and look at the output
		HapiContext context = new DefaultHapiContext();
		Parser parser = context.getGenericParser();
		String encodedMessage = parser.encode(oru);
		context.close();
		
		//skip wrapper and pdf encoding if human readable
		if (!humanReadable) {
			//add MLP wrapper
			encodedMessage = new String(new byte[] {0x0b}) + encodedMessage + new String(new byte[] {0x1c, 0x0d});
			
		}
		else {
			encodedMessage = encodedMessage.replaceAll("OBX.+999999", "");
		}
		pdfFile.delete();
		return encodedMessage;
	}

	private void generateData(ORU_R01 oru) throws DataTypeException, ClientProtocolException, IOException, URISyntaxException {
		// Populate the MSH Segment
		MSH mshSegment = oru.getMSH();
		mshSegment.getSendingApplication().getNamespaceID().setValue(UTSWProps.SENDING_APPLICATION);
		mshSegment.getSendingFacility().getNamespaceID().setValue(UTSWProps.SENDING_FACILITY);
		mshSegment.getReceivingApplication().getNamespaceID().setValue(UTSWProps.RECEIVING_APPLICATION);
		mshSegment.getReceivingFacility().getNamespaceID().setValue(UTSWProps.RECEIVING_FACILITY);
		mshSegment.getDateTimeOfMessage().getTime().setValue(TypeUtils.hl7DateTimeFormatter.format(LocalDateTime.now()));
		if (this.overridePatientName != null) {
			caseSummary.setPatientName(this.overridePatientName);
		}
		if (this.overrideMRN != null) {
			caseSummary.setMedicalRecordNumber(overrideMRN);
		}
		if (this.overrideDOB != null) {
			caseSummary.setDateOfBirth(overrideDOB);
		}
		if (this.overrideGender != null) {
			caseSummary.setGender(overrideGender);
		}
		if (this.overrideOrder != null) {
			caseSummary.setHl7OrderId(overrideOrder);
		}
		if (this.overrideProviderIdName != null) {
			caseSummary.setOrderingPhysician(overrideProviderIdName);
		}
		if (this.overrideTestName != null) {
			caseSummary.setLabTestName(overrideTestName);
			caseSummary.setTumorPanel(overrideTestName);
		}
		
		if (caseSummary.getTumorPanel() == null || caseSummary.getTumorPanel().equalsIgnoreCase("Solid")) {
			caseSummary.setLabTestName(UTSWProps.PAN_CANCER_NAME);
		}
		else if (caseSummary.getTumorPanel() != null && caseSummary.getTumorPanel().equalsIgnoreCase("Hematolymphoid")) {
			caseSummary.setLabTestName(UTSWProps.HEME_NAME);
		}
		if (this.beakerId != null) {
			caseSummary.setHl7SampleId(beakerId);
		}
		
		// Populate the PID Segment
		PID pid = oru.getPATIENT_RESULT().getPATIENT().getPID();
		pid.getSetIDPID().setValue("1");
		String[] patientName = caseSummary.getPatientName().split(",");
		String[] firstmiddleName = patientName[1].split(" ");
		pid.getPatientName(0).getFamilyName().getSurname().setValue(this.sanitizeHL7Text(patientName[0]));
		pid.getPatientName(0).getGivenName().setValue(this.sanitizeHL7Text(firstmiddleName[0]));
		if (firstmiddleName.length > 1) {
			pid.getPatientName(0).getSecondAndFurtherGivenNamesOrInitialsThereof().setValue(this.sanitizeHL7Text(firstmiddleName[1]));
		}
		pid.getPatientIdentifierList(0).getIDNumber().setValue(this.sanitizeHL7Text(caseSummary.getMedicalRecordNumber()));
		pid.getDateTimeOfBirth().getTime().setValue(this.sanitizeHL7Text(caseSummary.getDateOfBirth().replace("-", "")));
		String gender = caseSummary.getGender();
		String genderCode = "U";
		if ("Male".equals(gender)) {
			genderCode = "M";
		}
		else if ("Female".equals(gender)) {
			genderCode = "F";
		}
		
		
		pid.getAdministrativeSex().setValue(genderCode);
		
		OBR obr = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
		obr.getObservationDateTime().getTime().setValue(this.sanitizeHL7Text(caseSummary.getEpicOrderDate().replace("-", "")));
		obr.getSetIDOBR().setValue("1");
		obr.getPlacerOrderNumber().getEntityIdentifier().setValue(caseSummary.getHl7OrderId());
		if (caseSummary.getLabTestName().equals(UTSWProps.HEME_NAME)) {
			obr.getUniversalServiceIdentifier().getIdentifier().setValue("170170");
		}
		else if (caseSummary.getLabTestName().equals(UTSWProps.PAN_CANCER_NAME)) {
			obr.getUniversalServiceIdentifier().getIdentifier().setValue("170169");
		}
		obr.getUniversalServiceIdentifier().getText().setValue(caseSummary.getLabTestName());
		
		if (this.notNullOrEmpty(caseSummary.getHl7SampleId())) {
			obr.getFillerOrderNumber().getEntityIdentifier().setValue(caseSummary.getHl7SampleId());
			obr.getFillerOrderNumber().getNamespaceID().setValue("Beaker");
		}
		
		String[] physicianNameAndId = caseSummary.getOrderingPhysician().split("[, ]");
		if (physicianNameAndId[0] != null) {
			obr.getOrderingProvider(0).getIDNumber().setValue(this.sanitizeHL7Text(physicianNameAndId[0]));
		}
		if (physicianNameAndId.length > 1 && physicianNameAndId[1] != null) {
		obr.getOrderingProvider(0).getFamilyName().getSurname().setValue(this.sanitizeHL7Text(physicianNameAndId[1]));
		}
		if (physicianNameAndId.length > 2 && physicianNameAndId[2] != null) {
			obr.getOrderingProvider(0).getGivenName().setValue(this.sanitizeHL7Text(physicianNameAndId[2]));
		}
		if (physicianNameAndId.length > 3 && physicianNameAndId[3] != null) {
			obr.getOrderingProvider(0).getSecondAndFurtherGivenNamesOrInitialsThereof().setValue(this.sanitizeHL7Text(physicianNameAndId[3]));
		}
		
		if (this.overrideReportDate != null) {
			report.setDateFinalized(overrideReportDate);
			obr.getResultsRptStatusChngDateTime().getTime().setValue(overrideReportDate);
		}
		else if (report.getDateFinalized() != null) {
			LocalDateTime finalizedData = LocalDateTime.parse(report.getDateFinalized(), TypeUtils.mongoDateTimeFormatter);
			ZonedDateTime zonedDateTime = ZonedDateTime.of(finalizedData, ZoneId.of("UTC"));
			zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
			obr.getResultsRptStatusChngDateTime().getTime().setValue(zonedDateTime.format(TypeUtils.hl7DateTimeFormatter));
		}
		obr.getResultStatus().setValue("F"); //always final
		
		//Diagnosis
////		DG1 diagnosis = orm.getORDER() getDG1();
//		DG1 diagnosis = orm.getORDER().getORDER_DETAIL().getDG1();
//		diagnosis.getSetIDDG1().setValue("1");
//		diagnosis.getDiagnosisCodingMethod().setValue(UTSWProps.DIAGNOSIS_CODING_METHOD);
//		diagnosis.getDiagnosisCodeDG1().getText().setValue(caseSummary.getIcd10());
		
		List<HL7Therapy> therapies = new ArrayList<HL7Therapy>();
		List<HL7Variant> variants = new ArrayList<HL7Variant>();
		Map<String, HL7Variant> variantsByOid = new HashMap<String, HL7Variant>();
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsStrongClinicalSignificance().values()) {
			List<HL7Variant> vList = this.buildVariant("Tier 1", "Pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsPossibleClinicalSignificance().values()) {
			List<HL7Variant> vList = this.buildVariant("Tier 2", "Likely pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsUnknownClinicalSignificance().values()) {
			List<HL7Variant> vList = this.buildVariant("Tier 3","Uncertain significance", gva, true);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		for (CNVReport cnv : report.getCnvs()) {
			if (cnv != null && cnv.getMongoDBId() != null) {
				List<HL7Variant> vList = this.buildCNV(cnv, false);
				variantsByOid.put(cnv.getMongoDBId().getOid(), vList.get(0));
				variants.addAll(vList);
			}
		}
		for (TranslocationReport ftl : report.getTranslocations()) {
			if (ftl != null && ftl.getMongoDBId() != null) {
				List<HL7Variant> vList = this.buildFTL(ftl, false);
				variantsByOid.put(ftl.getMongoDBId().getOid(), vList.get(0));
				variants.addAll(vList);
			}
		}
		
		for (HL7Variant tv : variants) {
			addVariant(oru, tv);
		}
		
		boolean doInclude = false;
		if (doInclude) {
			for (IndicatedTherapy therapy :report.getIndicatedTherapies()) {
				HL7Therapy tt = new HL7Therapy();
				HL7Variant v = variantsByOid.get(therapy.getOid());
				if (v != null) {
//				if (v.getGene().equals("PDE4DIP")) {
//					therapy.setDrugs("Olaparib, niraparib,rucaparib");
//				}
					tt.setVariant(v);
					tt.setDrug(therapy.getDrugs());
					tt.setLevel(therapy.getLevel());
					tt.setIndication(therapy.getIndication());
				}
				therapies.add(tt);
				
			}
			
			//Pubmed
			if (report.getPubmeds() != null) {
				for (PubMed pubmedId : report.getPubmeds()) {
					CWE obxPubmed = new CWE(oru.getMessage());
					obxPubmed.getIdentifier().setValue(this.sanitizeHL7Text(pubmedId.getPmid()));
					obxPubmed.getText().setValue(this.sanitizeHL7Text(pubmedId.getTitle()));
					obxPubmed.getNameOfCodingSystem().setValue("Pubmed");
					createCaseOBX(oru, LOINC.getCode("Citation [Bibliographic Citation] in Reference lab test Narrative"), obxPubmed, null);
				}
			}
			
			//Therapy
			addTherapySegments(oru, therapies);
			
			//Trials
			List<TempusTrial> trials = new ArrayList<TempusTrial>();
			for (BiomarkerTrialsRow biomarkerTrial : report.getClinicalTrials()) {
				TempusTrial trial = new TempusTrial();
				trial.setNctId(biomarkerTrial.getNctid());
				trial.setBiomarkers(Arrays.asList(biomarkerTrial.getBiomarker()));
				trial.setTitle(biomarkerTrial.getTitle());
				trials.add(trial);
			}
			addClinicalTrialSegments(oru, trials);
			
		}
		
		//case level entries
		if (variants != null && !variants.isEmpty()) {
			createCaseOBXFromUTSWProp(oru, UTSWProps.GRCh38, LOINC.getCode("Human reference sequence assembly version"));
		}
		
		if (report.getTumorPanel() != null) {
			ST obxGenePanel = new ST(oru.getMessage());
			obxGenePanel.setValue(this.sanitizeHL7Text(report.getTumorPanel()));
			createCaseOBX(oru, LOINC.getCode("Description of ranges of DNA sequences examined"), obxGenePanel, null);
		}
		
		if (caseSummary.getTumorMutationBurden() != null) {
			NM obxTMB = new NM(oru.getMessage());
			obxTMB.setValue(String.format("%.2f", caseSummary.getTumorMutationBurden()));
			OBX tmbSegment = createCaseOBX(oru, LOINC.getCode("Gene mutations tested for [#] in Blood or Tissue by Molecular genetics method"), obxTMB, null);
			tmbSegment.getUnits().getIdentifier().setValue("m/MB");
		}
		
		CWE obxMSI = new CWE(oru.getMessage());
		LOINCItem msiLoinc = LOINC.getCode("Microsatellite instability [Interpretation] in Cancer specimen Qualitative");
		String[] msiCat = LOINCMSI.getLoincCode("Indeterminate");
		if (caseSummary.getMsiClass() != null && caseSummary.getMsiClass().equals("MSS")) {
			msiCat = LOINCMSI.getLoincCode("Stable");
		}
		else if (caseSummary.getMsiClass() != null && caseSummary.getMsiClass().equals("MSI")) {
			msiCat = LOINCMSI.getLoincCode("MSI-H");
		}
		obxMSI.getIdentifier().setValue(this.sanitizeHL7Text(msiCat[1]));
		obxMSI.getText().setValue(this.sanitizeHL7Text(msiCat[0]));
		obxMSI.getNameOfCodingSystem().setValue(msiLoinc.getSystem());
		createCaseOBX(oru, msiLoinc, obxMSI, null);
	
		//Report Summary
//		createCaseNTE(oru);
		ST obxSummary = new ST(oru.getMessage());
		obxSummary.setValue(this.sanitizeHL7Text(report.getSummary()));
		LOINCItem summaryLOINC = LOINC.getCode("Case Summary");
		createCaseOBX(oru, summaryLOINC, obxSummary, null);
		
		
//		if (humanReadable) {
			//Base64 PDF
		ED obxPDF = new ED(oru.getMessage());
		obxPDF.getDataSubtype().setValue("PDF");
		obxPDF.getEncoding().setValue("Base64");
		byte[] bytes = FileUtils.readFileToByteArray(this.pdfFile);
		obxPDF.getData().setValue(Base64.getEncoder().encodeToString(bytes));
		LOINCItem reportLOINC = new LOINCItem(UTSWProps.PDF_IDENTIFIER, UTSWProps.PDF_TEXT);
		createCaseOBX(oru, reportLOINC, obxPDF);
//		}
		
	}
	
	public List<HL7Variant> buildVariant(String clinicalSignificanceSomatic, String clinicalSignificanceGermline, GeneVariantAndAnnotation gva, boolean skipAnnotation) throws ClientProtocolException, IOException, URISyntaxException {
		List<HL7Variant> vList = new ArrayList<HL7Variant>();
		if (gva.getType().contentEquals("snp")) {
			HL7Variant v = new HL7Variant();
			v.setGene(gva.getGene());
			v.setHgncCode("0");
//			v.setAaChange(gva.getVariant());
			Variant variant = requestUtils.getVariantDetails(gva.getOid());
			v.setRef(variant.getReference());
			v.setAlt(variant.getAlt());
			v.setChr(variant.getChrom());
			v.setStartEnd2020v(variant.getChrom() + ":" + variant.getPos());
			v.setStart2018v(variant.getPos());
			v.setEnd2018v(variant.getPos() + v.getRef().length() - v.getAlt().length());
//			if (variant.getSomaticStatus() != null && variant.getSomaticStatus().equals("Germline")) {
//				v.setClinicalSignificance(LOINC.getCode(clinicalSignificanceGermline));
//			}
//			else {
//				v.setClinicalSignificance(LOINC.getCode(clinicalSignificanceSomatic));
//			}
			//Germline and Unknown are treated the same way
			if (variant.getSomaticStatus() != null && variant.getSomaticStatus().equals("Somatic")) {
				v.setClinicalSignificance(LOINC.getCode(clinicalSignificanceSomatic));
			}
			else {
				v.setClinicalSignificance(LOINC.getCode(clinicalSignificanceGermline));
			}
			String featureId = "";
			if (variant.getVcfAnnotations() != null && !variant.getVcfAnnotations().isEmpty()) {
				featureId = variant.getVcfAnnotations().get(0).getFeatureId();
				if (featureId.contains("ENST")) {
					featureId = "ENST" + featureId.split("ENST")[1];
				}
			}
			v.setTranscript(featureId);
			v.setAllFreq(variant.getTumorAltFrequency());
			v.setDepth(variant.getTumorTotalDepth());
			VCFAnnotation workingAnn = variant.getVcfAnnotations().get(0);
			for (VCFAnnotation vcfAnn : variant.getVcfAnnotations()) {
				String gene = vcfAnn.getGeneName();
				String hgnc = fetchHGNC(gene, false);
				if (!this.notNullOrEmpty(hgnc)) {
					hgnc = fetchHGNC(gene, true);
				}
				if (this.notNullOrEmpty(hgnc)) {
					v.setGene(gene);
					v.setHgncCode(hgnc);
					workingAnn = vcfAnn;
					break;
				}
			}
			v.setEnsemblCode(workingAnn.getGeneId());
//			v.setDisplayName(variant.getGeneName() + " " + variant.getNotation());
			v.setDisplayName(gva.getGeneVariant());
			v.setSomaticStatus(variant.getSomaticStatus());
//			String dbSNPId = null;
//			if (variant.getIds() != null) {
//				for (String id : variant.getIds()) {
//					if (id != null && id.startsWith("rs")) {
//						dbSNPId = id;
//						break;
//					}
//				}
//			}
//			v.setDbSNPId(dbSNPId);
			String cNotation = workingAnn.getCodingNotation();
			if (cNotation != null) {
				v.setcNotation(cNotation);
			}
			String pNotation = workingAnn.getProteinNotation();
			if (this.notNullOrEmpty(pNotation)) {
				v.setpNotation(pNotation);
			}
			//DNA Change Type
			if (v.getRef().length() != v.getAlt().length()) {
				v.setDnaChangeType("Insertion/Deletion");
			}
			else if (v.getRef().length() == v.getAlt().length() && v.getRef().length() == 1) {
				v.setDnaChangeType("Substitution");
			}
			//AA Change Type
			//TODO only pick one.
			List<String> loincEffects = new ArrayList<String>();
			for (String effect : variant.getEffects()) {
				String loincKey = LOINCAAChangeType.getLoincCodeKeyFromEffect(effect);
				if (loincKey != null) {
					loincEffects.add(loincKey);
				}
			}
			if (!loincEffects.isEmpty()) {
				String mainEffect = LOINCAAChangeType.selectMainEffect(loincEffects);
				if (mainEffect != null) {
					v.setAaMainChangeType(mainEffect);
				}
			}
			//DNA Region
			if (notNullOrEmpty(variant.getRank())) {
				if (variant.getRank().contains("/")) {
					String exonString = "exon" + variant.getRank().split("/")[0];
					v.setDnaRegion(exonString);
				}
				else {
					v.setDnaRegion(variant.getRank());
				}
			}
			v.setVariantCategory(LOINCVariantCategory.getLoincCode(gva.getType()));
			
			//skip for now
//			for (String id : variant.getIds()) {
//				if (!this.notNullOrEmpty(id)) {
//					continue;
//				}
//				if (id.startsWith("COSM")) {
//					v.setCosmicMVariantId(id);
//					break;
//				}
//				else if (id.startsWith("COSV")) {
////					v.setCosmicVVariantId(id);
//				}
//				else if (id.startsWith("rs")) {
//					v.setDbSNPVariantId(id);
//					break;
//				}
//				else {
//					v.setClinvarVariantId(id);
//					break;
//				}
//			}
			for (String id : variant.getIds()) {
				if (!this.notNullOrEmpty(id) || id.startsWith("COSM")
						|| id.startsWith("COSV") || id.startsWith("rs")) {
					continue;
				}
				else {
					v.setClinvarVariantId(id);
					break;
				}
			}
			
			if (!skipAnnotation) {
				v.setAnnotations(this.concatInterpretation(gva));
			}
			vList.add(v);
		}
		else if (gva.getType().contentEquals("cnv")) {

			String[] genes = gva.getGene().split(" ");
			for (String gene : genes) {
				gene = gene.trim();
				HL7Variant v = new HL7Variant();
				v.setHgncCode("0");
				if (gene.equals("FLT3-ITD")) {
					gene = "FLT3";
				}
				v.setGene(gene);
				v.setHgncCode("0");
				String hgnc = fetchHGNC(gene, false);
				if (!this.notNullOrEmpty(hgnc)) {
					hgnc = fetchHGNC(gene, true);
				}
				if (this.notNullOrEmpty(hgnc)) {
					v.setHgncCode(hgnc);
				}
				
//			v.setAaChange(gva.getVariant());
				CNV variant = requestUtils.getCNVDetails(gva.getOid());
				v.setChr(variant.getChrom());
				v.setCytoband(variant.getCytoband());
				//TODO maybe use CNV type
//				gva.getAberrationType()
				v.setSomaticStatus("Somatic");
//			v.setDisplayName(variant.getChrom() + " " + variant.getGenes().stream().collect(Collectors.joining(" ")));
				if (variant.getAberrationType() != null && variant.getAberrationType().equals("ITD")) {
					v.setDnaChangeType("Duplication");
					v.setDisplayName(gene + " " + v.getDnaChangeType());
				}
				if (variant.getAberrationType() != null) {
					v.setDisplayName(gene + " " + variant.getAberrationType());
				}
				v.setCopyNumber(variant.getCopyNumber());
				v.setStructuralVariantLength(Math.abs(variant.getEnd() - variant.getStart()));
				v.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(variant.getEnd(),  variant.getStart()), Math.max(variant.getEnd(),  variant.getStart())});
				
				v.setClinicalSignificance(LOINC.getCode(clinicalSignificanceSomatic));
				
				v.setVariantCategory(LOINCVariantCategory.getLoincCode(gva.getType()));
				if (!skipAnnotation) {
					v.setAnnotations(this.concatInterpretation(gva));
				}
				
				vList.add(v);
			}
			
		}
		else if (gva.getType().contentEquals("translocation")) {
			Translocation ftl = requestUtils.getTranslocationDetails(gva.getOid());
			HL7Variant v12 = new HL7Variant();
			v12.setLeftGene(ftl.getLeftGene());
			v12.setRightGene(ftl.getRightGene());
			v12.setLeftDNARegion(ftl.getFirstExon());
			v12.setRightDNARegion(ftl.getLastExon());
			
			String hgncLeft = fetchHGNC(ftl.getLeftGene(), false);
			if (!this.notNullOrEmpty(hgncLeft)) {
				hgncLeft = fetchHGNC(ftl.getLeftGene(), true);
			}
			v12.setLeftHGNC(hgncLeft);
			
			String hgncRight = fetchHGNC(ftl.getRightGene(), false);
			if (!this.notNullOrEmpty(hgncRight)) {
				hgncRight = fetchHGNC(ftl.getRightGene(), true);
			}
			v12.setRightHGNC(hgncRight);
			
			v12.setDisplayName(gva.getGeneVariant());
			v12.setSomaticStatus("Somatic");
			v12.setClinicalSignificance(LOINC.getCode(clinicalSignificanceSomatic));
//			v12.setDisplayName(ftl.getFusionName());
			
			String chrLeft = ftl.getLeftBreakpoint().split(":")[0];
			String chrRight = ftl.getRightBreakpoint().split(":")[0];
			Integer left = Integer.parseInt(ftl.getLeftBreakpoint().split(":")[1]);
			Integer right = Integer.parseInt(ftl.getRightBreakpoint().split(":")[1]);
			v12.setLeftDNARegion(chrLeft + "-" + chrRight);

			if (ftl.getChrType() != null && ftl.getChrType().equals("INTRACHROMOSOMAL")) {
				v12.setStructuralVariantLength(Math.abs(left - right));
				v12.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(left,  right), Math.max(left,  right)});
			}
			
//			if (ftl.getChrType() != null && ftl.getChrType().equals("INTERCHROMOSOMAL")) {
//				Integer left = Integer.parseInt(ftl.getLeftBreakpoint().split(":")[1]);
//				Integer right = Integer.parseInt(ftl.getRightBreakpoint().split(":")[1]);
//				v12.setStructuralVariantLength(Math.abs(left - right));
//				v12.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(left,  right), Math.max(left,  right)});
//				
//				v12.setLeftDNARegion(ftl.getChrDistance());
//			}
//			else if (ftl.getChrType() != null && ftl.getChrType().equals("INTRACHROMOSOMAL")) {
//				String chr = ftl.getLeftBreakpoint().split(":")[0];
//				String left = ftl.getLeftBreakpoint().split(":")[1];
//				String right = ftl.getRightBreakpoint().split(":")[1];
//				v12.setLeftDNARegion(chr + ":" + left + "-" + right);
//			}
			
//			v1.setDnaRegion(variant.getFirstExon());
//			v2.setDnaRegion(variant.getLastExon());
//			vList.add(v1);
//			vList.add(v2);
			vList.add(v12);
			v12.setVariantCategory(LOINCVariantCategory.getLoincCode("translocation"));
//			v12.setStructuralVariantType(LOINCStructuralVariantType.getLoincCode("Translocation"));
			
			v12.setFusedGenes(ftl.getLeftGene() + "~" + ftl.getRightGene());

			if (!skipAnnotation) {
				v12.setAnnotations(this.concatInterpretation(gva));
			}
			
		}
		return vList;
	}
	
	public List<HL7Variant> buildCNV(CNVReport cnv, boolean skipAnnotation) throws ClientProtocolException, IOException, URISyntaxException {
		List<HL7Variant> vList = new ArrayList<HL7Variant>();
		HL7Variant v = new HL7Variant();
		if (this.notNullOrEmpty(cnv.getGenes())) { //only the first one
			String gene = cnv.getGenes().split(" ")[0].trim();
			if (gene.equals("FLT3-ITD")) {
				gene = "FLT3";
			}
			v.setGene(gene);
			String hgnc = fetchHGNC(gene, false);
			if (!this.notNullOrEmpty(hgnc)) {
				hgnc = fetchHGNC(gene, true);
			}
			v.setHgncCode(hgnc);
		}
//			v.setAaChange(gva.getVariant());
//		CNV variant = requestUtils.getCNVDetails(gva.getOid());
		v.setChr(cnv.getChrom());
		v.setCytoband(cnv.getCytoband());
		
		if (cnv.getAberrationType() != null && cnv.getAberrationType().equals("ITD")) {
			
			v.setDnaChangeType("Duplication");
		}
		v.setCopyNumber(cnv.getCopyNumber());
		v.setStructuralVariantLength(Math.abs(cnv.getEnd() - cnv.getStart()));
		v.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(cnv.getEnd(),  cnv.getStart()), Math.max(cnv.getEnd(),  cnv.getStart())});
		v.setDisplayName(cnv.getChrom() + " " + cnv.getCytoband() + " " + cnv.getAberrationType());
		v.setVariantCategory(LOINCVariantCategory.getLoincCode("cnv"));
		if (!skipAnnotation) {
			v.setAnnotations(Arrays.asList(cnv.getComment()));
		}
		v.setSomaticStatus("Somatic");
		if (cnv.getHighestAnnotationTier() != null) {
			if (cnv.getHighestAnnotationTier().equals("1A") || cnv.getHighestAnnotationTier().equals("1B")) {
				v.setClinicalSignificance(LOINC.getCode("Tier 1"));
			}
			else if (cnv.getHighestAnnotationTier().equals("2C") || cnv.getHighestAnnotationTier().equals("2D")) {
				v.setClinicalSignificance(LOINC.getCode("Tier 2"));
			}
			else {
				v.setClinicalSignificance(LOINC.getCode("Tier 3"));
			}
		}
		vList.add(v);
		return vList;
	}
	
	public List<HL7Variant> buildFTL(TranslocationReport ftl, boolean skipAnnotation) throws ClientProtocolException, URISyntaxException, IOException {
		List<HL7Variant> vList = new ArrayList<HL7Variant>();
//		HL7Variant v1 = new HL7Variant();
//		HL7Variant v2 = new HL7Variant();
		HL7Variant v12 = new HL7Variant();

//		v1.setGene(ftl.getLeftGene());
//		v1.setHgncCode(fetchHGNC(ftl.getLeftGene()));
//		v2.setGene(ftl.getRightGene());
//		v2.setHgncCode(fetchHGNC(ftl.getRightGene()));
		Translocation variant = requestUtils.getTranslocationDetails(ftl.getMongoDBId().getOid()); //possibly no mongo id for old cases?
//		v1.setGene(variant.getLeftGene());
		
		v12.setLeftGene(ftl.getLeftGene());
		v12.setRightGene(ftl.getRightGene());
		v12.setLeftDNARegion(ftl.getFirstExon());
		v12.setRightDNARegion(ftl.getLastExon());

		String hgncLeft = fetchHGNC(ftl.getLeftGene(), false);
		if (!this.notNullOrEmpty(hgncLeft)) {
			hgncLeft = fetchHGNC(ftl.getLeftGene(), true);
		}
		v12.setLeftHGNC(hgncLeft);
		
		String hgncRight = fetchHGNC(ftl.getRightGene(), false);
		if (!this.notNullOrEmpty(hgncRight)) {
			hgncRight = fetchHGNC(ftl.getRightGene(), true);
		}
		v12.setRightHGNC(hgncRight);
		
		v12.setDisplayName(ftl.getFusionName());
		v12.setSomaticStatus("Somatic");
		
		if (ftl.getHighestAnnotationTier() == null) {
			fixMissingHighestAnnotationTier(ftl, variant);
		}
		
//		System.out.println("FTL from HL7: " + ftl.getFusionName() + " tier: " + ftl.getHighestAnnotationTier() + ".");
		if (ftl.getHighestAnnotationTier() != null) {
			if (ftl.getHighestAnnotationTier().equals("1A") || ftl.getHighestAnnotationTier().equals("1B")) {
				v12.setClinicalSignificance(LOINC.getCode("Tier 1"));
			}
			else if (ftl.getHighestAnnotationTier().equals("2C") || ftl.getHighestAnnotationTier().equals("2D")) {
				v12.setClinicalSignificance(LOINC.getCode("Tier 2"));
			}
			else {
				v12.setClinicalSignificance(LOINC.getCode("Tier 3"));
			}
		}
		else {
			v12.setClinicalSignificance(LOINC.getCode("Tier 3"));
		}
		v12.setFusedGenes(ftl.getLeftGene() + "~" + ftl.getRightGene());
		
		String chrLeft = null;
		Integer left = null;
		String chrRight = null;
		Integer right = null;
		
		if (notNullOrEmpty(variant.getLeftBreakpoint())) {
			chrLeft = variant.getLeftBreakpoint().split(":")[0];
			left = Integer.parseInt(variant.getLeftBreakpoint().split(":")[1]);
		}
		if (notNullOrEmpty(variant.getRightBreakpoint())) {
			chrRight = variant.getRightBreakpoint().split(":")[0];
			right = Integer.parseInt(variant.getRightBreakpoint().split(":")[1]);
		}
		if (notNullOrEmpty(chrLeft) && notNullOrEmpty(chrRight))
		v12.setLeftDNARegion(chrLeft + "-" + chrRight);
		
		if (variant.getChrType() != null && variant.getChrType().equals("INTRACHROMOSOMAL")
				&& left != null && right != null) {
			v12.setStructuralVariantLength(Math.abs(left - right));
			v12.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(left,  right), Math.max(left,  right)});
		}
//		
//		if (variant.getChrType() != null && variant.getChrType().equals("INTERCHROMOSOMAL")) {
//			Integer left = Integer.parseInt(variant.getLeftBreakpoint().split(":")[1]);
//			Integer right = Integer.parseInt(variant.getRightBreakpoint().split(":")[1]);
//			
//			v12.setStructuralVariantLength(Math.abs(left - right));
//			v12.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(left,  right), Math.max(left,  right)});
////			v12.setLeftDNARegion(variant.getChrDistance());
//			v12.setLeftDNARegion(variant.get);
//		}
//		else if (variant.getChrType() != null && variant.getChrType().equals("INTRACHROMOSOMAL")) {
//			String chr = variant.getLeftBreakpoint().split(":")[0];
//			String left = variant.getLeftBreakpoint().split(":")[1];
//			String right = variant.getRightBreakpoint().split(":")[1];
//			v12.setLeftDNARegion(chr + ":" + left + "-" + right);
//		}
//		v1.setDnaRegion(variant.getFirstExon());
//		v2.setDnaRegion(variant.getLastExon());
//		vList.add(v1);
//		vList.add(v2);
		vList.add(v12);
		v12.setVariantCategory(LOINCVariantCategory.getLoincCode("translocation"));
//		v12.setStructuralVariantType(LOINCStructuralVariantType.getLoincCode("Translocation"));
		if (!skipAnnotation) {
			v12.setAnnotations(Arrays.asList(ftl.getComment()));
		}
		return vList;
	}
	
	public void fixMissingHighestAnnotationTier(TranslocationReport ftl, Translocation variant) {
		System.out.println("FTL from fixing: " + ftl.getFusionName() + " tier: " + ftl.getHighestAnnotationTier() + ".");
		 //try to recalculate the highestTier 
		variant.getReferenceTranslocation().getUtswAnnotations().stream().forEach(a -> Annotation.init(a, variant.getAnnotationIdsForReporting(), modelDAO));
		List<Annotation> selectedAnnotations = variant.getReferenceTranslocation().getUtswAnnotations().stream().filter(a -> a.getIsSelected()).collect(Collectors.toList());
		selectedAnnotations.stream().filter(a -> a.getClassification() != null || a.getCategory() != null).forEach(a -> ReportBuilder.overrideTier(a));
		//set Uncategorized if needed
		selectedAnnotations.stream().forEach(a -> a.setCategory(a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory()));
		String highestTier = selectedAnnotations.stream().filter(a -> a.getTier() != null).map(a -> a.getTier()).sorted().collect(Collectors.toList()).get(0);
		ftl.setHighestAnnotationTier(highestTier);
		System.out.println("FTL after fixing: " + ftl.getFusionName() + " tier: " + highestTier + ".");
	}

	public void addTherapySegments(ORU_R01 oru, List<HL7Therapy> therapies) throws DataTypeException {
		int therapyCount = 0;
		int counter = 1;
		//each drug gets its own therapy entry (duplication of gene variant, level, indication)
		for (HL7Therapy t : therapies) {
			//Agent/Drugs
			if (this.notNullOrEmpty(t.getDrug())) {
				for (String drug : t.getDrug().split(",")) {
					therapyCount++;
					drug = drug.trim();
					ST obxDrug = new ST(oru.getMessage());
					obxDrug.setValue(this.sanitizeHL7Text(drug));
					createCaseOBX(oru, LOINC.getCode("THERAPYAGENT"), obxDrug, counter);
					buildDupTherapy(oru, counter, t);
					counter++;
				}
			}
			else { //in case the drugs are empty
				therapyCount++;
				buildDupTherapy(oru, counter, t);
				counter++;
			}
		}
		NM obxTherapyCount = new NM(oru.getMessage());
		obxTherapyCount.setValue(therapyCount + "");
		createCaseOBX(oru, LOINC.getCode("Therapy count"), obxTherapyCount, null);
	}

	private void buildDupTherapy(ORU_R01 oru, int counter, HL7Therapy t) throws DataTypeException {
		//Gene
		if (this.notNullOrEmpty(t.getVariant().getHgncCode())) {
			Type obxGene = null;
			if (t.getVariant().getHgncCode().equals("0")) {
				obxGene = createSTGene(oru, t.getVariant().getHgncCode(), t.getVariant().getGene());
			}
			else {
				obxGene = createCWEGene(oru, t.getVariant().getHgncCode(), t.getVariant().getGene());
			}
			createCaseOBX(oru, LOINC.getCode("THERAPYGENE"), obxGene, counter);
		}
		//Variant
		if (this.notNullOrEmpty(t.getVariant().getTranscript())) {
			CWE obxTranscript = new CWE(oru.getMessage());
			obxTranscript.getIdentifier().setValue(this.sanitizeHL7Text(t.getVariant().getTranscript()));
			obxTranscript.getText().setValue(this.sanitizeHL7Text(t.getVariant().getTranscript()));
			obxTranscript.getNameOfCodingSystem().setValue("Ensembl-T");
			createCaseOBX(oru, LOINC.getCode("THERAPYVARIANT"), obxTranscript, counter);
			
		}
		//Level
		if (this.notNullOrEmpty(t.getLevel())) {
			ST obxLevel = new ST(oru.getMessage());
			obxLevel.setValue(this.sanitizeHL7Text(t.getLevel()));
			createCaseOBX(oru, LOINC.getCode("THERAPYLEVEL"), obxLevel, counter);
		}
		//Indication
		if (this.notNullOrEmpty(t.getIndication())) {
			ST obxIndication = new ST(oru.getMessage());
			obxIndication.setValue(this.sanitizeHL7Text(t.getIndication()));
			createCaseOBX(oru, LOINC.getCode("THERAPYINDICATION"), obxIndication, counter);
		}
	}
	
	
	
	public void addClinicalTrialSegments(ORU_R01 oru, List<TempusTrial> trials) throws DataTypeException {
		NM obxTherapyCount = new NM(oru.getMessage());
		obxTherapyCount.setValue(trials.size() + "");
		createCaseOBX(oru, LOINC.getCode("Trial count"), obxTherapyCount, null);
		int counter = 1;
		for (TempusTrial t : trials) {
			//NCTID
			ST obxNCTID = new ST(oru.getMessage());
			obxNCTID.setValue(this.sanitizeHL7Text(t.getNctId()));
			createCaseOBX(oru, LOINC.getCode("TRIALNCTID"), obxNCTID, counter);
			//Title
			ST obxTitle = new ST(oru.getMessage());
			obxTitle.setValue(this.sanitizeHL7Text(t.getTitle()));
			createCaseOBX(oru, LOINC.getCode("TRIALTITLE"), obxTitle, counter);
			//Biomarkers
			//Here I'm using multiple entries while Tempus uses a comma-separated string
			//the ST type is too broad. Let's review this
			for (String biomarker : t.getBiomarkers()) {
				ST obxBiomarker = new ST(oru.getMessage());
				obxBiomarker.setValue(this.sanitizeHL7Text(biomarker));
				createCaseOBX(oru, LOINC.getCode("TRIALMATCHES"), obxBiomarker, counter);
			}
			counter++;
		}
	}	
	
	private void addVariant(ORU_R01 oru, HL7Variant v) throws DataTypeException {
		String currentVariantId = utils.getNextVariantId();
		CWE obxSVariantAnalysisMethod = new CWE(oru.getMessage());
		LOINCItem variantAnyalysisMethodLoinc = LOINC.getCode("Variant analysis method [Type]");
		obxSVariantAnalysisMethod.getIdentifier().setValue(this.sanitizeHL7Text(UTSWProps.VARIANT_ANALYSIS_METHOD_SEQUENCING[2]));
		obxSVariantAnalysisMethod.getText().setValue(this.sanitizeHL7Text(UTSWProps.VARIANT_ANALYSIS_METHOD_SEQUENCING[0]));
		obxSVariantAnalysisMethod.getNameOfCodingSystem().setValue(variantAnyalysisMethodLoinc.getSystem());
		createVariantOBX(oru, currentVariantId, variantAnyalysisMethodLoinc, obxSVariantAnalysisMethod);
		//Display Name
		if (this.notNullOrEmpty(v.getDisplayName())) {
			ST obxSbSNP = new ST(oru.getMessage());
			obxSbSNP.setValue(this.sanitizeHL7Text(v.getDisplayName()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("DNA sequence variation display name [Text] Narrative"), obxSbSNP);
		}
		
		//Fusion/Translocation
		if (v.getStructuralVariantType() != null) {
			CWE obxStructuralVariantType = new CWE(oru.getMessage());
			LOINCItem strucVariantTypeLoinc = LOINC.getCode("Structural variant [Type]");
			obxStructuralVariantType.getIdentifier().setValue(this.sanitizeHL7Text(v.getStructuralVariantType()[1]));
			obxStructuralVariantType.getText().setValue(this.sanitizeHL7Text(v.getStructuralVariantType()[0]));
			obxStructuralVariantType.getNameOfCodingSystem().setValue(strucVariantTypeLoinc.getSystem());
			createVariantOBX(oru, currentVariantId, strucVariantTypeLoinc, obxStructuralVariantType);
		}
		
		
		//DBSNP
		if (this.notNullOrEmpty(v.getDbSNPId())) {
			ST obxSbSNP = new ST(oru.getMessage());
			obxSbSNP.setValue(this.sanitizeHL7Text(v.getDbSNPId()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("dbSNP version [ID]"), obxSbSNP);
		}
		//Variant Category
		if (v.getVariantCategory() != null) {
			CWE obxVariantCategory = new CWE(oru.getMessage());
			LOINCItem variantCategoryLoinc = LOINC.getCode("Variant category");
			obxVariantCategory.getIdentifier().setValue(this.sanitizeHL7Text(v.getVariantCategory()[1]));
			obxVariantCategory.getText().setValue(this.sanitizeHL7Text(v.getVariantCategory()[0]));
			obxVariantCategory.getNameOfCodingSystem().setValue(this.sanitizeHL7Text(v.getVariantCategory()[2]));
			createVariantOBX(oru, currentVariantId, variantCategoryLoinc, obxVariantCategory);
		}
		//Gene
		if (this.notNullOrEmpty(v.getHgncCode())) {
			Type obxGeneValue = null;
			if (v.getHgncCode().equals("0")) {
				obxGeneValue = createSTGene(oru, v.getHgncCode(), v.getGene());
			}
			else {
				obxGeneValue = createCWEGene(oru, v.getHgncCode(), v.getGene());
			}
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Gene studied"), obxGeneValue);
		}
		if (this.notNullOrEmpty(v.getLeftHGNC()) || this.notNullOrEmpty(v.getLeftGene()) ) {
			CWE obxGeneValue = createCWEGene(oru, v.getLeftHGNC(), v.getLeftGene());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Gene studied"), obxGeneValue);
		}
//		if (this.notNullOrEmpty(v.getLeftHGNC()) || this.notNullOrEmpty(v.getLeftGene()) ) {
//			CWE obxGeneValue = createCWEGene(oru, v.getLeftHGNC(), v.getLeftGene());
//			createVariantOBX(oru, currentVariantId + ".a", LOINC.getCode("Gene studied"), obxGeneValue);
//		}
//		if (this.notNullOrEmpty(v.getRightHGNC()) || this.notNullOrEmpty(v.getRightGene())) {
//			CWE obxGeneValue = createCWEGene(oru, v.getRightHGNC(), v.getRightGene());
//			createVariantOBX(oru, currentVariantId + ".b", LOINC.getCode("Gene studied"), obxGeneValue);
//		}
		
		//Fused Genes
		if (this.notNullOrEmpty(v.getFusedGenes())) {
//			ST obxFusedGenes = new ST(oru.getMessage());
			ST[] obxFusedGenes = new ST[2];
			ST obxFusedGene1 = new ST(oru.getMessage());
			obxFusedGene1.setValue(this.sanitizeHL7Text(v.getLeftGene()));
			ST obxFusedGene2 = new ST(oru.getMessage());
			obxFusedGene2.setValue(this.sanitizeHL7Text(v.getRightGene()));
			obxFusedGenes[0] = obxFusedGene1;
			obxFusedGenes[1] = obxFusedGene2;
//			obxFusedGenes.setValue(this.sanitizeHL7Text(v.getFusedGenes()));
//			obxFusedGenes.setValue(v.getFusedGenes());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Fused Genes"), obxFusedGenes);
		}
		
		//Transcript
		if (this.notNullOrEmpty(v.getTranscript())) {
			CWE obxTranscript = new CWE(oru.getMessage());
			obxTranscript.getIdentifier().setValue(this.sanitizeHL7Text(v.getTranscript()));
			obxTranscript.getText().setValue(this.sanitizeHL7Text(v.getTranscript()));
			obxTranscript.getNameOfCodingSystem().setValue("Ensembl-T");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Transcript ref sequence ID"), obxTranscript);
			
			//TODO figure out if this one is needed. Might need ClinVar
//			CWE obxDiscreteGeneticVariant = new CWE(oru.getMessage());
//			obxDiscreteGeneticVariant.getIdentifier().setValue(this.sanitizeHL7Text(v.getTranscript()));
//			obxDiscreteGeneticVariant.getText().setValue(this.sanitizeHL7Text(v.getTranscript()));
//			obxDiscreteGeneticVariant.getNameOfCodingSystem().setValue("Ensembl-T");
//			createVariantOBX(oru, currentVariantId, LOINC.getCode("Discrete genetic variant"), obxDiscreteGeneticVariant);
		}
		//DNA change
		if (this.notNullOrEmpty(v.getcNotation())) {
			CWE obxDNAChange = new CWE(oru.getMessage());
			LOINCItem dnaChangeLoinc = LOINC.getCode("DNA change (c.HGVS)");
			obxDNAChange.getIdentifier().setValue(this.sanitizeHL7Text(v.getcNotation()));
			obxDNAChange.getText().setValue(this.sanitizeHL7Text(v.getcNotation()));
			obxDNAChange.getNameOfCodingSystem().setValue("c.HGVS");
			createVariantOBX(oru, currentVariantId, dnaChangeLoinc, obxDNAChange);
		}
		//DNA Change Type
		if (v.getDnaChangeType() != null) {
			CWE obxDNAChangeType = new CWE(oru.getMessage());
			LOINCItem DNAChangeTypeLoinc = LOINC.getCode("DNA change type");
			obxDNAChangeType.getIdentifier().setValue(this.sanitizeHL7Text(LOINCDNAChangeType.getLoincCode(v.getDnaChangeType())[1]));
			obxDNAChangeType.getText().setValue(this.sanitizeHL7Text(LOINCDNAChangeType.getLoincCode(v.getDnaChangeType())[0]));
			obxDNAChangeType.getNameOfCodingSystem().setValue(DNAChangeTypeLoinc.getSystem());
			createVariantOBX(oru, currentVariantId, DNAChangeTypeLoinc, obxDNAChangeType);
		}
		//AA change
		if (this.notNullOrEmpty(v.getpNotation()) && v.getpNotation().startsWith("p.")) {
			CWE obxAAChange = new CWE(oru.getMessage());
			obxAAChange.getIdentifier().setValue(this.sanitizeHL7Text(v.getpNotation()));
			LOINCItem aaChangeLoinc = LOINC.getCode("Amino acid change p.HGVS");
			obxAAChange.getText().setValue(this.sanitizeHL7Text(v.getpNotation()));
			obxAAChange.getNameOfCodingSystem().setValue("p.HGVS");
			createVariantOBX(oru, currentVariantId, aaChangeLoinc, obxAAChange);
		}
		//AA Change Type
		if (this.notNullOrEmpty(v.getAaMainChangeType())) {
			LOINCItem aaChangeTypeLoinc = LOINC.getCode("Amino acid change [Type]");
			String[] loincCode = LOINCAAChangeType.getLoincCode(v.getAaMainChangeType());
			CWE obxAAChangeType = new CWE(oru.getMessage());
			obxAAChangeType.getIdentifier().setValue(loincCode[1]);
			obxAAChangeType.getText().setValue(loincCode[0]);
			obxAAChangeType.getNameOfCodingSystem().setValue(aaChangeTypeLoinc.getSystem());
			createVariantOBX(oru, currentVariantId, aaChangeTypeLoinc, obxAAChangeType);
		}
		//DNA Change type
		
		//Discrete genetic variant
		int counter = 1;
		String notation = v.getTranscript();
		if (this.notNullOrEmpty(v.getGene())) {
			notation += "(" + v.getGene() + ")";
		}
		else {
			notation = null;
		}
		if (this.notNullOrEmpty(v.getcNotation())) {
			notation += ":" + v.getcNotation();
		}
		else {
			notation = null;
		}
		if (this.notNullOrEmpty(v.getpNotation())) {
			notation += " (" + v.getpNotation() + ")";
		}
		else {
			notation = null;
		}
		//abort if any field above is null
		if (notation != null) {
			if (this.notNullOrEmpty(v.getCosmicMVariantId())) {
				generateExternalId(oru, v.getCosmicMVariantId(), UTSWProps.COSMIC_M, currentVariantId, counter, notation);
				counter++;
			}
			if (this.notNullOrEmpty(v.getCosmicVVariantId())) {
				generateExternalId(oru, v.getCosmicVVariantId(), UTSWProps.COSMIC_V, currentVariantId, counter, notation);
				counter++;
			}
			if (this.notNullOrEmpty(v.getDbSNPVariantId())) {
				generateExternalId(oru, v.getDbSNPVariantId(), UTSWProps.DB_SNP, currentVariantId, counter, notation);
				counter++;
			}
			if (this.notNullOrEmpty(v.getClinvarVariantId())) {
				generateExternalId(oru, v.getClinvarVariantId(), UTSWProps.CLINVAR, currentVariantId, counter, notation);
				counter++;
			}
		}
		
		//Chromosome
		if (this.notNullOrEmpty(v.getChr())) {
			ST obxChromosome = new ST(oru.getMessage());
			obxChromosome.setValue(this.sanitizeHL7Text(v.getChr().replace("chr", "")));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Chromosome"), obxChromosome);
		}
		//Ref
		if (this.notNullOrEmpty(v.getRef())) {
			ST obxRef = new ST(oru.getMessage());
			obxRef.setValue(this.sanitizeHL7Text(v.getRef()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic ref allele"), obxRef);
		}
		//Lat
		if (this.notNullOrEmpty(v.getAlt())) {
			ST obxAlt = new ST(oru.getMessage());
			obxAlt.setValue(this.sanitizeHL7Text(v.getAlt()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic alt allele"), obxAlt);
		}
		//Clinical Significance
		if (v.getClinicalSignificance() != null) {
			CWE obxClinicalSignificance = new CWE(oru.getMessage());
			obxClinicalSignificance.getIdentifier().setValue(this.sanitizeHL7Text(v.getClinicalSignificance().getId()));
			obxClinicalSignificance.getText().setValue(this.sanitizeHL7Text(v.getClinicalSignificance().getText()));
			obxClinicalSignificance.getNameOfCodingSystem().setValue(v.getClinicalSignificance().getSystem());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genetic sequence variation clinical significance [Imp]"), obxClinicalSignificance);
		}
		//StartEnd 2020v
//		if (v.getStartEnd2020v() != null) {
//			ST obxStartEnd = new ST(oru.getMessage());
//			obxStartEnd.setValue(this.sanitizeHL7Text(v.getStartEnd2020v()));
//			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic allele start-end"), obxStartEnd);
//		}
		//StartEnd
		if (v.getStart2018v() != null && v.getEnd2018v() != null) {
			NR obxStartEnd = new NR(oru.getMessage());
			obxStartEnd.getLowValue().setValue(v.getStart2018v() + "");
			obxStartEnd.getHighValue().setValue(v.getEnd2018v() + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic allele start-end"), obxStartEnd);
		}
		//TAF
		if (v.getAllFreq() != null) {
			NM obxTAF = new NM(oru.getMessage());
			obxTAF.setValue(String.format("%.4f", v.getAllFreq()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Sample variant allelic frequency [NFr]"), obxTAF);
		}
		//Allelic Tumor Depth
		if (v.getDepth() != null) {
			NM obxTumorDepth = new NM(oru.getMessage());
			obxTumorDepth.setValue(v.getDepth() + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Allelic read depth"), obxTumorDepth);
		}
		//Allelic Tumor Depth
		if (v.getCopyNumber() != null) {
			NM obxCN = new NM(oru.getMessage());
			obxCN.setValue(v.getCopyNumber() + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic structural variant copy number"), obxCN);
		}
		//Structural Variant Length
		if (v.getStructuralVariantLength() != null) {
			NM obxVariantLength = new NM(oru.getMessage());
			obxVariantLength.setValue(v.getStructuralVariantLength() + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Structural variant [Length]"), obxVariantLength);
		}
		//DNA region name
		if (this.notNullOrEmpty(v.getDnaRegion())) {
			ST obxDNARegion = new ST(oru.getMessage());
			obxDNARegion.setValue(v.getDnaRegion());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("DNA region name [Identifier]"), obxDNARegion);
		}
		if (this.notNullOrEmpty(v.getLeftDNARegion())) {
			ST obxDNARegion = new ST(oru.getMessage());
			obxDNARegion.setValue(v.getLeftDNARegion());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("DNA region name [Identifier]"), obxDNARegion);
		}
//		if (this.notNullOrEmpty(v.getLeftDNARegion())) {
//			ST obxDNARegion = new ST(oru.getMessage());
//			obxDNARegion.setValue(v.getLeftDNARegion());
//			createVariantOBX(oru, currentVariantId + ".a", LOINC.getCode("DNA region name [Identifier]"), obxDNARegion);
//		}
//		if (this.notNullOrEmpty(v.getRightDNARegion())) {
//			ST obxDNARegion = new ST(oru.getMessage());
//			obxDNARegion.setValue(v.getRightDNARegion());
//			createVariantOBX(oru, currentVariantId + ".b", LOINC.getCode("DNA region name [Identifier]"), obxDNARegion);
//		}
		//Structural Variant Inner Start End
		if (v.getStructuralVariantInnerStartEnd() != null) {
			NR obxVariantRange = new NR(oru.getMessage());
			obxVariantRange.getLowValue().setValue(v.getStructuralVariantInnerStartEnd()[0] + "");
			obxVariantRange.getHighValue().setValue(v.getStructuralVariantInnerStartEnd()[1] + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Structural variant inner start and end"), obxVariantRange);
		}
		//Cytoband
		if (v.getCytoband() != null && v.getCytoband() != null) {
			ST obxCytoband = new ST(oru.getMessage());
			obxCytoband.setValue(this.sanitizeHL7Text(v.getChr().replace("chr", "") +  v.getCytoband()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Cytogenetic (chromosome) location"), obxCytoband);
		}
		//Somatic/Germline
		if (this.notNullOrEmpty(v.getSomaticStatus())) {
			CWE obxSomatic = new CWE(oru.getMessage());
			String[] loincGenomicStatus = LOINCGenomicSource.getLoincCode(v.getSomaticStatus());
			obxSomatic.getIdentifier().setValue(loincGenomicStatus[1]);
			obxSomatic.getText().setValue(loincGenomicStatus[0]);
			obxSomatic.getNameOfCodingSystem().setValue(LOINC.getCode("Genomic source class [Type]").getSystem());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic source class [Type]"), obxSomatic);
		}
		//Present
		if (UTSWProps.GENETIC_VARIANT_ASSESSMENT_PRESENT != null) {
			CWE obxSomatic = new CWE(oru.getMessage());
			obxSomatic.getIdentifier().setValue(UTSWProps.GENETIC_VARIANT_ASSESSMENT_PRESENT[2]);
			obxSomatic.getText().setValue(UTSWProps.GENETIC_VARIANT_ASSESSMENT_PRESENT[0]);
			obxSomatic.getNameOfCodingSystem().setValue(LOINC.getCode("Genetic variant assessment").getSystem());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genetic variant assessment"), obxSomatic);
		}
		
		
		//Annotation
		if (v.getAnnotations() != null) {
			counter = 1;
			for (String annotation : v.getAnnotations()) {
//				for (String splitAnnotation : Splitter.fixedLength(200).split(annotation)) {
//					ST obxAnnotation = new ST(oru.getMessage());
//					obxAnnotation.setValue(splitAnnotation);
//					String subVariantId = currentVariantId + "." + utils.getNextVariantIdSub(counter) ;
//					createVariantOBX(oru, subVariantId, LOINC.getCode("Annotation comment [Interpretation] Narrative"), obxAnnotation);
//					counter++;
//				}
				ST obxAnnotation = new ST(oru.getMessage());
				obxAnnotation.setValue(this.sanitizeHL7Text(annotation));
				String subVariantId = currentVariantId + "." + utils.getNextVariantIdSub(counter) ;
				createVariantOBX(oru, subVariantId, LOINC.getCode("Annotation comment [Interpretation] Narrative"), obxAnnotation);
				counter++;
			}
		}
		
		//Line break for readability. DO NOT USE IN PROD!
		if (this.humanReadable) {
			ST obxLineBreak = new ST(oru.getMessage());
			obxLineBreak.setValue("999999");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("LINEBREAK"), obxLineBreak);
		}
	}

	private void generateExternalId(ORU_R01 oru, String externalId, String externalDB, String currentVariantId, int counter, String notation) throws DataTypeException {
		CWE obxExternalID = new CWE(oru.getMessage());
		obxExternalID.getIdentifier().setValue(externalId);
		obxExternalID.getText().setValue(notation);
		obxExternalID.getNameOfCodingSystem().setValue(externalDB);
		String subVariantId = currentVariantId + "." + utils.getNextVariantIdSub(counter) ;
		createVariantOBX(oru, subVariantId, LOINC.getCode("Discrete genetic variant"), obxExternalID);
	}
	
	private CWE createCWEGene(ORU_R01 oru, String hgnc, String gene) throws DataTypeException {
		CWE obxGeneValue = new CWE(oru.getMessage());
		obxGeneValue.getIdentifier().setValue(hgnc);
		obxGeneValue.getNameOfCodingSystem().setValue("HGNC");
		obxGeneValue.getText().setValue(gene);
		return obxGeneValue;
	}
	
	private ST createSTGene(ORU_R01 oru, String hgnc, String gene) throws DataTypeException {
		ST obxGeneValue = new ST(oru.getMessage());
		obxGeneValue.setValue(this.sanitizeHL7Text(gene));
		return obxGeneValue;
	}

	private OBX createVariantOBX(ORU_R01 oru, String subId, LOINCItem loincItem, Type value) throws DataTypeException {
//		OBX obxSegment = adt.getOBX(utils.getObservationId() - 1);
//		OBX obxSegment = orm.getORDER().getORDER_DETAIL().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		OBX obxSegment = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		obxSegment.getValueType().setValue(value.getName());
		obxSegment.getSetIDOBX().setValue(utils.getObservationId() + "");
		obxSegment.getObservationIdentifier().getIdentifier().setValue(loincItem.getId());
		obxSegment.getObservationIdentifier().getText().setValue(loincItem.getText());
		obxSegment.getObservationIdentifier().getNameOfCodingSystem().setValue(loincItem.getSystem());
		obxSegment.getObservationSubID().setValue(subId);
		obxSegment.getObservationValue(0).setData(value);
		utils.incrementObservationCount();
		return obxSegment;
	}
	
	private OBX createVariantOBX(ORU_R01 oru, String subId, LOINCItem loincItem, Type[] value) throws DataTypeException {
//		OBX obxSegment = adt.getOBX(utils.getObservationId() - 1);
//		OBX obxSegment = orm.getORDER().getORDER_DETAIL().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		OBX obxSegment = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		obxSegment.getValueType().setValue(value[0].getName());
		obxSegment.getSetIDOBX().setValue(utils.getObservationId() + "");
		obxSegment.getObservationIdentifier().getIdentifier().setValue(loincItem.getId());
		obxSegment.getObservationIdentifier().getText().setValue(loincItem.getText());
		obxSegment.getObservationIdentifier().getNameOfCodingSystem().setValue(loincItem.getSystem());
		obxSegment.getObservationSubID().setValue(subId);
		obxSegment.getObservationValue(0).setData(value[0]);
		obxSegment.getObservationValue(1).setData(value[1]);
		utils.incrementObservationCount();
		return obxSegment;
	}
	
	private OBX createCaseOBX(ORU_R01 oru, LOINCItem loincItem, Type value, Integer counter) throws DataTypeException {
		OBX obxSegment = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		obxSegment.getValueType().setValue(value.getName());
		obxSegment.getSetIDOBX().setValue(utils.getObservationId() + "");
		String identifierId = loincItem.getId();
		String text = loincItem.getText();
		if (counter != null) {
			identifierId += counter;
			text = loincItem.getTextPart1() + counter + loincItem.getTextPart2();
		}
		obxSegment.getObservationIdentifier().getIdentifier().setValue(identifierId);
		obxSegment.getObservationIdentifier().getText().setValue(text);
		obxSegment.getObservationIdentifier().getNameOfCodingSystem().setValue(loincItem.getSystem());
		obxSegment.getObservationValue(0).setData(value);
		obxSegment.getObservationSubID().setValue("1");
		utils.incrementObservationCount();
		return obxSegment;
	}
	
	private OBX createCaseOBX(ORU_R01 oru, LOINCItem loincItem, Type value) throws DataTypeException {
		OBX obxSegment = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(utils.getObservationId() - 1).getOBX();
		obxSegment.getValueType().setValue(value.getName());
		obxSegment.getSetIDOBX().setValue(utils.getObservationId() + "");
		String identifierId = loincItem.getId();
		String text = loincItem.getText();
		obxSegment.getObservationIdentifier().getIdentifier().setValue(identifierId);
		obxSegment.getObservationIdentifier().getText().setValue(text);
		obxSegment.getObservationIdentifier().getNameOfCodingSystem().setValue(loincItem.getSystem());
		obxSegment.getObservationValue(0).setData(value);
		obxSegment.getObservationSubID().setValue("1");
		utils.incrementObservationCount();
		return obxSegment;
	}
	
	private NTE createCaseNTE(ORU_R01 oru) throws DataTypeException {
		NTE nteSegment = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getNTE();
		nteSegment.getSetIDNTE().setValue("1");
//		Character c1 = report.getSummary().charAt(3);
//		System.out.println(c1.charValue() + " " + c1.hashCode());
//		Character c2 = report.getSummary().charAt(41);
//		System.out.println(c2.charValue() + " " + c2.hashCode());
//		Character c3 = report.getSummary().charAt(72);
//		System.out.println(c3.charValue() + " " + c3.hashCode());
		
		nteSegment.getComment(0).setValue(this.sanitizeHL7Text(report.getSummary()));
		utils.incrementObservationCount();
		return nteSegment;
	}
	
	private String fetchHGNC(String geneTerm, boolean tryPrev) {
		EnsemblResponse ensembl;
		try {
			ensembl = ensemblUtils.fetchEnsembl(geneTerm);
			if (ensembl != null) {
				ensembl.init();
				if (ensembl.getHgncId() != null) {
					return ensembl.getHgncId().replace("HGNC:", "");
				}
			}
		} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException | SAXException
				| ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private OBX createCaseOBXFromUTSWProp(ORU_R01 oru, String[] utswProp, LOINCItem loinc) throws DataTypeException {
		CWE cwe = new CWE(oru.getMessage());
		cwe.getIdentifier().setValue(utswProp[2]);
		cwe.getText().setValue(utswProp[0]);
		cwe.getNameOfCodingSystem().setValue(loinc.getSystem());
		return createCaseOBX(oru, loinc, cwe);
	}
	
	private String sanitizeHL7Text(String text) {
		if (text != null) { 
//			return hl7Escaping.escape(text, EncodingCharacters.defaultInstance());
			//unescape seems to avoid escaping the escape characters
			// \R\ vs \E\R\E

			for (String c : HL7Utils.UTF8_CHARSET.keySet()) {
				text = text.replaceAll(c, HL7Utils.UTF8_CHARSET.get(c));
			}
			for (String c : HL7Utils.GREEK_CHARSET.keySet()) {
				text = text.replaceAll(c, HL7Utils.GREEK_CHARSET.get(c));
			}
			text = text.replaceAll("<br/>", "\\\\.br\\\\");
			
			String test =  hl7Escaping.escape(text, EncodingCharacters.defaultInstance());
			return hl7Escaping.unescape(test, EncodingCharacters.defaultInstance());
		}
		return null;
	}
	
	private boolean notNullOrEmpty(String value) {
		return value != null && !value.equals("");
	}
	
	private List<String> concatInterpretation(GeneVariantAndAnnotation gva) {
		List<String> annotations = new ArrayList<String>();
		List<String> sortedCategories = gva.getAnnotationsByCategory().keySet().stream().sorted().collect(Collectors.toList());
		for (String category : sortedCategories) {
			annotations.add(category + ": " + gva.getAnnotationsByCategory().get(category));
		}
		return annotations;
	}
	
}
