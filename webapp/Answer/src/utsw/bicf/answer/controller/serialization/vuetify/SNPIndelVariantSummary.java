package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

public class SNPIndelVariantSummary extends Summary<SNPIndelVariantRow> {
	
	public SNPIndelVariantSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField
			, List<ReportGroupForDisplay> reportGroups, List<HeaderOrder> headerOrders) {
		super(createRows(modelDAO, aCase, reportGroups, headerOrders), uniqueIdField, headerOrders);
	}

	private static List<SNPIndelVariantRow> createRows(ModelDAO modelDAO, OrderCase aCase, 
			List<ReportGroupForDisplay> reportGroups, List<HeaderOrder> headerOrders) {
		List<SNPIndelVariantRow> rows = new ArrayList<SNPIndelVariantRow>();
		for (Variant variant : aCase.getVariants()) {
			rows.add(new SNPIndelVariantRow(variant, reportGroups, aCase.getTotalCases()));
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Header chromPos = new Header("CHR", "chromPos");
		chromPos.setWidth("200px");
		chromPos.setAlign("right");
		headers.add(chromPos);
		Header geneVariant = new Header("Gene Variant", "geneVariant");
		geneVariant.setWidth("225px");
		geneVariant.setWidthValue(225);
		headers.add(geneVariant);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("150px");
		iconFlags.setWidthValue(150);
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		iconFlags.setAlign("left");
		headers.add(iconFlags);
		Header exonNb = new Header("Exon #", "rank");
		headers.add(exonNb);
		Header effects = new Header("Effects", "effects");
		effects.setWidth("220px");
		effects.setWidthValue(220);
		headers.add(effects);
		Header ref = new Header(new String[] {"Reference", "Allele(s)"}, "reference");
		headers.add(ref);
		Header alt = new Header(new String[] {"Alternate", "Allele(s)"}, "alt");
		headers.add(alt);
		Header tumorTotalDepth = new Header(new String[] {"Tumor"," Total Depth"}, "tumorTotalDepth", Units.NB);
		headers.add(tumorTotalDepth);
		Header taf = new Header(new String[] {"Tumor Alt", "Percent"}, "tumorAltFrequency", Units.PCT);
		taf.setWidth("100px");
		headers.add(taf);
//		Header tumorAltDepth = new Header(new String[] {"Tumor","Depth"}, "tumorAltDepth", Units.NB);
//		headers.add(tumorAltDepth);
		Header normalTotalDepth = new Header(new String[] {"Normal"," Total Depth"}, "normalTotalDepth", Units.NB);
		headers.add(normalTotalDepth);
		Header naf = new Header(new String[] {"Normal Alt", "Percent"}, "normalAltFrequency", Units.PCT);
		naf.setWidth("100px");
		headers.add(naf);
//		Header normalAltDepth = new Header(new String[] {"Normal","Depth"}, "normalAltDepth", Units.NB);
//		headers.add(normalAltDepth);
		Header rnaTotalDepth = new Header(new String[] {"RNA"," Total Depth"}, "rnaTotalDepth", Units.NB);
		headers.add(rnaTotalDepth);
		Header raf = new Header(new String[] {"RNA Alt", "Percent"}, "rnaAltFrequency", Units.PCT);
		raf.setWidth("100px");
		headers.add(raf);
		
		Header numCasesSeen = new Header(new String[] {"Nb Cases", "Seen"}, "numCasesSeenFormatted", Units.NB);
		numCasesSeen.setWidth("50px");
		headers.add(numCasesSeen);
		
		Header numCasesInCosmic = new Header(new String[] {"Nb Cases", "In Cosmic"}, "nbOfCosmicCases", Units.NB);
		numCasesInCosmic.setWidth("50px");
		headers.add(numCasesInCosmic);
		
		Header exacAlleleFrequency = new Header(new String[] {"ExAC Allele", "Percent"}, "exacAlleleFrequency", Units.PCT);
		exacAlleleFrequency.setWidth("100px");
		headers.add(exacAlleleFrequency);
		
		Header somaticStatus = new Header(new String[] {"Somatic", "Status"}, "somaticStatus");
		somaticStatus.setWidth("100px");
		headers.add(somaticStatus);
		
		Header gnomadPopmaxAlleleFrequency = new Header(new String[] {"gnomAD Pop. Max.", "Allele Frequency"}, "gnomadPopmaxAlleleFrequency", Units.PCT);
		gnomadPopmaxAlleleFrequency.setWidth("100px");
		headers.add(gnomadPopmaxAlleleFrequency);
		
//		Header rnaAltDepth = new Header(new String[] {"RNA","Depth"}, "rnaAltDepth", Units.NB);
//		headers.add(rnaAltDepth);
		
	}
	
}
