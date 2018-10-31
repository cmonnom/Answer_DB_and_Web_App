package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;

public class GenesInPanelItems extends SearchItems {
	
	public GenesInPanelItems(List<String> genes) {
		super();
		this.items = (genes.stream()
				.map(g -> new SearchItemString(g, g)))
				.collect(Collectors.toList());
	}

}



