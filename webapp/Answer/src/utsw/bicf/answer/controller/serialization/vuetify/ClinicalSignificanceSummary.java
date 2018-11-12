package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.hybrid.ClinicalSignificance;

public class ClinicalSignificanceSummary extends Summary<ClinicalSignificance> {

	public ClinicalSignificanceSummary() {
		super();
	}
	
	public ClinicalSignificanceSummary(List<ClinicalSignificance> clinicalSignificanceList, String uniqueIdField) {
		super(clinicalSignificanceList, uniqueIdField);
	}

	@Override
	public void initializeHeaders() {
		Header variant = new Header("Variant", "geneVariant");
		variant.setWidth("200px");
		variant.setAlign("left");
		headers.add(variant);
		Header category = new Header("Category", "category");
		category.setWidth("150px");
		category.setAlign("left");
		headers.add(category);
		Header annotation = new Header("Annotation", "annotation");
		annotation.setAlign("left");
		headers.add(annotation);

		// keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());

	}

}