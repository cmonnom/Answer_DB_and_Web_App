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
		Header variant = new Header("Variant", "variant");
		variant.setWidth("150px");
		headers.add(variant);
		Header level = new Header("Level", "level");
		level.setWidth("150px");
		headers.add(level);
		Header indication = new Header("Indication", "indication");
		indication.setAlign("left");
		headers.add(indication);

	}

}