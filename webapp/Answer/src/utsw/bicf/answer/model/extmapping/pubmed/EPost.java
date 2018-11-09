package utsw.bicf.answer.model.extmapping.pubmed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "ePostResult")
public class EPost {
	
	@JsonProperty("QueryKey")
	int queryKey;
	@JsonProperty("WebEnv")
	String webEnv;
	
	public EPost() {
		super();
	}

	public int getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(int queryKey) {
		this.queryKey = queryKey;
	}

	public String getWebEnv() {
		return webEnv;
	}

	public void setWebEnv(String webEnv) {
		this.webEnv = webEnv;
	}
	




	
}
