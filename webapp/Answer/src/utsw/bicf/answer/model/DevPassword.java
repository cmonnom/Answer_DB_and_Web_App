package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dev_password")
/**
 * This table only has temporary passwords training accounts
 * Only works if auth.type=dev
 * @author Guillaume
 *
 */
public class DevPassword {
	
	public DevPassword() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dev_password_id")
	Integer devPasswordId;
	
	@Column(name="answer_user_id")
	Integer answerUserId;
	
	@Column(name="password")
	String password;

	public Integer getDevPasswordId() {
		return devPasswordId;
	}

	public void setDevPasswordId(Integer devPasswordId) {
		this.devPasswordId = devPasswordId;
	}

	public Integer getAnswerUserId() {
		return answerUserId;
	}

	public void setAnswerUserId(Integer answerUserId) {
		this.answerUserId = answerUserId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
