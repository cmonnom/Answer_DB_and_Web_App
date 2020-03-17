package utsw.bicf.answer.reporting.ehr;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import utsw.bicf.answer.model.ehr.Alteration;
import utsw.bicf.answer.model.ehr.AlterationProperties;
import utsw.bicf.answer.model.ehr.Alterations;
import utsw.bicf.answer.model.ehr.Application;
import utsw.bicf.answer.model.ehr.ApplicationSetting;
import utsw.bicf.answer.model.ehr.ApplicationSettings;
import utsw.bicf.answer.model.ehr.CustomerInformation;
import utsw.bicf.answer.model.ehr.FinalReport;
import utsw.bicf.answer.model.ehr.Gene;
import utsw.bicf.answer.model.ehr.Genes;
import utsw.bicf.answer.model.ehr.PMI;
import utsw.bicf.answer.model.ehr.ProcessSite;
import utsw.bicf.answer.model.ehr.ProcessSites;
import utsw.bicf.answer.model.ehr.ResultsPayload;
import utsw.bicf.answer.model.ehr.ResultsReport;
import utsw.bicf.answer.model.ehr.Sample;
import utsw.bicf.answer.model.ehr.VariantReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;

public class EpicXML {
	
	Report report;
	OrderCase caseSummary;
	
	public EpicXML() {
		super();
	}

	public EpicXML(Report report, OrderCase caseSummary) {
		this.report = report;
		this.caseSummary = caseSummary;
	}
	
	public String buildXML() {
		ResultsReport resultsReport = new ResultsReport();
		CustomerInformation customerInformation = buildCustomerInformation();
		resultsReport.setCustomerInformation(customerInformation);
		ResultsPayload rpayload = new ResultsPayload();
		rpayload.setFinalReport(buildFinalReport());
		rpayload.setVariantReport(buildVariantReport());
		rpayload.setPdfReport(buildPDFHash());
		resultsReport.setResultsPayload(rpayload);
		
		
		return null;
	}
	
	private CustomerInformation buildCustomerInformation() {
		CustomerInformation ci = new CustomerInformation();
		ci.setMRN(caseSummary.getMedicalRecordNumber());
		ci.setTRF(caseSummary.getCaseId()); // caseId?;
		ci.setCSN(caseSummary.getCaseName()); //case name?
		ci.setReferenceID(caseSummary.getEpicOrderNumber()); //?
		ci.setPhysicianId(caseSummary.getOrderingPhysician()); // physisian id?
		return ci;
	}
	
	private FinalReport buildFinalReport() {
		FinalReport fr = new FinalReport();
		//Application
		Application app = new Application();
		fr.setApplication(app);
		ApplicationSettings as = new ApplicationSettings(); 
		app.getApplicationSettings().add(as);
		ApplicationSetting asItem = new ApplicationSetting();
		as.getApplicationSetting().add(asItem);
		asItem.setName("Statement");
		asItem.setValue("Some notes?");
		
		fr.setReportId(caseSummary.getCaseId()); //caseId?
		fr.setSampleName(caseSummary.getCaseName()); //caseName?
		fr.setVersion(new BigInteger("1"));
		
		//Sample
		Sample sample = new Sample();
		sample.setFMId(caseSummary.getCaseId());
		sample.setSampleId(caseSummary.getTumorBam());
		sample.setTRFNumber(caseSummary.getCaseId());
		sample.setTestType(caseSummary.getLabTestName());
		sample.setSpecFormat("Unknown"); //we don't have that in Answer
		try {
			XMLGregorianCalendar cal = DatatypeFactory.newInstance()
				    .newXMLGregorianCalendar(caseSummary.getReceivedDate());
			sample.setReceivedDate(cal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} 
		ProcessSites ps = new ProcessSites();
		ProcessSite psItem = new ProcessSite();
		psItem.setAddress("UTSW biocenter");
		psItem.setCliaNumber(caseSummary.getEpicOrderNumber());
		ps.getProcessSite().add(psItem);
		sample.setProcessSites(ps);
		
		//PMI
		PMI pmi = new PMI();
		pmi.setReportId(caseSummary.getCaseId());
		pmi.setMRN(caseSummary.getMedicalRecordNumber());
		pmi.setFullName(caseSummary.getPatientName());
		pmi.setGender(caseSummary.getGender());
		pmi.setDOB(caseSummary.getDateOfBirth());
		pmi.setOrderingMD(caseSummary.getOrderingPhysician());
		pmi.setPathologist(caseSummary.getCaseOwner()); //need name from mysql instead of id
		
		
		//Genes
		Genes genes = new Genes();
		QName name = new QName("Name");
		QName include = new QName("Include");
		QName interpretation = new QName("Interpretation");
		for (IndicatedTherapy therapy : report.getIndicatedTherapies()) {
			Gene gene = new Gene();
			genes.getGene().add(gene);
			JAXBElement<String> eltName = new JAXBElement<String>(name, String.class, therapy.getBiomarkers());
			gene.getContent().add(eltName);
			JAXBElement<Boolean> eltInclude = new JAXBElement<Boolean>(include, Boolean.class, true);
			gene.getContent().add(eltInclude);
			
			//Alterations
			Alterations alts = new Alterations();
			Alteration alt = new Alteration();
			alts.getAlteration().add(alt);
			gene.getContent().add(alts);
			
			JAXBElement<String> eltNameAlt = new JAXBElement<String>(name, String.class, therapy.getVariant());
			alt.getContent().add(eltNameAlt);
			JAXBElement<Boolean> eltIncludeAlt = new JAXBElement<Boolean>(include, Boolean.class, true);
			alt.getContent().add(eltIncludeAlt);
			JAXBElement<String> eltInterpretationAlt = new JAXBElement<String>(name, String.class, therapy.getIndication());
			alt.getContent().add(eltInterpretationAlt);
			
			
			
			
		}
		
		return fr;
	}
	
	private VariantReport buildVariantReport() {
		VariantReport vr = new VariantReport();
		
		return vr;
	}
	
	private String buildPDFHash() {
		return "";
	}

}
