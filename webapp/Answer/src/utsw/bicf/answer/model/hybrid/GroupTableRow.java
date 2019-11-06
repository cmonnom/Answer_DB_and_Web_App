package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.Group;

public class GroupTableRow {
	
	Integer groupId;
	String name;
	String description;
	String usersConcat;
	List<String> userIds;
	
	
	List<Button> buttons = new ArrayList<Button>();
	
	public GroupTableRow(Group group) {
		this.groupId = group.getGroupId();
		this.name = group.getName();
		this.usersConcat = group.getUsers() != null ? group.getUsers().stream().map(u -> u.getFirst() + "&nbsp;" + u.getLast()).collect(Collectors.joining(", ")) : "";
		this.userIds = group.getUsers() != null ? group.getUsers().stream().map(u -> u.getUserId() + "").collect(Collectors.toList()) : null;
		this.description = group.getDescription();
		
		buttons.add(new Button("create", "editGroup", "Edit Group", "info"));
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public String getUsersConcat() {
		return usersConcat;
	}

	public void setUsersConcat(String usersConcat) {
		this.usersConcat = usersConcat;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}




}
