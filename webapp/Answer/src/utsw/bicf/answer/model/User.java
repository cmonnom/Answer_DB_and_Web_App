package utsw.bicf.answer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.model.extmapping.MongoDBId;

@Entity
@Table(name="answer_user")
public class User {
	
	public User() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="answer_user_id")
	@JsonIgnore
	Integer userId;
	
	@Column(name="first")
	String first;
	
	@Column(name="last")
	String last;
	
	@JsonIgnore
	@Column(name="username")
	String username;
	
	@Column(name="email")
	String email;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="individual_permission_id", unique=true)
	IndividualPermission individualPermission;
	
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

}
