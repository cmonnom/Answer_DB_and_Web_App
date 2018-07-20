package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name="individual_permission")
public class IndividualPermission {
	
	//Add any new permission name here. It's used by PermissionUtils
	//to match the fields in user.getIndividualPermission()
	public static final String CAN_VIEW = "canView";
	public static final String CAN_ANNOTATE = "canAnnotate";
	public static final String CAN_SELECT = "canSelect";
	public static final String CAN_ASSIGN = "canAssign";
	
	public IndividualPermission() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="individual_permission_id")
	@JsonIgnore
	Integer individualPermission;
	
	@Column(name="can_view")
	Boolean canView;
	
	@Column(name="can_annotate")
	Boolean canAnnotate;
	
	@Column(name="can_select")
	Boolean canSelect;
	
	@Column(name="can_assign")
	Boolean canAssign;
	
	@Column(name="admin")
	Boolean admin;

	public Integer getIndividualPermission() {
		return individualPermission;
	}

	public void setIndividualPermission(Integer individualPermission) {
		this.individualPermission = individualPermission;
	}

	public Boolean getCanView() {
		return canView;
	}

	public void setCanView(Boolean canView) {
		this.canView = canView;
	}

	public Boolean getCanAnnotate() {
		return canAnnotate;
	}

	public void setCanAnnotate(Boolean canAnnotate) {
		this.canAnnotate = canAnnotate;
	}

	public Boolean getCanSelect() {
		return canSelect;
	}

	public void setCanSelect(Boolean canSelect) {
		this.canSelect = canSelect;
	}

	public Boolean getCanAssign() {
		return canAssign;
	}

	public void setCanAssign(Boolean canAssign) {
		this.canAssign = canAssign;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
}
