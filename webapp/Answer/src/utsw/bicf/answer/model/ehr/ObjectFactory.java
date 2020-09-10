//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:46:26 PM CST 
//


package utsw.bicf.answer.model.ehr;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the utsw.bicf.answer.model.ehr package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Eligibility_QNAME = new QName("", "Eligibility");
    private final static QName _CountryOfOrigin_QNAME = new QName("", "CountryOfOrigin");
    private final static QName _Include_QNAME = new QName("", "Include");
    private final static QName _Gender_QNAME = new QName("", "Gender");
    private final static QName _TestType_QNAME = new QName("", "TestType");
    private final static QName _Name_QNAME = new QName("", "Name");
    private final static QName _MedFacilID_QNAME = new QName("", "MedFacilID");
    private final static QName _GenericName_QNAME = new QName("", "GenericName");
    private final static QName _OrderingMDId_QNAME = new QName("", "OrderingMDId");
    private final static QName _Version_QNAME = new QName("", "Version");
    private final static QName _ClinicalTrialNote_QNAME = new QName("", "ClinicalTrialNote");
    private final static QName _DOB_QNAME = new QName("", "DOB");
    private final static QName _DemographicCorrectionDate_QNAME = new QName("", "DemographicCorrectionDate");
    private final static QName _BlockId_QNAME = new QName("", "BlockId");
    private final static QName _SampleId_QNAME = new QName("", "SampleId");
    private final static QName _SampleName_QNAME = new QName("", "SampleName");
    private final static QName _Pathologist_QNAME = new QName("", "Pathologist");
    private final static QName _MedFacilName_QNAME = new QName("", "MedFacilName");
    private final static QName _CopiedPhysician1_QNAME = new QName("", "CopiedPhysician1");
    private final static QName _Relavance_QNAME = new QName("", "Relavance");
    private final static QName _Locations_QNAME = new QName("", "Locations");
    private final static QName _Interpretation_QNAME = new QName("", "Interpretation");
    private final static QName _ServerTime_QNAME = new QName("", "ServerTime");
    private final static QName _ApplicationSettings_QNAME = new QName("", "ApplicationSettings");
    private final static QName _ReceivedDate_QNAME = new QName("", "ReceivedDate");
    private final static QName _OpName_QNAME = new QName("", "OpName");
    private final static QName _IncludeInSummary_QNAME = new QName("", "IncludeInSummary");
    private final static QName _TRFNumber_QNAME = new QName("", "TRFNumber");
    private final static QName _Note_QNAME = new QName("", "Note");
    private final static QName _ClinicalTrialSummary_QNAME = new QName("", "ClinicalTrialSummary");
    private final static QName _Value_QNAME = new QName("", "Value");
    private final static QName _Effect_QNAME = new QName("", "Effect");
    private final static QName _IsSigned_QNAME = new QName("", "IsSigned");
    private final static QName _LastName_QNAME = new QName("", "LastName");
    private final static QName _Indication_QNAME = new QName("", "Indication");
    private final static QName _MRN_QNAME = new QName("", "MRN");
    private final static QName _ApprovedUses_QNAME = new QName("", "ApprovedUses");
    private final static QName _FMId_QNAME = new QName("", "FM_Id");
    private final static QName _Target_QNAME = new QName("", "Target");
    private final static QName _ReferenceId_QNAME = new QName("", "ReferenceId");
    private final static QName _SpecFormat_QNAME = new QName("", "SpecFormat");
    private final static QName _Rationale_QNAME = new QName("", "Rationale");
    private final static QName _PertinentNegatives_QNAME = new QName("", "PertinentNegatives");
    private final static QName _Condition_QNAME = new QName("", "Condition");
    private final static QName _Comment_QNAME = new QName("", "Comment");
    private final static QName _ModifiedDts_QNAME = new QName("", "ModifiedDts");
    private final static QName _FirstName_QNAME = new QName("", "FirstName");
    private final static QName _ReportId_QNAME = new QName("", "ReportId");
    private final static QName _Title_QNAME = new QName("", "Title");
    private final static QName _Text_QNAME = new QName("", "Text");
    private final static QName _SubmittedDiagnosis_QNAME = new QName("", "SubmittedDiagnosis");
    private final static QName _SpecSite_QNAME = new QName("", "SpecSite");
    private final static QName _StudyPhase_QNAME = new QName("", "StudyPhase");
    private final static QName _FDAApproved_QNAME = new QName("", "FDAApproved");
    private final static QName _NCTID_QNAME = new QName("", "NCTID");
    private final static QName _Type_QNAME = new QName("", "Type");
    private final static QName _FullName_QNAME = new QName("", "FullName");
    private final static QName _FullCitation_QNAME = new QName("", "FullCitation");
    private final static QName _OrderingMD_QNAME = new QName("", "OrderingMD");
    private final static QName _CollDate_QNAME = new QName("", "CollDate");
    private final static QName _AmendmentTypeIsActive_QNAME = new QName("", "IsActive");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: utsw.bicf.answer.model.ehr
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PriorTest }
     * 
     */
    public PriorTest createPriorTest() {
        return new PriorTest();
    }

    /**
     * Create an instance of {@link PriorGenes }
     * 
     */
    public PriorGenes createPriorGenes() {
        return new PriorGenes();
    }

    /**
     * Create an instance of {@link PriorGene }
     * 
     */
    public PriorGene createPriorGene() {
        return new PriorGene();
    }

    /**
     * Create an instance of {@link PriorAlterations }
     * 
     */
    public PriorAlterations createPriorAlterations() {
        return new PriorAlterations();
    }

    /**
     * Create an instance of {@link PriorAlteration }
     * 
     */
    public PriorAlteration createPriorAlteration() {
        return new PriorAlteration();
    }

    /**
     * Create an instance of {@link AlterationProperties }
     * 
     */
    public AlterationProperties createAlterationProperties() {
        return new AlterationProperties();
    }

    /**
     * Create an instance of {@link AlterationProperty }
     * 
     */
    public AlterationProperty createAlterationProperty() {
        return new AlterationProperty();
    }

    /**
     * Create an instance of {@link Summaries }
     * 
     */
    public Summaries createSummaries() {
        return new Summaries();
    }

    /**
     * Create an instance of {@link Alterations }
     * 
     */
    public Alterations createAlterations() {
        return new Alterations();
    }

    /**
     * Create an instance of {@link Alteration }
     * 
     */
    public Alteration createAlteration() {
        return new Alteration();
    }

    /**
     * Create an instance of {@link Therapies }
     * 
     */
    public Therapies createTherapies() {
        return new Therapies();
    }

    /**
     * Create an instance of {@link Therapy }
     * 
     */
    public Therapy createTherapy() {
        return new Therapy();
    }

    /**
     * Create an instance of {@link ReferenceLinks }
     * 
     */
    public ReferenceLinks createReferenceLinks() {
        return new ReferenceLinks();
    }

    /**
     * Create an instance of {@link ReferenceLink }
     * 
     */
    public ReferenceLink createReferenceLink() {
        return new ReferenceLink();
    }

    /**
     * Create an instance of {@link ClinicalTrialLinks }
     * 
     */
    public ClinicalTrialLinks createClinicalTrialLinks() {
        return new ClinicalTrialLinks();
    }

    /**
     * Create an instance of {@link ClinicalTrialLink }
     * 
     */
    public ClinicalTrialLink createClinicalTrialLink() {
        return new ClinicalTrialLink();
    }

    /**
     * Create an instance of {@link Gene }
     * 
     */
    public Gene createGene() {
        return new Gene();
    }

    /**
     * Create an instance of {@link FinalReport }
     * 
     */
    public FinalReport createFinalReport() {
        return new FinalReport();
    }

    /**
     * Create an instance of {@link Application }
     * 
     */
    public Application createApplication() {
        return new Application();
    }

    /**
     * Create an instance of {@link ApplicationSettings }
     * 
     */
    public ApplicationSettings createApplicationSettings() {
        return new ApplicationSettings();
    }

    /**
     * Create an instance of {@link Sample }
     * 
     */
    public Sample createSample() {
        return new Sample();
    }

    /**
     * Create an instance of {@link ProcessSites }
     * 
     */
    public ProcessSites createProcessSites() {
        return new ProcessSites();
    }

    /**
     * Create an instance of {@link PMI }
     * 
     */
    public PMI createPMI() {
        return new PMI();
    }

    /**
     * Create an instance of {@link PertinentNegatives }
     * 
     */
    public PertinentNegatives createPertinentNegatives() {
        return new PertinentNegatives();
    }

    /**
     * Create an instance of {@link VariantProperties }
     * 
     */
    public VariantProperties createVariantProperties() {
        return new VariantProperties();
    }

    /**
     * Create an instance of {@link VariantProperty }
     * 
     */
    public VariantProperty createVariantProperty() {
        return new VariantProperty();
    }

    /**
     * Create an instance of {@link Genes }
     * 
     */
    public Genes createGenes() {
        return new Genes();
    }

    /**
     * Create an instance of {@link Trials }
     * 
     */
    public Trials createTrials() {
        return new Trials();
    }

    /**
     * Create an instance of {@link Trial }
     * 
     */
    public Trial createTrial() {
        return new Trial();
    }

    /**
     * Create an instance of {@link Summary }
     * 
     */
    public Summary createSummary() {
        return new Summary();
    }

    /**
     * Create an instance of {@link References }
     * 
     */
    public References createReferences() {
        return new References();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link Signatures }
     * 
     */
    public Signatures createSignatures() {
        return new Signatures();
    }

    /**
     * Create an instance of {@link Signature }
     * 
     */
    public Signature createSignature() {
        return new Signature();
    }

    /**
     * Create an instance of {@link AAC }
     * 
     */
    public AAC createAAC() {
        return new AAC();
    }

    /**
     * Create an instance of {@link Amendmends }
     * 
     */
    public Amendmends createAmendmends() {
        return new Amendmends();
    }

    /**
     * Create an instance of {@link Amendmend }
     * 
     */
    public Amendmend createAmendmend() {
        return new Amendmend();
    }

    /**
     * Create an instance of {@link Amendment }
     * 
     */
    public Amendment createAmendment() {
        return new Amendment();
    }

    /**
     * Create an instance of {@link AmendmentType }
     * 
     */
    public AmendmentType createAmendmentType() {
        return new AmendmentType();
    }

    /**
     * Create an instance of {@link PriorTests }
     * 
     */
    public PriorTests createPriorTests() {
        return new PriorTests();
    }

    /**
     * Create an instance of {@link ReportProperties }
     * 
     */
    public ReportProperties createReportProperties() {
        return new ReportProperties();
    }

    /**
     * Create an instance of {@link Comments }
     * 
     */
    public Comments createComments() {
        return new Comments();
    }

    /**
     * Create an instance of {@link PertinentNegative }
     * 
     */
    public PertinentNegative createPertinentNegative() {
        return new PertinentNegative();
    }

    /**
     * Create an instance of {@link ApplicationSetting }
     * 
     */
    public ApplicationSetting createApplicationSetting() {
        return new ApplicationSetting();
    }

    /**
     * Create an instance of {@link ProcessSite }
     * 
     */
    public ProcessSite createProcessSite() {
        return new ProcessSite();
    }

    /**
     * Create an instance of {@link Comment }
     * 
     */
    public Comment createComment() {
        return new Comment();
    }

    /**
     * Create an instance of {@link ReportProperty }
     * 
     */
    public ReportProperty createReportProperty() {
        return new ReportProperty();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Eligibility")
    public JAXBElement<String> createEligibility(String value) {
        return new JAXBElement<String>(_Eligibility_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CountryOfOrigin")
    public JAXBElement<String> createCountryOfOrigin(String value) {
        return new JAXBElement<String>(_CountryOfOrigin_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Include")
    public JAXBElement<Boolean> createInclude(Boolean value) {
        return new JAXBElement<Boolean>(_Include_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Gender")
    public JAXBElement<String> createGender(String value) {
        return new JAXBElement<String>(_Gender_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TestType")
    public JAXBElement<String> createTestType(String value) {
        return new JAXBElement<String>(_TestType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MedFacilID")
    public JAXBElement<String> createMedFacilID(String value) {
        return new JAXBElement<String>(_MedFacilID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "GenericName")
    public JAXBElement<String> createGenericName(String value) {
        return new JAXBElement<String>(_GenericName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "OrderingMDId")
    public JAXBElement<String> createOrderingMDId(String value) {
        return new JAXBElement<String>(_OrderingMDId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Version")
    public JAXBElement<BigInteger> createVersion(BigInteger value) {
        return new JAXBElement<BigInteger>(_Version_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ClinicalTrialNote")
    public JAXBElement<String> createClinicalTrialNote(String value) {
        return new JAXBElement<String>(_ClinicalTrialNote_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DOB")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createDOB(String value) {
        return new JAXBElement<String>(_DOB_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DemographicCorrectionDate")
    public JAXBElement<String> createDemographicCorrectionDate(String value) {
        return new JAXBElement<String>(_DemographicCorrectionDate_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "BlockId")
    public JAXBElement<String> createBlockId(String value) {
        return new JAXBElement<String>(_BlockId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SampleId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createSampleId(String value) {
        return new JAXBElement<String>(_SampleId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SampleName")
    public JAXBElement<String> createSampleName(String value) {
        return new JAXBElement<String>(_SampleName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Pathologist")
    public JAXBElement<String> createPathologist(String value) {
        return new JAXBElement<String>(_Pathologist_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MedFacilName")
    public JAXBElement<String> createMedFacilName(String value) {
        return new JAXBElement<String>(_MedFacilName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CopiedPhysician1")
    public JAXBElement<String> createCopiedPhysician1(String value) {
        return new JAXBElement<String>(_CopiedPhysician1_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Relavance")
    public JAXBElement<String> createRelavance(String value) {
        return new JAXBElement<String>(_Relavance_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Locations")
    public JAXBElement<String> createLocations(String value) {
        return new JAXBElement<String>(_Locations_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Interpretation")
    public JAXBElement<String> createInterpretation(String value) {
        return new JAXBElement<String>(_Interpretation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ServerTime")
    public JAXBElement<String> createServerTime(String value) {
        return new JAXBElement<String>(_ServerTime_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApplicationSettings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ApplicationSettings")
    public JAXBElement<ApplicationSettings> createApplicationSettings(ApplicationSettings value) {
        return new JAXBElement<ApplicationSettings>(_ApplicationSettings_QNAME, ApplicationSettings.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ReceivedDate")
    public JAXBElement<XMLGregorianCalendar> createReceivedDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ReceivedDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "OpName")
    public JAXBElement<String> createOpName(String value) {
        return new JAXBElement<String>(_OpName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IncludeInSummary")
    public JAXBElement<Boolean> createIncludeInSummary(Boolean value) {
        return new JAXBElement<Boolean>(_IncludeInSummary_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TRFNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTRFNumber(String value) {
        return new JAXBElement<String>(_TRFNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Note")
    public JAXBElement<String> createNote(String value) {
        return new JAXBElement<String>(_Note_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ClinicalTrialSummary")
    public JAXBElement<String> createClinicalTrialSummary(String value) {
        return new JAXBElement<String>(_ClinicalTrialSummary_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Value")
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Effect")
    public JAXBElement<String> createEffect(String value) {
        return new JAXBElement<String>(_Effect_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IsSigned")
    public JAXBElement<Boolean> createIsSigned(Boolean value) {
        return new JAXBElement<Boolean>(_IsSigned_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "LastName")
    public JAXBElement<String> createLastName(String value) {
        return new JAXBElement<String>(_LastName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Indication")
    public JAXBElement<String> createIndication(String value) {
        return new JAXBElement<String>(_Indication_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MRN")
    public JAXBElement<String> createMRN(String value) {
        return new JAXBElement<String>(_MRN_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ApprovedUses")
    public JAXBElement<String> createApprovedUses(String value) {
        return new JAXBElement<String>(_ApprovedUses_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FM_Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFMId(String value) {
        return new JAXBElement<String>(_FMId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Target")
    public JAXBElement<String> createTarget(String value) {
        return new JAXBElement<String>(_Target_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ReferenceId")
    public JAXBElement<String> createReferenceId(String value) {
        return new JAXBElement<String>(_ReferenceId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SpecFormat")
    public JAXBElement<String> createSpecFormat(String value) {
        return new JAXBElement<String>(_SpecFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Rationale")
    public JAXBElement<String> createRationale(String value) {
        return new JAXBElement<String>(_Rationale_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PertinentNegatives }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PertinentNegatives")
    public JAXBElement<PertinentNegatives> createPertinentNegatives(PertinentNegatives value) {
        return new JAXBElement<PertinentNegatives>(_PertinentNegatives_QNAME, PertinentNegatives.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Condition")
    public JAXBElement<String> createCondition(String value) {
        return new JAXBElement<String>(_Condition_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Comment")
    public JAXBElement<Object> createComment(Object value) {
        return new JAXBElement<Object>(_Comment_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ModifiedDts")
    public JAXBElement<String> createModifiedDts(String value) {
        return new JAXBElement<String>(_ModifiedDts_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FirstName")
    public JAXBElement<String> createFirstName(String value) {
        return new JAXBElement<String>(_FirstName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ReportId")
    public JAXBElement<String> createReportId(String value) {
        return new JAXBElement<String>(_ReportId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Title")
    public JAXBElement<String> createTitle(String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Text")
    public JAXBElement<String> createText(String value) {
        return new JAXBElement<String>(_Text_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SubmittedDiagnosis")
    public JAXBElement<String> createSubmittedDiagnosis(String value) {
        return new JAXBElement<String>(_SubmittedDiagnosis_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SpecSite")
    public JAXBElement<String> createSpecSite(String value) {
        return new JAXBElement<String>(_SpecSite_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "StudyPhase")
    public JAXBElement<String> createStudyPhase(String value) {
        return new JAXBElement<String>(_StudyPhase_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FDAApproved")
    public JAXBElement<Boolean> createFDAApproved(Boolean value) {
        return new JAXBElement<Boolean>(_FDAApproved_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NCTID")
    public JAXBElement<String> createNCTID(String value) {
        return new JAXBElement<String>(_NCTID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Type")
    public JAXBElement<String> createType(String value) {
        return new JAXBElement<String>(_Type_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FullName")
    public JAXBElement<String> createFullName(String value) {
        return new JAXBElement<String>(_FullName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FullCitation")
    public JAXBElement<String> createFullCitation(String value) {
        return new JAXBElement<String>(_FullCitation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "OrderingMD")
    public JAXBElement<String> createOrderingMD(String value) {
        return new JAXBElement<String>(_OrderingMD_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CollDate")
    public JAXBElement<XMLGregorianCalendar> createCollDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_CollDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IsActive", scope = AmendmentType.class)
    public JAXBElement<Boolean> createAmendmentTypeIsActive(Boolean value) {
        return new JAXBElement<Boolean>(_AmendmentTypeIsActive_QNAME, Boolean.class, AmendmentType.class, value);
    }

}