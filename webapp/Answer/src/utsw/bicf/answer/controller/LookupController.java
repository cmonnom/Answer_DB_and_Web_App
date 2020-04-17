package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.TreeViewSummary;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.CivicRequestUtils;
import utsw.bicf.answer.db.api.utils.EnsemblRequestUtils;
import utsw.bicf.answer.db.api.utils.JaxCKBRequestUtils;
import utsw.bicf.answer.db.api.utils.NCBIRequestUtils;
import utsw.bicf.answer.db.api.utils.OncoKBRequestUtils;
import utsw.bicf.answer.db.api.utils.OncotreeRequestUtils;
import utsw.bicf.answer.db.api.utils.ReactomeRequestUtils;
import utsw.bicf.answer.db.api.utils.UniProtRequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.model.extmapping.lookup.LookupGeneSummaries;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.model.extmapping.oncotree.OncotreeTumorType;
import utsw.bicf.answer.security.CivicProperties;
import utsw.bicf.answer.security.EnsemblProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.JaxCKBProperties;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OncoKBProperties;
import utsw.bicf.answer.security.OncotreeProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;
import utsw.bicf.answer.security.ReactomeProperties;
import utsw.bicf.answer.security.UniProtProperties;

@Controller
@RequestMapping("/")
public class LookupController {
	
	static {
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".lookupTool", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getGeneSummary", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getOncotreeTumorType", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getReactomeLocations", IndividualPermission.CAN_VIEW);
	}

	@Autowired 
	ServletContext servletContext;
	@Autowired
	FileProperties fileProps;
	@Autowired
	LoginDAO loginDAO;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	OtherProperties otherProps;
	@Autowired
	NCBIProperties ncbiProps;
	@Autowired
	UniProtProperties uniprotProps;
	@Autowired
	CivicProperties civicProps;
	@Autowired
	OncoKBProperties oncoKBProps;
	@Autowired
	EnsemblProperties ensemblProps;
	@Autowired
	OncotreeProperties oncotreeProps;
	@Autowired
	JaxCKBProperties jaxProps;
	@Autowired
	ReactomeProperties reactomeProps;
	
	@RequestMapping("/lookupTool")
	public String lookupTool(Model model, HttpSession session,
			@RequestParam(defaultValue="", required=false) String gene,
			@RequestParam(defaultValue="", required=false) String variant,
			@RequestParam(defaultValue="", required=false) String oncotree,
			@RequestParam(defaultValue="", required=false) String button) throws IOException {
		String url = "lookupTool?gene=" + gene
				+ "%26variant=" + variant + "%26oncotree=" + oncotree
				 + "%26button=" + button;
		model.addAttribute("urlRedirect", url);
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	

	@RequestMapping("/getGeneSummary")
	@ResponseBody
	public String getGeneSummary(Model model, HttpSession session, @RequestParam String geneTerm,
			@RequestParam String databases) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException, InterruptedException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			
			EnsemblRequestUtils utils = new EnsemblRequestUtils(ensemblProps, otherProps);
			EnsemblResponse ensembl = utils.fetchEnsembl(geneTerm);
			ensembl.init();
			
			if (ensembl != null && ensembl.getEntrezId() != null) {
				LookupGeneSummaries summaryResponse = new LookupGeneSummaries(ensembl);
				summaryResponse.setDatabases(Arrays.asList(databases.split(",")));
				ExecutorService executor = Executors.newFixedThreadPool(summaryResponse.getDatabases().size());
				Runnable ncbiWorker = new Runnable() {
					@Override
					public void run() {
						NCBIRequestUtils ncbiUtils = new NCBIRequestUtils(ncbiProps, otherProps);
						try {
							LookupSummary ncbiSummary = ncbiUtils.getGeneSummary(ensembl.getEntrezId());
							summaryResponse.getSummaries().put("RefSeq", ncbiSummary);
						} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException
								| SAXException | ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
				};
				executor.execute(ncbiWorker);
				
				Runnable oncoKBWorker = new Runnable() {
					@Override
					public void run() {
						OncoKBRequestUtils oncoKBUtils = new OncoKBRequestUtils(oncoKBProps, otherProps);
						try {
							LookupSummary oncoKBSummary = oncoKBUtils.getGeneSummaryByEntrezId(ensembl.getEntrezId());
							summaryResponse.getSummaries().put("OncoKB", oncoKBSummary);
						} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException
								| SAXException | ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
				};
				executor.execute(oncoKBWorker);
				
				Runnable uniProtWorker = new Runnable() {
					@Override
					public void run() {
						UniProtRequestUtils uniProtUtils = new UniProtRequestUtils(uniprotProps, otherProps);
						try {
							LookupSummary uniProtSummary = uniProtUtils.getGeneSummary(geneTerm);
							summaryResponse.getSummaries().put("UniProt", uniProtSummary);
						} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException
								| SAXException | ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
				};
				executor.execute(uniProtWorker);
				
				Runnable civicWorker = new Runnable() {
					@Override
					public void run() {
						CivicRequestUtils civicUtils = new CivicRequestUtils(civicProps, otherProps);
						try {
							LookupSummary civicSummary = civicUtils.getGeneSummary(geneTerm);
							summaryResponse.getSummaries().put("Civic DB", civicSummary);
						} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException
								| SAXException | ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
				};
				executor.execute(civicWorker);
				
				Runnable jaxWorker = new Runnable() {
					@Override
					public void run() {
						JaxCKBRequestUtils jaxUtils = new JaxCKBRequestUtils(jaxProps, otherProps);
						try {
							LookupSummary jaxSummary = jaxUtils.getGeneSummary(geneTerm, ensembl.getEntrezId());
							summaryResponse.getSummaries().put("Jackson Labs", jaxSummary);
						} catch (UnsupportedOperationException | URISyntaxException | IOException | JAXBException
								| SAXException | ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
				};
				executor.execute(jaxWorker);
				
				executor.shutdown();
				executor.awaitTermination(10, TimeUnit.SECONDS);
				
				response.setPayload(summaryResponse);
				response.setSuccess(true);
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getRefSeqSummary")
	@ResponseBody
	public String getRefSeqSummary(Model model, HttpSession session, @RequestParam String geneTerm,
			@RequestParam String database) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			NCBIRequestUtils utils = new NCBIRequestUtils(ncbiProps, otherProps);
			LookupSummary summary = utils.getGeneSummary(geneTerm, modelDAO);
			if (summary != null) {
				summary.setDatabase(database);
				if (summary.getSummary() != null) {
					response.setPayload(summary);
					response.setSuccess(true);
				}
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	@RequestMapping("/getOncotreeTumorType")
	@ResponseBody
	public String getOncotreeTumorType(Model model, HttpSession session, @RequestParam String oncotreeCode) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			OncotreeRequestUtils utils = new OncotreeRequestUtils(oncotreeProps, otherProps);
			OncotreeTumorType oncotree = utils.getOncotreeTumorType(oncotreeCode);
			if (oncotree != null) {
				response.setPayload(oncotree);
				response.setSuccess(true);
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getReactomeLocations")
	@ResponseBody
	public String getReactomeLocations(Model model, HttpSession session, @RequestParam String levels,
			@RequestParam String geneTerm) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			List<String> levelList = Arrays.asList(levels.split(",")); 
			ReactomeRequestUtils utils = new ReactomeRequestUtils(reactomeProps, otherProps);
			TreeViewSummary summary = utils.getLocations(geneTerm, levelList);
			if (summary != null) {
				response.setPayload(summary);
				response.setSuccess(true);
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
