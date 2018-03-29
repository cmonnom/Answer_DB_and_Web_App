package utsw.bicf.answer.clarity.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarityTapeStation extends ClarityProcess implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "protocol-name")
	ClarityValue protocolName;

	public ClarityTapeStation() {
		super();
	}

	public ClarityValue getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(ClarityValue protocolName) {
		this.protocolName = protocolName;
	}

//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder("Machine Name: ");
//		sb.append(getMachineName()).append(" | LIMS: ").append(getLimsid()).append(" | FlowCell ID: ")
//		.append(getFlowCellId()).append(" |  technician: ").append(getTechnician())
//				.append(" | URI: ").append(getUri());
//		return sb.toString();
//	}
	
}
