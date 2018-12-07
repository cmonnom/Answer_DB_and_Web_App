package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.hybrid.VCFAnnotationRow;

public class VariantVcfAnnotationSummary extends Summary<VCFAnnotationRow> {
	

	public VariantVcfAnnotationSummary(List<VCFAnnotationRow> vcfAnnotations, String uniqueIdField, boolean actionable) {
		super(vcfAnnotations, uniqueIdField, actionable);
		
	}

	@Override
	public void initializeHeaders() {
		if (this.actionable) {
			Header actions = new Header("Change", "actions");
			actions.setButtons(true);
			actions.setAlign("center");
			headers.add(actions);
		}
		Header allele = new Header("Allele", "allele");
		headers.add(allele);
		Header effects = new Header("Effects", "effects");
		effects.setWidth("225px");
		headers.add(effects);
		Header impact = new Header("Impact", "impact");
		headers.add(impact);
		Header geneName = new Header("Gene", "geneName");
		headers.add(geneName);
		Header geneId = new Header(new String[] { "Ensemble", "Gene Id" }, "geneId");
		headers.add(geneId);
		Header featureType = new Header(new String[] { "Feature", "Type" }, "featureType");
		headers.add(featureType);
		Header featureId = new Header(new String[] { "Feature", "Id" }, "featureId");
		headers.add(featureId);
		Header transcriptBiotype = new Header(new String[] { "Transcript", "Biotype" }, "transcriptBiotype");
		headers.add(transcriptBiotype);
		Header rank = new Header("Exon #", "rank");
		headers.add(rank);
		Header codingNotation = new Header(new String[] { "Coding", "Notation" }, "codingNotation");
		headers.add(codingNotation);
		Header proteinNotation = new Header(new String[] { "Protein", "Notation" }, "proteinNotation");
		headers.add(proteinNotation);
		Header proteinPosition = new Header(new String[] { "Protein", "Position" }, "proteinPosition");
		headers.add(proteinPosition);
		Header cdnaPosition = new Header(new String[] { "CDNA", "Position" }, "cdnaPosition");
		headers.add(cdnaPosition);
		Header cdsPosition = new Header(new String[] { "CDS", "Position" }, "cdsPosition");
		headers.add(cdsPosition);
		Header distanceToFeature = new Header(new String[] { "Distance To", "Feature" }, "distanceToFeature");
		headers.add(distanceToFeature);
		Header appris = new Header("Appris", "appris");
		headers.add(appris);
		Header tsl = new Header("TSL", "tsl");
		headers.add(tsl);

		// keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());

	}

}