package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.IndicatedTherapy;

public class IndicatedTherapySummary extends Summary<IndicatedTherapy> {

	public IndicatedTherapySummary() {
		super();
	}
	
	public IndicatedTherapySummary(List<IndicatedTherapy> indicatedTherapies, String uniqueIdField) {
		super(indicatedTherapies, uniqueIdField);
	}

	@Override
	public void initializeHeaders() {
		Header gene = new Header("Gene", "gene");
		headers.add(gene);
		Header variant = new Header("Variant", "variant");
		headers.add(variant);
		Header level = new Header("Level", "level");
		headers.add(level);
		Header indication = new Header("Indication", "indication");
		headers.add(indication);

		// keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());

	}

}