package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="answer_db_credentials")
public class AnswerDBCredentials {
	
	public AnswerDBCredentials() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="answer_db_credentials_id")
	Integer answerDBCredentialsId;
	
	@Column(name="token")
	String token;
	
	@Column(name="url")
	String url;
	
	@Column(name="username")
	String username;
	
	@Column(name="password")
	String password;

	public Integer getAnswerDBCredentialsId() {
		return answerDBCredentialsId;
	}

	public void setAnswerDBCredentialsId(Integer answerDBCredentialsId) {
		this.answerDBCredentialsId = answerDBCredentialsId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
