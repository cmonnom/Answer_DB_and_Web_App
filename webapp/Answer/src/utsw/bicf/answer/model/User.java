package utsw.bicf.answer.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="answer_user")
public class User {
	
	public User() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="answer_user_id")
	Integer userId;
	
	@Column(name="first")
	String first;
	
	@Column(name="last")
	String last;
	
	@Column(name="username")
	String username;
	
	@Column(name="email")
	String email;
	
	@OneToOne
	@JoinColumn(name="individual_permission_id", unique=true)
	IndividualPermission individualPermission;
	
	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	@JoinTable(name="answer_user_answer_group",
	joinColumns=@JoinColumn(name="answer_user_id"),
	inverseJoinColumns=@JoinColumn(name="answer_group_id"))
	List<Group> groups;
	
	/**
	  * Some attempt at building a leaderboard. For now the idea doesn't seem
	  * constructive enough.
	  * This class is not currently used.
	 */
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="user_rank_id")
	UserRank userRank;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="user_pref_id", unique=true)
	UserPref userPref;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

//	public Permission getPermission() {
//		return permission;
//	}
//
//	public void setPermission(Permission permission) {
//		this.permission = permission;
//	}
	
	public String getFullName() {
		return this.first + " " + this.last;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public IndividualPermission getIndividualPermission() {
		return individualPermission;
	}

	public void setIndividualPermission(IndividualPermission individualPermission) {
		this.individualPermission = individualPermission;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   User rhs = (User) obj;
		   return new EqualsBuilder()
//		                 .appendSuper(super.equals(obj))
		                 .append(userId, rhs.userId)
		                 .isEquals();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public UserRank getUserRank() {
		return userRank;
	}

	public void setUserRank(UserRank userRank) {
		this.userRank = userRank;
	}

	public UserPref getUserPref() {
		return userPref;
	}

	public void setUserPref(UserPref userPref) {
		this.userPref = userPref;
	}

	@Override
	public String toString() {
		return this.getFullName();
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
