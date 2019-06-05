package utsw.bicf.answer.model.hybrid;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.ListTable;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class PatientInfo {
	
	List<ListTable> patientTables = new ArrayList<ListTable>();
	Boolean isAllowed = true;
	
	public PatientInfo() {
		
	}
	
	public PatientInfo(OrderCase orderCase) {
		this.createPatientTables(orderCase);
	}
	
	//tables are slightly different in the PDF report
//	public PatientInfo(PatientInfo patientInfo) {
//		this.createPatientTablesForPDF(patientInfo);
//	}

	private void createPatientTables(OrderCase orderCase) {
		ListTable table = new ListTable();
		List<CellItem> items = new ArrayList<CellItem>();
		table.setItems(items);
		CellItem caseName = new CellItem("Name", orderCase.getPatientName());
		caseName.setField("caseName");
		items.add(caseName);
		items.add(new CellItem("MRN", orderCase.getMedicalRecordNumber()));
		String dab = orderCase.getDateOfBirth();
		if (dab != null) {
			LocalDate dabDate = LocalDate.parse(dab, TypeUtils.monthFormatter);
			LocalDate now = LocalDate.now();
			int years = Period.between(dabDate, now).getYears();
			dab = dab + " (" + years + " years old)";
		}
		items.add(new CellItem("DOB", dab));
		items.add(new CellItem("Sex", orderCase.getGender()));
//		items.add(new CellItem("Order #", orderCase.getEpicOrderNumber())); 
//		items.add(new CellItem("Lab Accession #", orderCase.getCaseName()));
//		items.add(new CellItem("Tumor Specimen #", orderCase.getTumorId()));
//		items.add(new CellItem("Germline Specimen #", orderCase.getNormalId()));
		items.add(new CellItem("Institution", orderCase.getInstitution()));
		String orderedBy = null;
		if (orderCase.getOrderingPhysician() != null) {
			int separator = orderCase.getOrderingPhysician().indexOf(" ");
			String rawValue = null;
			if (separator > -1) {
				rawValue = orderCase.getOrderingPhysician().substring(separator, orderCase.getOrderingPhysician().length() - 1);
			}
			else {
				rawValue = orderCase.getOrderingPhysician();
			}
			orderedBy = "Dr. "  + WordUtils.capitalize(rawValue.toLowerCase(), ' ', ',');
		}
		else {
			orderedBy = "";
		}
		String authorizedBy = null;
		if (orderCase.getAuthorizingPhysician() != null) {
			int separator = orderCase.getAuthorizingPhysician().indexOf(" ");
			String rawValue = null;
			if (separator > -1) {
				rawValue = orderCase.getAuthorizingPhysician().substring(separator, orderCase.getAuthorizingPhysician().length() - 1);
			}
			else {
				rawValue = orderCase.getAuthorizingPhysician();
			}
			authorizedBy = "Dr. "  + WordUtils.capitalize(rawValue.toLowerCase(), ' ', ',');
		}
		else {
			authorizedBy = "";
		}
		items.add(new CellItem("Ordered By", orderedBy));
		items.add(new CellItem("Authorized By", authorizedBy));
		items.add(new CellItem("ICD10", orderCase.getIcd10()));
		
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
//		String orderedBy = null;
//		if (orderCase.getOrderingPhysician() != null) {
//			int separator = orderCase.getOrderingPhysician().indexOf(" ");
//			String rawValue = null;
//			if (separator > -1) {
//				rawValue = orderCase.getOrderingPhysician().substring(separator, orderCase.getOrderingPhysician().length() - 1);
//			}
//			else {
//				rawValue = orderCase.getOrderingPhysician();
//			}
//			orderedBy = "Dr. "  + WordUtils.capitalize(rawValue.toLowerCase(), ' ', ',');
//		}
//		else {
//			orderedBy = "";
//		}
//		String authorizedBy = null;
//		if (orderCase.getAuthorizingPhysician() != null) {
//			int separator = orderCase.getAuthorizingPhysician().indexOf(" ");
//			String rawValue = null;
//			if (separator > -1) {
//				rawValue = orderCase.getAuthorizingPhysician().substring(separator, orderCase.getAuthorizingPhysician().length() - 1);
//			}
//			else {
//				rawValue = orderCase.getAuthorizingPhysician();
//			}
//			authorizedBy = "Dr. "  + WordUtils.capitalize(rawValue.toLowerCase(), ' ', ',');
//		}
//		else {
//			authorizedBy = "";
//		}
//		items.add(new CellItem("Ordered By", orderedBy));
//		items.add(new CellItem("Authorized By", authorizedBy));
//		items.add(new CellItem("Institution", orderCase.getInstitution()));
		items.add(new CellItem("Order #", orderCase.getEpicOrderNumber())); 
		items.add(new CellItem("Lab Accession #", orderCase.getCaseName()));
		items.add(new CellItem("Tumor Specimen #", orderCase.getTumorId()));
		items.add(new CellItem("Germline Specimen #", orderCase.getNormalId()));
		CellItem tumorTissueItem = new CellItem("Tumor Tissue", orderCase.getTumorTissueType());
		tumorTissueItem.setType(CellItem.TYPE_TEXT_FIELD);
		tumorTissueItem.setField("tumorTissueType");
		items.add(tumorTissueItem);
		items.add(new CellItem("Germline Tissue", orderCase.getNormalTissueType()));
//		items.add(new CellItem("ICD10", orderCase.getIcd10()));
		CellItem oncoTreeItem = new CellItem("OncoTree Diagnosis", orderCase.getOncotreeDiagnosis());
		oncoTreeItem.setType(CellItem.TYPE_TEXT);
		oncoTreeItem.setField("oncotree");
		items.add(oncoTreeItem);
//		CellItem clinicalStage = new CellItem("Clinical Stage", orderCase.getClinicalStage());
//		items.add(clinicalStage);//TODO
//		CellItem treatmentStatus = new CellItem("Treatment Status", orderCase.getTreatmentStatus());
//		items.add(treatmentStatus);//TODO
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
		items.add(new CellItem("Order Date", orderCase.getEpicOrderDate()));
		items.add(new CellItem("Tumor Collection Date", orderCase.getTumorCollectionDate()));
		items.add(new CellItem("Lab Received Date", orderCase.getReceivedDate()));
		CellItem dedupPctOver100XItem = new CellItem("Pct. Over 100X", orderCase.getDedupPctOver100X()  != null ? orderCase.getDedupPctOver100X() + "" : "");
		dedupPctOver100XItem.setField("dedupPctOver100X");
		dedupPctOver100XItem.setType(CellItem.TYPE_TEXT_FIELD);
		items.add(dedupPctOver100XItem);
		CellItem dedupAvgDepthItem = new CellItem("Avg. Depth", orderCase.getDedupAvgDepth()  != null ? orderCase.getDedupAvgDepth() + "" : "");
		dedupAvgDepthItem.setField("dedupAvgDepth");
		dedupAvgDepthItem.setType(CellItem.TYPE_TEXT_FIELD);
		items.add(dedupAvgDepthItem);
		CellItem tumorPercentItem = new CellItem("Tumor Percent", orderCase.getTumorPercent() != null ? orderCase.getTumorPercent() + "" : "");
		tumorPercentItem.setField("tumorPercent");
		tumorPercentItem.setType(CellItem.TYPE_TEXT_FIELD);
		items.add(tumorPercentItem);
		items.add(new CellItem("Tumor Mutation Burden (Mutations/MB)", orderCase.getTumorMutationBurden() + ""));
		patientTables.add(table);
		
	}
	
//	private void createPatientTablesForPDF(PatientInfo patientInfo) {
//		ListTable table0 = patientInfo.getPatientTables().get(0);
//		CellItem tumorSpecimen = table0.getItems()
//				.stream()
//				.filter(i -> i.getLabel().equals("Tumor Specimen #"))
//				.collect(Collectors.toList()).get(0);
//		CellItem germlineSpecimen = table0.getItems()
//				.stream()
//				.filter(i -> i.getLabel().equals("Germline Specimen #"))
//				.collect(Collectors.toList()).get(0);
//		table0.setItems(table0.getItems()
//		.stream()
//		.filter(i -> !i.getLabel().equals("Tumor Specimen #")
//				&& !i.getLabel().equals("Germline Specimen #"))
//		.collect(Collectors.toList()));
//		
//		ListTable table1 = patientInfo.getPatientTables().get(1);
//		table1.setItems(table1.getItems()
//				.stream()
//				.filter(i -> !i.getLabel().equals("Clinical Stage")
//						&& !i.getLabel().equals("Treatment Status"))
//				.collect(Collectors.toList()));
//		
//		ListTable table2 = patientInfo.getPatientTables().get(2);
//		table2.getItems().add(tumorSpecimen);
//		table2.getItems().add(germlineSpecimen);
//		
//		this.patientTables = new ArrayList<ListTable>();
//		patientTables.add(table0);
//		patientTables.add(table1);
//		patientTables.add(table2);
//		
//	}


	public List<ListTable> getPatientTables() {
		return patientTables;
	}

	public void setPatientTables(List<ListTable> patientTables) {
		this.patientTables = patientTables;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


}
