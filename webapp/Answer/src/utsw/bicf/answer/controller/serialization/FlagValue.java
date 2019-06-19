package utsw.bicf.answer.controller.serialization;

import java.util.List;

public class FlagValue {
	
	public FlagValue() {
		super();
	}

	List<VuetifyIcon> iconFlags;
	
	public FlagValue(List<VuetifyIcon> iconFlags) {
		super();
		this.iconFlags = iconFlags;
	}

	public List<VuetifyIcon> getIconFlags() {
		return iconFlags;
	}

	public void setIconFlags(List<VuetifyIcon> iconFlags) {
		this.iconFlags = iconFlags;
	}
	
	
}
