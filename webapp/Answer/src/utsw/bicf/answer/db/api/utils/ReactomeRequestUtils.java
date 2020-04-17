package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.aop.AOPAspect;
import utsw.bicf.answer.controller.serialization.TreeViewItem;
import utsw.bicf.answer.controller.serialization.vuetify.TreeViewSummary;
import utsw.bicf.answer.model.reactome.Event;
import utsw.bicf.answer.model.reactome.SearchResponse;
import utsw.bicf.answer.model.reactome.SearchResult;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.ReactomeProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class ReactomeRequestUtils {

	ReactomeProperties reactomeProps;
	OtherProperties otherProps;
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
	private static final Logger logger = Logger.getLogger(AOPAspect.class);

	public ReactomeRequestUtils(ReactomeProperties reactomeProps, OtherProperties otherProps) {
		this.reactomeProps = reactomeProps;
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

	public TreeViewSummary getLocations(String geneTerm, List<String> levels)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		StringBuilder sbUrl = new StringBuilder(reactomeProps.getSearchUrl())
				.append(geneTerm).append("/&species=Homo%20sapiens&cluster=true");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			SearchResponse searchJson = mapper.readValue(response.getEntity().getContent(), SearchResponse.class);
			if (searchJson != null && searchJson.getResults() != null
					&& !searchJson.getResults().isEmpty()) {
					SearchResult result = searchJson.getResults().get(0);
					if (result != null && result.getEntries() != null && !result.getEntries().isEmpty()) {
						String stId = result.getEntries().get(0).getStId();
						String uniProtId = result.getEntries().get(0).getReferenceIdentifier();
						sbUrl = new StringBuilder(reactomeProps.getEventUrl());
						uri = new URI(sbUrl.toString());
						requestGet = new HttpGet(uri);
						context = HttpClientContext.create();
						response = client.execute(requestGet, context);

						statusCode = response.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							Event[] eventsJson = mapper.readValue(response.getEntity().getContent(), Event[].class);
							List<Event> events = new ArrayList<Event>();
							for (Event e : eventsJson) {
								if (levels.contains(e.getName())) {
									events.add(e);
								}
							}
							return buildViewTree(events, uniProtId);
						}
					}
			}
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		return null;
	}

	private TreeViewSummary buildViewTree(List<Event> events, String uniProtId) {
		List<TreeViewItem> items = new ArrayList<TreeViewItem>();
		List<Event> meiosisEvents = new ArrayList<Event>();
		String cellCycleId = "";
		String meiosisId = "";
		for (Event e : events) {
			if (e.getName().equals("Cell Cycle") && e.getChildren() != null) {
				cellCycleId = e.getStId();
				for (Event child : e.getChildren()) {
					if  (child.getName().equals("Meiosis")) {
						meiosisId = child.getStId();
						child.setName("Cell Cycle : Meiosis");
						meiosisEvents.add(child);
						break;
					}
				}
				break;
			}
		}
		for (Event e : meiosisEvents) {
			String id = meiosisId;
			String sel = "&SEL=" + e.getStId();
			String path = "&PATH=" + cellCycleId + "," + meiosisId;
			String flg = "&FLG=" + uniProtId;
			String url = reactomeProps.getBrowserUrl() + id + sel + path + flg;
			TreeViewItem item = new TreeViewItem(e.getName(), e.getStId(), url);
			for (Event child : e.getChildren()) {
				id = e.getStId();
				sel = "&SEL=" + child.getStId();
				url = reactomeProps.getBrowserUrl() + id + sel + path + flg;
				TreeViewItem childItem = new TreeViewItem(child.getName(), child.getStId(), url);
				item.getChildren().add(childItem);
			}
//			buildChildItems(e, item);
			items.add(item);
		}
		TreeViewSummary summary = new TreeViewSummary(items);
		
		return summary;
	}
	
	/**
	 * Recursive method going through each children
	 * @param e
	 * @param parent
	 */
	private void buildChildItems(Event e, TreeViewItem parent) {
		if (e.getChildren() == null || e.getChildren().isEmpty()) {
			return;
		}
		for (Event child : e.getChildren()) {
			String url = "";
			TreeViewItem item = new TreeViewItem(child.getName(), child.getStId(), url);
			buildChildItems(child, item);
			parent.getChildren().add(item);
		}
	}
	
}
