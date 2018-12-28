package utsw.bicf.answer.controller.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NotificationUtils;

@Controller
@RequestMapping("/")
public class APIController {

	@Autowired
	private ModelDAO modelDAO;
	@Autowired
	private FileProperties fileProps;
	@Autowired
	EmailProperties emailProps;
	

	@RequestMapping("/parseMDAEmail")
	@ResponseBody
	public String parseMDAEmail(Model model, @RequestParam String token,
			@RequestParam String emailPath, HttpSession httpSession) {
		httpSession.setAttribute("user", "API User from parseMDAEmail");
		// check that token is valid
		Token theToken = modelDAO.getParseMDAToken(token);
		if (theToken == null) {
			return "{error: 'You are not allowed to run this servlet.'}";
		}
		//parse MDA Email
		//make sure there is no sneaky stuff on the file path
		String sanitized = emailPath.replaceAll("[.]{2}", "");
		if (!emailPath.equals(sanitized)) {
			return "{error: 'Invalid Path.'}";
		}
		String fullPath = fileProps.getMdaFilesDir().getAbsolutePath() + "/" + sanitized;
		
		File emailFile = new File(fullPath);
		MDAReportTemplate mdaEmail;
		try {
			mdaEmail = new MDAReportTemplate(emailFile);
			return mdaEmail.createObjectJSON();
		} catch (IOException e) {
			e.printStackTrace();
			return "{error: 'File does not exist.'}";
		} 
	}
	
	@RequestMapping("/newCaseUploadedEmail")
	@ResponseBody
	public String newCaseUploadedEmail(Model model, @RequestParam String token,
			@RequestParam String caseId, HttpSession httpSession) throws IOException, InterruptedException {
		httpSession.setAttribute("user", "API User from newCaseUploadedEmail");
		// check that token is valid
		Token theToken = modelDAO.getParseMDAToken(token);
		AjaxResponse response = new AjaxResponse();
		if (theToken == null) {
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("You are not allowed to run this servlet.");
			return response.createObjectJSON();
		}
		
		List<User> users = modelDAO.getAllUsers();
		for (User aUser : users) {
			if (aUser.getIndividualPermission().getReceiveAllNotifications()) {
				this.sendNewCaseEmail(caseId, aUser);
			}
		}
		response.setIsAllowed(true);
		response.setSuccess(true);
		return response.createObjectJSON();
	}
	
	private void sendNewCaseEmail(String caseId, User user) throws IOException, InterruptedException {
		String subject = "A new case has be uploaded: " + caseId;
		String reason = "You are receiving this message because your account is set to receive all notifications.<br/><br/>";
		StringBuilder message = new StringBuilder()
				.append("<p>Dr. ").append(user.getLast()).append(",</p><br/>")
				.append("A new case has been uploaded ")
				.append("<b>Case Id: ").append(caseId).append("</b><br/>")
				.append("<br/>")
				.append(reason);
				
		String toEmail = user.getEmail();
//		String toEmail = "guillaume.jimenez@utsouthwestern.edu"; //for testing to avoid sending other people emails
		String link = new StringBuilder().append(emailProps.getRootUrl()).append("openCaseReadOnly/").append(caseId)
				.append("?showReview=true").toString();
		String fullMessage = NotificationUtils.buildStandardMessage(message.toString(), emailProps, link);
		boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, fullMessage);
		System.out.println("An email was sent. Success:" + success);
	}
	
}
