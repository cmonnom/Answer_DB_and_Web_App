package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.controller.serialization.TreeViewItem;

public class TreeViewSummary {
	
	List<TreeViewItem> items;
	String mainPageUrl;

	public TreeViewSummary() {
		super();
	}

	public TreeViewSummary(List<TreeViewItem> items) {
		super();
		this.items = items;
	}

	public List<TreeViewItem> getItems() {
		return items;
	}

	public void setItems(List<TreeViewItem> items) {
		this.items = items;
	}

	public String getMainPageUrl() {
		return mainPageUrl;
	}

	public void setMainPageUrl(String mainPageUrl) {
		this.mainPageUrl = mainPageUrl;
	}

}
