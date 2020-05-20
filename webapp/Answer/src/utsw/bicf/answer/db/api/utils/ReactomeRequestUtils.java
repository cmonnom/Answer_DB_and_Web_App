package utsw.bicf.answer.db.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
	private static final AtomicInteger COUNTER = new AtomicInteger(0);

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
//						String uniProtId = result.getEntries().get(0).getReferenceIdentifier();
//						sbUrl = new StringBuilder(reactomeProps.getEventUrl());
//						uri = new URI(sbUrl.toString());
//						requestGet = new HttpGet(uri);
//						context = HttpClientContext.create();
//						response = client.execute(requestGet, context);
//
//						statusCode = response.getStatusLine().getStatusCode();
//						if (statusCode == HttpStatus.SC_OK) {
//							Event[] eventsJson = mapper.readValue(response.getEntity().getContent(), Event[].class);
//							List<Event> events = new ArrayList<Event>();
//							for (Event e : eventsJson) {
//								if (levels.contains(e.getName())) {
//									events.add(e);
//								}
//							}
//							return buildViewTree(events, uniProtId);
//						}
						TreeViewSummary summary = new TreeViewSummary();
						String contentDetailUri = reactomeProps.getContentDetailUrl() + stId;
						summary.setMainPageUrl(contentDetailUri);
						summary.setItems(this.scrapeViewTree(contentDetailUri));
						return summary;
					}
			}
		}
		else {
			logger.info("Something went wrong UniProtRequest:114 HTTP_STATUS: " + statusCode);
		}
		return null;
	}
	
	private List<TreeViewItem> scrapeViewTree(String url) throws MalformedURLException, IOException, URISyntaxException {
		List<TreeViewItem> items = new ArrayList<TreeViewItem>();
		URI uri = new URI(url);
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			StringBuffer tmp = new StringBuffer();
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    while ((line = in.readLine()) != null) {
		    	tmp.append(line);
		    }
		    Document doc = Jsoup.parse(tmp.toString());
		    Elements rootElts = doc.getElementsByClass("tplSpe_Homo_sapiens");
		    for (Element elt : rootElts) {
		    	TreeViewItem item = new TreeViewItem();
		    	item.setStId("id" + COUNTER.addAndGet(1));
		    	String title = elt.child(0).firstElementSibling().text();
		    	item.setName(title);
//			System.out.println(title);
		    	item.setRootLevel(true);
		    	items.add(item);
		    	Elements children = elt.child(1).child(0).children(); // div > ul
		    	Element first = children.first();
		    	while(first.nodeName().equals("ul")) {
		    		first = first.child(0);
		    	}
		    	drillIntoTree(item, first.getElementsByTag("ul").first());
		    }
		}
		return items;
	}
	
	private void drillIntoTree(TreeViewItem parentItem, Element ulParent) {
		for (Element li : ulParent.children()) {
			TreeViewItem item = new TreeViewItem();
			item.setStId("id" + COUNTER.addAndGet(1));
			this.extractTitleLink(item, li);
			parentItem.getChildren().add(item);
			drillIntoTree(item, li.getElementsByTag("ul").first());
		}
		
	}
	
	/**
	 * 
	 * @param item current item
	 * @param elt <li> element
	 */
	private void extractTitleLink(TreeViewItem item, Element elt) {
		Element spanOrA = elt.child(1);
		if (spanOrA.nodeName().equals("a")) {
			String url = spanOrA.attr("href");
			item.setUrl(reactomeProps.getBrowserUrl() + url);
		}
		String title = spanOrA.text();
		item.setName(title);
//		System.out.println(" -> " + title);
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
