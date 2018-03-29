package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "input-output-map")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityInputOutputMap implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ClarityInput input;
	ClarityOutput output;
	

	public ClarityInputOutputMap() {
		
	}
	
	@JacksonXmlRootElement(localName = "input")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class ClarityInput implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@JacksonXmlProperty(localName = "parent-process")
		ClarityProcess parentProcess;
		@JacksonXmlProperty(localName = "post-process-uri")
		String postProcessUri;
		String uri;
		String limsid;
		
		public ClarityInput() {
		}

		public ClarityProcess getParentProcess() {
			return parentProcess;
		}

		public void setParentProcess(ClarityProcess parentProcess) {
			this.parentProcess = parentProcess;
		}

		public String getPostProcessUri() {
			return postProcessUri;
		}

		public void setPostProcessUri(String postProcessUri) {
			this.postProcessUri = postProcessUri;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getLimsid() {
			return limsid;
		}

		public void setLimsid(String limsid) {
			this.limsid = limsid;
		}
		
		

	}
	
	@JacksonXmlRootElement(localName = "output")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class ClarityOutput implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String uri;
		@JacksonXmlProperty(localName = "output-generation-type")
		String outputGenerationType;
		@JacksonXmlProperty(localName = "output-type")
		String outputType;
		String limsid;
		
		
		public ClarityOutput() {
		}


		public String getUri() {
			return uri;
		}


		public void setUri(String uri) {
			this.uri = uri;
		}


		public String getOutputGenerationType() {
			return outputGenerationType;
		}


		public void setOutputGenerationType(String outputGenerationType) {
			this.outputGenerationType = outputGenerationType;
		}


		public String getOutputType() {
			return outputType;
		}


		public void setOutputType(String outputType) {
			this.outputType = outputType;
		}


		public String getLimsid() {
			return limsid;
		}


		public void setLimsid(String limsid) {
			this.limsid = limsid;
		}
		
		

	}

	public ClarityInput getInput() {
		return input;
	}

	public void setInput(ClarityInput input) {
		this.input = input;
	}

	public ClarityOutput getOutput() {
		return output;
	}

	public void setOutput(ClarityOutput output) {
		this.output = output;
	}

}
