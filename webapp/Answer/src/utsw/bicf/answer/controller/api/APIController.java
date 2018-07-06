package utsw.bicf.answer.controller.api;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.FileProperties;

@Controller
@RequestMapping("/")
public class APIController {

	@Autowired
	private ModelDAO modelDAO;
	@Autowired
	private FileProperties fileProps;
	
	

	@RequestMapping("/parseMDAEmail")
	@ResponseBody
	public String parseMDAEmail(Model model, @RequestParam String token,
			@RequestParam String emailPath, HttpSession httpSession) {
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
	
}
