package utsw.bicf.answer.controller.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import utsw.bicf.answer.model.GenieCNA;
import utsw.bicf.answer.model.GenieCNACount;
import utsw.bicf.answer.model.GenieFusion;
import utsw.bicf.answer.model.GenieFusionCount;
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
			@RequestParam String cnaDataPath,
			@RequestParam String fusionDataPath,
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
		File cnaDataFile = new File(cnaDataPath);
		File fusionDataFile = new File(fusionDataPath);
		
		if (sampleDataFile.exists() && sampleDataFile.canRead()
				&& mutationDataFile.exists() && mutationDataFile.canRead()
				&& cnaDataFile.exists() && cnaDataFile.canRead()
				&& fusionDataFile.exists() && fusionDataFile.canRead()) {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			modelDAO.deleteGenieTables();
			Runnable worker = new Runnable() {
				@Override
				public void run() {
					try {
						
						List<Object> toSave = new ArrayList<Object>();
						int counter = 0;
						importSamples(sampleDataFile, toSave, counter);
						Map<String, Integer> sampleIdFKey = modelDAO.getAllGenieSampleIdByTumorBarcode();
						importMutations(mutationDataFile, toSave, sampleIdFKey);
						
						importCNA(cnaDataFile, toSave, sampleIdFKey);
						importFusion(fusionDataFile, toSave, sampleIdFKey);
						
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

	public void importSamples(File sampleDataFile, List<Object> toSave, int counter)
			throws FileNotFoundException, IOException {
		String line;
		BufferedReader reader1 = new BufferedReader(new FileReader(sampleDataFile));
		while ((line = reader1.readLine()) != null) {
			if (line.startsWith("PATIENT_ID")) {
				break;
			}
			continue; //skip until the first sample row
		}
		
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
	}

	public void importMutations(File mutationDataFile, List<Object> toSave, Map<String, Integer> sampleIdFKey)
			throws FileNotFoundException, IOException {
		int counter;
		String line;
		BufferedReader reader2 = new BufferedReader(new FileReader(mutationDataFile));
		line = null;
		while ((line = reader2.readLine()) != null) {
			if (line.startsWith("Hugo_Symbol")) {
				break;
			}
			continue; //skip until the first sample row
		}
		counter = 0;
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
	}

	public void importCNA(File cnaDataFile, List<Object> toSave, Map<String, Integer> sampleIdFKey)
			throws FileNotFoundException, IOException {
		int counter;
		String line;
		BufferedReader reader3 = new BufferedReader(new FileReader(cnaDataFile));
		line = null;
		List<String> headers = null;
		while ((line = reader3.readLine()) != null) {
			if (line.startsWith("Hugo_Symbol")) {
				headers = Arrays.asList(line.split("\t"));
				break;
			}
			continue; //skip until the first sample row
		}
		counter = 0;
		Map<String, GenieSample> genieSampleFKey = modelDAO.getAllGenieSamplesByTumorBarcode();
		Map<String, Map<String, GenieCNACount>> countCasesPerCancerPerGene = new HashMap<String, Map<String, GenieCNACount>>();
		while ((line = reader3.readLine()) != null ) {
			String[] items = line.split("\t");
			String hugoSymbol = items[0];
			for (int i = 1; i < items.length; i++) {
				String item = items[i];
				if (!item.equals("NA")) {
					float value = Float.parseFloat(item);
					if (value <= -2 || value >= 2) {
						GenieCNA cna = new GenieCNA();
						cna.setCnaValue(value);
						cna.setHugoSymbol(hugoSymbol);
						cna.setTumorSampleBarcode(headers.get(i));
						cna.setGenieSampleId(sampleIdFKey.get(cna.getTumorSampleBarcode()));
						toSave.add(cna);
						counter++;
						if (counter % 1000 == 0) {
							modelDAO.saveBatch(toSave);
							toSave = new ArrayList<Object>();
							System.out.println("CNA: " + counter);
						}
					}
					GenieSample gs = genieSampleFKey.get(headers.get(i));
					if (gs != null) {
						Map<String, GenieCNACount> geneCount = countCasesPerCancerPerGene.get(gs.getOncotreeCode());
						if (geneCount == null) {
							geneCount = new HashMap<String, GenieCNACount>();
							countCasesPerCancerPerGene.put(gs.getOncotreeCode(), geneCount);
						}
						GenieCNACount cnaCount = geneCount.get(hugoSymbol);
						if (cnaCount == null) {
							cnaCount = new GenieCNACount(gs.getOncotreeCode(), hugoSymbol, 0);
							geneCount.put(hugoSymbol, cnaCount);
						}
						cnaCount.incrementCount();
					}
				}
			}
		}
		if (!toSave.isEmpty() ) {
			modelDAO.saveBatch(toSave);
			toSave = new ArrayList<Object>();
		}
		reader3.close();
		counter = 0;
		for (Map<String, GenieCNACount> cnCountPerOncotreeCode : countCasesPerCancerPerGene.values()) {
			List<Object> cnaToSave = cnCountPerOncotreeCode.values().stream().collect(Collectors.toList());
			modelDAO.saveBatch(cnaToSave);
			counter += cnaToSave.size();
			System.out.println("CNA counts: " + counter);
		}
	}
	
	public void importFusion(File fusionDataFile, List<Object> toSave, Map<String, Integer> sampleIdFKey)
			throws FileNotFoundException, IOException {
		int counter;
		String line;
		BufferedReader reader2 = new BufferedReader(new FileReader(fusionDataFile));
		line = null;
		while ((line = reader2.readLine()) != null) {
			if (line.startsWith("Hugo_Symbol")) {
				break;
			}
			continue; //skip until the first sample row
		}
		counter = 0;
		while ((line = reader2.readLine()) != null ) {
			String[] items = line.split("\t");
			if (items[0] == null || items[0].length() == 0) {
				continue;
			}
			GenieFusion m = new GenieFusion();
			m.setHugoSymbol(items[0]);
			m.setFusionName(items[4]);
			m.setTumorSampleBarcode(items[3]);
//							Integer fkey = modelDAO.getGenieSampleIdByTumorBarcode(m.getTumorSampleBarcode());
			m.setGenieSampleId(sampleIdFKey.get(m.getTumorSampleBarcode()));
			
			toSave.add(m);
			counter++;
			if (counter % 1000 == 0) {
				modelDAO.saveBatch(toSave);
				toSave = new ArrayList<Object>();
				System.out.println("Fusions: " + counter);
			}
		}
		if (!toSave.isEmpty() ) {
			modelDAO.saveBatch(toSave);
			toSave = new ArrayList<Object>();
		}
		reader2.close();
		List<Object> counts = modelDAO.populateGenieFusionCountTable();
		modelDAO.saveBatch(counts);
	}
	
}
