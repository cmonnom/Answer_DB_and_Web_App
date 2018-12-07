package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.controller.serialization.Utils;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.AnnotationSearchResult;
import utsw.bicf.answer.model.extmapping.CNRData;
import utsw.bicf.answer.model.extmapping.CNSData;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVPlotData;
import utsw.bicf.answer.model.extmapping.CNVPlotDataRaw;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.CaseAnnotation;
import utsw.bicf.answer.model.extmapping.ExistingReports;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.SearchSNPAnnotation;
import utsw.bicf.answer.model.extmapping.SelectedVariantIds;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.extmapping.Trial;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.QcAPIAuthentication;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class RequestUtils {

	ModelDAO modelDAO;
	AnswerDBCredentials dbProps;
	QcAPIAuthentication qcAPI;

	public RequestUtils(ModelDAO modelDAO) {
		super();
		this.modelDAO = modelDAO;
		this.dbProps = modelDAO.getAnswerDBCredentials();
	}
	
	public RequestUtils(QcAPIAuthentication qcAPI) {
		this.qcAPI = qcAPI;
	}

	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpClient client = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper();

	private void addAuthenticationHeader(HttpGet requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private void addAuthenticationHeader(HttpPost requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private void addAuthenticationHeader(HttpPut requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private String createAuthHeader() {
		String auth = dbProps.getUsername() + ":" + dbProps.getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth);
		return authHeader;
	}

	public OrderCase[] getActiveCases() throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("cases");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase[] cases = mapper.readValue(response.getEntity().getContent(), OrderCase[].class);
			return cases;
		}
		return null;
	}
	
	public void test() throws URISyntaxException, ClientProtocolException, IOException {
//		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
//		sbUrl.append("cases");
		URI uri = new URI("https://clinicaltrials.gov/ct2/show/" + "NCT02674568" + "?displayxml=true");
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			Object test = mapper.readValue(response.getEntity().getContent(), OrderCase[].class);
			System.out.println(test);
		}
	}

	public AjaxResponse assignCaseToUser(List<User> users, String caseId)
			throws ClientProtocolException, IOException, URISyntaxException {
		String userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.joining(","));
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/assignusers").append("?userIds=").append(userIds);
		URI uri = new URI(sbUrl.toString());

		requestPut = new HttpPut(uri);

		addAuthenticationHeader(requestPut);

		HttpResponse response = client.execute(requestPut);

		AjaxResponse ajaxResponse = new AjaxResponse();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return ajaxResponse;
	}

	public OrderCase getCaseDetails(String caseId, String data)
			throws ClientProtocolException, IOException, URISyntaxException {
		if (data == null) {
			data = "{\"filters\": []}";
		}
		VariantFilterList filterList = Utils.parseFilters(data, false);
		String filterParam = filterList.createJSON();
		System.out.println(filterParam);

		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/filter");
		URI uri = new URI(sbUrl.toString());

		// requestGet = new HttpGet(uri);
		// addAuthenticationHeader(requestGet);

		// TODO Ben needs to build the API for filtering
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		requestPost.setEntity(new StringEntity(filterParam, ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}

	/**
	 * Get all information about a variant, including annotations
	 * @param variantType 
	 * 
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Variant getVariantDetails(String variantId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("variant/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			
			Variant variant = mapper.readValue(response.getEntity().getContent(), Variant.class);
			return variant;
		}
		return null;
	}
	
	/**
	 * Get all information about a variant, including annotations
	 * @param variantType 
	 * 
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public CNV getCNVDetails(String variantId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("cnv/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CNV cnv = mapper.readValue(response.getEntity().getContent(), CNV.class);
			return cnv;
		}
		return null;
	}
	
	public Translocation getTranslocationDetails(String variantId) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("translocation/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			Translocation translocation = mapper.readValue(response.getEntity().getContent(), Translocation.class);
			return translocation;
		}
		return null;
	}

	public void saveVariantSelection(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds, 
			List<String> selectedCNVIds, List<String> selectedTranslocationIds)
			throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/selectvariants");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SelectedVariantIds variantIds = new SelectedVariantIds();
		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
		variantIds.setSelectedCNVIds(selectedCNVIds);
		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
	}

	public boolean commitAnnotation(AjaxResponse ajaxResponse, String caseId, String variantId,
			List<Annotation> annotations) throws URISyntaxException, ClientProtocolException, IOException {
		boolean didChange = false;
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("annotations/");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		System.out.println(mapper.writeValueAsString(annotations));
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(annotations), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			String r = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			didChange = r.contains("true");
			ajaxResponse.setSuccess(true);
		}
		return didChange;
	}

	/**
	 * Get a summary of the case with basic information
	 * and no variant info
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public OrderCase getCaseSummary(String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/summary");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}
	
	/**
	 * Get a summary of the case with basic information
	 * and no variant info
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public OrderCase saveCaseSummary(String caseId, OrderCase caseSummary) throws ClientProtocolException, IOException, URISyntaxException {

		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/summary");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(caseSummary), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}

	public CaseAnnotation getCaseAnnotation(String caseId) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/annotation");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CaseAnnotation caseAnnotation = mapper.readValue(response.getEntity().getContent(), CaseAnnotation.class);
			return caseAnnotation;
		}
		return null;
	}

	public void saveCaseAnnotation(AjaxResponse ajaxResponse, CaseAnnotation annotationToSave) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(annotationToSave.getCaseId()).append("/annotation");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		System.out.println(mapper.writeValueAsString(annotationToSave));
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(annotationToSave), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}

		
	}

	public void saveVariant(AjaxResponse ajaxResponse, Object variant, String variantType) throws URISyntaxException, ClientProtocolException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		String oid = null;
		if (variantType.equals("snp")) {
			oid = ((Variant) variant).getMongoDBId().getOid();
			sbUrl.append("variant/");
		}
		if (variantType.equals("cnv")) {
			oid = ((CNV) variant).getMongoDBId().getOid();
			sbUrl.append("cnv/");
		}
		else {
			ajaxResponse.setSuccess(false);
		}
		sbUrl.append(oid);
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(variant), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);
//		System.out.println(mapper.writeValueAsString(variant));

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}

	}
	
	public void saveSelectedAnnotations(AjaxResponse ajaxResponse, Object variant, String variantType, String oid) throws URISyntaxException, ClientProtocolException, IOException {
			ObjectMapper mapper = new ObjectMapper();
			StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
			if (variantType.equals("snp")) {
				sbUrl.append("variant/");
			}
			else if (variantType.equals("cnv")) {
				sbUrl.append("cnv/"); //TODO we might use the same api "variant"
			}
			else if (variantType.equals("translocation")) {
				sbUrl.append("translocation/");  //TODO we might use the same api "variant"
			}
			else {
				ajaxResponse.setSuccess(false);
			}
			sbUrl.append(oid).append("/selectannotations");
			URI uri = new URI(sbUrl.toString());
			requestPost = new HttpPost(uri);
			addAuthenticationHeader(requestPost);
			
			requestPost.setEntity(new StringEntity(mapper.writeValueAsString(variant), ContentType.APPLICATION_JSON));
			HttpResponse response = client.execute(requestPost);
//			System.out.println(mapper.writeValueAsString(variant));

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				ajaxResponse.setSuccess(false);
				ajaxResponse.setMessage("Something went wrong");
			}
			else {
				ajaxResponse.setSuccess(true);
				ajaxResponse.setIsAllowed(true);
			}
		
	}

	public void sendVariantSelectionToMDA(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds,
			List<String> selectedCNVIds, List<String> selectedTranslocationIds) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/sendToMDA");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SelectedVariantIds variantIds = new SelectedVariantIds();
		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
		variantIds.setSelectedCNVIds(selectedCNVIds);
		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMocliaContent(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds,
			List<String> selectedCNVIds, List<String> selectedTranslocationIds) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/moclia");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
//		SelectedVariantIds variantIds = new SelectedVariantIds();
//		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
//		variantIds.setSelectedCNVIds(selectedCNVIds);
//		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
//		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestGet);
		List<String> result = null;
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			result = mapper.readValue(response.getEntity().getContent(), List.class);
			if (result != null && !result.isEmpty()) {
				ajaxResponse.setSuccess(true);
			}
			else {
				ajaxResponse.setSuccess(false);
				ajaxResponse.setMessage("Nothing to do. No variant matching MDA requirements were selected.");
			}
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return result;
	}

	public AnnotationSearchResult getGetAnnotationsByGeneAndVariant(String gene, String variant) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("searchannotations/");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SearchSNPAnnotation search = new SearchSNPAnnotation();
		search.setGeneSymbolOrSynonym(gene);
		search.setVariant(variant);
		
		requestPost.setEntity(new StringEntity(search.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AnnotationSearchResult result = mapper.readValue(response.getEntity().getContent(), AnnotationSearchResult.class);
			return result;
		}
		return null;
	}

	public void caseReadyForReview(AjaxResponse ajaxResponse, String caseId) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/review");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}
		
	}
	
	public void caseReadyForReport(AjaxResponse ajaxResponse, String caseId) throws URISyntaxException, ClientProtocolException, IOException  {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/report");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}
		
	}
	
	public Set<String> getCNVChromomosomes(String caseId) throws URISyntaxException, ClientProtocolException, IOException{
		List<String> toSkip = new ArrayList<String>();
		toSkip.add("chrX");
		toSkip.add("chrY");
		toSkip.add("chr22_KI270879v1_alt");
		
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/cnvplot");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);
		
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			Set<String> uniqItems = new HashSet<String>();
			CNVPlotDataRaw plotDataRaw = mapper.readValue(response.getEntity().getContent(), CNVPlotDataRaw.class);
			for (List<String> items : plotDataRaw.getCnr()) {
				if (items.get(0).equals("Gene")) {
					continue; //skip the 1st row
				}
				String cnrChrom = items.get(1);
				if (!toSkip.contains(cnrChrom)) { //skip chromosomes in the toSkip list
					uniqItems.add(cnrChrom);
				}
				
			}
			return uniqItems;
		}
		return null;
	}

	public CNVPlotData getCnvPlotData(String caseId, String chrom) throws URISyntaxException, ClientProtocolException, IOException {
		List<String> toSkip = new ArrayList<String>();
		toSkip.add("chrX");
		toSkip.add("chrY");
		toSkip.add("chr22_KI270879v1_alt");
		
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/cnvplot");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

//		return test(chrom);
		
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CNVPlotDataRaw plotDataRaw = mapper.readValue(response.getEntity().getContent(), CNVPlotDataRaw.class);
			CNVPlotData plotData = new CNVPlotData();
			List<CNRData> cnrDataList = new ArrayList<CNRData>();
			List<CNSData> cnsDataList = new ArrayList<CNSData>();
			
			for (List<String> items : plotDataRaw.getCnr()) {
				if (items.get(0).equals("Gene")) {
					continue; //skip the 1st row
				}
				String cnrChrom = items.get(1);
				if (chrom == null || cnrChrom.equals(chrom)) {
					String gene = items.get(0);
					Long start = Long.parseLong(items.get(2));
					Long end = Long.parseLong(items.get(3));
					Double depth = Double.parseDouble(items.get(5));
					Double log2 = Double.parseDouble(items.get(4));
					Double weight = Double.parseDouble(items.get(6));
					if (!toSkip.contains(cnrChrom)) { //skip chromosomes in the toSkip list
						cnrDataList.add(new CNRData(cnrChrom, start, end, gene, log2, weight));
					}
				}
				
			}
			
			for (List<String> items : plotDataRaw.getCns()) {
				if (items.get(0).equals("Chromosome")) {
					continue; //skip the 1st row
				}
				String cnrChrom = items.get(0);
				if (chrom == null || cnrChrom.equals(chrom)) {
					Long start = Long.parseLong(items.get(1));
					Long end = Long.parseLong(items.get(2));
					Double log2 = Double.parseDouble(items.get(3));
					Integer cn = Integer.parseInt(items.get(4));
					if (!toSkip.contains(cnrChrom)) { //skip chromosomes in the toSkip list
						cnsDataList.add(new CNSData(cnrChrom, start, end, log2, cn));
					}
				}
				
			}
			
			
			plotData.setCaseId(plotDataRaw.getCaseId());
			plotData.setCnrData(cnrDataList);
			plotData.setCnsData(cnsDataList);
			return plotData;
		}
		return null;
	}

//	private CNVPlotData test(String chromFilter) throws IOException {
//		List<String> cnrRows = FileUtils.readLines(new File("/opt/answer/files/cnr2.csv"));
//		List<String> cnsRows = FileUtils.readLines(new File("/opt/answer/files/cns2.csv"));
//		
//		List<CNRData> cnrDataList = new ArrayList<CNRData>();
//		List<CNSData> cnsDataList = new ArrayList<CNSData>();
//		for (String row : cnrRows) {
//			if (row.startsWith("chromosome")) {
//				continue;
//			}
//			String[] items = row.split(",");
//			String cnrChrom = items[0];
//			if (chromFilter == null || cnrChrom.equals(chromFilter)) {
//				cnrDataList.add(new CNRData(cnrChrom, Long.parseLong(items[1]), Long.parseLong(items[2]), items[3], Double.parseDouble(items[4]), Double.parseDouble(items[4])));
//			}
//		}
//		for (String row : cnsRows) {
//			if (row.startsWith("chromosome")) {
//				continue;
//			}
//			String[] items = row.split(",");
//			if (items.length >= 5) {
//				String cnsChrom = items[0];
//				if (chromFilter == null || cnsChrom.equals(chromFilter)) {
//					cnsDataList.add(new CNSData(cnsChrom, Long.parseLong(items[1]), Long.parseLong(items[2]), Double.parseDouble(items[3]), Integer.parseInt(items[4])));
//				}
//			}
//		}
//		CNVPlotData data = new CNVPlotData();
//		data.setCnrData(cnrDataList);
//		data.setCnsData(cnsDataList);
//		return data;
//	}

	public Report getReportDetails(String reportId) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("report/"); 
		sbUrl.append(reportId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			Report report = mapper.readValue(response.getEntity().getContent(), Report.class);
			return report;
		}
		return null;
	}
	
	public MDAReportTemplate getMDATrials(String caseId) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/"); 
		sbUrl.append(caseId);
		sbUrl.append("/trials");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			MDAReportTemplate mdaEmail = mapper.readValue(response.getEntity().getContent(), MDAReportTemplate.class);
			return mdaEmail;
		}
		return null;
	}

	//temp method to test displaying the report
	//while Ben implements the API
	public Report buildReportManually(String caseId, User user, OtherProperties otherProps, NCBIProperties ncbiProps) throws ClientProtocolException, IOException, URISyntaxException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		Report report = new Report();
		OrderCase caseDetails = getCaseDetails(caseId, null);
		report.setCaseId(caseDetails.getCaseId());
		report.setCaseName(caseDetails.getCaseName());
		report.setLabTestName(caseDetails.getLabTestName());
		PatientInfo patientInfo = new PatientInfo(caseDetails);
		report.setPatientInfo(patientInfo);
		report.setReportName(caseDetails.getCaseName());
		
//		report.setSummary("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		
		List<String> strongTiers = Arrays.asList("1A", "1B");
		List<String> possibleTiers = Arrays.asList("2C", "2D");
		List<String> unknownTiers = Arrays.asList("3");
		
		
		MDAReportTemplate mdaEmail = this.getMDATrials(caseId);
		List<BiomarkerTrialsRow> trials = null;
		if (mdaEmail != null) {
			trials = mdaEmail.getSelectedBiomarkers();
			if (trials != null) {
				if (mdaEmail.getSelectedAdditionalBiomarkers() != null)
					trials.addAll(mdaEmail.getSelectedAdditionalBiomarkers());
				if (mdaEmail.getRelevantBiomarkers() != null)
					trials.addAll(mdaEmail.getRelevantBiomarkers());
				if (mdaEmail.getRelevantAdditionalBiomarkers() != null)
					trials.addAll(mdaEmail.getRelevantAdditionalBiomarkers());

			}
			else {
				trials = new ArrayList<BiomarkerTrialsRow>();
			}


		}
		
		Set<String> pmIds = new HashSet<String>();
		List<CNVReport> cnvReports = new ArrayList<CNVReport>();
		for (CNV cnv : caseDetails.getCnvs()) {
			if (cnv.getUtswAnnotated() != null && cnv.getUtswAnnotated()
					&& cnv.getSelected() != null && cnv.getSelected()
					) {
				cnv = getCNVDetails(cnv.getMongoDBId().getOid());
				if (cnv.getReferenceCnv() != null && cnv.getReferenceCnv().getUtswAnnotations() != null
						&& !cnv.getReferenceCnv().getUtswAnnotations().isEmpty()) {
					StringBuilder sb = new StringBuilder();
					boolean atLeastOneSelected = false; //only add row if at least one annotation is selected
					for (Annotation a : cnv.getReferenceCnv().getUtswAnnotations()) {
						Annotation.init(a, cnv.getAnnotationIdsForReporting(), modelDAO);
						if (a.getIsSelected() != null && a.getIsSelected()
								&& a.getBreadth() != null && a.getBreadth().equals("Chromosomal")
								&& !"Clinical Trial".equals(a.getCategory())) {
							sb.append(a.getText()).append(" ");
							atLeastOneSelected = true;
							if (a.getPmids() != null) {
								pmIds.addAll(this.trimPmIds(a.getPmids()));
							}
						}
						//get the trials
						if (a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null && a.getCategory().equals("Clinical Trial")) {
							trials.add(new BiomarkerTrialsRow(a.getTrial()));
						}
					}
					if (atLeastOneSelected) {
						cnvReports.add(new CNVReport(sb.toString(), cnv));
						report.getCnvIds().add(cnv.getMongoDBId().getOid());
//						report.incrementCnvCount(cnv.getCytoband()); //don't count CNVs at this stage
					}
				}
			}
		}
		report.setCnvs(cnvReports);
		
		List<TranslocationReport> translocationReports = new ArrayList<TranslocationReport>();
		for (Translocation ftl : caseDetails.getTranslocations()) {
			if (ftl.getUtswAnnotated() != null && ftl.getUtswAnnotated()
					&& ftl.getSelected() != null && ftl.getSelected()) {
				ftl = getTranslocationDetails(ftl.getMongoDBId().getOid());
				if (ftl.getReferenceTranslocation() != null && ftl.getReferenceTranslocation().getUtswAnnotations() != null
						&& !ftl.getReferenceTranslocation().getUtswAnnotations().isEmpty()) {
					StringBuilder sb = new StringBuilder();
					boolean atLeastOneSelected = false; //only add row if at least one annotation is selected
					for (Annotation a : ftl.getReferenceTranslocation().getUtswAnnotations()) {
						Annotation.init(a, ftl.getAnnotationIdsForReporting(), modelDAO);
						if (a.getIsSelected() != null && a.getIsSelected()
								&& !"Therapy".equals(a.getCategory())
								&& !"Clinical Trial".equals(a.getCategory())) { 
								sb.append(a.getText()).append(" ");
								atLeastOneSelected = true;
								if (a.getPmids() != null) {
									pmIds.addAll(this.trimPmIds(a.getPmids()));
								}
						}
						//get the trials
						if (a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null && a.getCategory().equals("Clinical Trial")) {
							trials.add(new BiomarkerTrialsRow(a.getTrial()));
						}
					}
					if (atLeastOneSelected) {
						translocationReports.add(new TranslocationReport(sb.toString(), ftl));
						report.getFtlIds().add(ftl.getMongoDBId().getOid());
						report.incrementFusionCount(ftl.getFusionName());
					}
				}
			}
		}
		report.setTranslocations(translocationReports);
		
		
		List<IndicatedTherapy> indicatedTherapies = new ArrayList<IndicatedTherapy>();
		for (Variant v : caseDetails.getVariants()) {
			if (v.getSelected() != null && v.getSelected()) {
				v = getVariantDetails(v.getMongoDBId().getOid());
				List<IndicatedTherapy> annotations = new ArrayList<IndicatedTherapy>();
				if (v.getReferenceVariant() != null && 
						v.getReferenceVariant().getUtswAnnotations() != null) {
					for (Annotation a : v.getReferenceVariant().getUtswAnnotations()) {
						Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
						if (a != null && a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null && a.getCategory().equals("Therapy")
								&& !"Clinical Trial".equals(a.getCategory())) {
							annotations.add(new IndicatedTherapy(a, v));
							report.getSnpIds().add(v.getMongoDBId().getOid());
							report.incrementIndicatedTherapyCount(v.getGeneName());
							if (a.getPmids() != null) {
								pmIds.addAll(this.trimPmIds(a.getPmids()));
							}
						}
						//get the trials
						if (a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null && a.getCategory().equals("Clinical Trial")) {
							trials.add(new BiomarkerTrialsRow(a.getTrial()));
						}
					}
				}
				indicatedTherapies.addAll(annotations);
			}
		}
		for (Translocation v : caseDetails.getTranslocations()) {
			if (v.getSelected() != null && v.getSelected()) {
				v = getTranslocationDetails(v.getMongoDBId().getOid());
				List<IndicatedTherapy> annotations = new ArrayList<IndicatedTherapy>();
				if (v.getReferenceTranslocation() != null && 
						v.getReferenceTranslocation().getUtswAnnotations() != null) {
					for (Annotation a : v.getReferenceTranslocation().getUtswAnnotations()) {
						Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
						if (a != null && a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null && a.getCategory().equals("Therapy")) {
							annotations.add(new IndicatedTherapy(a, v));
							report.getFtlIds().add(v.getMongoDBId().getOid());
							report.incrementIndicatedTherapyCount(v.getFusionName());
							if (a.getPmids() != null) {
								pmIds.addAll(this.trimPmIds(a.getPmids()));
							}
						}
					}
				}
				indicatedTherapies.addAll(annotations);
			}
		}
		
		report.setClinicalTrials(trials); //after adding all the UTSW trials
		
		report.setIndicatedTherapies(indicatedTherapies);
		
		report.setModifiedBy(user.getUserId());
		report.setCreatedBy(user.getUserId());
		report.setDateCreated(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		report.setDateModified(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		
		report.setLive(true);
		
		List<Variant> variants = caseDetails.getVariants().stream().filter(v -> v.getSelected()).collect(Collectors.toList());
		List<CNV> cnvVariants = caseDetails.getCnvs().stream().filter(v -> v.getSelected()).collect(Collectors.toList());
		Map<String, GeneVariantAndAnnotation> annotationsStrongByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsPossibleByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsUnknownByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		
		
		for (Variant v : variants) {
			if (v.getUtswAnnotated() && v.getSelected()) {
				v = getVariantDetails(v.getMongoDBId().getOid());
				List<Annotation> selectedAnnotationsForVariant = new ArrayList<Annotation>();
				List<String> tiers = new ArrayList<String>(); //to determine the highest tier for this variant
				if (v.getReferenceVariant() != null && v.getReferenceVariant().getUtswAnnotations() != null) {
					for (Annotation a : v.getReferenceVariant().getUtswAnnotations()) {
						Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
						if (a != null && a.getIsSelected() != null && a.getIsSelected()
								&& a.getCategory() != null ) {
							selectedAnnotationsForVariant.add(a);
							tiers.add(a.getTier());
							if (a.getPmids() != null) {
								pmIds.addAll(this.trimPmIds(a.getPmids()));
							}
						}
					}
				}
				Map<String, List<String>> strongAnnotations = new HashMap<String, List<String>>();
				Map<String, List<String>> possibleAnnotations = new HashMap<String, List<String>>();
				Map<String, List<String>> unknownAnnotations = new HashMap<String, List<String>>();
				String highestTierForVariant = null;
				tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
				if (!tiers.isEmpty()) {
					highestTierForVariant = tiers.get(0);
					if (strongTiers.contains(highestTierForVariant)) {
						for (Annotation a : selectedAnnotationsForVariant) {
							String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
							List<String> annotations = strongAnnotations.get(category);
							if (annotations == null) {
								annotations = new ArrayList<String>();
							}
							annotations.add(a.getText());
							strongAnnotations.put(category, annotations);
						}
					}
					else if (possibleTiers.contains(highestTierForVariant)) {
						for (Annotation a : selectedAnnotationsForVariant) {
							String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
							List<String> annotations = possibleAnnotations.get(category);
							if (annotations == null) {
								annotations = new ArrayList<String>();
							}
							annotations.add(a.getText());
							possibleAnnotations.put(category, annotations);
						}
					}
					else if (unknownTiers.contains(highestTierForVariant)) {
						for (Annotation a : selectedAnnotationsForVariant) {
							String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
							List<String> annotations = unknownAnnotations.get(category);
							if (annotations == null) {
								annotations = new ArrayList<String>();
							}
							annotations.add(a.getText());
							unknownAnnotations.put(category, annotations);
						}
					}
				}
				else if (v.getType().equals("snp")){
					//TODO inform user that no tier was selected
					report.getMissingTierVariants().add(v);
				}
				
				String name = v.getGeneName() + " " + v.getNotation();
				if (!strongAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> strongAnnotationsConcat = new HashMap<String, String>();
					for (String cat : strongAnnotations.keySet()) {
						strongAnnotationsConcat.put(cat, strongAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, strongAnnotationsConcat));
					report.incrementStrongClinicalSignificanceCount(v.getGeneName());
				}
				if (!possibleAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> possibleAnnotationsConcat = new HashMap<String, String>();
					for (String cat : possibleAnnotations.keySet()) {
						possibleAnnotationsConcat.put(cat, possibleAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, possibleAnnotationsConcat));
					report.incrementPossibleClinicalSignificanceCount(v.getGeneName());
				}
				if (!unknownAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> unknownAnnotationsConcat = new HashMap<String, String>();
					for (String cat : unknownAnnotations.keySet()) {
						unknownAnnotationsConcat.put(cat, unknownAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, unknownAnnotationsConcat));
					report.incrementUnknownClinicalSignificanceCount(v.getGeneName());
				}
					
			}
		}
		
		for (CNV v : cnvVariants) {
			if (v.getUtswAnnotated() && v.getSelected()) {
				boolean hasTiers = false;
				v = getCNVDetails(v.getMongoDBId().getOid());
				Map<String, List<Annotation>> selectedAnnotationsForVariant = new HashMap<String, List<Annotation>>();
				Map<String, List<String>> tiersByGenes = new HashMap<String, List<String>>(); //to determine the highest tier for this variant
				if (v.getReferenceCnv() != null && v.getReferenceCnv().getUtswAnnotations() != null) {
					for (Annotation a : v.getReferenceCnv().getUtswAnnotations()) {
						Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
						if (a != null && a.getIsSelected() != null && a.getIsSelected()
								&& a.getBreadth() != null && a.getBreadth().equals("Focal")) {
							String key = a.getCnvGenes().stream().collect(Collectors.joining(" "));
							List<Annotation> annotations = selectedAnnotationsForVariant.get(key);
							if (annotations == null) {
								annotations = new ArrayList<Annotation>();
							}
							annotations.add(a);
							selectedAnnotationsForVariant.put(key, annotations);
							List<String> tiers = tiersByGenes.get(key);
							if (tiers == null) {
								tiers = new ArrayList<String>();
							}
							tiers.add(a.getTier());
							tiersByGenes.put(key, tiers);
							if (a.getPmids() != null) {
								pmIds.addAll(this.trimPmIds(a.getPmids()));
							}
						}
					}
				}
				for (String genes : selectedAnnotationsForVariant.keySet()) {
					List<Annotation> annotations = selectedAnnotationsForVariant.get(genes);
					List<String> tiers = tiersByGenes.get(genes);
					Map<String, List<String>> strongAnnotations = new HashMap<String, List<String>>();
					Map<String, List<String>> possibleAnnotations = new HashMap<String, List<String>>();
					Map<String, List<String>> unknownAnnotations = new HashMap<String, List<String>>();
					String highestTierForVariant = null;
					tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
					if (!tiers.isEmpty()) {
						hasTiers = true;
						highestTierForVariant = tiers.get(0);
						if (strongTiers.contains(highestTierForVariant)) {
							for (Annotation a : annotations) {
								String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
								List<String> annotationsFormatted = strongAnnotations.get(category);
								if (annotationsFormatted == null) {
									annotationsFormatted = new ArrayList<String>();
								}
								annotationsFormatted.add(a.getText());
								strongAnnotations.put(category, annotationsFormatted);
							}
						}
						else if (possibleTiers.contains(highestTierForVariant)) {
							for (Annotation a : annotations) {
								String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
								List<String> annotationsFormatted = possibleAnnotations.get(category);
								if (annotationsFormatted == null) {
									annotationsFormatted = new ArrayList<String>();
								}
								annotationsFormatted.add(a.getText());
								possibleAnnotations.put(category, annotationsFormatted);
							}
						}
						else if (unknownTiers.contains(highestTierForVariant)) {
							for (Annotation a : annotations) {
								String category = a.getCategory() == null ? "Uncategorized" : a.getCategory();
								List<String> annotationsFormatted = unknownAnnotations.get(category);
								if (annotationsFormatted == null) {
									annotationsFormatted = new ArrayList<String>();
								}
								annotationsFormatted.add(a.getText());
								unknownAnnotations.put(category, annotationsFormatted);
							}
						}
					}
					String name = genes;
					if (!strongAnnotations.isEmpty()) {
						report.getCnvIds().add(v.getMongoDBId().getOid());
						Map<String, String> strongAnnotationsConcat = new HashMap<String, String>();
						for (String cat : strongAnnotations.keySet()) {
							strongAnnotationsConcat.put(cat, strongAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
						}
						annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, name, strongAnnotationsConcat));
						report.incrementStrongClinicalSignificanceCount(name.replaceAll("\\.", ""));
					}
					if (!possibleAnnotations.isEmpty()) {
						report.getCnvIds().add(v.getMongoDBId().getOid());
						Map<String, String> possibleAnnotationsConcat = new HashMap<String, String>();
						for (String cat : possibleAnnotations.keySet()) {
							possibleAnnotationsConcat.put(cat, possibleAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
						}
						annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, name, possibleAnnotationsConcat));
						report.incrementPossibleClinicalSignificanceCount(name.replaceAll("\\.", ""));
					}
					if (!unknownAnnotations.isEmpty()) {
						report.getCnvIds().add(v.getMongoDBId().getOid());
						Map<String, String> unknownAnnotationsConcat = new HashMap<String, String>();
						for (String cat : unknownAnnotations.keySet()) {
							unknownAnnotationsConcat.put(cat, unknownAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
						}
						annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, name, unknownAnnotationsConcat));
						report.incrementUnknownClinicalSignificanceCount(name.replaceAll("\\.", ""));
					}
				}
				if (!hasTiers) {
					v.setType("cnv"); //somehow, the type is not set on CNV
					report.getMissingTierCNVs().add(v);
				}
			}
		}
		
		report.setSnpVariantsStrongClinicalSignificance(annotationsStrongByVariant);
		report.setSnpVariantsPossibleClinicalSignificance(annotationsPossibleByVariant);
		report.setSnpVariantsUnknownClinicalSignificance(annotationsUnknownByVariant);
		
		//convert pmids to PubMed objects
		NCBIRequestUtils utils = new NCBIRequestUtils(ncbiProps, otherProps);
		List<PubMed> pubmeds = utils.getPubmedDetails(pmIds);
		report.setPubmeds(pubmeds);
		
		return report;
	}
	private List<String> trimPmIds(List<String> pmIds) {
		return pmIds.stream().map(p -> p.trim()).collect(Collectors.toList());
	}
	
	public void saveReport(AjaxResponse ajaxResponse, Report reportToSave) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		boolean isNewReport = reportToSave.getMongoDBId() == null;
		sbUrl.append("case/").append(reportToSave.getCaseId()).append("/savereport");
		URI uri = new URI(sbUrl.toString());

		System.out.println(reportToSave.createObjectJSON());
		HttpResponse response = null;
		if (isNewReport) {
			requestPost = new HttpPost(uri);
			addAuthenticationHeader(requestPost);
			requestPost.setEntity(new StringEntity(reportToSave.createObjectJSON(), ContentType.APPLICATION_JSON));
			response = client.execute(requestPost);
		}
		else {
			requestPut = new HttpPut(uri);
			addAuthenticationHeader(requestPut);
			requestPut.setEntity(new StringEntity(reportToSave.createObjectJSON(), ContentType.APPLICATION_JSON));
			response = client.execute(requestPut);
		}


		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
			Report savedReport = mapper.readValue(response.getEntity().getContent(), Report.class);
			ajaxResponse.setMessage(savedReport.getMongoDBId().getOid());
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		
	}
	
	public void saveCNV(AjaxResponse ajaxResponse, CNV cnvToSave, String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/cnv");
		URI uri = new URI(sbUrl.toString());

		System.out.println(cnvToSave.createObjectJSON());
		HttpResponse response = null;
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		requestPost.setEntity(new StringEntity(cnvToSave.createObjectJSON(), ContentType.APPLICATION_JSON));
		response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AjaxResponse dbResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
			if (dbResponse.getSuccess()) {
				ajaxResponse.setSuccess(true);
			}
			else {
				ajaxResponse.setSuccess(false);
				ajaxResponse.setMessage(dbResponse.getMessage());
			}
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		
	}

	public List<Report> getExistingReports(String caseId) throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/"); 
		sbUrl.append(caseId).append("/reports");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ExistingReports reports = mapper.readValue(response.getEntity().getContent(), ExistingReports.class);
			return reports.getResult();
		}
		return null;
	}

	public void finalizeReport(AjaxResponse ajaxResponse, Report reportDetails) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("report/"); 
		sbUrl.append(reportDetails.getMongoDBId().getOid());
		sbUrl.append("/finalize");
		URI uri = new URI(sbUrl.toString());
		requestPut = new HttpPut(uri);

		addAuthenticationHeader(requestPut);

		HttpResponse response = client.execute(requestPut);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		}
		else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
	}

	public Trial getNCTData(AjaxResponse ajaxResponse, String nctId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("trials/").append(nctId);
		URI uri = new URI(sbUrl.toString());

		HttpResponse response = null;
		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
		response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AjaxResponse mongoDBResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
			ajaxResponse.setSuccess(mongoDBResponse.getSuccess());
			ajaxResponse.setMessage(mongoDBResponse.getMessage());
			if (ajaxResponse.getSuccess()) {
				Trial trial = mapper.convertValue(mongoDBResponse.getPayload(), Trial.class);
				String[] items = trial.getContact().split("\n");
				List<String> itemsLineReturns = new ArrayList<String>();
				for (String item : items) {
					if (item != null && !item.equals("")) {
						itemsLineReturns.add(item);
					}
				}
				trial.setContact(itemsLineReturns.stream().collect(Collectors.joining("<br/>")));
				trial.setNctId(nctId);
				return trial;
			}
			else {
				return null;
			}
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
			return null;
		}
	}

	public void setDefaultTranscript(AjaxResponse ajaxResponse, String featureId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("xxxxx/").append(featureId);
		URI uri = new URI(sbUrl.toString());

		HttpResponse response = null;
		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
		response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse = mapper.readValue(response.getEntity().getContent(), AjaxResponse.class);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Someting went wrong");
		}
	}
	



}
