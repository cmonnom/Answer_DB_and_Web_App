package utsw.bicf.answer.reporting.ehr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v251.datatype.CWE;
import ca.uhn.hl7v2.model.v251.datatype.ED;
import ca.uhn.hl7v2.model.v251.datatype.NM;
import ca.uhn.hl7v2.model.v251.datatype.ST;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.segment.DG1;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.reporting.ehr.loinc.LOINC;
import utsw.bicf.answer.reporting.ehr.loinc.LOINCItem;
import utsw.bicf.answer.reporting.ehr.model.TempusTherapy;
import utsw.bicf.answer.reporting.ehr.model.TempusTrial;
import utsw.bicf.answer.reporting.ehr.model.TempusVariant;
import utsw.bicf.answer.reporting.ehr.utils.TempusUtils;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;

public class HL7v251Factory {
	
	private TempusUtils utils = new TempusUtils();
	Report report;
	OrderCase caseSummary;
	RequestUtils requestUtils;
	File pdfFile;
	
	public HL7v251Factory(Report report, OrderCase caseSummary, RequestUtils requestUtils, File pdfFile) {
		super();
		this.report = report;
		this.caseSummary = caseSummary;
		this.requestUtils = requestUtils;
		this.pdfFile = pdfFile;
	}
	
	public String reportToHL7() throws HL7Exception, IOException, URISyntaxException {
//		ORM_O01 orm = new ORM_O01();
//		orm.initQuickstart("ORM", "O01", null);
		ADT_A01 adt = new ADT_A01();
//		adt.initQuickstart("ADT", "A01", "P");
		adt.initQuickstart("ORM", "O01", "T");
		
		generateData(adt);
//		generateData(orm);
		
		// Now, let's encode the message and look at the output
		HapiContext context = new DefaultHapiContext();
		Parser parser = context.getPipeParser();
		String encodedMessage = parser.encode(adt);
		context.close();
		
		encodedMessage = encodedMessage.replace("^ADT_A01", ""); //hack to be able to use ADT objects for Java
		
		//add MLP wrapper
		encodedMessage = new String(new byte[] {0x0b}) + encodedMessage + new String(new byte[] {0x1c, 0x0d});
		return encodedMessage;
	}

	private void generateData(ADT_A01 orm) throws DataTypeException, ClientProtocolException, IOException, URISyntaxException {
		// Populate the MSH Segment
		MSH mshSegment = orm.getMSH();
		mshSegment.getSendingApplication().getNamespaceID().setValue(UTSWProps.SENDING_APPLICATION);
		mshSegment.getSendingFacility().getNamespaceID().setValue(UTSWProps.SENDING_FACILITY);
		mshSegment.getReceivingApplication().getNamespaceID().setValue(UTSWProps.RECEIVING_APPLICATION);
		mshSegment.getReceivingFacility().getNamespaceID().setValue(UTSWProps.RECEIVING_FACILITY);
		mshSegment.getDateTimeOfMessage().getTime().setValue(new Date());

		// Populate the PID Segment
//		ORM_O01_PATIENT pid = orm.getPATIENT();
//		pid.getPID().getSetIDPID().setValue("1");
//		pid.getSetIDPID().setValue("1");
		PID pid = orm.getPID();
		pid.getSetIDPID().setValue("1");
		String[] patientName = caseSummary.getPatientName().split(",");
		pid.getPatientName(0).getFamilyName().getSurname().setValue(patientName[0]);
		pid.getPatientName(0).getGivenName().setValue(patientName[1]);
		pid.getPatientIdentifierList(0).getIDNumber().setValue(caseSummary.getMedicalRecordNumber());
		pid.getPatientIdentifierList(1).getIDNumber().setValue(caseSummary.getEpicOrderNumber());
		pid.getPatientIdentifierList(2).getIDNumber().setValue(caseSummary.getCaseId());
		
		//Diagnosis
		DG1 diagnosis = orm.getDG1();
		diagnosis.getSetIDDG1().setValue("1");
		diagnosis.getDiagnosisCodingMethod().setValue(UTSWProps.DIAGNOSIS_CODING_METHOD);
		diagnosis.getDiagnosisCodeDG1().getText().setValue(caseSummary.getIcd10());
		
		List<TempusTherapy> therapies = new ArrayList<TempusTherapy>();
		List<TempusVariant> variants = new ArrayList<TempusVariant>();
		Map<String, TempusVariant> variantsByOid = new HashMap<String, TempusVariant>();
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsStrongClinicalSignificance().values()) {
			TempusVariant v = this.buildVariant("Pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), v);
			variants.add(v);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsPossibleClinicalSignificance().values()) {
			TempusVariant v = this.buildVariant("Likely pathogenic", gva, false);
			variantsByOid.put(gva.getOid(), v);
			variants.add(v);
		}
		for (GeneVariantAndAnnotation gva : report.getSnpVariantsUnknownClinicalSignificance().values()) {
			TempusVariant v = this.buildVariant("Uncertain significance", gva, true);
			variantsByOid.put(gva.getOid(), v);
			variants.add(v);
		}
		
		for (TempusVariant tv : variants) {
			addVariant(orm, tv);
		}
		
		for (IndicatedTherapy therapy :report.getIndicatedTherapies()) {
			TempusTherapy tt = new TempusTherapy();
			TempusVariant v = variantsByOid.get(therapy.getOid());
			if (v != null) {
				tt.setVariant(v);
				tt.setDrug(therapy.getDrugs());
			}
			therapies.add(tt);
			
		}
		
		//case level entries
		
		NM obxTMB = new NM(orm.getMessage());
		obxTMB.setValue(String.format("%.2f", caseSummary.getTumorMutationBurden()));
		OBX tmbSegment = createCaseOBX(orm, LOINC.getCode("Tumor Mutational Burden (Gene Mut Tested Bld/T)"), obxTMB, null);
		tmbSegment.getUnits().getIdentifier().setValue("m/MB");
		
		//Therapy
		addTherapySegments(orm, therapies);
		
		//Trials
		List<TempusTrial> trials = new ArrayList<TempusTrial>();
		for (BiomarkerTrialsRow biomarkerTrial : report.getClinicalTrials()) {
			TempusTrial trial = new TempusTrial();
			trial.setNctId(biomarkerTrial.getNctid());
			trial.setBiomarkers(Arrays.asList(biomarkerTrial.getBiomarker()));
			trials.add(trial);
		}
		addClinicalTrialSegments(orm, trials);
		
		//Base64 PDF
		ED obxPDF = new ED(orm.getMessage());
		obxPDF.getDataSubtype().setValue("PDF");
		obxPDF.getEncoding().setValue("Base64");
		byte[] bytes = FileUtils.readFileToByteArray(this.pdfFile);
		obxPDF.getData().setValue(Base64.getEncoder().encodeToString(bytes));
		OBX pdfSegment = createCaseOBX(orm, obxPDF);
	}
	
