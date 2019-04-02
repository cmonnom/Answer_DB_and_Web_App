package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.Group;

public class GroupSearchItems extends SearchItems {
	
	public GroupSearchItems(List<Group> groups) {
		super();
		this.items = groups.stream()
				.map(group -> new SearchItemString(group.getName(), group.getGroupId().toString()))
				.collect(Collectors.toList());
	}

}



