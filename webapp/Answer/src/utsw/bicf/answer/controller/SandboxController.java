package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.plotly.CNVChartData;
import utsw.bicf.answer.controller.serialization.vuetify.CNVChromosomeItems;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.BAlleleFrequencyData;
import utsw.bicf.answer.model.extmapping.CNVPlotData;
import utsw.bicf.answer.model.extmapping.CNVPlotDataRaw;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;
import utsw.bicf.answer.security.QcAPIAuthentication;

@Controller
@RequestMapping("/")
public class SandboxController {

	static {
		PermissionUtils.addPermission(SandboxController.class.getCanonicalName() + ".sandbox",
				IndividualPermission.CAN_VIEW); //allow can_view to handle redirect inside the openCase method
		PermissionUtils.addPermission(SandboxController.class.getCanonicalName() + ".getCNVChromListStatic",
				IndividualPermission.CAN_VIEW); //allow can_view to handle redirect inside the openCase method
		PermissionUtils.addPermission(SandboxController.class.getCanonicalName() + ".getCNVChartDataStatic",
				IndividualPermission.CAN_VIEW); //allow can_view to handle redirect inside the openCase method
		
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	QcAPIAuthentication qcAPI;
	@Autowired
	EmailProperties emailProps;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;
	@Autowired
	LoginDAO loginDAO;


	@RequestMapping("/sandbox")
	public String openCase(Model model, HttpSession session) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "sandbox";
		User user = ControllerUtil.getSessionUser(session);
		model.addAttribute("urlRedirect", url);
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
	@RequestMapping(value = "/getCNVChromListStatic", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCNVChromListStatic(Model model, HttpSession session) throws Exception {

		// send user to Ben's API
//		RequestUtils utils = new RequestUtils(modelDAO);
		Set<String> selectItems = new HashSet<String>();
		for (int i = 1; i < 23; i++) {
			selectItems.add("chr" + i);
			
		}
		selectItems.add("chrX");
		CNVChromosomeItems items = new CNVChromosomeItems(selectItems);
		return items.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/getCNVChartDataStatic", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCNVChartDataStatic(Model model, HttpSession session, @RequestParam(defaultValue="all", required=false) String chrom,
			@RequestParam(defaultValue="", required=false) String genesParam) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		if (chrom.equals("all")) {
			chrom = null;
			genesParam = "";//don't color genes in this view
		}
		List<String> selectedGenes = new ArrayList<String>();
		if (!genesParam.equals("")) {
			String[] selectedGenesArray = genesParam.split(",");
			for (String gene : selectedGenesArray) {
				selectedGenes.add(gene.trim());
			}
		}
		CNVPlotDataRaw rawData = new CNVPlotDataRaw();
		File cnvDir = new File(fileProps.getBamFilesDir().getParentFile(), "cases");
		List<Path> caseFiles = Files.list(cnvDir.toPath()).sorted(new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				return new Long(o1.toFile().lastModified()).compareTo(new Long(o2.toFile().lastModified()));
			}
		}).collect(Collectors.toList());
		File caseFile =  caseFiles.get(caseFiles.size() - 1).toFile();
		String caseId = caseFile.getName();
		File cnsFile = new File(caseFile, "cns.cns");
		File cnrFile = new File(caseFile, "cnr.cnr");
		File bAllFreqFile = new File(caseFile, "ballfreq.txt");
		List<List<String>> cns = new ArrayList<List<String>>();
		List<List<String>> cnr = new ArrayList<List<String>>();
		List<BAlleleFrequencyData> ballelefreqs = new ArrayList<BAlleleFrequencyData>();
		
		List<String> cnsLines = Files.readAllLines(cnsFile.toPath());
		for (String line : cnsLines) {
			List<String> splitLine = Arrays.asList(line.split("\t"));
			if (!splitLine.isEmpty() && !splitLine.get(0).equals("Chromosome")) {
				cns.add(splitLine);
			}
		}
		List<String> cnrLines = Files.readAllLines(cnrFile.toPath());
		for (String line : cnrLines) {
			List<String> splitLine = Arrays.asList(line.split("\t"));
			if (!splitLine.isEmpty() && !splitLine.get(0).equals("Gene")) {
				cnr.add(splitLine);
			}
		}
		List<String> bAllFreqLines = Files.readAllLines(bAllFreqFile.toPath());
		for (String line : bAllFreqLines) {
			List<String> splitLine = Arrays.asList(line.split("\t"));
			if (!splitLine.isEmpty() && !splitLine.get(0).equals("CHROM")) {
				String chr = splitLine.get(0);
				Long pos = Long.parseLong(splitLine.get(1));
//				Long ao = Long.parseLong(splitLine.get(2));
				Long ro = Long.parseLong(splitLine.get(3));
				Double depth = Double.parseDouble(splitLine.get(4));
				Double log2 = Double.parseDouble(splitLine.get(5));
//				ballelefreqs.add(new BAlleleFrequencyData(chr, pos, ao, ro, depth, log2));
				ballelefreqs.add(new BAlleleFrequencyData(chr, pos, ro, depth, log2));
			}
		}
		rawData.setCaseId(caseId);
		rawData.setCns(cns);
		rawData.setCnr(cnr);
		rawData.setBallelefreqs(ballelefreqs);
		
		CNVPlotData cnvPlotData = utils.parseRawData(chrom, rawData, true);
		if (cnvPlotData != null) {
			return new CNVChartData(cnvPlotData.getCnsData(), cnvPlotData.getCnrData(), cnvPlotData.getBAllData(), selectedGenes, caseId).createObjectJSON();
		}
		else {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(false);
			response.setSuccess(false);
			
			return response.createObjectJSON();
		}

	}
	
}
