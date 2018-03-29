package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "projects", namespace = "prc")
public class ClarityProjects implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JacksonXmlElementWrapper(localName = "project", useWrapping = false)
	@JacksonXmlProperty(localName = "project")
	ClarityProject[] projects;
	@JacksonXmlProperty(localName = "next-page")
	ClarityNextPage nextPage;
	
	


	public ClarityProjects() {
		super();
	}




	public ClarityProject[] getProjects() {
		return projects;
	}




	public void setProjects(ClarityProject[] projects) {
		this.projects = projects;
	}




	public ClarityNextPage getNextPage() {
		return nextPage;
	}




	public void setNextPage(ClarityNextPage nextPage) {
		this.nextPage = nextPage;
	}
	
}
