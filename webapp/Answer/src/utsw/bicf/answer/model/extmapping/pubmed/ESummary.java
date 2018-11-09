package utsw.bicf.answer.model.extmapping.pubmed;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ESummary {
	
	@JsonProperty("DocSum")
	List<DocSum> docSumList;
	
	public ESummary() {
	}

	public List<DocSum> getDocSumList() {
		return docSumList;
	}

	public void setDocSumList(List<DocSum> docSumList) {
		this.docSumList = docSumList;
	}

//	public List<DocSum> getDocSumList() {
//		return docSumList;
//	}
//
//	public void setDocSumList(List<DocSum> docSumList) {
//		this.docSumList = docSumList;
//	}






	
}
