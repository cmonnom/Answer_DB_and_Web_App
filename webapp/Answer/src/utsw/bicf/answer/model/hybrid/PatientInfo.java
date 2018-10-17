package utsw.bicf.answer.model.hybrid;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.ListTable;
import utsw.bicf.answer.model.FinalReport;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class PatientInfo {
	
	List<ListTable> patientTables = new ArrayList<ListTable>();
	Boolean isAllowed = true;
	
	public PatientInfo() {
		
	}
	
	public PatientInfo(OrderCase orderCase) {
		this.createPatientTables(orderCase);
	}

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
		items.add(new CellItem("Order #", orderCase.getEpicOrderNumber())); 
		items.add(new CellItem("Lab Accession #", orderCase.getCaseName()));
//		if (finalReport != null) {
//			items.add(new CellItem("Report Accession #", finalReport.getVersion().toString()));
//		}
//		else {
//			items.add(new CellItem("Report Accession #", "No report yet"));
//		}
		items.add(new CellItem("Tumor Specimen #", orderCase.getTumorId()));
		items.add(new CellItem("Germline Specimen #", orderCase.getNormalId()));
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
		String orderedBy = null;
		if (orderCase.getOrderingPhysician() != null) {
			int separator = orderCase.getOrderingPhysician().indexOf(" ");
			if (separator > -1) {
				orderedBy = orderCase.getOrderingPhysician().substring(separator, orderCase.getOrderingPhysician().length() - 1);
			}
			else {
				orderedBy = orderCase.getOrderingPhysician();
			}
		}
		else {
			orderedBy = "";
		}
		String authorizedBy = null;
		if (orderCase.getAuthorizingPhysician() != null) {
			int separator = orderCase.getAuthorizingPhysician().indexOf(" ");
			if (separator > -1) {
				authorizedBy = orderCase.getAuthorizingPhysician().substring(separator, orderCase.getAuthorizingPhysician().length() - 1);
			}
			else {
				authorizedBy = orderCase.getAuthorizingPhysician();
			}
		}
		else {
			authorizedBy = "";
		}
		items.add(new CellItem("Ordered by", orderedBy));
		items.add(new CellItem("Authorized by", authorizedBy));
		items.add(new CellItem("Institution", orderCase.getInstitution()));
		items.add(new CellItem("Tumor Tissue", orderCase.getTumorTissueType()));
		items.add(new CellItem("Germline Tissue", orderCase.getNormalTissueType()));
		items.add(new CellItem("ICD10", orderCase.getIcd10()));
		CellItem clinicalStage = new CellItem("Clinical Stage", orderCase.getClinicalStage());
		items.add(clinicalStage);//TODO
		CellItem treatmentStatus = new CellItem("Treatment Status", orderCase.getTreatmentStatus());
		items.add(treatmentStatus);//TODO
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
		CellItem oncoTreeItem = new CellItem("OncoTree Diagnosis", orderCase.getOncotreeDiagnosis());
		oncoTreeItem.setType(CellItem.TYPE_TEXT);
		oncoTreeItem.setField("oncotree");
		items.add(oncoTreeItem);
		items.add(new CellItem("Order Date", orderCase.getEpicOrderDate()));
		items.add(new CellItem("Tumor Collection Date", orderCase.getTumorCollectionDate()));
		items.add(new CellItem("Lab Received Date", orderCase.getReceivedDate()));
		patientTables.add(table);
		
	}

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
