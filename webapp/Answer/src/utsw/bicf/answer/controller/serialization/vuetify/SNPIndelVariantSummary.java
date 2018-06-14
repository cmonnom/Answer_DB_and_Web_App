package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

public class SNPIndelVariantSummary extends Summary<SNPIndelVariantRow> {
	
	public SNPIndelVariantSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField) {
		super(createRows(modelDAO, aCase), uniqueIdField);
	}

	private static List<SNPIndelVariantRow> createRows(ModelDAO modelDAO, OrderCase aCase) {
		List<SNPIndelVariantRow> rows = new ArrayList<SNPIndelVariantRow>();
		for (Variant variant : aCase.getVariants()) {
			rows.add(new SNPIndelVariantRow(variant));
			
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Header chromPos = new Header("CHR", "chromPos");
		chromPos.setWidth("200px");
		headers.add(chromPos);
		Header geneVariant = new Header("Gene Variant", "geneVariant");
		geneVariant.setWidth("225px");
		headers.add(geneVariant);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("150px");
		iconFlags.setIsFlag(true);
		headers.add(iconFlags);
		Header effects = new Header("Effects", "effects");
		headers.add(effects);
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
//		Header rnaAltDepth = new Header(new String[] {"RNA","Depth"}, "rnaAltDepth", Units.NB);
//		headers.add(rnaAltDepth);
		//keep in the same order
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
	}
	
}
