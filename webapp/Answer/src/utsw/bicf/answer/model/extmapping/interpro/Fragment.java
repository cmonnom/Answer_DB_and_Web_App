package utsw.bicf.answer.model.extmapping.interpro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fragment implements Comparable<Fragment>{
	
	@JsonProperty("start")
	Integer start;
	@JsonProperty("end")
	Integer end;
	@JsonProperty("dc-status")
	String dcStatus;
	
	//Not part of the response object but useful to
	//keep all the values in the same object
	@JsonIgnore
	String name;
	@JsonIgnore
	String shortName;
	
	
	public Fragment() {
		super();
	}


	public Integer getStart() {
		return start;
	}


	public void setStart(Integer start) {
		this.start = start;
	}


	public Integer getEnd() {
		return end;
	}


	public void setEnd(Integer end) {
		this.end = end;
	}


	public String getDcStatus() {
		return dcStatus;
	}


	public void setDcStatus(String dcStatus) {
		this.dcStatus = dcStatus;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getShortName() {
		return shortName;
	}


	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	@Override
	public int compareTo(Fragment o) {
		return start.compareTo(o.start);
	}






}
