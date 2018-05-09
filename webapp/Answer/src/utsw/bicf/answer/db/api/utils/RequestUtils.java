package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
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
import utsw.bicf.answer.controller.serialization.DataFilterList;
import utsw.bicf.answer.controller.serialization.DataTableFilter;
import utsw.bicf.answer.controller.serialization.SearchItem;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.extmapping.VariantFilter;
import utsw.bicf.answer.model.extmapping.VariantFilterList;

/**
 * All API requests to the annotation DB should
 * be here.
 * @author Guillaume
 *
 */
public class RequestUtils {
	
	ModelDAO modelDAO;
	AnswerDBCredentials dbProps;
	
	public RequestUtils(ModelDAO modelDAO) {
		super();
		this.modelDAO = modelDAO;
		this.dbProps = modelDAO.getAnswerDBCredentials();
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

	public AjaxResponse assignCaseToUser(List<User> users, String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		String userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.joining(","));
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("assigncase?caseId=")
		.append(caseId).append("&userIds=").append(userIds);
		URI uri = new URI(sbUrl.toString());
		
		requestPut = new HttpPut(uri);
		
		addAuthenticationHeader(requestPut);

		HttpResponse response = client.execute(requestPut);

		AjaxResponse ajaxResponse = new AjaxResponse();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		}
		else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return ajaxResponse;
	}

	public OrderCase getCaseDetails(String caseId, String filters) throws ClientProtocolException, IOException, URISyntaxException {
		VariantFilterList filterList = parseFilters(filters);
		String filterParam = filterList.createJSON();
		
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/filter");
		URI uri = new URI(sbUrl.toString());
		
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
		
		//TODO Ben needs to build the API for filtering
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
	 * Create a list of active filters (filters with values)
	 * to be passed to the AnswerDB API.
	 * It's up to the API to figure out which values/fields are populated
	 * @param filters
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private VariantFilterList parseFilters(String filters) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		DataFilterList filterList = mapper.readValue(filters, DataFilterList.class);
//		}
		List<VariantFilter> activeFilters = new ArrayList<VariantFilter>();
		for (DataTableFilter filter : filterList.getFilters()) {
			if (filter.isBoolean() != null && filter.isBoolean()) {
				if (filter.getValueTrue() != null || filter.getValueFalse() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName());
					if (filter.getValueTrue() != null && filter.getValueTrue()) {
						if (filter.getFieldName().equals(Variant.FIELD_FILTERS)) {
							vf.getStringValues().add(Variant.VALUE_PASS);
						}
						if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)) {
							vf.getStringValues().add(""); //TODO
						}
					}
					if (filter.getValueFalse() != null && filter.getValueFalse()) {
						if (filter.getFieldName().equals(Variant.FIELD_FILTERS)) {
							vf.getStringValues().add(Variant.VALUE_FAIL);
						}
						if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)) {
							vf.getStringValues().add(""); //TODO
						}
					}
					if (!vf.getStringValues().isEmpty()) {
						activeFilters.add(vf);
					}
				}
			}
			else if (filter.isCheckBox() != null && filter.isCheckBox()) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				List<SearchItem> checkBoxes = filter.getCheckBoxes();
				for (SearchItem cb : checkBoxes) {
					if (cb.getValue() != null && (boolean) cb.getValue()) {
						vf.getStringValues().add(cb.getName().replaceAll(" ", "_").toLowerCase());
					}
				}
				if (!vf.getStringValues().isEmpty() ) {
					activeFilters.add(vf);
				}
			}
			else if (filter.isDate() != null && filter.isDate()) {
				//TODO
			}
			else if (filter.isNumber() != null && filter.isNumber()) {
				if (filter.getMinValue() != null || filter.getMaxValue() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName());
					if (filter.getMinValue() != null) {
						if (vf.getField().contains("Frequency")) { //frequencies are converted to pct. Need to revert it to ratio
							vf.setMinValue(filter.getMinValue() / 100);
						}
						else {
							vf.setMinValue(filter.getMinValue());
						}
					}
					if (filter.getMaxValue() != null) {
						if (vf.getField().contains("Frequency")) { //frequencies are converted to pct. Need to revert it to ratio
							vf.setMaxValue(filter.getMaxValue() / 100);
						}
						else {
							vf.setMaxValue(filter.getMaxValue());
						}
					}
					activeFilters.add(vf);
				}
			}
			else if (filter.isSelect() != null && filter.isSelect() && filter.getValue() != null) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				vf.setValue(filter.getValue());
				activeFilters.add(vf);
			}
			else if (filter.isString() != null && filter.isString()) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				vf.setValue(filter.getValue());
				activeFilters.add(vf);
			}
				
		}
		VariantFilterList list = new VariantFilterList();
		list.setFilters(activeFilters);
		return list;
	}

	/**
	 * Get all information about a variant, including annotations
	 * @param caseId
	 * @param chrom
	 * @param pos
	 * @param alt
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Variant getVariantDetails(String caseId, String chrom, Integer pos, String alt) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("variant").append("?caseId=").append(caseId)
		.append("&chrom=").append(chrom)
		.append("&pos=").append(pos)
		.append("&alt=").append(alt);
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

	
}
