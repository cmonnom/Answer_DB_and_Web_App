package utsw.bicf.answer.db.api.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.controller.serialization.Utils;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.MDAEmail;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.AnnotationSearchResult;
import utsw.bicf.answer.model.extmapping.BiomarkerTrials;
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
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
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

	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
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
	public Report buildReportManually(String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		Report report = new Report();
		OrderCase caseDetails = getCaseDetails(caseId, null);
		report.setCaseId(caseDetails.getCaseId());
		report.setCaseName(caseDetails.getCaseName());
		PatientInfo patientInfo = new PatientInfo(caseDetails);
		report.setPatientInfo(patientInfo);
		report.setReportName(caseDetails.getCaseName());
		
		report.setSummary("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		
		List<String> strongTiers = Arrays.asList("1A", "1B");
		List<String> possibleTiers = Arrays.asList("2C", "2D");
		List<String> unknownTiers = Arrays.asList("3");
		
		
		MDAReportTemplate mdaEmail = this.getMDATrials(caseId);
		if (mdaEmail != null) {
			List<BiomarkerTrialsRow> trials = mdaEmail.getSelectedBiomarkers();
			trials.addAll(mdaEmail.getSelectedAdditionalBiomarkers());
			trials.addAll(mdaEmail.getRelevantBiomarkers());
			trials.addAll(mdaEmail.getRelevantAdditionalBiomarkers());
			report.setClinicalTrials(trials);

		}
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
								&& a.getBreadth() != null && a.getBreadth().equals("Chromosomal")) { 
							sb.append(a.getText()).append(" ");
							atLeastOneSelected = true;
						}
					}
					if (atLeastOneSelected) {
						cnvReports.add(new CNVReport(sb.toString(), cnv));
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
								&& !"Therapy".equals(a.getCategory())) { 
							sb.append(a.getText()).append(" ");
							atLeastOneSelected = true;
						}
					}
					if (atLeastOneSelected) {
						translocationReports.add(new TranslocationReport(sb.toString(), ftl));
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
								&& a.getCategory() != null && a.getCategory().equals("Therapy")) {
							annotations.add(new IndicatedTherapy(a, v));
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
						}
					}
				}
				indicatedTherapies.addAll(annotations);
			}
		}
		report.setIndicatedTherapies(indicatedTherapies);
		
		report.setModifiedBy(1);
		report.setCreatedBy(1);
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
								&& a.getCategory() != null && !a.getCategory().equals("Therapy")) {
							selectedAnnotationsForVariant.add(a);
							tiers.add(a.getTier());
						}
					}
				}
				List<String> strongAnnotations = new ArrayList<String>();
				List<String> possibleAnnotations = new ArrayList<String>();
				List<String> unknownAnnotations = new ArrayList<String>();
				String highestTierForVariant = null;
				tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
				if (!tiers.isEmpty()) {
					highestTierForVariant = tiers.get(0);
					if (strongTiers.contains(highestTierForVariant)) {
						strongAnnotations.addAll(selectedAnnotationsForVariant.stream().map(a -> a.getText()).collect(Collectors.toList()));
					}
					else if (possibleTiers.contains(highestTierForVariant)) {
						possibleAnnotations.addAll(selectedAnnotationsForVariant.stream().map(a -> a.getText()).collect(Collectors.toList()));
					}
					else if (unknownTiers.contains(highestTierForVariant)) {
						unknownAnnotations.addAll(selectedAnnotationsForVariant.stream().map(a -> a.getText()).collect(Collectors.toList()));
					}
				}
				else {
					//TODO inform user that no tier was selected
					report.getMissingTierVariants().add(v);
				}
				
				String name = v.getGeneName() + " " + v.getNotation();
				if (!strongAnnotations.isEmpty()) {
					annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, strongAnnotations.stream().collect(Collectors.joining(" "))));
				}
				if (!possibleAnnotations.isEmpty()) {
					annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, possibleAnnotations.stream().collect(Collectors.joining(" "))));
				}
				if (!unknownAnnotations.isEmpty()) {
					annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, unknownAnnotations.stream().collect(Collectors.joining(" "))));
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
						}
					}
				}
				for (String genes : selectedAnnotationsForVariant.keySet()) {
					List<Annotation> annotations = selectedAnnotationsForVariant.get(genes);
					List<String> tiers = tiersByGenes.get(genes);
					List<String> strongAnnotations = new ArrayList<String>();
					List<String> possibleAnnotations = new ArrayList<String>();
					List<String> unknownAnnotations = new ArrayList<String>();
					String highestTierForVariant = null;
					tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
					if (!tiers.isEmpty()) {
						hasTiers = true;
						highestTierForVariant = tiers.get(0);
						if (strongTiers.contains(highestTierForVariant)) {
							strongAnnotations.addAll(annotations.stream().map(a -> a.getText()).collect(Collectors.toList()));
						}
						else if (possibleTiers.contains(highestTierForVariant)) {
							possibleAnnotations.addAll(annotations.stream().map(a -> a.getText()).collect(Collectors.toList()));
						}
						else if (unknownTiers.contains(highestTierForVariant)) {
							unknownAnnotations.addAll(annotations.stream().map(a -> a.getText()).collect(Collectors.toList()));
						}
					}
					String name = genes;
					if (!strongAnnotations.isEmpty()) {
						annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, strongAnnotations.stream().collect(Collectors.joining(" "))));
					}
					if (!possibleAnnotations.isEmpty()) {
						annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, possibleAnnotations.stream().collect(Collectors.joining(" "))));
					}
					if (!unknownAnnotations.isEmpty()) {
						annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(name, unknownAnnotations.stream().collect(Collectors.joining(" "))));
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
		
		return report;
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



}
