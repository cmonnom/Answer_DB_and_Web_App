package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.ListTable;
import utsw.bicf.answer.controller.serialization.vuetify.MDAReportTableSummary;
import utsw.bicf.answer.model.Patient;

public class MDAFileImportedResponse extends AjaxResponse {
	
	Integer caseId; //only id for now. We'll see if we need more
	Patient patient;
	List<ListTable> patientTables = new ArrayList<ListTable>();
	String currentFile;
	MDAReportTableSummary data;

	public Integer getCaseId() {
		return caseId;
	}


	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}


	public Patient getPatient() {
		return patient;
	}


	public void setPatient(Patient patient) {
		this.patient = patient;
		createPatientTables();
	}


	public List<ListTable> getPatientTables() {
		return patientTables;
	}


	public void setPatientTables(List<ListTable> patientTables) {
		this.patientTables = patientTables;
	}
	
	private void createPatientTables() {
		ListTable table = new ListTable();
		List<CellItem> items = new ArrayList<CellItem>();
		table.setItems(items);
		items.add(new CellItem("Name", patient.getFirstName() + " " + patient.getLastName()));
		items.add(new CellItem("MRN", patient.getMRN()));
		String dateOfBirth = null;
		if (patient.getDateOfBirth() != null) {
			dateOfBirth = patient.getDateOfBirth().format(TypeUtils.shortDayMonthYearFormatter);
		}
		items.add(new CellItem("DOB", dateOfBirth));
		items.add(new CellItem("Sex", patient.getSex()));
		items.add(new CellItem("Order #", patient.getOrderNb()));
		items.add(new CellItem("Lab Accession #", patient.getLabAccessionNb()));
		items.add(new CellItem("Report Accession #", patient.getReportAccessionNb()));
		items.add(new CellItem("Tumor Specimen #", patient.getTumorSpecimenNb()));
		items.add(new CellItem("Germline Specimen #", patient.getGermlineSpecimenNb()));
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
		items.add(new CellItem("Ordered by", patient.getOrderedBy()));
		items.add(new CellItem("Institution", patient.getInstitution()));
		items.add(new CellItem("Tumor Tissue", patient.getTumorTissue()));
		items.add(new CellItem("Germline Tissue", patient.getGermlineTissue()));
		items.add(new CellItem("ICD 10", patient.getIcd10()));
		items.add(new CellItem("Clinical Stage", patient.getClinicalStage()));
		items.add(new CellItem("Treatment Status", patient.getTreatmentStatus()));
		patientTables.add(table);
		
		table = new ListTable();
		items = new ArrayList<CellItem>();
		table.setItems(items);
		items.add(new CellItem("Order date", patient.getOrderedBy()));
		String tumorCollectionDate = null;
		if (patient.getTumorCollectionDate() != null) {
			tumorCollectionDate = patient.getTumorCollectionDate().format(TypeUtils.shortDayMonthYearFormatter);
		}
		items.add(new CellItem("Tumor Collection Date", tumorCollectionDate));
		String labReceivedDate = null;
		if (patient.getLabReceivedDate() != null) {
			labReceivedDate = patient.getLabReceivedDate().format(TypeUtils.shortDayMonthYearFormatter);
		}
		items.add(new CellItem("Lab Received date", labReceivedDate));
		patientTables.add(table);
		
	}


	public String getCurrentFile() {
		return currentFile;
	}


	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}


	public MDAReportTableSummary getData() {
		return data;
	}


	public void setData(MDAReportTableSummary data) {
		this.data = data;
	}


	
	
	
}