	public TempusVariant buildVariant(String clinicalSignificance, GeneVariantAndAnnotation gva, boolean skipAnnotation) throws ClientProtocolException, IOException, URISyntaxException {
		TempusVariant v = new TempusVariant();
		v = new TempusVariant();
		v.setGene(gva.getGene());
		v.setAaChange(gva.getVariant());
		Variant variant = requestUtils.getVariantDetails(gva.getOid());
		v.setRef(variant.getReference());
		v.setAlt(variant.getAlt());
		v.setChr(variant.getChrom());
		v.setStart(variant.getPos());
		v.setClinicalSignificance(LOINC.getCode(clinicalSignificance));
		v.setTranscript(variant.getVcfAnnotations().get(0).getFeatureId());
		v.setAllFreq(variant.getExacAlleleFrequency());
		v.setDepth(variant.getTumorAltDepth());
//		v.setHgncCode(10471); //TODO
		v.setEnsemblCode(variant.getVcfAnnotations().get(0).getGeneId());
		if (!skipAnnotation) {
			StringBuilder interpretation = new StringBuilder();
			for (String category : gva.getAnnotationsByCategory().keySet()) {
				interpretation.append(category + ": " + gva.getAnnotationsByCategory().get(category));
			}
			v.setAnnotation(interpretation.toString());
		}
		return v;
	}

	public void addTherapySegments(ADT_A01 adt, List<TempusTherapy> therapies) throws DataTypeException {
		NM obxTherapyCount = new NM(adt.getMessage());
		obxTherapyCount.setValue(therapies.size() + "");
		createCaseOBX(adt, LOINC.getCode("Therapy count"), obxTherapyCount, null);
		int counter = 1;
		for (TempusTherapy t : therapies) {
			//Gene
			CWE obxGene = createCWEGene(adt, t.getVariant());
			createCaseOBX(adt, LOINC.getCode("THERAPYGENE"), obxGene, counter);
			//Variant
			ST obxVariant = new ST(adt.getMessage());
			obxVariant.setValue(t.getVariant().getAaChange());
			createCaseOBX(adt, LOINC.getCode("THERAPYVARIANT"), obxVariant, counter);
			//Agent
			if (t.getDrug() != null) {
				ST obxDrug = new ST(adt.getMessage());
				obxDrug.setValue(t.getDrug());
				createCaseOBX(adt, LOINC.getCode("THERAPYAGENT"), obxDrug, counter);
			}
			//Pubmed
			//Here I'm using multiple entries while Tempus uses a comma-separated string
			//the ST type is too broad. Let's review this
			for (String pubmed : t.getPubmedIds()) {
				ST obxPubmed = new ST(adt.getMessage());
				obxPubmed.setValue(pubmed);
				createCaseOBX(adt, LOINC.getCode("THERAPYPUBMEDID"), obxPubmed, counter);
			}
			counter++;
		}
	}	
	
