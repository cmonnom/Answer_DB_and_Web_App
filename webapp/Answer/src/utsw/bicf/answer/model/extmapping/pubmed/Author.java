package utsw.bicf.answer.model.extmapping.pubmed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
	
	@JsonProperty("LastName")
	String lastName;
	@JsonProperty("ForeName")
	String foreName;
	@JsonProperty("Initials")
	String initials;
	
	

	public Author() {
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getForeName() {
		return foreName;
	}



	public void setForeName(String foreName) {
		this.foreName = foreName;
	}



	public String getInitials() {
		return initials;
	}



	public void setInitials(String initials) {
		this.initials = initials;
	}


	public String getPrettyPrint() {
		return lastName.toString() + " " + initials.toString() + ".";
	}





	
}
