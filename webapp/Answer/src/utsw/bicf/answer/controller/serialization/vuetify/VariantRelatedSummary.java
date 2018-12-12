package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class VariantRelatedSummary extends Summary<Variant> {

	public VariantRelatedSummary(List<Variant> relatedVariants, String uniqueIdField, List<HeaderOrder> headerOrders) {
		super(relatedVariants, uniqueIdField, headerOrders);
	}

	@Override
	public void initializeHeaders() {
		Header chromPos = new Header("CHR", "chrom");
		chromPos.setWidth("200px");
		headers.add(chromPos);
		Header geneVariant = new Header("Position", "pos");
		geneVariant.setWidth("225px");
		headers.add(geneVariant);
		Header notation = new Header("Notation", "notation");
		notation.setWidth("200px");
		headers.add(notation);
		Header alt = new Header("Alt", "alt");
		headers.add(alt);


	}

}