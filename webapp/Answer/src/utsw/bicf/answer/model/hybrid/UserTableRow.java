package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.PassableValue;
import utsw.bicf.answer.model.Permission;
import utsw.bicf.answer.model.User;

public class UserTableRow {
	
	Integer userId;
	String fullName;
	String firstName;
	String lastName;
	String userName;
	PassableValue viewValue;
	PassableValue editValue;
	PassableValue finalizeValue;
	PassableValue adminValue;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public UserTableRow(User user) {
		this.userId = user.getUserId();
		this.firstName = user.getFirst();
		this.lastName = user.getLast();
		this.fullName = user.getFirst() + " " + user.getLast();
		this.userName = user.getUsername();
		Permission p = user.getPermission();
		
		viewValue = new PassableValue("viewValue", "", p.getView());
		editValue = new PassableValue("editValue", "", p.getEdit());
		finalizeValue = new PassableValue("finalizeValue", "", p.getFinalize());
		adminValue = new PassableValue("adminValue", "", p.getAdmin());
		
		buttons.add(new Button("create", "editUser", "Edit User", "info"));
		buttons.add(new Button("block", "blockUser", "Block User", "error"));
	}

	public String getFullName() {
		return fullName;
	}


	public PassableValue getEditValue() {
		return editValue;
	}


	public PassableValue getFinalizeValue() {
		return finalizeValue;
	}

	public PassableValue getAdminValue() {
		return adminValue;
	}

	public PassableValue getViewValue() {
		return viewValue;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}



}