	public void addClinicalTrialSegments(ADT_A01 adt, List<TempusTrial> trials) throws DataTypeException {
		NM obxTherapyCount = new NM(adt.getMessage());
		obxTherapyCount.setValue(trials.size() + "");
		createCaseOBX(adt, LOINC.getCode("Trial count"), obxTherapyCount, null);
		int counter = 1;
		for (TempusTrial t : trials) {
			//NCTID
			ST obxNCTID = new ST(adt.getMessage());
			obxNCTID.setValue(t.getNctId());
			createCaseOBX(adt, LOINC.getCode("TRIALNCTID"), obxNCTID, counter);
			//Biomarkers
			//Here I'm using multiple entries while Tempus uses a comma-separated string
			//the ST type is too broad. Let's review this
			for (String biomarker : t.getBiomarkers()) {
				ST obxBiomarker = new ST(adt.getMessage());
				obxBiomarker.setValue(biomarker);
				createCaseOBX(adt, LOINC.getCode("TRIALMATCHES"), obxBiomarker, counter);
			}
			counter++;
		}
	}	
	
	private void addVariant(ADT_A01 adt, TempusVariant v) throws DataTypeException {
		String currentVariantId = utils.getNextVariantId();
		//Gene
		CWE obxGeneValue = createCWEGene(adt, v);
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Gene studied"), obxGeneValue);
		//Transcript
		CWE obxTranscript = new CWE(adt.getMessage());
		obxTranscript.getIdentifier().setValue(v.getTranscript());
		obxTranscript.getText().setValue(v.getTranscript());
		obxTranscript.getNameOfCodingSystem().setValue("Ensembl");
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Transcript ref sequence ID"), obxTranscript);
		//AA change
		CWE obxAAChange = new CWE(adt.getMessage());
		obxAAChange.getIdentifier().setValue(v.getAaChange());
		obxAAChange.getText().setValue(v.getAaChange());
		obxAAChange.getNameOfCodingSystem().setValue("HGVS.p");
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Amino acid change p.HGVS"), obxAAChange);
		//Chromosome
		ST obxChromosome = new ST(adt.getMessage());
		obxChromosome.setValue(v.getChr());
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Chromosome"), obxChromosome);
		//Ref
		ST obxRef = new ST(adt.getMessage());
		obxRef.setValue(v.getRef());
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Genomic ref allele"), obxRef);
		//Pos
		NM obxPos = new NM(adt.getMessage());
		obxPos.setValue(v.getStart() + "");
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Genomic allele start-end"), obxPos);
		//Lat
		ST obxAlt = new ST(adt.getMessage());
		obxAlt.setValue(v.getAlt());
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Genomic alt allele"), obxAlt);
		//Clinical Significance
		CWE obxClinicalSignificance = new CWE(adt.getMessage());
		obxClinicalSignificance.getIdentifier().setValue(v.getClinicalSignificance().getId());
		obxClinicalSignificance.getText().setValue(v.getClinicalSignificance().getText());
		obxClinicalSignificance.getNameOfCodingSystem().setValue(v.getClinicalSignificance().getSystem());
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Genetic sequence variation clinical significance [Imp]"), obxClinicalSignificance);
		//TAF
		NM obxTAF = new NM(adt.getMessage());
		obxTAF.setValue(String.format("%.2f", v.getAllFreq()));
		createVariantOBX(adt, currentVariantId, LOINC.getCode("Allelic freq NFr"), obxTAF);
		//Annotation
		if (v.getAnnotation() != null) {
			ST obxAnnotation = new ST(adt.getMessage());
			obxAnnotation.setValue(v.getAnnotation());
			createVariantOBX(adt, currentVariantId, LOINC.getCode("Annotation comment [Interpretation] Narrative"), obxAnnotation);
		}
	}
	
	private CWE createCWEGene(ADT_A01 adt, TempusVariant v) throws DataTypeException {
		CWE obxGeneValue = new CWE(adt.getMessage());
		if (v.getHgncCode() == 0) {
			obxGeneValue.getIdentifier().setValue(v.getTranscript());
			obxGeneValue.getNameOfCodingSystem().setValue("Ensembl");
		}
		else {
			obxGeneValue.getIdentifier().setValue(v.getHgncCode() + "");
			obxGeneValue.getNameOfCodingSystem().setValue("HGNC");
		}
		obxGeneValue.getText().setValue(v.getGene());
		return obxGeneValue;
	}

	private OBX createVariantOBX(ADT_A01 adt, String subId, LOINCItem loincItem, Type value) throws DataTypeException {
		OBX obxSegment = adt.getOBX(utils.getObservationId() - 1);
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
	
	private OBX createCaseOBX(ADT_A01 adt, LOINCItem loincItem, Type value, Integer counter) throws DataTypeException {
		OBX obxSegment = adt.getOBX(utils.getObservationId() - 1);
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
		utils.incrementObservationCount();
		return obxSegment;
	}
	
	private OBX createCaseOBX(ADT_A01 adt, Type value) throws DataTypeException {
		OBX obxSegment = adt.getOBX(utils.getObservationId() - 1);
		obxSegment.getValueType().setValue(value.getName());
		obxSegment.getSetIDOBX().setValue(utils.getObservationId() + "");
		obxSegment.getObservationIdentifier().getIdentifier().setValue(UTSWProps.PDF_IDENTIFIER);
		obxSegment.getObservationIdentifier().getText().setValue(UTSWProps.PDF_TEXT);
		obxSegment.getObservationValue(0).setData(value);
		utils.incrementObservationCount();
		return obxSegment;
	}
	
}
