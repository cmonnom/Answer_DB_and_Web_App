package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microsoft.azure.storage.StorageException;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.IGVPayload;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.igv.Global;
import utsw.bicf.answer.igv.JNLPTemplate;
import utsw.bicf.answer.igv.Resource;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CloudBams;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.AzureOAuth;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class BamViewerController {

	static {
		PermissionUtils.addPermission(BamViewerController.class.getCanonicalName() + ".bamViewer", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(BamViewerController.class.getCanonicalName() + ".getBams", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(BamViewerController.class.getCanonicalName() + ".downloadLocalIGVFile", IndividualPermission.CAN_VIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;
	@Autowired
	AzureOAuth azureProps;

	@RequestMapping("/bamViewer")
	public String bamViewer(Model model, HttpSession session, HttpServletRequest request, @RequestParam String locus, @RequestParam String caseId)
			throws Exception {
		model.addAttribute("urlRedirect", "bamViewer?locus=" + locus + "%26caseId=" + caseId);
		model.addAttribute("locus", locus);
		model.addAttribute("caseId", caseId);
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (user == null) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}
		if (caseSummary == null) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}
		// At this point everything should be granted to access the bams
		AjaxResponse response = populateModel(model, utils, caseSummary, caseId);
		if (!response.getSuccess()) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}

		return ControllerUtil.initializeExternalModel(model, servletContext, user, "bam-viewer");

	}

	private AjaxResponse populateModel(Model model, RequestUtils utils, OrderCase caseSummary, String caseId) throws IOException {
		//check if local storage or cloud
		model.addAttribute("storageType", caseSummary.getStorageType());
		AjaxResponse response = new AjaxResponse();
		if ("azure".equals(caseSummary.getStorageType())) {
			response = utils.getAzureBams(caseSummary, azureProps);
			if (!response.getSuccess()) {
				return response;
			}
			ObjectMapper mapper = new ObjectMapper();
			CloudBams bams = mapper.convertValue(response.getPayload(), CloudBams.class);
			if (bams != null) {
				String normalBam = bams.getNormalBam();
				String tumorBam = bams.getTumorBam();
				String rnaBam = bams.getRnaBam();

				if (normalBam != null) {
					model.addAttribute("normalBam", normalBam);
					model.addAttribute("normalBai", bams.getNormalBai());
					model.addAttribute("normalLabel", caseSummary.getNormalBam());
				}

				if (tumorBam != null) {
					model.addAttribute("tumorBam", tumorBam);
					model.addAttribute("tumorBai", bams.getTumorBai());
					model.addAttribute("tumorLabel", caseSummary.getTumorBam());
				}

				if (rnaBam != null) {
					model.addAttribute("rnaBam", rnaBam);
					model.addAttribute("rnaBai", bams.getRnaBai());
					model.addAttribute("rnaLabel", caseSummary.getRnaBam());
				}
			}

		}
		else { //local storage
			String normalBam = caseSummary.getNormalBam();
			String tumorBam = caseSummary.getTumorBam();
			String rnaBam = caseSummary.getRnaBam();
//			normalBam = "SHI710-27-6271_T_DNA_panel1385v2-1.final.bam"; //TODO delete this


			if (normalBam != null) {
				String bamLink = createBamLink(fileProps, normalBam);
				model.addAttribute("normalBam", bamLink);
				model.addAttribute("normalBai", createIndexLink(fileProps, normalBam.replaceAll(".bam", ".bai"), bamLink + ".bai"));
				model.addAttribute("normalLabel", normalBam);
			}

			if (tumorBam != null) {
				String bamLink = createBamLink(fileProps, tumorBam);
				model.addAttribute("tumorBam", bamLink);
				model.addAttribute("tumorBai", createIndexLink(fileProps, tumorBam.replaceAll(".bam", ".bai"), bamLink + ".bai"));
				model.addAttribute("tumorLabel", tumorBam);
			}

			if (rnaBam != null) {
				String bamLink = createBamLink(fileProps, rnaBam);
				model.addAttribute("rnaBam", bamLink);
				model.addAttribute("rnaBai", createIndexLink(fileProps, rnaBam.replaceAll(".bam", ".bai"), bamLink + ".bai"));
				model.addAttribute("rnaLabel", rnaBam);
			}

		}
		response.setIsAllowed(true);
		response.setSuccess(true);
		return response;
	}

	@RequestMapping(value = "/downloadLocalIGVFile", produces= "application/json; charset=utf-8", method= RequestMethod.GET)
	@ResponseBody
	public String downloadLocalIGVFile(Model model, HttpSession session, HttpServletRequest request, @RequestParam String caseId,
			@RequestParam String locus, @RequestParam String type) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		AjaxResponse response = new AjaxResponse();
		if (caseSummary == null) {
			response.setIsAllowed(true);
			response.setSuccess(false);
			response.setMessage("No case with id: " + caseId);
			return response.createObjectJSON();
		}
		// At this point everything should be granted to access the bams
		response = populateModel(model, utils, caseSummary, caseId);
		response.setIsAllowed(true);
		response.setSuccess(true);

		//create a session file and jnlp file to launch IGV locally
		List<Resource> resources = new ArrayList<Resource>();
		String urlRoot = otherProps.getRootUrl() + otherProps.getWebappName() + "/";
		String bamsRoot = ("azure".equals(caseSummary.getStorageType()) ? "" : urlRoot + "bams/");
		Map<String, Object> attributes = model.asMap();

		Object normalLabel = attributes.get("normalLabel");
		Object normalBam = attributes.get("normalBam");
		Object normalBai = attributes.get("normalBai");
		if (normalLabel != null && normalBam != null) {
			Resource normalBamResource = new Resource(normalLabel.toString(), bamsRoot + normalBam.toString(), bamsRoot + normalBai.toString());
			resources.add(normalBamResource);
		}
		Object tumorLabel = attributes.get("tumorLabel");
		Object tumorBam = attributes.get("tumorBam");
		Object tumorBai = attributes.get("tumorBai");
		if (tumorLabel != null && tumorBam != null) {
			Resource tumorBamResource = new Resource(tumorLabel.toString(), bamsRoot + tumorBam.toString(), bamsRoot + tumorBai.toString());
			resources.add(tumorBamResource);
		}
		Object rnaLabel = attributes.get("rnaLabel");
		Object rnaBam = attributes.get("rnaBam");
		Object rnaBai = attributes.get("rnaBai");
		if (rnaLabel != null && rnaBam != null) {
			Resource rnaBamResource = new Resource(rnaLabel.toString(), bamsRoot + rnaBam.toString(), bamsRoot + rnaBai.toString());
			resources.add(rnaBamResource);
		}
		Resource rnaBamResource = new Resource("Gencode v24", "https://s3.amazonaws.com/igv.broadinstitute.org/data/hg38/gencode.v24.annotation.sorted.gtf.gz", null);
		resources.add(rnaBamResource);
		Global igvSession = new Global(locus, resources);
		XmlMapper mapper = new XmlMapper();
		File igvSessionFile = createIGVSessionFile(fileProps);
		mapper.writer().withRootName("Global").writeValue(igvSessionFile, igvSession);
		String sessionUrl = urlRoot + "/igv/" + igvSessionFile.getName();
		if (type.equals("jnlp")) {
			JNLPTemplate template = new JNLPTemplate(sessionUrl, fileProps, sessionUrl);
			response.setPayload(template.getTemplateOutput().getName());
		}
		else if (type.equals("sessionLink")) {
			IGVPayload payload = new IGVPayload(type, sessionUrl);
			response.setPayload(payload);
		}
		else {
			response.setPayload(igvSessionFile.getName());
		}
		
		return response.createObjectJSON(); 
	}

	private static String createBamLink(FileProperties fileProps, String targetName) throws IOException {
		File target = new File(fileProps.getBamFilesDir(), targetName);
		if (!target.exists()) {
			return null;
		}
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".bam";
		File link = new File(fileProps.getBamLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}

	private static File createIGVSessionFile(FileProperties fileProps) throws IOException {
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".xml";
		File file = new File(fileProps.getIgvLinksDir(), linkName);
		return file;
	}

	/**
	 * Create a link for the index file based on the name of the bam link
	 * @param fileProps
	 * @param targetName
	 * @param linkName
	 * @return
	 * @throws IOException
	 */
	private static String createIndexLink(FileProperties fileProps, String targetName, String linkName) throws IOException {
		File target = new File(fileProps.getBamFilesDir(), targetName);
		//		System.out.println("link target " + target.getAbsolutePath() + " linkName: " + linkName + " exists? " + target.exists() );
		if (!target.exists()) {
			return null;
		}
		File link = new File(fileProps.getBamLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}
}
