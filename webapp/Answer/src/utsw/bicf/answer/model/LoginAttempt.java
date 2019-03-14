package utsw.bicf.answer.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="login_attempt")
public class LoginAttempt {
	
	public LoginAttempt() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="login_attempt_id")
	@JsonIgnore
	Integer loginAttemptId;
	
	@Column(name="counter")
	Integer counter;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="answer_user_id", unique=true)
	User user;
	
	@Column(name="last_attempt_datetime")
	LocalDateTime lastAttemptDatetime;

	public Integer getLoginAttemptId() {
		return loginAttemptId;
	}

	public void setLoginAttemptId(Integer loginAttemptId) {
		this.loginAttemptId = loginAttemptId;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getLastAttemptDatetime() {
		return lastAttemptDatetime;
	}

	public void setLastAttemptDatetime(LocalDateTime lastAttemptDatetime) {
		this.lastAttemptDatetime = lastAttemptDatetime;
	}

	
}
