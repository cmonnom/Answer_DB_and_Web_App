package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="user_pref")
public class UserPref {
	
	public UserPref() {
		this.showGoodies = true;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_pref_id")
	@JsonIgnore
	Integer userPrefId;
	
	@Transient
	Boolean isAllowed;
	@Transient
	boolean success;
	
	@Column(name="show_goodies")
	Boolean showGoodies;
	
	@Column(name="home_tab")
	String homeTab;
	
	public Integer getUserPrefId() {
		return userPrefId;
	}

	public void setUserPrefId(Integer userPrefId) {
		this.userPrefId = userPrefId;
	}

	public Boolean getShowGoodies() {
		return showGoodies;
	}

	public void setShowGoodies(Boolean showGoodies) {
		this.showGoodies = showGoodies;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getHomeTab() {
		return homeTab;
	}

	public void setHomeTab(String homeTab) {
		this.homeTab = homeTab;
	}
	
	
	
}
