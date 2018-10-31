package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.Set;
import java.util.stream.Collectors;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;

public class CNVChromosomeItems extends SearchItems {
	
	public CNVChromosomeItems(Set<String> chrItems) {
		super();
		this.items = (chrItems.stream()
				.map(chr -> new SearchItemString(TypeUtils.formatChromosome(chr), chr)))
				.sorted()
				.collect(Collectors.toList());
	}

}



