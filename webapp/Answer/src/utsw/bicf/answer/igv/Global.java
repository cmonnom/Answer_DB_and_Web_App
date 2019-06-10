package utsw.bicf.answer.igv;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Global {
	
	public static final String GENOME = "hg38";
	public static final String VERSION = "3";
	
	@JacksonXmlProperty(isAttribute = true)
	String genome = "hg38";
	@JacksonXmlProperty(isAttribute = true)
	String locus;
	@JacksonXmlProperty(isAttribute = true)
	String version = "3";
	@JacksonXmlElementWrapper(localName = "Resources")
	@JacksonXmlProperty(localName = "Resource")
	List<Resource> resources;
	
	
	public Global() {
		super();
	}
	
	public Global(String locus, List<Resource> resources) {
		super();
		this.genome = GENOME;
		this.locus = locus;
		this.version = VERSION;
		this.resources = resources;
	}

	public String getGenome() {
		return genome;
	}
	public void setGenome(String genome) {
		this.genome = genome;
	}
	public String getLocus() {
		return locus;
	}
	public void setLocus(String locus) {
		this.locus = locus;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
}
