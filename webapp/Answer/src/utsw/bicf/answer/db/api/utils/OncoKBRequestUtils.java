package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.model.extmapping.lookup.LookupSummary;
import utsw.bicf.answer.model.extmapping.oncokb.EvidenceResponse;
import utsw.bicf.answer.model.extmapping.oncokb.OncoKBResponse;
import utsw.bicf.answer.security.OncoKBProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class OncoKBRequestUtils extends AbstractRequestUtils{

	OncoKBProperties oncoKBProps;

	public OncoKBRequestUtils(OncoKBProperties oncoKBProps, OtherProperties otherProps) {
		this.oncoKBProps = oncoKBProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	public LookupSummary getGeneSummary(String geneTerm)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(oncoKBProps.getSearchUrl());
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			OncoKBResponse[] oncoKBJson = mapper.readValue(response.getEntity().getContent(), OncoKBResponse[].class);
			Integer entrezId = null;
			for (int i = 0; i < oncoKBJson.length; i++) {
				if (oncoKBJson[i].getHugoSymbol().equals(geneTerm)) {
					entrezId = oncoKBJson[i].getEntrezGeneId();
					break;
				}
			}
			if (entrezId != null) {
				sbUrl = new StringBuilder(oncoKBProps.getSummaryUrl()).append(entrezId).append("/evidences?evidenceTypes=GENE_BACKGROUND");
				uri = new URI(sbUrl.toString());
				requestGet = new HttpGet(uri);
				context = HttpClientContext.create();
				response = client.execute(requestGet, context);

				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					OncoKBResponse[] oncoKBSummaryJson = mapper.readValue(response.getEntity().getContent(), OncoKBResponse[].class);
					if (oncoKBSummaryJson.length > 0) {
						
					}
					summary.setSummary(oncoKBSummaryJson[0].getDesc());
					summary.setMoreInfoUrl(oncoKBProps.getOncoKBGeneUrl() + entrezId);
				}
			}
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return summary;
	}
	
	public LookupSummary getGeneSummaryByEntrezId(String entrezId)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		LookupSummary summary = new LookupSummary();
		StringBuilder sbUrl = new StringBuilder(oncoKBProps.getSummaryUrl()).append(entrezId).append("/evidences?evidenceTypes=GENE_BACKGROUND");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			OncoKBResponse[] oncoKBSummaryJson = mapper.readValue(response.getEntity().getContent(), OncoKBResponse[].class);
			if (oncoKBSummaryJson.length > 0) {
				
			}
			summary.setSummary(oncoKBSummaryJson[0].getDesc());
			summary.setMoreInfoUrl(oncoKBProps.getOncoKBGeneUrl() + entrezId);
		}
		else {
			logger.info("Something went wrong OncoKBRequest:135 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return summary;
	}
	
	public EvidenceResponse[] getVariantSummary(String geneTerm, String variant, String oncotreeCode)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		StringBuilder sbUrl = new StringBuilder(oncoKBProps.getVariantSearchUrl());
		sbUrl.append(geneTerm).append("&variant=").append(StringEscapeUtils.escapeHtml4(variant))
		.append("&tumorType=").append(oncotreeCode)
		.append("&source=oncotree");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			EvidenceResponse[] oncoKBJson = mapper.readValue(response.getEntity().getContent(), EvidenceResponse[].class);
			this.closeGetRequest();
			return oncoKBJson;
		}
		else {
			logger.info("Something went wrong OncoKBRequest:158 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return null;
	}

}
