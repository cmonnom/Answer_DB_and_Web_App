package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.extmapping.Variant;

public class VariantsForGeneItems extends SearchItems {
	
	String annotationId;
	
	public VariantsForGeneItems(List<Variant> variants, String annotationId) {
		super();
		this.items = (variants.stream()
				.map(v -> new SearchItemString(v.getNotation(), v.getMongoDBId().getOid())))
				.collect(Collectors.toList());
		this.annotationId = annotationId;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(String annotationId) {
		this.annotationId = annotationId;
	}

}



