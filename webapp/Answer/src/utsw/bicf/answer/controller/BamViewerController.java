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
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class BamViewerController {

	static {
		PermissionUtils.addPermission(BamViewerController.class.getCanonicalName() + ".bamViewer", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(BamViewerController.class.getCanonicalName() + ".getBams", IndividualPermission.CAN_VIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;

	@RequestMapping("/bamViewer")
	public String bamViewer(Model model, HttpSession session, @RequestParam String locus, @RequestParam String caseId)
			throws IOException, URISyntaxException {
		model.addAttribute("urlRedirect", "bamViewer?locus=" + locus + "%26caseId=" + caseId);
		model.addAttribute("locus", locus);
		model.addAttribute("caseId", caseId);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		User user = (User) session.getAttribute("user");
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (user == null) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}
		if (caseSummary == null) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}
		if (!caseSummary.getAssignedTo().contains(user.getUserId().toString())) {
			return ControllerUtil.initializeModelError(model, servletContext);
		}
		// At this point everything should be granted to access the bams
		String normalBam = caseSummary.getNormalBam();
		String tumorBam = caseSummary.getTumorBam();
		String rnaBam = caseSummary.getRnaBam();
//		normalBam = "SHI710-27-6271_T_DNA_panel1385v2-1.final.bam"; //TODO delete this


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

		return ControllerUtil.initializeExternalModel(model, servletContext, user, "bam-viewer");
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
		System.out.println("link target " + target.getAbsolutePath() + " linkName: " + linkName + " exists? " + target.exists() );
		if (!target.exists()) {
			return null;
		}
		File link = new File(fileProps.getBamLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}
}
