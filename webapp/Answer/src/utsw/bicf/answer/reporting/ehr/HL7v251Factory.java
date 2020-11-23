package utsw.bicf.answer.reporting.ehr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import utsw.bicf.answer.db.api.utils.EnsemblRequestUtils;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.reporting.ehr.loinc.LOINC;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCAAChangeType;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCChromosomes;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCItem;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCVariantCategory;
import utsw.bicf.answer.reporting.ehr.model.HL7Therapy;
import utsw.bicf.answer.reporting.ehr.model.TempusTrial;
import utsw.bicf.answer.reporting.ehr.model.HL7Variant;
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
	
	String overridePatientName;
	String overrideMRN;
	String overrideDOB;
	String overrideGender;
	String overrideOrder;
	
	public HL7v251Factory(Report report, OrderCase caseSummary, RequestUtils requestUtils, File pdfFile, EnsemblProperties ensemblProps, OtherProperties otherProps, String overridePatientName, String overrideMRN, String overrideDOB, String overrideGender, String overrideOrder) {
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
		
		
	}
	
	public String reportToHL7(boolean humanReadable) throws HL7Exception, IOException, URISyntaxException {
		ORU_R01 oru = new ORU_R01();
		oru.initQuickstart("ORU", "R01", "T");
		
		generateData(oru, humanReadable);
		
		// Now, let's encode the message and look at the output
		HapiContext context = new DefaultHapiContext();
		Parser parser = context.getPipeParser();
		String encodedMessage = parser.encode(oru);
		context.close();
		
		//skip wrapper and pdf encoding if human readable
		if (!humanReadable) {
			//add MLP wrapper
			encodedMessage = new String(new byte[] {0x0b}) + encodedMessage + new String(new byte[] {0x1c, 0x0d});
			
		}
		else {
			
		}
		return encodedMessage;
	}

	private void generateData(ORU_R01 oru, boolean humanReadable) throws DataTypeException, ClientProtocolException, IOException, URISyntaxException {
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
			caseSummary.setEpicOrderNumber(overrideOrder);
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
		obr.getSetIDOBR().setValue("1");
		obr.getPlacerOrderNumber().getEntityIdentifier().setValue(caseSummary.getEpicOrderNumber());
		obr.getUniversalServiceIdentifier().getIdentifier().setValue("NGSPCT");
		obr.getUniversalServiceIdentifier().getText().setValue(caseSummary.getLabTestName());
		
		//TODO wait to see if it's Authorized by or Ordered By
		String[] physicianNameAndId = caseSummary.getOrderingPhysician().split("[, ]");
		if (physicianNameAndId[0] != null) {
			obr.getOrderingProvider(0).getIDNumber().setValue(this.sanitizeHL7Text(physicianNameAndId[0]));
		}
		if (physicianNameAndId[1] != null) {
		obr.getOrderingProvider(0).getFamilyName().getSurname().setValue(this.sanitizeHL7Text(physicianNameAndId[1]));
		}
		if (physicianNameAndId[2] != null) {
			obr.getOrderingProvider(0).getGivenName().setValue(this.sanitizeHL7Text(physicianNameAndId[2]));
		}
		if (physicianNameAndId[3] != null) {
			obr.getOrderingProvider(0).getSecondAndFurtherGivenNamesOrInitialsThereof().setValue(this.sanitizeHL7Text(physicianNameAndId[3]));
		}
		
		if (report.getDateFinalized() != null) {
			LocalDateTime finalizedData = LocalDateTime.parse(report.getDateFinalized(), TypeUtils.mongoDateTimeFormatter);
			obr.getResultsRptStatusChngDateTime().getTime().setValue(finalizedData.format(TypeUtils.hl7DateTimeFormatter));
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
			List<HL7Variant> vList = this.buildVariant("Pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsPossibleClinicalSignificance().values()) {
			List<HL7Variant> vList = this.buildVariant("Likely pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsUnknownClinicalSignificance().values()) {
			List<HL7Variant> vList = this.buildVariant("Uncertain significance", gva, true);
			variantsByOid.put(gva.getOid(), vList.get(0));
			variants.addAll(vList);
		}
		
		for (HL7Variant tv : variants) {
			addVariant(oru, tv);
		}
		
		for (IndicatedTherapy therapy :report.getIndicatedTherapies()) {
			HL7Therapy tt = new HL7Therapy();
			HL7Variant v = variantsByOid.get(therapy.getOid());
			if (v != null) {
				if (v.getGene().equals("PDE4DIP")) {
					therapy.setDrugs("Olaparib, niraparib,rucaparib");
				}
				tt.setVariant(v);
				tt.setDrug(therapy.getDrugs());
				tt.setLevel(therapy.getLevel());
				tt.setIndication(therapy.getIndication());
			}
			therapies.add(tt);
			
		}
		
		//case level entries
		createCaseOBXFromUTSWProp(oru, UTSWProps.GRCh38, LOINC.getCode("Human reference sequence assembly version"));
		createCaseOBXFromUTSWProp(oru, UTSWProps.VARIANT_ANALYSIS_METHOD_SEQUENCING, LOINC.getCode("Variant analysis method [Type]"));
		createCaseOBXFromUTSWProp(oru, UTSWProps.GENOMIC_SOURCE_CLASS_SOMATIC, LOINC.getCode("Genomic source class [Type]"));
		
		NM obxTMB = new NM(oru.getMessage());
		obxTMB.setValue(String.format("%.2f", caseSummary.getTumorMutationBurden()));
		OBX tmbSegment = createCaseOBX(oru, LOINC.getCode("Tumor mutation burden [Interpretation]"), obxTMB, null);
		tmbSegment.getUnits().getIdentifier().setValue("m/MB");
		
		if (caseSummary.getMsi() != null) {
			NM obxMSI = new NM(oru.getMessage());
			obxMSI.setValue(String.format("%.2f", caseSummary.getMsi()));
			OBX msiSegment = createCaseOBX(oru, LOINC.getCode("Microsatellite instability [Interpretation] in Cancer specimen Qualitative"), obxMSI, null);
			msiSegment.getUnits().getIdentifier().setValue("%");
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
			trials.add(trial);
		}
		addClinicalTrialSegments(oru, trials);
		
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
		
		//Report Summary
		createCaseNTE(oru);
	}
	
	public List<HL7Variant> buildVariant(String clinicalSignificance, GeneVariantAndAnnotation gva, boolean skipAnnotation) throws ClientProtocolException, IOException, URISyntaxException {
		List<HL7Variant> vList = new ArrayList<HL7Variant>();
		if (gva.getType().contentEquals("snp")) {
			HL7Variant v = new HL7Variant();
			v.setGene(gva.getGene());
//			v.setAaChange(gva.getVariant());
			Variant variant = requestUtils.getVariantDetails(gva.getOid());
			v.setRef(variant.getReference());
			v.setAlt(variant.getAlt());
			v.setChr(variant.getChrom());
			v.setStartEnd2020v(variant.getChrom() + ":" + variant.getPos());
			v.setStart2018v(variant.getPos());
			v.setEnd2018v(variant.getPos() + v.getRef().length() - v.getAlt().length());
			v.setClinicalSignificance(LOINC.getCode(clinicalSignificance));
			v.setTranscript(variant.getVcfAnnotations().get(0).getFeatureId());
			v.setAllFreq(variant.getExacAlleleFrequency());
			v.setDepth(variant.getTumorAltDepth());
			v.setHgncCode(fetchHGNC(gva.getGene()));
			v.setEnsemblCode(variant.getVcfAnnotations().get(0).getGeneId());
			String dbSNPId = null;
			if (variant.getIds() != null) {
				for (String id : variant.getIds()) {
					if (id != null && id.startsWith("rs")) {
						dbSNPId = id;
						break;
					}
				}
			}
			v.setDbSNPId(dbSNPId);
			String cNotation = variant.getVcfAnnotations().get(0).getCodingNotation();
			if (cNotation != null) {
				v.setcNotation(cNotation);
			}
			String pNotation = variant.getVcfAnnotations().get(0).getProteinNotation();
			if (pNotation != null && !pNotation.equals("")) {
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
			for (String effect : variant.getEffects()) {
				String loincKey = LOINCAAChangeType.getLoincCodeKeyFromEffect(effect);
				if (loincKey != null) {
					v.getAaChangeTypes().add(loincKey);
				}
			}
			//DNA Region
			v.setDnaRegion(variant.getRank());
			
			v.setVariantCategory(LOINCVariantCategory.getLoincCode(gva.getType()));
			if (!skipAnnotation) {
				StringBuilder interpretation = new StringBuilder();
				for (String category : gva.getAnnotationsByCategory().keySet()) {
					interpretation.append(category + ": " + gva.getAnnotationsByCategory().get(category));
				}
				v.setAnnotation(interpretation.toString());
			}
			vList.add(v);
		}
		else if (gva.getType().contentEquals("cnv")) {
			HL7Variant v = new HL7Variant();

			v.setGene(gva.getGene());
//			v.setAaChange(gva.getVariant());
			CNV variant = requestUtils.getCNVDetails(gva.getOid());
			v.setChr(variant.getChrom());
			v.setClinicalSignificance(LOINC.getCode(clinicalSignificance));
			v.setCytoband(variant.getCytoband());
			
			if (variant.getAberrationType() != null && variant.getAberrationType().equals("ITD")) {
				v.setDnaChangeType("Duplication");
			}
			v.setCopyNumber(variant.getCopyNumber());
			v.setStructuralVariantLength(Math.abs(variant.getEnd() - variant.getStart()));
			v.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(variant.getEnd(),  variant.getStart()), Math.max(variant.getEnd(),  variant.getStart())});
			
			v.setVariantCategory(LOINCVariantCategory.getLoincCode(gva.getType()));
			if (!skipAnnotation) {
				StringBuilder interpretation = new StringBuilder();
				for (String category : gva.getAnnotationsByCategory().keySet()) {
					interpretation.append(category + ": " + gva.getAnnotationsByCategory().get(category));
				}
				v.setAnnotation(interpretation.toString());
			}
			vList.add(v);
		}
		else if (gva.getType().contentEquals("translocation")) {
			HL7Variant v1 = new HL7Variant();
			HL7Variant v2 = new HL7Variant();
			HL7Variant v12 = new HL7Variant();

//			v.setAaChange(gva.getVariant());
			Translocation variant = requestUtils.getTranslocationDetails(gva.getOid());
			v1.setGene(variant.getLeftGene());
			v12.setClinicalSignificance(LOINC.getCode(clinicalSignificance));
			if (variant.getChrType() != null && variant.getChrType().equals("INTERCHROMOSOMAL")) {
				Integer left = Integer.parseInt(variant.getLeftBreakpoint().split(":")[1]);
				Integer right = Integer.parseInt(variant.getRightBreakpoint().split(":")[1]);
				v12.setStructuralVariantLength(Math.abs(left - right));
				v12.setStructuralVariantInnerStartEnd(new Integer[] {Math.min(left,  right), Math.max(left,  right)});
			}
			v1.setDnaRegion(variant.getFirstExon());
			v2.setDnaRegion(variant.getLastExon());
			vList.add(v1);
			vList.add(v2);
			vList.add(v12);
			v12.setVariantCategory(LOINCVariantCategory.getLoincCode(gva.getType()));
			if (!skipAnnotation) {
				StringBuilder interpretation = new StringBuilder();
				for (String category : gva.getAnnotationsByCategory().keySet()) {
					interpretation.append(category + ": " + gva.getAnnotationsByCategory().get(category));
				}
				v12.setAnnotation(interpretation.toString());
			}
			
		}
		return vList;
	}

	public void addTherapySegments(ORU_R01 oru, List<HL7Therapy> therapies) throws DataTypeException {
		int therapyCount = 0;
		int counter = 1;
		//each drug gets its own therapy entry (duplication of gene variant, level, indication)
		for (HL7Therapy t : therapies) {
			//Agent/Drugs
			if (t.getDrug() != null && !t.getDrug().equals("")) {
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
		if (t.getVariant().getHgncCode() != null) {
			CWE obxGene = createCWEGene(oru, t.getVariant());
			createCaseOBX(oru, LOINC.getCode("THERAPYGENE"), obxGene, counter);
		}
		//Variant
		if (t.getVariant().getTranscript() != null) {
			CWE obxTranscript = new CWE(oru.getMessage());
			obxTranscript.getIdentifier().setValue(this.sanitizeHL7Text(t.getVariant().getTranscript()));
			obxTranscript.getText().setValue(this.sanitizeHL7Text(t.getVariant().getTranscript()));
			obxTranscript.getNameOfCodingSystem().setValue("Ensembl");
			createCaseOBX(oru, LOINC.getCode("THERAPYVARIANT"), obxTranscript, counter);
		}
		//Level
		if (t.getLevel() != null) {
			ST obxLevel = new ST(oru.getMessage());
			obxLevel.setValue(this.sanitizeHL7Text(t.getLevel()));
			createCaseOBX(oru, LOINC.getCode("THERAPYLEVEL"), obxLevel, counter);
		}
		//Indication
		if (t.getIndication() != null) {
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
		//DBSNP
		if (v.getDbSNPId() != null) {
			ST obxSbSNP = new ST(oru.getMessage());
			obxSbSNP.setValue(this.sanitizeHL7Text(v.getDbSNPId()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("dbSNP version [ID]"), obxSbSNP);
		}
		//Variant Category
		CWE obxVariantCategory = new CWE(oru.getMessage());
		LOINCItem variantCategoryLoinc = LOINC.getCode("Variant category");
		obxVariantCategory.getIdentifier().setValue(this.sanitizeHL7Text(v.getVariantCategory()[1]));
		obxVariantCategory.getText().setValue(this.sanitizeHL7Text(v.getVariantCategory()[0]));
		obxVariantCategory.getNameOfCodingSystem().setValue(variantCategoryLoinc.getSystem());
		createVariantOBX(oru, currentVariantId, variantCategoryLoinc, obxVariantCategory);
		//Gene
		if (v.getHgncCode() != null) {
			CWE obxGeneValue = createCWEGene(oru, v);
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Gene studied"), obxGeneValue);
		}
		//Transcript
		if (v.getTranscript() != null) {
			CWE obxTranscript = new CWE(oru.getMessage());
			obxTranscript.getIdentifier().setValue(this.sanitizeHL7Text(v.getTranscript()));
			obxTranscript.getText().setValue(this.sanitizeHL7Text(v.getTranscript()));
			obxTranscript.getNameOfCodingSystem().setValue("Ensembl");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Transcript ref sequence ID"), obxTranscript);
		}
		//DNA change
		if (v.getcNotation() != null) {
			CWE obxDNAChange = new CWE(oru.getMessage());
			LOINCItem dnaChangeLoinc = LOINC.getCode("DNA change (c.HGVS)");
			obxDNAChange.getIdentifier().setValue(this.sanitizeHL7Text(v.getcNotation()));
			obxDNAChange.getText().setValue(this.sanitizeHL7Text(v.getcNotation()));
			obxDNAChange.getNameOfCodingSystem().setValue("c.HGVS");
			createVariantOBX(oru, currentVariantId, dnaChangeLoinc, obxDNAChange);
		}
		//AA change
		if (v.getpNotation() != null) {
			CWE obxAAChange = new CWE(oru.getMessage());
			obxAAChange.getIdentifier().setValue(this.sanitizeHL7Text(v.getpNotation()));
			LOINCItem aaChangeLoinc = LOINC.getCode("Amino acid change p.HGVS");
			obxAAChange.getText().setValue(this.sanitizeHL7Text(v.getpNotation()));
			obxAAChange.getNameOfCodingSystem().setValue("p.HGVS");
			createVariantOBX(oru, currentVariantId, aaChangeLoinc, obxAAChange);
		}
		//AA Change Type
		LOINCItem aaChangeTypeLoinc = LOINC.getCode("Amino acid change [Type]");
		for (String loincKey : v.getAaChangeTypes()) {
			String[] loincCode = LOINCAAChangeType.getLoincCode(loincKey);
			CWE obxAAChangeType = new CWE(oru.getMessage());
			obxAAChangeType.getIdentifier().setValue(loincCode[1]);
			obxAAChangeType.getText().setValue(loincCode[0]);
			obxAAChangeType.getNameOfCodingSystem().setValue(aaChangeTypeLoinc.getSystem());
			createVariantOBX(oru, currentVariantId, aaChangeTypeLoinc, obxAAChangeType);
		}
		//Chromosome
		if (v.getChr() != null) {
			CWE obxChromosome = new CWE(oru.getMessage());
			String[] loingChrom = LOINCChromosomes.getLoincCode(v.getChr());
			obxChromosome.getIdentifier().setValue(loingChrom[1]);
			obxChromosome.getText().setValue(loingChrom[0]);
			obxChromosome.getNameOfCodingSystem().setValue(LOINC.getCode("Chromosome").getSystem());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Chromosome"), obxChromosome);
		}
		//Ref
		if (v.getRef() != null) {
			ST obxRef = new ST(oru.getMessage());
			obxRef.setValue(this.sanitizeHL7Text(v.getRef()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic ref allele"), obxRef);
		}
		//Lat
		if (v.getAlt() != null) {
			ST obxAlt = new ST(oru.getMessage());
			obxAlt.setValue(this.sanitizeHL7Text(v.getAlt()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Genomic alt allele"), obxAlt);
			//Clinical Significance
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
			obxTAF.setValue(String.format("%.2f", v.getAllFreq()));
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
		if (v.getDnaRegion() != null) {
			ST obxDNARegion = new ST(oru.getMessage());
			obxDNARegion.setValue(v.getDnaRegion());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("DNA region name [Identifier]"), obxDNARegion);
		}
		//Structural Variant Inner Start End
		if (v.getStructuralVariantInnerStartEnd() != null) {
			NR obxVariantRange = new NR(oru.getMessage());
			obxVariantRange.getLowValue().setValue(v.getStructuralVariantInnerStartEnd()[0] + "");
			obxVariantRange.getHighValue().setValue(v.getStructuralVariantInnerStartEnd()[1] + "");
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Structural variant inner start and end"), obxVariantRange);
		}
		//DNA region name
		if (v.getCytoband() != null) {
			ST obxCytoband = new ST(oru.getMessage());
			obxCytoband.setValue(this.sanitizeHL7Text(v.getCytoband()));
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Cytogenetic (chromosome) location"), obxCytoband);
		}
		//Annotation
		if (v.getAnnotation() != null) {
			ST obxAnnotation = new ST(oru.getMessage());
			obxAnnotation.setValue(v.getAnnotation());
			createVariantOBX(oru, currentVariantId, LOINC.getCode("Annotation comment [Interpretation] Narrative"), obxAnnotation);
		}
	}
	
	private CWE createCWEGene(ORU_R01 oru, HL7Variant v) throws DataTypeException {
		CWE obxGeneValue = new CWE(oru.getMessage());
		obxGeneValue.getIdentifier().setValue(v.getHgncCode());
		obxGeneValue.getNameOfCodingSystem().setValue("HGNC");
		obxGeneValue.getText().setValue(v.getGene());
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
		nteSegment.getComment(0).setValue(this.sanitizeHL7Text(report.getSummary()));
		utils.incrementObservationCount();
		return nteSegment;
	}
	
	private String fetchHGNC(String geneTerm) {
		EnsemblRequestUtils utils = new EnsemblRequestUtils(ensemblProps, otherProps);
		EnsemblResponse ensembl;
		try {
			ensembl = utils.fetchEnsembl(geneTerm);
			ensembl.init();
			if (ensembl != null && ensembl.getHgncId() != null) {
				return ensembl.getHgncId().replace("HGNC:", "");
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
			return hl7Escaping.escape(text, EncodingCharacters.defaultInstance());
		}
		return null;
	}
}
