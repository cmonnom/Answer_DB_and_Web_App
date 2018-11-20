package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
  * Some attempt at building a leaderboard. For now the idea doesn't seem
  * constructive enough.
  * This class is not currently used.
 */
@Entity
@Table(name="user_rank")
public class UserRank {
	
	public UserRank() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_rank_id")
	@JsonIgnore
	Integer userRankId;
	
	@Column(name="title")
	String title;
	
	@Column(name="level")
	Integer level;
	
	@JsonIgnore
	@Column(name="threshold")
	String threshold;
	
	@Column(name="icon")
	String icon;
	
	@Column(name="color")
	String color;

	public Integer getUserRankId() {
		return userRankId;
	}

	public void setUserRankId(Integer userRankId) {
		this.userRankId = userRankId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

}
