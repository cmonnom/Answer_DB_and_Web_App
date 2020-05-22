package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.plotly.BarPlotData;
import utsw.bicf.answer.controller.serialization.plotly.LollipopPlotData;
import utsw.bicf.answer.controller.serialization.plotly.StackedBarPlotData;
import utsw.bicf.answer.controller.serialization.plotly.Trace;
import utsw.bicf.answer.controller.serialization.vuetify.TreeViewSummary;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.CivicRequestUtils;
import utsw.bicf.answer.db.api.utils.EnsemblRequestUtils;
import utsw.bicf.answer.db.api.utils.InterProRequestUtils;
import utsw.bicf.answer.db.api.utils.JaxCKBRequestUtils;
import utsw.bicf.answer.db.api.utils.NCBIRequestUtils;
import utsw.bicf.answer.db.api.utils.OncoKBRequestUtils;
import utsw.bicf.answer.db.api.utils.OncotreeRequestUtils;
import utsw.bicf.answer.db.api.utils.ReactomeRequestUtils;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.db.api.utils.UniProtRequestUtils;
import utsw.bicf.answer.model.GenieSummary;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.model.extmapping.interpro.EntryProteinLocation;
import utsw.bicf.answer.model.extmapping.interpro.Fragment;
import utsw.bicf.answer.model.extmapping.interpro.InterProResponse;
import utsw.bicf.answer.model.extmapping.interpro.Protein;
import utsw.bicf.answer.model.extmapping.interpro.Result;
import utsw.bicf.answer.model.extmapping.lookup.LookupGeneSummaries;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.model.extmapping.oncotree.OncotreeTumorType;
import utsw.bicf.answer.model.hybrid.GenericBarPlotData;
import utsw.bicf.answer.model.hybrid.GenericBarPlotDataSummary;
import utsw.bicf.answer.model.hybrid.GenericLollipopPlotData;
import utsw.bicf.answer.model.hybrid.GenericStackedBarPlotData;
import utsw.bicf.answer.model.hybrid.StringSortableByInteger;
import utsw.bicf.answer.security.CivicProperties;
import utsw.bicf.answer.security.EnsemblProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.InterProProperties;
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
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".discovar", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getGeneSummary", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getOncotreeTumorType", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getReactomeLocations", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getVariantSummary", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getAlterationByCancer", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getCancerbyPercent", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getGenieGeneLollipop", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(LookupController.class.getCanonicalName() + ".getMutatedGeneByCancer", IndividualPermission.CAN_VIEW);
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
	@Autowired
	InterProProperties interProProps;
	
	@RequestMapping("/discovar")
	public String discovar(Model model, HttpSession session,
			@RequestParam(defaultValue="", required=false) String gene,
			@RequestParam(defaultValue="", required=false) String variant,
			@RequestParam(defaultValue="", required=false) String oncotree,
			@RequestParam(defaultValue="", required=false) String button) throws IOException {
		String url = "discovar?gene=" + gene
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
	
	@RequestMapping("/getMutatedGeneByCancer")
	@ResponseBody
	public String getMutatedGeneByCancer(Model model, HttpSession session, @RequestParam String oncotreeCode,
			@RequestParam String plotId) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			OncotreeRequestUtils utils = new OncotreeRequestUtils(oncotreeProps, otherProps);
			Set<String> oncotrees = utils.getOncotreeTumorTypeChildren(oncotreeCode);
			if (oncotrees != null) {
				try {
					response.setIsAllowed(true);
					response.setSuccess(false);
					List<GenericStackedBarPlotData> data = modelDAO.getMutatedGenesPerCancer(oncotrees);
					Map<String, List<Object>> xPerVariantType = new HashMap<String, List<Object>>();
					List<String> variantTypes = Arrays.asList("SNP", "INS", "DEL");
					if (data != null && !data.isEmpty()) {
						StackedBarPlotData chart = new StackedBarPlotData();
						chart.setPlotId(plotId);
						List<Object> y = new ArrayList<Object>();
						for (GenericStackedBarPlotData d : data) {
							y.add(d.getY());
//							variantTypes.addAll(d.getCatX());
						}
						Collections.reverse(y); //need to reverse for Plotly to show the highest on top
						//init map
						for (String variantType : variantTypes) {
							xPerVariantType.put(variantType, new ArrayList<Object>());
						}
						//second pass. Find each variant type x
						for (GenericStackedBarPlotData d : data) {
							for (String variantType : variantTypes) {
								xPerVariantType.get(variantType).add(d.getxByCat().get(variantType));
							}
						}
						//third pass. Build traces
						for (String variantType : variantTypes) {
							List<Object> xList = xPerVariantType.get(variantType);
							if (xList.stream().filter(x -> x != null).count() == 0) {
								continue;
							}
							Trace trace = new Trace();
							trace.setY(y);
							trace.setName(variantType);
							Collections.reverse(xList);
							trace.setX(xList);
							List<String> labels = xList.stream().map(x -> variantType).collect(Collectors.toList());
							Collections.reverse(labels);
							trace.setLabels(labels);
							chart.getTraces().add(trace);
						}
						
						return chart.createObjectJSON();
					}
					return response.createObjectJSON();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return null;
				}
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
	
	@RequestMapping("/getVariantSummary")
	@ResponseBody
	public String getVariantSummary(Model model, HttpSession session, @RequestParam String geneTerm,
			@RequestParam String oncotreeCode, @RequestParam String hgvs, @RequestParam(defaultValue="") String originalVariant, 
			@RequestParam(defaultValue="")  String oncokbVariantName,	@RequestParam String databases) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException, InterruptedException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);

			boolean needAnswerQuery = oncokbVariantName == null || oncokbVariantName.equals("")
					|| originalVariant == null || originalVariant.equals("")
					|| !hgvs.equals(originalVariant);
			String variantName = null;
			if (needAnswerQuery) {
				//TODO build API to retrive oncokb variant from Answer
//				variantName = "M918T"; //TODO remove this
				RequestUtils utils = new RequestUtils(modelDAO);
				utils.getOncoKbName(response, geneTerm, hgvs);
				if (response.getSuccess() && response.getPayload() != null) {
					variantName = (String) response.getPayload();
				}
			}
			else {
				variantName = oncokbVariantName;
			}
			final String finalVariantName = variantName;
			
			LookupGeneSummaries summaryResponse = new LookupGeneSummaries();
			summaryResponse.setDatabases(Arrays.asList(databases.split(",")));
			ExecutorService executor = Executors.newFixedThreadPool(summaryResponse.getDatabases().size());


			Runnable civicWorker = new Runnable() {
				@Override
				public void run() {
					CivicRequestUtils civicUtils = new CivicRequestUtils(civicProps, otherProps);
					try {
						LookupSummary civicSummary = null;
						if (finalVariantName == null || finalVariantName.equals("")) {
							civicSummary = new LookupSummary();
						}
						else {
							civicSummary = civicUtils.getVariantSummary(geneTerm, finalVariantName);
						}
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
						LookupSummary jaxSummary = null;
						if (finalVariantName == null || finalVariantName.equals("")) {
							jaxSummary = new LookupSummary();
						}
						else {
							String query = null;
							query = geneTerm + " " + finalVariantName;
							jaxSummary = jaxUtils.getVariantSummary(query);
						}
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
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getAlterationByCancer")
	@ResponseBody
	public String getAlterationByCancer(Model model, HttpSession session, @RequestParam String hugoSymbol, 
			@RequestParam String plotId) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			List<GenericBarPlotData> data = modelDAO.getGenieVariantCountForGene(hugoSymbol);
			if (data != null && !data.isEmpty()) {
				BarPlotData chart = new BarPlotData();
				chart.setPlotId(plotId);
				Trace trace = new Trace();
				for (int i = data.size() -1; i >=0; i--) {
					GenericBarPlotData d = data.get(i);
					trace.addX(d.getX());
					trace.addY(d.getY());
					trace.addLabel("<b>Cancer:</b> " + d.getY() + "<br><b>Variants:</b> " + d.getX());
				}
				chart.setTrace(trace);
				return chart.createObjectJSON();
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getCancerbyPercent")
	@ResponseBody
	public String getCancerbyPercent(Model model, HttpSession session, @RequestParam String hugoSymbol, 
			@RequestParam String plotId) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		try {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(true);
			response.setSuccess(false);
			List<GenieSummary> dataAll = modelDAO.getGenieSummary(GenieSummary.CATEGORY_CANCER_COUNT);
			List<GenericBarPlotData> dataGene = modelDAO.getGeniePatientCountForGene(hugoSymbol, dataAll.stream().map(d -> d.getLabel()).collect(Collectors.toList()));
			Map<String, GenericBarPlotData> geneCountPerCancer = dataGene.stream().collect(Collectors.toMap(GenericBarPlotData::getY, Function.identity()));
			if (dataAll != null && dataGene != null && !dataGene.isEmpty()) {
				BarPlotData chart = new BarPlotData();
				chart.setPlotId(plotId);
				Trace trace = new Trace();
				//need to sort by pct. Need a list too because there could be duplicate pct (key)
				List<GenericBarPlotDataSummary> toSortData = new ArrayList<GenericBarPlotDataSummary>();
				for (int i = dataAll.size() -1; i >=0; i--) {
					GenieSummary dAll = dataAll.get(i);
					GenericBarPlotData dGene = geneCountPerCancer.get(dAll.getLabel());
					if (dGene != null) {
						float pct = dGene.getX().floatValue() / dAll.getTally().floatValue() * 100;
						toSortData.add(new GenericBarPlotDataSummary(dAll, dGene, pct));
					}
				}
				toSortData = toSortData.stream().sorted(new Comparator<GenericBarPlotDataSummary>() {
					@Override
					public int compare(GenericBarPlotDataSummary o1, GenericBarPlotDataSummary o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				}).collect(Collectors.toList());
				for (GenericBarPlotDataSummary s : toSortData) {
					trace.addX(s.getKey());
					trace.addY(s.getdAll().getLabel());
					trace.addLabel("<b>Cancer:</b> " + s.getdAll().getLabel() 
					+ "<br><b>Altered Cases:</b> " + s.getdGene().getX()
					+ "<br><b>Total Cases:</b> " + s.getdAll().getTally()
					+ "<br><b>Percent:</b> " + String.format("%.2f", s.getKey()));
				}
				chart.setTrace(trace);
				return chart.createObjectJSON();
			}
			return response.createObjectJSON();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getGenieGeneLollipop")
	@ResponseBody
	public String getGenieGeneLollipop(Model model, HttpSession session, @RequestParam String hugoSymbol, 
			@RequestParam String plotId) throws ClientProtocolException, URISyntaxException, IOException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {

		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(false);

		EnsemblRequestUtils utils = new EnsemblRequestUtils(ensemblProps, otherProps);
		EnsemblResponse ensembl = utils.fetchEnsembl(hugoSymbol);
		ensembl.init();
		if (ensembl != null && ensembl.getUniProtId() != null) {
			ExecutorService executor = Executors.newFixedThreadPool(2);
			LollipopPlotData chart = new LollipopPlotData();

			Runnable pfamWorker = new Runnable() {
				@Override
				public void run() {
					InterProRequestUtils interProUtils = new InterProRequestUtils(interProProps, otherProps);
					try {
						InterProResponse response = interProUtils.getPfam(ensembl.getUniProtId());
						if (response != null) {
							List<Trace> pfamTraces = new ArrayList<Trace>();
							List<String> annotations = new ArrayList<String>();
							Trace bottomTrace = new Trace();
							List<Fragment> fragmentsToSort = new ArrayList<Fragment>();
							for (Result result : response.getResults()) {
								String name = result.getMetadata().getName();
								String shortName = result.getExtra_fields().getShortName();
								//need to sort fragment by start value
								//so that they can be displayed in a creneleded fashion _-_-_-
								for (Protein protein : result.getProteins()) {
									for (EntryProteinLocation epl : protein.getEntryProteinLocations()) {
										for (Fragment fragment : epl.getFragments()) {
											fragmentsToSort.add(fragment);
											fragment.setName(name);
											fragment.setShortName(shortName);
										}
									}
								}
							}
							fragmentsToSort = fragmentsToSort.stream().sorted().collect(Collectors.toList());
							for (Fragment fragment : fragmentsToSort) {
								bottomTrace.addStart(fragment.getStart());
								bottomTrace.addEnd(fragment.getEnd());
								bottomTrace.addX((fragment.getStart() + fragment.getEnd()) / 2);
								String label = "Domain: " + fragment.getName() + "<br>"
										+ "Start: " + fragment.getStart() + "<br>"
										+ "End: " + fragment.getEnd();
								bottomTrace.addLabel(label);
								annotations.add(fragment.getShortName().toUpperCase());
							}
							pfamTraces.add(bottomTrace);
							chart.setUnderlineTraces(pfamTraces);
							chart.setAnnotations(annotations);
						}
					} catch (UnsupportedOperationException | URISyntaxException | IOException e	) {
						e.printStackTrace();
					}
				}
			};
			executor.execute(pfamWorker);

			Runnable dbWorker = new Runnable() {
				@Override
				public void run() {
					List<GenericLollipopPlotData> dataAll = modelDAO.getGeniePatientCountForGene(hugoSymbol);
					if (dataAll != null && !dataAll.isEmpty()) {

						chart.setPlotId(plotId);
						Trace trace = new Trace();
						//need to create the proper label and count the number of variants per row
						for (GenericLollipopPlotData row : dataAll) {
							StringBuilder sb = new StringBuilder();
							if (row.getLabel2() != null) {
								List<String> variants = Arrays.asList(row.getLabel2().split("/"));
								Map<String, StringSortableByInteger> variantCount = new HashMap<String, StringSortableByInteger>();
								for (String v : variants) {
									StringSortableByInteger sortableString = variantCount.get(v);
									if (sortableString  == null) {
										sortableString = new StringSortableByInteger(v, 0, true);
										variantCount.put(v, sortableString);
									}
									sortableString.setIntValue(sortableString.getIntValue() + 1);
								}
								sb.append("Amino Acid: ").append(row.getLabel1()).append("<br>")
								.append("Variant Count: ").append(row.getY()).append("<br>");
								if (!variantCount.isEmpty()) {
									String sortedVariants = variantCount.values().stream().sorted().map(s -> s.getStringValue()).collect(Collectors.joining("/"));
									sb.append("Variants: ").append(sortedVariants);
								}
							}
							trace.addLabel(sb.toString());
							trace.addX(row.getX());
							trace.addY(row.getY());

						}
						chart.setTrace(trace);
						Number maxY = (Number) chart.getTrace().getY().stream().max(Comparators.comparable()).get();
						chart.setMaxY(maxY);
					}
				}
			};
			executor.execute(dbWorker);

			executor.shutdown();
			try {
				executor.awaitTermination(10, TimeUnit.SECONDS);
				response.setSuccess(true);
				response.setPayload(chart);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		

		return response.createObjectJSON();
	}

}
