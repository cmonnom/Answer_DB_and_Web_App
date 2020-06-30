package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.PassableValue;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;

public class UserTableRow {
	
	Integer userId;
	String fullName;
	String firstName;
	String lastName;
	String userName;
	String email;
	PassableValue viewValue;
	PassableValue annotateValue;
	PassableValue selectValue;
	PassableValue assignValue;
	PassableValue reviewValue;
	PassableValue hideValue;
	PassableValue notificationValue;
	PassableValue adminValue;
	String groupsConcat;
	List<String> groupIds;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public UserTableRow(User user) {
		this.userId = user.getUserId();
		this.groupsConcat = user.getGroups() != null ? user.getGroups().stream().map(g -> g.getName()).collect(Collectors.joining("<br/>")) : "";
		this.groupIds = user.getGroups() != null ? user.getGroups().stream().map(g -> g.getGroupId() + "").collect(Collectors.toList()) : null;
		this.firstName = user.getFirst();
		this.lastName = user.getLast();
		this.fullName = user.getFirst() + " " + user.getLast();
		this.userName = user.getUsername();
		this.email = user.getEmail();
		IndividualPermission p = user.getIndividualPermission();
		
		viewValue = new PassableValue("viewValue", "", p.getCanView());
		annotateValue = new PassableValue("annotateValue", "", p.getCanAnnotate());
		selectValue = new PassableValue("selectValue", "", p.getCanSelect());
		assignValue = new PassableValue("assignValue", "", p.getCanAssign());
		reviewValue = new PassableValue("reviewValue", "", p.getCanReview());
		hideValue = new PassableValue("hideValue", "", p.getCanHide());
		notificationValue = new PassableValue("notificationValue", "", p.getReceiveAllNotifications());
		adminValue = new PassableValue("adminValue", "", p.getAdmin());
		
		buttons.add(new Button("create", "editUser", "Edit User", "info"));
		buttons.add(new Button("block", "blockUser", "Block User", "error"));
	}

	public String getFullName() {
		return fullName;
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

	public String getEmail() {
		return email;
	}

	public PassableValue getAnnotateValue() {
		return annotateValue;
	}

	public PassableValue getSelectValue() {
		return selectValue;
	}

	public PassableValue getAssignValue() {
		return assignValue;
	}

	public PassableValue getReviewValue() {
		return reviewValue;
	}

	public PassableValue getNotificationValue() {
		return notificationValue;
	}

	public String getGroupsConcat() {
		return groupsConcat;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public PassableValue getHideValue() {
		return hideValue;
	}



}
