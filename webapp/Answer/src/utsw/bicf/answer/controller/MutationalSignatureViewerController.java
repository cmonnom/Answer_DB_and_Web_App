package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class MutationalSignatureViewerController {

	static {
		PermissionUtils.addPermission(MutationalSignatureViewerController.class.getCanonicalName() + ".mutationalSignatureViewer", IndividualPermission.CAN_VIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/mutationalSignatureViewer")
	public String mutationalSignatureViewer(Model model, HttpSession session, @RequestParam String caseId,
			HttpServletRequest request)
			throws IOException, URISyntaxException {
		model.addAttribute("urlRedirect", "mutationalSignatureViewer?caseId=" + caseId);
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
		// At this point everything should be granted to access the vcfs
		
		//check if local storage or cloud
		model.addAttribute("storageType", caseSummary.getStorageType());
		String tumorVcf = null;
		if ("azure".equals(caseSummary.getStorageType())) {
			AjaxResponse response = utils.getAzureBams(caseId);
			if (!response.getSuccess()) {
				return ControllerUtil.initializeModelError(model, servletContext);
			}
			URL vcfUrl = new URL(response.getPayload().toString());
			
			String random = RandomStringUtils.random(25, true, true);
			String destinationName = random + ".vcf";
			FileUtils.copyURLToFile(vcfUrl, new File(fileProps.getVcfLinksDir(), destinationName));
			
			model.addAttribute("tumorVcf", destinationName);

		}
		else { //local storage
			tumorVcf = caseSummary.getTumorVcf();
//			tumorVcf = "GM12878.vcf";//TODO delete this
			String vcfLink = createVcfLink(fileProps, tumorVcf);
			model.addAttribute("tumorVcf", vcfLink);
		}
		

		if (tumorVcf != null) {
			model.addAttribute("sampleName", tumorVcf);
			model.addAttribute("mutationalSignatureUrl", otherProps.getMutationalSignatureUrl());
		}

		return ControllerUtil.initializeExternalModel(model, servletContext, user, "mutational-signature-viewer");
	}

	private static String createVcfLink(FileProperties fileProps, String targetName) throws IOException {
		File target = new File(fileProps.getVcfFilesDir(), targetName);
		if (!target.exists()) {
			return null;
		}
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".vcf";
		File link = new File(fileProps.getVcfLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}
	
}
