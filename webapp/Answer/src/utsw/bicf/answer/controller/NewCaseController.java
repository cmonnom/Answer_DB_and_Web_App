package utsw.bicf.answer.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import utsw.bicf.answer.clarity.api.model.ClarityProject;
import utsw.bicf.answer.clarity.api.model.ClarityProjects;
import utsw.bicf.answer.clarity.api.utils.RequestUtils;
import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.AnnotationSummary;
import utsw.bicf.answer.controller.serialization.vuetify.MDAReportTableSummary;
import utsw.bicf.answer.dao.CaseDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.Annotation;
import utsw.bicf.answer.model.CurrentCaseUser;
import utsw.bicf.answer.model.FreeText;
import utsw.bicf.answer.model.Gene;
import utsw.bicf.answer.model.MDAEmail;
import utsw.bicf.answer.model.OrderCase;
import utsw.bicf.answer.model.Patient;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantSelected;
import utsw.bicf.answer.model.hybrid.MDAFileImportedResponse;
import utsw.bicf.answer.reporting.finalreport.GeneVariantDetails;
import utsw.bicf.answer.reporting.finalreport.TreatmentOption;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.ClarityAPIAuthentication;
import utsw.bicf.answer.security.MDAFileProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class NewCaseController {

	@Autowired
	MDAFileProperties mdaProps;
	@Autowired
	private ClarityAPIAuthentication clarityAuth;
	@Autowired
	private RequestUtils requestUtils;

	static {
		PermissionUtils.permissionPerUrl.put("newCase", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("importMDAFile", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getLocalAnnotations", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("commitAnnotations", new PermissionUtils(true, true, false));
		PermissionUtils.permissionPerUrl.put("saveCase", new PermissionUtils(true, true, false));
		PermissionUtils.permissionPerUrl.put("finalizeCase", new PermissionUtils(true, true, true));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	CaseDAO caseDAO;

	@RequestMapping("/newCase")
	public String newCase(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "newCase");
		return ControllerUtil.initializeModel(model, servletContext);
	}

	@RequestMapping(value = "/importMDAFile", method = { RequestMethod.POST })
	@ResponseBody
	public String importMDAFile(Model model, HttpSession session, @RequestParam("file") MultipartFile mdaFile)
			throws Exception {

		MDAFileImportedResponse response = new MDAFileImportedResponse();
		if (mdaFile != null && !mdaFile.isEmpty()) {
			byte[] bytes = mdaFile.getBytes();
			File outputDir = mdaProps.getMdaFilesDir();
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			String fileName = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "_" + mdaFile.getOriginalFilename();
			File mdaFileOutput = new File(outputDir, fileName);

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(mdaFileOutput));
			stream.write(bytes);
			stream.close();

			response.setCurrentFile(fileName);

			MDAEmail email = caseDAO.createMDAEmail(mdaFileOutput);

			// parse mda file
			MDAReportTemplate report = new MDAReportTemplate(mdaFileOutput);
			// figure out if case already exist
			if (report != null) {
				User user = (User) session.getAttribute("user");
				String mrn = report.getMrn();
				if (mrn != null) {
					PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
					String cleanMRN = policy.sanitize(mrn);
					if (mrn.equals(cleanMRN)) {

						Patient patient = getProjectDetailsFromClarity(cleanMRN);
						if (patient != null) {
							response.setPatient(patient);
							OrderCase aCase = caseDAO.getCaseByPatientMRN(cleanMRN);
							CurrentCaseUser currentCase = caseDAO.getCurrentCaseUserByUser(user); //make sure user is not working on another case
							
							if (aCase == null) {
								aCase = new OrderCase();
								aCase.setCreatedBy(user);
								aCase.setDateCreated(LocalDateTime.now());
								aCase.setInUse(true);
								aCase.setMdaEmail(email);
								aCase.setPatientMrn(cleanMRN);
								// create a new case opened for this user
								
								if (currentCase != null && !currentCase.getOrderCase().getCaseId().equals(aCase.getCaseId())) {
									//the user is working on another case
									response.setSuccess(false);
									response.setMessage("Please finalize the other case you are working on first");
									return response.createObjectJSON();
								}
								currentCase = new CurrentCaseUser();
								currentCase.setOrderCase(aCase);
								currentCase.setUser(user);
							} else { // replace existing email for now. TODO need to deal with warning the user
										// open a current case user
								currentCase = caseDAO.getCurrentCaseUserByUserAndCase(aCase, user);
								MDAEmail emailToDelete = aCase.getMdaEmail();
								aCase.setMdaEmail(email);
								modelDAO.saveObject(aCase);
								modelDAO.deleteObject(emailToDelete);
							}
							modelDAO.saveObject(aCase);
							modelDAO.saveObject(currentCase);
							response.setCaseId(aCase.getCaseId());

							// get table data
							List<TreatmentOption> treatmentOptions = TreatmentOption.createFromDMAReport(report);
							Map<String, GeneVariantDetails> geneVariantDetails = GeneVariantDetails
									.createFromMDAReportasMap(report);
							MDAReportTableSummary summary = new MDAReportTableSummary(modelDAO, aCase, treatmentOptions,
									geneVariantDetails, "uniqueIdField");
							response.setData(summary);
							response.setSuccess(true);

						} else {
							response.setSuccess(false);
							response.setMessage("Patient not found for MRN: " + cleanMRN);

						}
					} else {
						response.setSuccess(false);
						response.setMessage("The MRN format is invalid");
					}
				} else {
					response.setSuccess(false);
					response.setMessage("The MRN was not found in the MDA Report");
				}

				// if (aCase == null) {
				// aCase = new Case();
				// aCase.setPatientMrn(mrn);
				// aCase.setCreatedBy(user);
				// }
				// aCase.setMdaEmail(mdaEmail);
				// aCase.setInUse(true);
				// aCase.setLastModifiedBy(user);
			}

		} else {
			response.setSuccess(false);
			response.setMessage("Something is wrong with the MDA Report");
		}
		return response.createObjectJSON();
	}

	private Patient getProjectDetailsFromClarity(String mrn) throws Exception {
		// retrieve project details
		ObjectMapper xmlMapper = new XmlMapper();
		String param = URLEncoder.encode("udf.Medical record number", StandardCharsets.UTF_8.toString());
		URI uri = new URL(clarityAuth.getUrl() + "projects?" + param + "=" + mrn).toURI();
		HttpGet requestGet = new HttpGet(uri);
		requestUtils.addAuthenticationHeader(requestGet);
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse apiResponse = client.execute(requestGet);

		int statusCode = apiResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String projectXml = EntityUtils.toString(apiResponse.getEntity(), "UTF-8");
			ClarityProjects projects = xmlMapper.readValue(projectXml, ClarityProjects.class);
			// process the current page
			for (ClarityProject project : projects.getProjects()) {
				ClarityProject clarityProject = requestUtils.getProjectByLimsId(project.getLimsid());
				Patient patient = new Patient();
				// patient.setClinicalStage(clinicalStage);
				patient.setDateOfBirth(LocalDate.parse(clarityProject.getDateOfBirth(), TypeUtils.monthFormatter));
				String patientName = clarityProject.getPatientName();
				if (patientName != null) {
					String[] items = patientName.split(",");
					patient.setLastName(items[0]);
					if (items.length > 1) {
						patient.setFirstName(items[1]);
					}
				}
				// patient.setGermlineSpecimenNb(clarityProject);
				patient.setGermlineTissue(clarityProject.getNormalTissueType());
				patient.setIcd10(clarityProject.getICD10());
				patient.setInstitution(clarityProject.getReferringInstitution());
				patient.setLabAccessionNb(clarityProject.getName());
				// patient.setLabReceivedDate(clarityProject);
				patient.setMRN(clarityProject.getMRN());
				patient.setOrderDate(LocalDate.parse(clarityProject.getEpicOrderDate(), TypeUtils.monthFormatter));
				// patient.setOrderedBy(clarityProject);
				patient.setOrderNb(clarityProject.getEpicOrderNb());
				// patient.setReportAccessionNb(clarityProject); //could be a field from when
				// the report is generated
				// patient.setReportDate(clarityProject); //could be a field from when the
				// report is generated
				// patient.setReportSignedBy(clarityProject); //could be a field from when the
				// report is generated
				patient.setSex(clarityProject.getGender());
				patient.setTreatmentStatus(clarityProject.getPatientStatus());
				patient.setTumorCollectionDate(
						LocalDate.parse(clarityProject.getTumorCollectionDate(), TypeUtils.monthFormatter));
				// patient.setTumorSpecimenNb(clarityProject);
				patient.setTumorTissue(clarityProject.getTumorTissueType());

				return patient;
			}
		}
		return null;

	}

	@RequestMapping(value = "/getLocalAnnotations")
	@ResponseBody
	public String getLocalAnnotations(Model model, HttpSession session,
			@RequestParam(defaultValue = "") String geneName, @RequestParam(defaultValue = "") Integer geneId)
			throws Exception {

		User user = (User) session.getAttribute("user");
		Gene gene = null;
		if (!geneName.equals("")) {
			gene = modelDAO.getGeneBySymbol(geneName);
		} else if (geneId != null) {
			gene = modelDAO.getObject(Gene.class, geneId);
		}
		if (gene == null) {
			gene = new Gene();
			gene.setSymbol(geneName);
			modelDAO.saveObject(gene);
		}
		List<Annotation> utswAnnotations = modelDAO.getAnnotationsForOrganisationAndUser("UTSW", null, gene);
		List<Annotation> userAnnotations = modelDAO.getAnnotationsForOrganisationAndUser("UTSW", user, gene);

		AnnotationSummary summary = new AnnotationSummary(utswAnnotations, userAnnotations);

		return summary.createVuetifyObjectJSON();
	}

	@RequestMapping(value = "/commitAnnotations")
	@ResponseBody
	public String commitAnnotations(Model model, HttpSession session, @RequestParam String annotations,
			@RequestParam Integer geneId) throws Exception {
		AjaxResponse response = new AjaxResponse();
		User user = (User) session.getAttribute("user");
		// TODO verify that the user owns the annotation first.
		// Basically if the annotation id provided is not in originalAnnoations
		// then something is not right.
		Gene gene = modelDAO.getObject(Gene.class, geneId);
		List<Annotation> originalAnnotations = modelDAO.getAnnotationsForOrganisationAndUser("UTSW", user, gene);

		ObjectMapper mapper = new ObjectMapper();
		if (annotations != null && !annotations.equals("")) {
			JsonNode jsonNodeArray = mapper.readTree(annotations);
			if (jsonNodeArray != null) {
				for (JsonNode annotationNode : jsonNodeArray) {
					int annotationId = annotationNode.get("annotationId").intValue();
					Boolean markedForDeletion = annotationNode.get("markedForDeletion").booleanValue();
					// check that the id is in originalAnnotations
					if (annotationId > -1) { // place holder for new annotations
						Annotation currentAnnotation = null;
						for (Annotation a : originalAnnotations) {
							if (a.getAnnotationId() == annotationId) {
								currentAnnotation = a;
								break;
							}
						}
						if (currentAnnotation != null) {
							if (markedForDeletion) { // delete
								currentAnnotation.setDeleted(true);
							} else { // save changes
								String text = annotationNode.get("text").textValue();
								currentAnnotation.setModifiedDate(LocalDateTime.now());
								currentAnnotation.setText(text);
							}
							modelDAO.saveObject(currentAnnotation);
						} else {
							// TODO issue here. Hack?
							response.setSuccess(false);
							response.setMessage("Corrupt Data. Save failed");
						}
					} else if (!markedForDeletion) { // new annotation but careful as users could have marked it for
														// deletion
						Annotation currentAnnotation = new Annotation();
						String text = annotationNode.get("text").textValue();
						currentAnnotation.setCreatedDate(LocalDateTime.now());
						currentAnnotation.setModifiedDate(LocalDateTime.now());
						currentAnnotation.setGene(gene);
						currentAnnotation.setOrigin("UTSW");
						currentAnnotation.setText(text);
						currentAnnotation.setUser(user);
						currentAnnotation.setDeleted(false);
						modelDAO.saveObject(currentAnnotation);
						response.setSuccess(true);
					}
				}
			}
		}

		return response.createObjectJSON();
	}

	@RequestMapping(value = "/saveCase")
	@ResponseBody
	public String saveCase(Model model, HttpSession session, @RequestParam String selectedVariants, @RequestParam String toUnselect,
			@RequestParam String curatorComments) throws Exception {

		AjaxResponse response = new AjaxResponse();
		
		User user = (User) session.getAttribute("user");

		CurrentCaseUser currentCase = caseDAO.getCurrentCaseUserByUser(user); //only one user per case.
		if (currentCase != null) { // TODO deal with no case opened for this user
			LocalDateTime now = LocalDateTime.now();
			OrderCase orderCase = currentCase.getOrderCase();
			if (orderCase.getReadyForReview() != null && orderCase.getReadyForReview() == false) {
				response.setMessage("This case is under review, you cannot make changes.");
				response.setSuccess(false);
				return response.createObjectJSON();
			}

			FreeText comment = new FreeText();
			comment.setContent(curatorComments);
			comment.setCreatedBy(user);
			comment.setDateCreated(now);

			orderCase.setCuratorComment(comment);
			orderCase.setDateModified(now);
			orderCase.setInUse(true);
			orderCase.setLastModifiedBy(user);

			// create variant selected entries if they don't already exist
			if (!selectedVariants.equals("")) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNodeArray = mapper.readTree(selectedVariants);
				if (jsonNodeArray != null) {
					for (JsonNode variantNode : jsonNodeArray) {
						String gene = variantNode.get("gene").textValue();
						String variant = variantNode.get("aberration").textValue();
						VariantSelected variantSelected = modelDAO.getVariantSelectedByGeneVariantAndCase(gene + variant,
								orderCase);
						if (variantSelected == null) {
							variantSelected = new VariantSelected();
							variantSelected.setGeneAndVariant(gene + variant);
							variantSelected.setOrderCase(orderCase);
							modelDAO.saveObject(variantSelected);
						}
					}
				}
				//remove unselected ones if exist
				jsonNodeArray = mapper.readTree(toUnselect);
				if (jsonNodeArray != null) {
					for (JsonNode variantNode : jsonNodeArray) {
						String gene = variantNode.get("gene").textValue();
						String variant = variantNode.get("aberration").textValue();
						VariantSelected variantSelected = modelDAO.getVariantSelectedByGeneVariantAndCase(gene + variant,
								orderCase);
						if (variantSelected != null) {
							modelDAO.deleteObject(variantSelected);
						}
					}
				}
			}
			response.setSuccess(true);
			response.setMessage("Case Saved Successfully");
		}
		else {
			response.setSuccess(false);
			response.setMessage("No case opened. Import a MD Anderson file before saving");
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/finalizeCase")
	@ResponseBody
	public String finalizeCase(Model model, HttpSession session, @RequestParam String selectedVariants, @RequestParam String toUnselect,
			@RequestParam String curatorComments) throws Exception {
		//save last minute changes
		saveCase(model, session, selectedVariants, toUnselect, curatorComments);
		AjaxResponse response = new AjaxResponse();
		User user = (User) session.getAttribute("user");

		CurrentCaseUser currentCase = caseDAO.getCurrentCaseUserByUser(user);
		if (currentCase != null) { // TODO deal with no case opened for this user
			OrderCase orderCase = currentCase.getOrderCase();
			orderCase.setReadyForReview(true);
			orderCase.setInUse(false);
		}
		response.setMessage("Case is ready for review.");
		response.setSuccess(true);
		return response.createObjectJSON();
		
	}
}
