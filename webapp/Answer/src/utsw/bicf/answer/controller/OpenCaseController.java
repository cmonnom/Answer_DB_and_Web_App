package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.DataTableFilter;
import utsw.bicf.answer.controller.serialization.SearchItem;
import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterItems;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class OpenCaseController {
	
	static {
		PermissionUtils.permissionPerUrl.put("openCase", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getCaseDetails", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getVariantFilters", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/openCase/{caseId}")
	public String openCase(Model model, HttpSession session, @PathVariable String caseId) throws IOException {
		model.addAttribute("urlRedirect", "openCase/" + caseId);
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	
	
	@RequestMapping(value = "/getCaseDetails")
	@ResponseBody
	public String getCaseDetails(Model model, HttpSession session, @RequestParam String caseId)
			throws Exception {
		
		User user = (User) session.getAttribute("user"); //to verify that the user is assigned to the case
		//send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		OrderCase detailedCase = null;
		if (cases != null) {
			for (OrderCase c : cases) {
				if (c.getCaseId().equals(caseId)) {
					detailedCase = utils.getCaseDetails(caseId);
					break; //found that the case exists
				}
			}
		}
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, detailedCase, null, "chromPos");
		return summary.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/getVariantFilters")
	@ResponseBody
	public String getVariantFilters(Model model, HttpSession session)
			throws Exception {
		List<DataTableFilter> filters = new ArrayList<DataTableFilter>();
		
		DataTableFilter chrFilter = new DataTableFilter("Chromosome", "chrom");
		chrFilter.setSelect(true);
		List<SearchItem> selectItems = new ArrayList<SearchItem>();
		for (int i = 1; i <= 23; i++) {
			selectItems.add(new SearchItemString("CHR" + i, "chr" + i));
		}
		selectItems.add(new SearchItemString("CHRX", "chrX"));
		selectItems.add(new SearchItemString("CHRY", "chrY"));
		filters.add(chrFilter);
		chrFilter.setSelectItems(selectItems);
		
		DataTableFilter passQCFilter = new DataTableFilter("Pass QC", "Fail QC", "filters");
		passQCFilter.setBoolean(true);
		filters.add(passQCFilter);
		
		DataTableFilter annotatedFilter = new DataTableFilter("Annotated", "Unknown", "annotations");
		annotatedFilter.setBoolean(true);
		filters.add(annotatedFilter);
		
		DataTableFilter tafFilter = new DataTableFilter("Tumor Alt %", "tumorAltFrequency");
		tafFilter.setNumber(true);
		filters.add(tafFilter);
		
//		DataTableFilter tumorDepthFilter = new DataTableFilter("Tumor Depth", "tumorAltDepth");
//		tumorDepthFilter.setNumber(true);
//		filters.add(tumorDepthFilter);
		
		DataTableFilter tumorTotalDepthFilter = new DataTableFilter("Tumor Total Depth", "tumorTotalDepth");
		tumorTotalDepthFilter.setNumber(true);
		filters.add(tumorTotalDepthFilter);
		
		DataTableFilter nafFilter = new DataTableFilter("Normal Alt %", "normalAltFrequency");
		nafFilter.setNumber(true);
		filters.add(nafFilter);
		
//		DataTableFilter normalDepthFilter = new DataTableFilter("Normal Depth", "normalAltDepth");
//		normalDepthFilter.setNumber(true);
//		filters.add(normalDepthFilter);
		
		DataTableFilter normalTotalDepthFilter = new DataTableFilter("Normal Total Depth", "normalTotalDepth");
		normalTotalDepthFilter.setNumber(true);
		filters.add(normalTotalDepthFilter);
		
		DataTableFilter rafFilter = new DataTableFilter("Rna Alt %", "rnaAltFrequency");
		rafFilter.setNumber(true);
		filters.add(rafFilter);
		
//		DataTableFilter rnaDepthFilter = new DataTableFilter("RNA Depth", "rnaAltDepth");
//		rnaDepthFilter.setNumber(true);
//		filters.add(rnaDepthFilter);
		
		DataTableFilter rnaTotalDepthFilter = new DataTableFilter("RNA Total Depth", "rnaTotalDepth");
		rnaTotalDepthFilter.setNumber(true);
		filters.add(rnaTotalDepthFilter);
		
		DataTableFilter effectFilter= new DataTableFilter("Effects", "effects");
		effectFilter.setCheckBox(true);
		filters.add(effectFilter);
		
		VariantFilterItems items = new VariantFilterItems();
		items.setFilters(filters);
		return  items.createVuetifyObjectJSON();
		
	}
}
