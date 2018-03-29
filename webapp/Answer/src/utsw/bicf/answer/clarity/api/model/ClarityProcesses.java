package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "prc:processes", namespace = "prc")
public class ClarityProcesses implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JacksonXmlElementWrapper(localName = "process", useWrapping = false)
	@JacksonXmlProperty(localName = "process")
	ClarityProcess[] processes;
	
	

	public ClarityProcess[] getProcesses() {
		return processes;
	}

	public void setProcesses(ClarityProcess[] processes) {
		this.processes = processes;
	}

	public ClarityProcesses() {
		super();
	}
	
}
