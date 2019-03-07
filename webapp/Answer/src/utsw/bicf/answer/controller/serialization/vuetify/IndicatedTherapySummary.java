package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.IndicatedTherapy;

public class IndicatedTherapySummary extends Summary<IndicatedTherapy> {

	public IndicatedTherapySummary() {
		super();
	}
	
	public IndicatedTherapySummary(List<IndicatedTherapy> indicatedTherapies, String uniqueIdField) {
		super(indicatedTherapies, uniqueIdField, null);
	}

	@Override
	public void initializeHeaders() {
		Header drugs = new Header("Drugs", "drugs");
		drugs.setWidth("200px");
		drugs.setIsSafe(true);
		headers.add(drugs);
		Header variant = new Header("Variant", "variant");
		variant.setWidth("150px");
		variant.setIsSafe(true);
		headers.add(variant);
		Header level = new Header("Level", "level");
		level.setWidth("150px");
		level.setIsSafe(true);
		headers.add(level);
		Header indication = new Header("Indication", "indication");
		indication.setAlign("left");
		indication.setWidth("400px");
		indication.setIsSafe(false);
		headers.add(indication);

	}

}