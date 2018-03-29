package utsw.bicf.answer.controller;
//package utsw.bicf.answer.controller;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//
//import utsw.bicf.answer.controller.serialization.vuetify.ChromItems;
//import utsw.bicf.answer.controller.serialization.vuetify.SampleItems;
//import utsw.bicf.answer.controller.serialization.zingchart.SampleCoverageZingChartData;
//import utsw.bicf.answer.dao.CoverageDAO;
//import utsw.bicf.answer.dao.ExonDAO;
//import utsw.bicf.answer.dao.SampleDAO;
//import utsw.bicf.answer.model.Sample;
//import utsw.bicf.answer.model.hybrid.SampleCoverage;
//import utsw.bicf.answer.controller.ControllerUtil;
//
//@Controller
//@RequestMapping("/")
//public class ChartController {
//	
//	@Autowired 
//	ServletContext servletContext;
//	@Autowired
//	private CoverageDAO coverageDAO;
//	@Autowired
//	private SampleDAO sampleDAO;
//	@Autowired
//	private ExonDAO exonDAO;
//
//
//	@RequestMapping("/sampleCoverage/{sampleId}/{chrom}")
//	public String sampleCoverage(Model model, HttpSession session,  @PathVariable String sampleId,  @PathVariable String chrom) throws IOException {
//		model.addAttribute("urlRedirect", "sampleCoverage/" + sampleId + "/" + chrom);
//		return ControllerUtil.initializeModel(model, servletContext);
//	}
//	
//	@RequestMapping("/sampleCoverage/{sampleId}")
//	public String sampleCoverage(Model model, HttpSession session,  @PathVariable String sampleId) throws IOException {
//		model.addAttribute("urlRedirect", "sampleCoverage/" + sampleId);
//		return ControllerUtil.initializeModel(model, servletContext);
//	}
//	
//	@RequestMapping("/sampleCoverage")
//	public String sampleCoverage(Model model, HttpSession session) throws IOException {
//		model.addAttribute("urlRedirect", "sampleCoverage");
//		return ControllerUtil.initializeModel(model, servletContext);
//	}
//
//	@RequestMapping("/getSampleCoverageData")
//	@ResponseBody
//	public String getSampleCoverageData(Model model, HttpSession session, @RequestParam int sampleId, 
//			@RequestParam String chrom, @RequestParam Boolean isTier1) {
//		try {
//			List<SampleCoverage> coverages = coverageDAO.getCoverageForSample(sampleId, chrom, isTier1);
//			Sample sample = sampleDAO.getSample(sampleId);
//			SampleCoverageZingChartData chartData = new SampleCoverageZingChartData(sample, coverages);
//			return chartData.createVuetifyObjectJSON();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	@RequestMapping("/getSampleItems")
//	@ResponseBody
//	public String getSampleItems(Model model, HttpSession session) {
//		try {
////			List<Sample> samples = sampleDAO.getAllSamples();
//			List<Sample> samples = sampleDAO.getAllDNASamples();
//			SampleItems items = new SampleItems(samples);
//			return items.createVuetifyObjectJSON();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	@RequestMapping("/getSampleItemsFiltered")
//	@ResponseBody
//	public String getSampleItemsFiltered(Model model, HttpSession session, @RequestParam String query, @RequestParam int sampleId) {
//		try {
////			List<Sample> samples = sampleDAO.getAllSamples();
////			List<Sample> samples = sampleDAO.getAllDNASamples();
//			List<Sample> samples = null;
//			if (query != null && query.length() > 0) {
//				samples = sampleDAO.getAllSamplesFiltered(query);
//			}
//			else {
//				samples = new ArrayList<Sample>();
//				samples.add(sampleDAO.getSample(sampleId));
//			}
//			SampleItems items = new SampleItems(samples);
//			return items.createVuetifyObjectJSON();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	@RequestMapping("/getChromItems")
//	@ResponseBody
//	public String getChromItems(Model model, HttpSession session) {
//		try {
//			List<String> chroms = exonDAO.getAllChromosomes();
//			ChromItems items = new ChromItems(chroms);
//			return items.createVuetifyObjectJSON();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//}
