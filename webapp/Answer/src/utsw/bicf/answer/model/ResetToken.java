package utsw.bicf.answer.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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
@Table(name="reset_token")
public class ResetToken {

	public ResetToken() {
		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="reset_token_id")
	Integer resetTokenId;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="answer_user_id", unique=true)
	User user;
	
	@Column(name="token")
	String token;
	
	@Column(name="date_created")
	LocalDateTime dateCreated;

	public Integer getResetTokenId() {
		return resetTokenId;
	}

	public void setResetTokenId(Integer resetTokenId) {
		this.resetTokenId = resetTokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	
	
	
}
