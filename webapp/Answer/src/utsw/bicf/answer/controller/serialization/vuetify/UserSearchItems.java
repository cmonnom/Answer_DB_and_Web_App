package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.User;

public class UserSearchItems extends SearchItems {
	
	public UserSearchItems(List<User> users) {
		super();
		this.items = users.stream()
				.map(user -> new SearchItemString(user.getFullName(), user.getUserId().toString()))
				.collect(Collectors.toList());
	}

}



