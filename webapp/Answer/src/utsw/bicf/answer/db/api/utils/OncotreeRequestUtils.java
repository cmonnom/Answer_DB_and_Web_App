package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.model.extmapping.oncotree.OncotreeTumorType;
import utsw.bicf.answer.security.OncotreeProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class OncotreeRequestUtils {

	OncotreeProperties oncotreeProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public OncotreeRequestUtils(OncotreeProperties oncotreeProps, OtherProperties otherProps) {
		this.oncotreeProps = oncotreeProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpHost proxy = null;
	private HttpClient client = null;

	private void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxy = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxy).build();
		} else {
			client = HttpClientBuilder.create().build();
		}
	}

	public OncotreeTumorType getOncotreeTumorType(String oncotreeCode)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		OncotreeTumorType summary = null;
		StringBuilder sbUrl = new StringBuilder(oncotreeProps.getTumorTypeUrl()).append(oncotreeCode)
				.append("?exactMatch=true&levels=1%2C2%2C3%2C4%2C5");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			OncotreeTumorType[] oncotreeJson = mapper.readValue(response.getEntity().getContent(), OncotreeTumorType[].class);
			if (oncotreeJson.length > 0) {
				summary = oncotreeJson[0];
			}
		}
		else {
			logger.info("Something went wrong OncotreeRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		return summary;
	}
	
	/**
	 * Find all children under a node
	 * @param oncotreeCode
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws UnsupportedOperationException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Set<String> getOncotreeTumorTypeChildren(String oncotreeCode)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		OncotreeTumorType summary = null;
		StringBuilder sbUrl = new StringBuilder(oncotreeProps.getTumorTypeUrl().replace("search/code/", "tree"));
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, OncotreeTumorType> oncotreeJson = mapper.readValue(response.getEntity().getContent(), new TypeReference<HashMap<String, OncotreeTumorType>>(){});
			OncotreeTumorType oncotree = oncotreeJson.get("TISSUE");
			OncotreeTumorType highestParentOncotree = this.goThroughChildren(oncotree.getChildren().values(), oncotreeCode);
			Set<String> childrenCodes = new HashSet<String>();
			childrenCodes.add(oncotreeCode);
			this.findAllChildren(childrenCodes, highestParentOncotree.getChildren().values());
			return childrenCodes;
		}
		else {
			logger.info("Something went wrong OncotreeRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		return null;
	}

	private OncotreeTumorType goThroughChildren(Collection<OncotreeTumorType> children, String oncotreeCode) {
		OncotreeTumorType found = null;
		for (OncotreeTumorType child : children) {
			if (child.getCode().equals(oncotreeCode)) {
				found = child;
				break;
			}
			else {
				OncotreeTumorType possibleFound = goThroughChildren(child.getChildren().values(), oncotreeCode);
				if (possibleFound != null) {
					found = possibleFound;
				}
			}
		}
		return found;
	}
	
	private void findAllChildren(Set<String> allChildren, Collection<OncotreeTumorType> children) {
		for (OncotreeTumorType child : children) {
			allChildren.add(child.getCode());
			findAllChildren(allChildren, child.getChildren().values());
		}
	}
	
}
