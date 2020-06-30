package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.clinicaltrials.ClinicalTrial;
import utsw.bicf.answer.model.extmapping.clinicaltrials.ClinicalTrialResponse;
import utsw.bicf.answer.model.extmapping.oncotree.OncotreeTumorType;
import utsw.bicf.answer.security.ClinicalTrialsProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class ClinicalTrialsRequestUtils  extends AbstractRequestUtils{

	ClinicalTrialsProperties clinicalTrialsProps;

	public ClinicalTrialsRequestUtils(ClinicalTrialsProperties clinicalTrialsProps, OtherProperties otherProps) {
		this.clinicalTrialsProps = clinicalTrialsProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	public List<ClinicalTrial> getClinicalTrials(String geneTerm, String variant, OncotreeTumorType tumorType, boolean fusion) throws URISyntaxException, ClientProtocolException, IOException {
		List<ClinicalTrial> clinicalTrials = new ArrayList<ClinicalTrial>();
			parseResponse(clinicalTrials, geneTerm, variant, tumorType, fusion);
			return clinicalTrials;
	}

	public void parseResponse(List<ClinicalTrial> clinicalTrials,
			String geneTerm, String variant, OncotreeTumorType tumorType, boolean fusion)
			throws IOException, JsonParseException, JsonMappingException, URISyntaxException {
		String url = null;
		if (fusion) {
			url = this.buildURLFusion(geneTerm, variant, tumorType.getName());
		}
		else {
			url = this.buildURL(geneTerm, variant, tumorType.getName());
		}
		ObjectMapper mapper = new ObjectMapper();
		this.addClinicalTrials(url, mapper, clinicalTrials);
		if (clinicalTrials.size() <= 3) {
			if (fusion) {
				url = this.buildURLFusion(geneTerm, variant, tumorType.getMainType());
			}
			else {
				url = this.buildURL(geneTerm, variant, tumorType.getMainType());
			}
			this.addClinicalTrials(url, mapper, clinicalTrials);
		}
	}
	
	private void addClinicalTrials(String url, ObjectMapper mapper, List<ClinicalTrial> clinicalTrials) throws ClientProtocolException, IOException, URISyntaxException {
		URI uri = new URI(url);
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ClinicalTrialResponse trialJson = mapper.readValue(response.getEntity().getContent(), ClinicalTrialResponse.class);
			this.closeGetRequest();
			if (trialJson != null && trialJson.getStudyFieldsResponse() != null 
					&& trialJson.getStudyFieldsResponse().getClinicalTrials() != null) {
				clinicalTrials.addAll(trialJson.getStudyFieldsResponse().getClinicalTrials());
			}
		}
		else {
			logger.info("Something went wrong ClinicalTrialsRequestUtils:70 HTTP_STATUS: " + statusCode);
		}
	}
	
	private String buildURL(String geneTerm, String variant, String condition) {
		StringBuilder sbUrl = new StringBuilder(clinicalTrialsProps.getApiUrl());
		sbUrl.append("AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneTerm)
		.append(" AND AREA[EligibilityCriteria]").append(variant)
		.append("&fields=NCTId,BriefTitle,Condition,InterventionName&min_rnk=1&max_rnk=&fmt=json");
		return sbUrl.toString().replaceAll(" ", "+");
	}
	
	private String buildURLFusion(String geneFive, String geneThree, String condition) {
		StringBuilder sbUrl = new StringBuilder(clinicalTrialsProps.getApiUrl());
		sbUrl.append("AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneFive).append("-").append(geneThree)
		.append(" OR AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneFive).append(" fusion")
		.append(" OR AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneThree).append(" fusion")
		.append(" OR AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneFive).append(" rearrangement")
		.append(" OR AREA[Condition]").append(condition)
		.append(" AND AREA[EligibilityCriteria]").append(geneThree).append(" rearrangement")
		.append("&fields=NCTId,BriefTitle,Condition,InterventionName&min_rnk=1&max_rnk=&fmt=json");
		return sbUrl.toString().replaceAll(" ", "+");
	}
}
