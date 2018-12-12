package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.hybrid.ClinicalSignificance;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class ClinicalSignificanceSummary extends Summary<ClinicalSignificance> {

	public ClinicalSignificanceSummary() {
		super();
	}
	
	public ClinicalSignificanceSummary(List<ClinicalSignificance> clinicalSignificanceList, String uniqueIdField, List<HeaderOrder> headerOrders) {
		super(clinicalSignificanceList, uniqueIdField, headerOrders);
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

	}

}