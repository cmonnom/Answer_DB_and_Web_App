package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataReportGroup {

	public DataReportGroup() {
		super();
	}
	String groupName;
	String description;
	String referenceUrl;
	String genes;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGenes() {
		return genes;
	}
	public void setGenes(String genes) {
		this.genes = genes;
	}
	public String getReferenceUrl() {
		return referenceUrl;
	}
	public void setReferenceUrl(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}


}
