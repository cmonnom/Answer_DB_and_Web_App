package utsw.bicf.answer.controller.serialization;

import java.util.ArrayList;
import java.util.List;

public class TreeViewItem {
	
	String name;
	String stId;
	String url;
	List<TreeViewItem> children = new ArrayList<TreeViewItem>();
	Boolean rootLevel = false;
	
	
	public TreeViewItem() {
		super();
	}
	public TreeViewItem(String name, String stId, String url) {
		super();
		this.name = name;
		this.stId = stId;
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStId() {
		return stId;
	}
	public void setStId(String stId) {
		this.stId = stId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<TreeViewItem> getChildren() {
		return children;
	}
	public void setChildren(List<TreeViewItem> children) {
		this.children = children;
	}
	public Boolean getRootLevel() {
		return rootLevel;
	}
	public void setRootLevel(Boolean rootLevel) {
		this.rootLevel = rootLevel;
	}
	

}
