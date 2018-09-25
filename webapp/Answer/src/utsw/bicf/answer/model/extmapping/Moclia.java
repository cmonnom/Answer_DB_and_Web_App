package utsw.bicf.answer.model.extmapping;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Moclia {
	
	public static final String HEADER = "Patient de-Identified ID,Provider*,Panel Name,Report ID (Accession),Report Date,Result Status*,Gene,Alteration**,Fusion Partner,Test Type,Somatic Germline*,Copy Number Change,Allelic Frequency,Transcript ID,Nucleotide Change,External Specimen ID,Specimen Collect Date,Path Tissue Site";
	
	Boolean isAllowed = true;
	
	@JsonIgnore
	List<String> rows;
	
	String fullContent;
	
	public Moclia(List<String> rows) {
		this.rows = rows;
		this.init();
	}

	private void init() {
		// -------- temp fix for missing columns ----------
		StringBuilder sb = new StringBuilder();
		for (String row : rows) {
			String[] items = row.split("\t");
			int counter = 0;
			for (String item : items) {
				sb.append(item).append(",");
				if (counter == 9 || counter == 11) { //missing columns
					sb.append(",");
				}
				counter++;
			}
			sb.append("\n");
		}
		String rowContent = sb.toString();
		// -------------------------------------------
//		String rowContent = rows.stream().map(r -> r.replaceAll("\t", ",")).collect(Collectors.joining("\n"));
		StringBuilder content = new StringBuilder(HEADER);
		content.append("\n");
		content.append(rowContent);
		this.fullContent = content.toString();
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}




	public List<String> getRows() {
		return rows;
	}




	public void setRows(List<String> rows) {
		this.rows = rows;
	}


	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getFullContent() {
		return fullContent;
	}

	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}



}
