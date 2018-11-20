package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.UserRank;

/**
 * Some attempt at building a leaderboard. For now the idea doesn't seem
 * constructive enough.
 * This class is not currently used.
 * @author Guillaume
 *
 */
public class UserLeaderBoardInfo {
	
	String fullName;
	String rankTitle;
	Integer level;
	String icon;
	String color;
	Boolean isAllowed = true;
	

	public UserLeaderBoardInfo() {
	}
	
	public UserLeaderBoardInfo(User user, ModelDAO model) {
		this.fullName = user.getFullName();
		UserRank rank = user.getUserRank();
		if (rank == null) { //update user with new rank if needed
			rank = model.getFirstRank(); 
			user.setUserRank(rank);
			model.saveObject(user);
		}
		this.rankTitle = rank.getTitle();
		this.level = rank.getLevel();
		this.icon = rank.getIcon();
		this.color = rank.getColor();
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRankTitle() {
		return rankTitle;
	}

	public void setRankTitle(String rankTitle) {
		this.rankTitle = rankTitle;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
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
