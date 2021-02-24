package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.ensembl.EnsemblResponse;
import utsw.bicf.answer.security.EnsemblProperties;
import utsw.bicf.answer.security.OtherProperties;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class EnsemblRequestUtils extends AbstractRequestUtils{

	EnsemblProperties ensemblProps;

	public EnsemblRequestUtils(EnsemblProperties ensemblProps, OtherProperties otherProps) {
		this.ensemblProps = ensemblProps;
		this.otherProps = otherProps;
		this.setupClient();
	}

	public EnsemblResponse fetchEnsembl(String geneTerm, boolean tryPrev)
			throws URISyntaxException, ClientProtocolException, IOException, JAXBException,
			UnsupportedOperationException, SAXException, ParserConfigurationException {
		StringBuilder sbUrl = new StringBuilder(ensemblProps.getFetchUrl());
		sbUrl.append(geneTerm);
		URI uri = new URI(sbUrl.toString());
		if (tryPrev) {
			uri = new URI(sbUrl.toString().replace("/symbol/", "/prev_symbol/"));
		}
		requestGet = new HttpGet(uri);
		HttpClientContext context = HttpClientContext.create();
		requestGet.addHeader("Content-Type", "application/json;charset=utf-8");
		requestGet.addHeader("Accept", "application/json;charset=utf-8");
		HttpResponse response = client.execute(requestGet, context);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			EnsemblResponse ensembl = mapper.readValue(response.getEntity().getContent(), EnsemblResponse.class);
			this.closeGetRequest();
			return ensembl;
		}
		else {
			logger.info("Something went wrong EnsemblRequest:59 HTTP_STATUS: " + statusCode);
		}
		this.closeGetRequest();
		return null;
	}
	
	public EnsemblResponse fetchEnsembl(String geneTerm) throws ClientProtocolException, UnsupportedOperationException, URISyntaxException, IOException, JAXBException, SAXException, ParserConfigurationException {
		return this.fetchEnsembl(geneTerm, false);
	}

}
