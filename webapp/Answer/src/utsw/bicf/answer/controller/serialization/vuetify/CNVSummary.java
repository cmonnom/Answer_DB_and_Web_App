package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.hybrid.CNVRow;

public class CNVSummary extends Summary<CNVRow> {
	
	public CNVSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField) {
		super(createRows(modelDAO, aCase), uniqueIdField);
	}

	private static List<CNVRow> createRows(ModelDAO modelDAO, OrderCase aCase) {
		List<CNVRow> rows = new ArrayList<CNVRow>();
		for (CNV cnv : aCase.getCnvs()) {
			rows.add(new CNVRow(cnv));
			
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Header chrom = new Header("CHR", "chrom");
		chrom.setWidth("100px");
		headers.add(chrom);
		Header gene = new Header("Genes", "genes");
		gene.setWidth("200px");
		gene.setAlign("left");
		gene.setCanHighlight(true);
		headers.add(gene);
		Header cytoband = new Header("Cytoband", "cytoband");
		cytoband.setWidth("100px");
		cytoband.setAlign("left");
//		cytoband.setCanHighlight(true);
		headers.add(cytoband);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("50px");
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		headers.add(iconFlags);
		Header start = new Header("Start", "start");
		start.setWidth("100px");
		headers.add(start);
		Header end = new Header("End", "end");
		end.setWidth("100px");
		headers.add(end);
		Header aberrationType = new Header(new String[] {"Aberration", "Type"}, "aberrationType");
		aberrationType.setWidth("100px");
		headers.add(aberrationType);
		Header copyNumber = new Header(new String[] {"Copy", "Number"}, "copyNumber");
		copyNumber.setWidth("100px");
		headers.add(copyNumber);
		Header score = new Header("Score", "score");
		score.setWidth("100px");
		headers.add(score);
		//keep in the same order
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
	}
	

}
