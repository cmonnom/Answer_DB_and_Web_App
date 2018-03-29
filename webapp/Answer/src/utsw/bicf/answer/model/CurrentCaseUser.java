package utsw.bicf.answer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Table to record the current case a user is working on.
 * This way we don't rely on passing case ids and such back and forth with the front end
 * @author Guillaume
 *
 */
@Entity
@Table(name="current_case_user")
public class CurrentCaseUser {
	
	public CurrentCaseUser() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="current_case_user_id")
	Integer currentCaseUserId;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="order_case_id")
	OrderCase orderCase;
	
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="answer_user_id")
	User user;

	public Integer getCurrentCaseUserId() {
		return currentCaseUserId;
	}

	public void setCurrentCaseUserId(Integer currentCaseUserId) {
		this.currentCaseUserId = currentCaseUserId;
	}

	public OrderCase getOrderCase() {
		return orderCase;
	}

	public void setOrderCase(OrderCase orderCase) {
		this.orderCase = orderCase;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

	
	
}
