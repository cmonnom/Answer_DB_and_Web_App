package utsw.bicf.answer.controller.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.GenieMutation;
import utsw.bicf.answer.model.GenieSample;
import utsw.bicf.answer.model.GenieSummary;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.GenericBarPlotData;
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
	
	@RequestMapping("/updateGenieData")
	@ResponseBody
	public String updateGenieData(Model model, @RequestParam String token, 
			@RequestParam String sampleDataPath, 
			@RequestParam String mutationDataPath, 
			HttpSession httpSession) throws IOException, InterruptedException {
		httpSession.setAttribute("user", "API User from updateGenieData");
		long now = System.currentTimeMillis();
		// check that token is valid
		Token theToken = modelDAO.getUpdateGenieDataToken(token);
		AjaxResponse response = new AjaxResponse();
		if (theToken == null) {
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("You are not allowed to run this servlet.");
			return response.createObjectJSON();
		}
		
		File sampleDataFile = new File(sampleDataPath);
		File mutationDataFile = new File(mutationDataPath);
		
		if (sampleDataFile.exists() && sampleDataFile.canRead()
				&& mutationDataFile.exists() && mutationDataFile.canRead()) {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			modelDAO.deleteGenieTables();
			Runnable worker = new Runnable() {
				@Override
				public void run() {
					try {
						
						List<Object> toSave = new ArrayList<Object>();
						BufferedReader reader1 = new BufferedReader(new FileReader(sampleDataFile));
						String line = null;
						while ((line = reader1.readLine()) != null) {
							if (line.startsWith("PATIENT_ID")) {
								break;
							}
							continue; //skip until the first sample row
						}
						int counter = 0; //try without a counter first
						while ((line = reader1.readLine()) != null) {
							String[] items = line.split("\t");
							GenieSample s = new GenieSample();
							s.setPatientId(items[0]);
							s.setSampleId(items[1]);
							s.setOncotreeCode(items[3]);
							s.setCancerType(items[6]);
//							modelDAO.saveObject(s);
							toSave.add(s);
							counter++;
							if (counter % 1000 == 0) {
								System.out.println("Samples: " + counter);
								modelDAO.saveBatch(toSave);
								toSave = new ArrayList<Object>();
							}
						}
						if (!toSave.isEmpty() ) {
							modelDAO.saveBatch(toSave);
							toSave = new ArrayList<Object>();
						}
						reader1.close();
						
						BufferedReader reader2 = new BufferedReader(new FileReader(mutationDataFile));
						line = null;
						while ((line = reader2.readLine()) != null) {
							if (line.startsWith("Hugo_Symbol")) {
								break;
							}
							continue; //skip until the first sample row
						}
						counter = 0;
						Map<String, Integer> sampleIdFKey = modelDAO.getAllGenieSampleIdByTumorBarcode();
						Pattern variantPattern = Pattern.compile("([a-z]\\.)([A-Z]+)([0-9]+)([*_=A-Za-z]+[0-9a-z]*)");
						while ((line = reader2.readLine()) != null ) {
							String[] items = line.split("\t");
							GenieMutation m = new GenieMutation();
							m.setHugoSymbol(items[0]);
							m.setEntrezId(Integer.parseInt(items[1]));
							m.setVariantClassification(items[8]);
							m.setVariantType(items[9]);
							m.setTumorSampleBarcode(items[15]);
//							Integer fkey = modelDAO.getGenieSampleIdByTumorBarcode(m.getTumorSampleBarcode());
							m.setGenieSampleId(sampleIdFKey.get(m.getTumorSampleBarcode()));
							String notation = items[32];
							m.setVariantNotation(notation);
							if (notation != null && !notation.equals("")) {
								Matcher matcher = variantPattern.matcher(notation);
								while(matcher.find()) {
									int groupCount = matcher.groupCount();
									if (groupCount >= 3) {
//										String protein = matcher.group(1);
										String positionString =  matcher.group(3);
										if (positionString != null && !positionString.equals("")) {
											Integer position = Integer.parseInt(positionString);
											m.setAminoAcidPosition(position);
										}
										String aaNotation = matcher.group(2) + positionString;
										m.setAminoAcidNotation(aaNotation);
									}
									if (groupCount >= 4) {
										String variantChange = matcher.group(4);
										m.setVariantChange(variantChange);
									}
								}
							}
							
							toSave.add(m);
							counter++;
							if (counter % 1000 == 0) {
								modelDAO.saveBatch(toSave);
								toSave = new ArrayList<Object>();
								System.out.println("Mutations: " + counter);
							}
						}
						if (!toSave.isEmpty() ) {
							modelDAO.saveBatch(toSave);
							toSave = new ArrayList<Object>();
						}
						reader2.close();
						
						long afterRequest = System.currentTimeMillis();
						System.out.println("After request " + (afterRequest - now) + "ms");
						
//						modelDAO.test();
						//insert summary (intermediate results)
						List<GenericBarPlotData> summaryData = modelDAO.getGeniePatientCountPerCancer();
						List<Object> summaryToSave = summaryData
								.stream().map(i -> new GenieSummary(i.getX().intValue(), i.getY(), GenieSummary.CATEGORY_CANCER_COUNT))
								.collect(Collectors.toList());
						modelDAO.saveBatch(summaryToSave);
						afterRequest = System.currentTimeMillis();
						System.out.println("After request " + (afterRequest - now) + "ms");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			};
			
			executor.execute(worker);
		}
		else {
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("The files don't exist or can't be read by Answer.");
			return response.createObjectJSON();
		}
		
		response.setIsAllowed(true);
		response.setSuccess(true);
		
	
		return response.createObjectJSON();
	}
	
}
