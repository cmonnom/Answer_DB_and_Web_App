package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.oncotree.OncotreeTumorType;
import utsw.bicf.answer.security.OncotreeProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class OncotreeRequestUtils extends AbstractRequestUtils {

	OncotreeProperties oncotreeProps;

	public OncotreeRequestUtils(OncotreeProperties oncotreeProps, OtherProperties otherProps) {
		this.oncotreeProps = oncotreeProps;
		this.otherProps = otherProps;
		this.setupClient();
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
		this.closeGetRequest();
		return summary;
	}
	
	public List<OncotreeTumorType> getAllOncotreeTumorTypes()
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		StringBuilder sbUrl = new StringBuilder(oncotreeProps.getTumorTypeUrl().replace("/search/code/", ""));
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			OncotreeTumorType[] oncotreeJson = mapper.readValue(response.getEntity().getContent(), OncotreeTumorType[].class);
			if (oncotreeJson.length > 0) {
				return Arrays.asList(oncotreeJson);
			}
		}
		else {
			logger.info("Something went wrong OncotreeRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return null;
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
			this.closeGetRequest();
			return childrenCodes;
		}
		else {
			logger.info("Something went wrong OncotreeRequestUtils:90 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
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
