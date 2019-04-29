package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
public class MutationalSignatureViewer {

	static {
		PermissionUtils.addPermission(MutationalSignatureViewer.class.getCanonicalName() + ".mutationalSignatureViewer", IndividualPermission.CAN_VIEW);
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
	public String mutationalSignatureViewer(Model model, HttpSession session, @RequestParam String caseId)
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
		String tumorVcf = caseSummary.getTumorVcf();
		tumorVcf = "GM12878.vcf";//TODO delete this


		if (tumorVcf != null) {
			String vcfLink = createVcfLink(fileProps, tumorVcf);
			model.addAttribute("tumorVcf", vcfLink);
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
