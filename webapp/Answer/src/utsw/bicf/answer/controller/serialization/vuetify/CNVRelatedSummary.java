package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class CNVRelatedSummary extends Summary<CNV> {

	public CNVRelatedSummary(List<CNV> relatedCNVs, String uniqueIdField, List<HeaderOrder> headerOrders) {
		super(relatedCNVs, uniqueIdField, headerOrders);
	}

	@Override
	public void initializeHeaders() {
		Header copyNumber = new Header(new String[] {"Copy","Number"}, "copyNumber");
		copyNumber.setWidth("100px");
		headers.add(copyNumber);
		Header aberrationType = new Header(new String[] {"Aberration","Type"}, "aberrationType");
		aberrationType.setWidth("100px");
		headers.add(aberrationType);


	}

}