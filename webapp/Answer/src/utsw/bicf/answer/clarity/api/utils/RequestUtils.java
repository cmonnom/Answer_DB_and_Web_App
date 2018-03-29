package utsw.bicf.answer.clarity.api.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import utsw.bicf.answer.clarity.api.model.ClarityNextPage;
import utsw.bicf.answer.clarity.api.model.ClarityProcess;
import utsw.bicf.answer.clarity.api.model.ClarityProcesses;
import utsw.bicf.answer.clarity.api.model.ClarityProject;
import utsw.bicf.answer.clarity.api.model.ClarityProjects;
import utsw.bicf.answer.clarity.api.model.ClarityTapeStation;
import utsw.bicf.answer.security.ClarityAPIAuthentication;

public class RequestUtils {
	
	@Autowired
	ClarityAPIAuthentication clarityAuth;

	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public HttpGet requestGet = null;
	public HttpPost requestPost = null;
	public HttpClient client = HttpClientBuilder.create().build();
	public ObjectMapper xmlMapper = new XmlMapper();
	
	public void addAuthenticationHeader(HttpGet requestMethod) {
		String auth = clarityAuth.getUsername() + ":" + clarityAuth.getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth);
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
	}

	private void addAuthenticationHeader(HttpPost requestMethod) {
		String auth = clarityAuth.getUsername() + ":" + clarityAuth.getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth);
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		requestPost.addHeader(HttpHeaders.ACCEPT, "application/xml");
		requestPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");

	}

	/**
	 * Does a single batch Retrieve POST to get all the containers or artifacts
	 * represented by the provided URIs.
	 *
	 * @param uris
	 *            URIs of the artifacts or containers to get (set must be
	 *            homogeneous)
	 * @return all of the retrieved nodes
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String batchGET(Set<String> uris, String type) throws ClientProtocolException, IOException {
		List<String> urisList = new ArrayList<String>(uris);
		BatchRetrieve br = new BatchRetrieve(type, urisList);
		requestPost = new HttpPost(br.getUrl());

		addAuthenticationHeader(requestPost);

		HttpEntity entity = new ByteArrayEntity(br.buildXML().getBytes("UTF-8"));
		requestPost.setEntity(entity);

		HttpResponse response = client.execute(requestPost);

		String responseXml = EntityUtils.toString(response.getEntity(), "UTF-8");
		// System.out.println(responseXml);
		return responseXml;
	}

//	public List<ClarityInputOutputMap> getResultFileArtifact(ClarityProcess process)
//			throws URISyntaxException, ClientProtocolException, IOException {
//		List<ClarityInputOutputMap> outputResultFileMaps = new ArrayList<ClarityInputOutputMap>();
//		for (ClarityInputOutputMap map : process.getIoMaps()) {
//			if (map.getOutput() != null && ClarityArtifact.RESULTFILE.equals(map.getOutput().getOutputType())) {
//				outputResultFileMaps.add(map);
//			}
//		}
//		return outputResultFileMaps;
//	}
//
//	public List<ClarityInputOutputMap> getAnalyteArtifact(ClarityProcess process)
//			throws URISyntaxException, ClientProtocolException, IOException {
//		List<ClarityInputOutputMap> outputResultFileMaps = new ArrayList<ClarityInputOutputMap>();
//		for (ClarityInputOutputMap map : process.getIoMaps()) {
//			if (map.getOutput() != null && ClarityArtifact.ANALYTE.equals(map.getOutput().getOutputType())) {
//				outputResultFileMaps.add(map);
//			}
//		}
//		return outputResultFileMaps;
//	}

	/**
	 * Filters the processes to find the latest by type name. Relies on date and
	 * lims id to find the latest process
	 * 
	 * @param processTypeName
	 * @param processes
	 * @return
	 * @throws ParseException
	 */
	public ClarityProcess findLatestProcess(List<ClarityProcess> processes) throws ParseException {
		ClarityProcess latestProcess = null;

		for (ClarityProcess p : processes) {
			if (p.getDateRun() == null) {
				continue;
			}
			LocalDate currentProcessDate = getProcessDate(p);
			Integer currentProcessLimsId = Integer.parseInt(p.getLimsid().replaceAll("-", ""));
			if (latestProcess == null) {
				latestProcess = p;
			} else {
				LocalDate latestProcessDate = getProcessDate(latestProcess);
				Integer latestProcessLimsId = Integer.parseInt(latestProcess.getLimsid().replaceAll("-", ""));
				// need to check for lims id in case the dates are the same.
				// TODO could also just to it by lims id at this point
				if (latestProcessDate.isBefore(currentProcessDate) || (latestProcessDate.isEqual(currentProcessDate)
						&& latestProcessLimsId < currentProcessLimsId)) {
					latestProcess = p;
				}
			}
		}
		return latestProcess;
	}

	/**
	 * Filters the processes to find the earliest by type name. Relies on date and
	 * lims id to find the earliest process
	 * 
	 * @param processTypeName
	 * @param processes
	 * @return
	 * @throws ParseException
	 */
	public ClarityProcess findEarliestProcess(List<ClarityProcess> processes) throws ParseException {
		ClarityProcess earliestProcess = null;

		for (ClarityProcess p : processes) {
			if (p.getDateRun() == null) {
				continue;
			}
			LocalDate currentProcessDate = getProcessDate(p);
			Integer currentProcessLimsId = Integer.parseInt(p.getLimsid().replaceAll("-", ""));
			if (earliestProcess == null) {
				earliestProcess = p;
			} else {
				LocalDate latestProcessDate = getProcessDate(earliestProcess);
				Integer latestProcessLimsId = Integer.parseInt(earliestProcess.getLimsid().replaceAll("-", ""));
				// need to check for lims id in case the dates are the same.
				// TODO could also just to it by lims id at this point
				if (latestProcessDate.isAfter(currentProcessDate) || (latestProcessDate.isEqual(currentProcessDate)
						&& latestProcessLimsId > currentProcessLimsId)) {
					earliestProcess = p;
				}
			}
		}
		return earliestProcess;
	}

	// TODO
	private ClarityProcess findProcessBetween(ClarityProcess processBefore, ClarityProcess processAfter,
			List<ClarityProcess> processes) throws ParseException {
		
		LocalDate ealiestDateBoundary = getProcessDate(processBefore);
		LocalDate latestDateBoundary = getProcessDate(processAfter);

		for (ClarityProcess p : processes) {
			if (p.getDateRun() == null) {
				continue;
			}
			
			LocalDate currentProcessDate = getProcessDate(p);
			if (currentProcessDate.isEqual(latestDateBoundary) || currentProcessDate.isEqual(ealiestDateBoundary)
					|| (currentProcessDate.isAfter(ealiestDateBoundary)
							&& currentProcessDate.isBefore(latestDateBoundary))) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Create a request to get all processes under the given project name
	 * 
	 * @param <T>
	 * @param <T>
	 * 
	 * @param projectName
	 * @param processTypes
	 *            name of the processes
	 * @param clazz
	 *            a class of process extending ClarityProcess (eg. ClaritySeqRun)
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private <T extends ClarityProcess> Map<String, List<T>> getProjectProcessesByType(String projectName,
			String[] processTypes, Class<T> clazz) throws ClientProtocolException, IOException, URISyntaxException {
		Map<String, List<T>> fullProcessesByType = new HashMap<String, List<T>>();
		StringBuilder sbUrl = new StringBuilder(clarityAuth.getUrl());
		sbUrl.append("processes/?projectname=")
				.append(URLEncoder.encode(projectName, StandardCharsets.UTF_8.toString()));
		for (String type : processTypes) {
			sbUrl.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8.toString()));
		}
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String processesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			ClarityProcesses processes = xmlMapper.readValue(processesXml, ClarityProcesses.class);
			if (processes.getProcesses() != null) {
				for (ClarityProcess p : processes.getProcesses()) {
					requestGet.setURI(new URI(p.getUri()));
					response = client.execute(requestGet);
					String processXml = EntityUtils.toString(response.getEntity(), "UTF-8");
					T process = xmlMapper.readValue(processXml, clazz);
					List<T> processesForType = fullProcessesByType.get(process.getType().getValue());
					if (processesForType == null) {
						processesForType = new ArrayList<T>();
					}
					processesForType.add(process);
					fullProcessesByType.put(process.getType().getValue(), processesForType);
				}
			}
		}
		return fullProcessesByType;
	}

//	public ClaritySample getSampleByName(String name) throws Exception {
//		String url = "samples/?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
//		URI uri = new URL(clarityAuth.getUrl() + url).toURI();
//
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
//		HttpResponse response = client.execute(requestGet);
//
//		int statusCode = response.getStatusLine().getStatusCode();
//		if (statusCode == HttpStatus.SC_OK) {
//			String samplesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//			ClaritySamples samples = xmlMapper.readValue(samplesXml, ClaritySamples.class);
//			ClaritySample[] sampleList = samples.getSamples();
//			if (sampleList != null && sampleList.length == 1) {
//				requestGet = new HttpGet(sampleList[0].getUri()); // get the full details of the sample
//				addAuthenticationHeader(requestGet);
//				response = client.execute(requestGet);
//				statusCode = response.getStatusLine().getStatusCode();
//				if (statusCode == HttpStatus.SC_OK) {
//					String sampleXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//					ClaritySample sample = xmlMapper.readValue(sampleXml, ClaritySample.class);
//					return sample;
//				}
//			} else {
//				throw new Exception("More than one sample found with name " + name);
//			}
//		}
//		return null;
//	}

	public ClarityProject getProjectByLimsId(String limsid) throws Exception {
		String url = "projects/" + URLEncoder.encode(limsid, StandardCharsets.UTF_8.toString());
		URI uri = new URL(clarityAuth.getUrl() + url).toURI();

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String projectsXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			ClarityProject project = xmlMapper.readValue(projectsXml, ClarityProject.class);
			return project;
		}
		return null;
	}

//	public <T> T getObjectByLimsId(String limsid, Class<T> clazz)
//			throws ClientProtocolException, IOException, URISyntaxException {
//		String object = null;
//		if (clazz.equals(ClarityProject.class)) {
//			object = "projects/";
//		} else if (clazz.equals(ClaritySample.class)) {
//			object = "samples/";
//		} else if (clazz.equals(ClarityArtifact.class)) {
//			object = "artifacts/";
//		} else if (clazz.equals(ClarityProcess.class)) {
//			object = "processes/";
//		}
//		// TODO add more
//		String url = object + URLEncoder.encode(limsid, StandardCharsets.UTF_8.toString());
//		URI uri = new URL(clarityAuth.getUrl() + url).toURI();
//
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
//		HttpResponse response = client.execute(requestGet);
//
//		int statusCode = response.getStatusLine().getStatusCode();
//		if (statusCode == HttpStatus.SC_OK) {
//			String responseXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//			return (T) xmlMapper.readValue(responseXml, clazz);
//		}
//		return null;
//	}

	public <T> T getObjectByUri(String uri, Class<T> clazz)
			throws ClientProtocolException, IOException, URISyntaxException {

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String responseXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			return (T) xmlMapper.readValue(responseXml, clazz);
		}
		return null;
	}

//	public ClaritySample getSampleByLimsId(String limsid) throws Exception {
//		String url = "samples/" + URLEncoder.encode(limsid, StandardCharsets.UTF_8.toString());
//		URI uri = new URL(clarityAuth.getUrl() + url).toURI();
//
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
//		HttpResponse response = client.execute(requestGet);
//
//		int statusCode = response.getStatusLine().getStatusCode();
//		if (statusCode == HttpStatus.SC_OK) {
//			String samplesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//			ClaritySample sample = xmlMapper.readValue(samplesXml, ClaritySample.class);
//			return sample;
//		}
//		return null;
//	}

	public Map<String, List<ClarityProcess>> getProjectProcessesByType(String projectName, String[] processTypes)
			throws ClientProtocolException, IOException, URISyntaxException {
		return getProjectProcessesByType(projectName, processTypes, ClarityProcess.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends ClarityProcess> List<T> getProcessesForProject(String[] processTypes,
			ClarityProject project, Class<ClarityTapeStation> clazz)
			throws ClientProtocolException, IOException, URISyntaxException {
		List<T> fullProcesses = new ArrayList<T>();
		StringBuilder sbUrl = new StringBuilder(clarityAuth.getUrl());
		sbUrl.append("processes?projectlimsid=")
				.append(URLEncoder.encode(project.getLimsid(), StandardCharsets.UTF_8.toString()));
		for (String type : processTypes) {
			sbUrl.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8.toString()));
		}
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String processesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			ClarityProcesses processes = xmlMapper.readValue(processesXml, ClarityProcesses.class);
			if (processes.getProcesses() != null) {
				for (ClarityProcess p : processes.getProcesses()) {
					requestGet.setURI(new URI(p.getUri()));
					response = client.execute(requestGet);
					String processXml = EntityUtils.toString(response.getEntity(), "UTF-8");
					fullProcesses.add((T) xmlMapper.readValue(processXml, clazz));
				}
			}
		}
		return fullProcesses;
	}

	public <T extends ClarityProcess> List<T> getProcesses(String processType, Class<T> clazz)
			throws ClientProtocolException, IOException, URISyntaxException {
		List<T> fullProcesses = new ArrayList<T>();
		StringBuilder sbUrl = new StringBuilder(clarityAuth.getUrl());
		sbUrl.append("processes/?type=").append(URLEncoder.encode(processType, StandardCharsets.UTF_8.toString()));
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String processesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			ClarityProcesses processes = xmlMapper.readValue(processesXml, ClarityProcesses.class);
			if (processes.getProcesses() != null) {
				for (ClarityProcess p : processes.getProcesses()) {
					requestGet.setURI(new URI(p.getUri()));
					response = client.execute(requestGet);
					String processXml = EntityUtils.toString(response.getEntity(), "UTF-8");
					fullProcesses.add(xmlMapper.readValue(processXml, clazz));
				}
			}
		}
		return fullProcesses;
	}

	/**
	 * Retrieve all the artifacts from the given ClarityProject list and process
	 * types using a batch retrieval
	 * 
	 * @param projects
	 * @param processTypes
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws URISyntaxException
	 * @throws ParseException
	 */
//	public ClarityBatchRetrieveArtifactsDetails getArtifactDetailsForProjects(List<ClarityProject> projects,
//			String[] processTypes, String artifactType, boolean isFirstProcess)
//			throws JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
//		Set<String> uris = new HashSet<String>();
//		Map<String, List<String>> urisByProcessType = new HashMap<String, List<String>>();
//		Map<String, ClarityTechnician> technicianByProcessType = new HashMap<String, ClarityTechnician>();
//		Map<String, LocalDate> processDateByProcessType = new HashMap<String, LocalDate>();
//
//		for (ClarityProject project : projects) {
//			Map<String, List<ClarityProcess>> processes = getProjectProcessesByType(project.getName(), processTypes);
//			for (String type : processes.keySet()) { // select only one process per type
//				ClarityProcess process = null;
//				if (isFirstProcess) {
//					process = findEarliestProcess(processes.get(type));
//
//				} else {
//					process = findLatestProcess(processes.get(type));
//				}
//				addIOMaps(artifactType, uris, process, urisByProcessType);
//				if (process != null) {
//					// TODO for now it does not really matter who run the process
//					// later, it needs to be set by the origin process if more than once in keySet
//					technicianByProcessType.put(type, process.getTechnician());
//					processDateByProcessType.put(type,
//							getProcessDate(process));
//				}
//			}
//		}
//		return retrieveResults(uris, technicianByProcessType, processDateByProcessType, urisByProcessType);
//	}

//	public ClarityBatchRetrieveArtifactsDetails getArtifactDetailsForProject(ClarityProject project,
//			String[] processTypes, String artifactType, boolean isFirstProcess)
//			throws JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
//		Set<String> uris = new HashSet<String>();
//		Map<String, List<String>> urisByProcessType = new HashMap<String, List<String>>();
//		Map<String, ClarityTechnician> technicianByProcessType = new HashMap<String, ClarityTechnician>();
//		Map<String, LocalDate> processDateByProcessType = new HashMap<String, LocalDate>();
//
//		Map<String, List<ClarityProcess>> processes = getProjectProcessesByType(project.getName(), processTypes);
//		for (String type : processes.keySet()) { // select only one process per type
//			ClarityProcess process = null;
//			if (isFirstProcess) {
//				process = findEarliestProcess(processes.get(type));
//
//			} else {
//				process = findLatestProcess(processes.get(type));
//			}
//			addIOMaps(artifactType, uris, process, urisByProcessType);
//			if (process != null) {
//				// TODO for now it does not really matter who run the process
//				// later, it needs to be set by the origin process if more than once in keySet
//				technicianByProcessType.put(type, process.getTechnician());
//				processDateByProcessType.put(type, getProcessDate(process));
//			}
//		}
//		return retrieveResults(uris, technicianByProcessType, processDateByProcessType, urisByProcessType);
//	}
//
//	public ClarityBatchRetrieveArtifactsDetails getArtifactDetailsForProject(ClarityProject project,
//			String[] processTypes, String artifactType, String processNameBefore, String processNameAfter)
//			throws JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
//		Set<String> uris = new HashSet<String>();
//		// keep track of uris, techs and dates by process type
//		Map<String, List<String>> urisByProcessType = new HashMap<String, List<String>>();
//		Map<String, ClarityTechnician> technicianByProcessType = new HashMap<String, ClarityTechnician>();
//		Map<String, LocalDate> processDateByProcessType = new HashMap<String, LocalDate>();
//
//		Map<String, List<ClarityProcess>> processes = getProjectProcessesByType(project.getName(), processTypes);
//		List<ClarityProcess> processesBefore = processes.get(processNameBefore);
//		List<ClarityProcess> processesAfter = processes.get(processNameAfter);
//		if (processesBefore != null && processesAfter != null) {
//			ClarityProcess processBefore = findEarliestProcess(processesBefore);
//			ClarityProcess processAfter = findEarliestProcess(processesAfter);
//			technicianByProcessType.put(processNameBefore, processBefore.getTechnician());
//			processDateByProcessType.put(processNameBefore,
//					getProcessDate(processBefore));
//			if (processBefore != null && processAfter != null) {
//				for (String type : processes.keySet()) { // select only one process per type
//					ClarityProcess process = findProcessBetween(processBefore, processAfter, processes.get(type));
//					addIOMaps(artifactType, uris, process, urisByProcessType);
//				}
//			}
//		}
//		return retrieveResults(uris, technicianByProcessType, processDateByProcessType, urisByProcessType);
//	}
//
//	private void addIOMaps(String artifactType, Set<String> uris, ClarityProcess process,
//			Map<String, List<String>> urisByProcessType)
//			throws ClientProtocolException, URISyntaxException, IOException {
//		if (process != null) {
//			List<ClarityInputOutputMap> ioMaps = null;
//			if (artifactType.equals(ClarityArtifact.ANALYTE)) {
//				ioMaps = getAnalyteArtifact(process);
//			} else {
//				ioMaps = getResultFileArtifact(process);
//			}
//			for (ClarityInputOutputMap map : ioMaps) {
//				String currentURI = map.getOutput().getUri();
//				String currentType = process.getType().getValue();
//				uris.add(currentURI);
//				List<String> urisForType = urisByProcessType.get(currentType);
//				if (urisForType == null) {
//					urisForType = new ArrayList<String>();
//				}
//				urisForType.add(currentURI);
//				urisByProcessType.put(currentType, urisForType);
//			}
//		}
//	}
//
//	private ClarityBatchRetrieveArtifactsDetails retrieveResults(Set<String> uris,
//			Map<String, ClarityTechnician> technicianByProcessType, Map<String, LocalDate> processDateByProcessType,
//			Map<String, List<String>> urisByProcessType) throws JsonParseException, JsonMappingException, IOException {
//		if (!uris.isEmpty()) {
//			String artifactsXML = batchGET(uris, "artifacts");
//			ClarityBatchRetrieveArtifactsDetails results = xmlMapper.readValue(artifactsXML,
//					ClarityBatchRetrieveArtifactsDetails.class);
//			results.setProcessDateByProcessType(processDateByProcessType);
//			results.setTechnicianByProcessType(technicianByProcessType);
//			results.setUrisByProcessType(urisByProcessType);
//			return results;
//		}
//		return null;
//	}
//
//	public ClarityBatchRetrieveArtifactsDetails getArtifactDetailsForProjects(List<ClarityProject> projects,
//			String[] processTypes, String artifactType, String processNameBefore, String processNameAfter)
//			throws JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
//		Set<String> uris = new HashSet<String>();
//		// keep track of uris, techs and dates by process type
//		Map<String, List<String>> urisByProcessType = new HashMap<String, List<String>>();
//		Map<String, ClarityTechnician> technicianByProcessType = new HashMap<String, ClarityTechnician>();
//		Map<String, LocalDate> processDateByProcessType = new HashMap<String, LocalDate>();
//
//		for (ClarityProject project : projects) {
//			Map<String, List<ClarityProcess>> processes = getProjectProcessesByType(project.getName(), processTypes);
//			List<ClarityProcess> processesBefore = processes.get(processNameBefore);
//			List<ClarityProcess> processesAfter = processes.get(processNameAfter);
//			if (processesBefore != null && processesAfter != null) {
//				ClarityProcess processBefore = findEarliestProcess(processesBefore);
//				ClarityProcess processAfter = findEarliestProcess(processesAfter);
//				technicianByProcessType.put(processNameBefore, processBefore.getTechnician());
//				processDateByProcessType.put(processNameBefore, getProcessDate(processBefore));
//				if (processBefore != null && processAfter != null) {
//					for (String type : processes.keySet()) { // select only one process per type
//						ClarityProcess process = findProcessBetween(processBefore, processAfter, processes.get(type));
//						addIOMaps(artifactType, uris, process, urisByProcessType);
//					}
//				}
//			}
//		}
//		return retrieveResults(uris, technicianByProcessType, processDateByProcessType, urisByProcessType);
//	}
//
//	public ClarityArtifact getArtifactForSample(ClaritySample sample, String artifactType, String processType,
//			boolean getFirstArtifact) throws org.apache.http.ParseException, IOException, URISyntaxException {
//		StringBuilder sbUrl = new StringBuilder(clarityAuth.getUrl());
//		sbUrl.append("artifacts/?samplelimsid=")
//				.append(URLEncoder.encode(sample.getLimsid(), StandardCharsets.UTF_8.toString()));
//		sbUrl.append("&type=").append(URLEncoder.encode(artifactType, StandardCharsets.UTF_8.toString()));
//		sbUrl.append("&process-type=").append(URLEncoder.encode(processType, StandardCharsets.UTF_8.toString()));
//		if (processType.contains("Tapestation")) {
//			sbUrl.append("&name=")
//					.append(URLEncoder.encode(sample.getName() + " Tapestation", StandardCharsets.UTF_8.toString()));
//		}
//
//		URI uri = new URI(sbUrl.toString());
//
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
//
//		HttpResponse response = client.execute(requestGet);
//
//		int statusCode = response.getStatusLine().getStatusCode();
//		if (statusCode == HttpStatus.SC_OK) {
//			String processesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//			ClarityArtifacts artifacts = xmlMapper.readValue(processesXml, ClarityArtifacts.class);
//			if (artifacts.getArtifacts() != null && artifacts.getArtifacts().length == 1) { // should only be one?
//				String limsid = artifacts.getArtifacts()[0].getLimsid();
//				return getObjectByLimsId(limsid, ClarityArtifact.class);
//			}
//		}
//		return null;
//	}
//
//	public List<ClarityArtifact> getArtifactsForSample(ClaritySample sample,
//			List<ClarityArtifact> serializedArtifacts)
//			throws org.apache.http.ParseException, IOException, URISyntaxException {
//		List<ClarityArtifact> artifactDetailsList = new ArrayList<ClarityArtifact>();
//		// get all artifact uris
//		StringBuilder sbUrl = new StringBuilder(clarityAuth.getUrl());
//		sbUrl.append("artifacts/?samplelimsid=")
//				.append(URLEncoder.encode(sample.getLimsid(), StandardCharsets.UTF_8.toString()));
//		URI uri = new URI(sbUrl.toString());
//
//		requestGet = new HttpGet(uri);
//		addAuthenticationHeader(requestGet);
//
//		HttpResponse response = client.execute(requestGet);
//
//		int statusCode = response.getStatusLine().getStatusCode();
//		if (statusCode == HttpStatus.SC_OK) {
//			String processesXml = EntityUtils.toString(response.getEntity(), "UTF-8");
//			ClarityArtifacts artifacts = xmlMapper.readValue(processesXml, ClarityArtifacts.class);
//			if (artifacts.getArtifacts() != null) {
//				Set<String> artifactUris = new HashSet<String>();
//				for (ClarityArtifact a : artifacts.getArtifacts()) {
//					if (artifactAlreadySerialized(a, serializedArtifacts) == null) {
//						artifactUris.add(a.getUri());
//					}
//				}
//				// batch get all artifact details
//				if (!artifactUris.isEmpty()) {
//					String atifactsXML = batchGET(artifactUris, "artifacts");
//					ClarityBatchRetrieveArtifactsDetails results = xmlMapper.readValue(atifactsXML,
//							ClarityBatchRetrieveArtifactsDetails.class);
//					
//					for (ClarityArtifact a : results.getArtifacts()) {
//						// add the parent process details
//						// filters out artifacts with output type of SharedResultFile
//						if (!skipArtifact(a)) {
//							if (a.getParentProcess() != null) {
//								ClarityArtifact serialized = artifactAlreadySerialized(a, serializedArtifacts);
//								if (serialized != null) {
//									a = serialized; // use the serialized version instead
//								} else {
//									ClarityProcess parentProcessDetails = getObjectByLimsId(
//											a.getParentProcess().getLimsid(), ClarityProcess.class);
//									if (parentProcessDetails.getType().getValue().equals("Process Summary Report 5.0")) {
//										//too big of a process and not needed
//										ClarityInputOutputMap[] emptyMap = new ClarityInputOutputMap[] {};
//										parentProcessDetails.setIoMaps(emptyMap);
//									}
//									a.setParentProcess(parentProcessDetails);
//								}
//							}
//							artifactDetailsList.add(a);
//						}
//					}
//				}
//
//			}
//		}
//		return artifactDetailsList;
//	}
//	
//	private boolean skipArtifact(ClarityArtifact a) {
//		return a.getOutputType() == null 
//				|| a.getOutputType().getValue().equals("SharedResultFile")
//				|| a.getName().equals("process history report")
//				|| a.getName().equals("log file");
//		//TODO add more exclusions here
//	}
//
//	/**
//	 * Look up the list of serialized artifacts to see if it has already been
//	 * serialized
//	 * 
//	 * @param artifact
//	 * @param serializedArtifacts
//	 * @return
//	 */
//	public ClarityArtifact artifactAlreadySerialized(ClarityArtifact artifact,
//			List<ClarityArtifact> serializedArtifacts) {
//		if (serializedArtifacts == null) {
//			return null;
//		}
//		for (ClarityArtifact a : serializedArtifacts) {
//			if (artifact.getLimsid().equals(a.getLimsid())) {
////				System.out.println("Using existing artifact " + a.getLimsid());
//				return a;
//			}
//		}
////		System.out.println("Fetching new artifact " + artifact.getLimsid());
//		return null;
//	}

	/**
	 * Grabs all projects in Clarity and returns a list of project details
	 * 
	 * @param uri
	 *            full url to the projects api including pagination when needed
	 * @return
	 * @throws Exception
	 */
	public List<ClarityProject> getAllClarityProjectDetails(URI uri) throws Exception {
		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String projectXml = EntityUtils.toString(response.getEntity(), "UTF-8");
			ClarityProjects projects = xmlMapper.readValue(projectXml, ClarityProjects.class);
			List<ClarityProject> projectDetails = new ArrayList<ClarityProject>();
			// process the current page
			for (ClarityProject project : projects.getProjects()) {
				projectDetails.add(getProjectByLimsId(project.getLimsid()));
			}
			// could be more than one page
			// process the other pages recursively
			ClarityNextPage nextPage = projects.getNextPage();

			if (nextPage != null) {
				List<ClarityProject> nextProjects = getAllClarityProjectDetails(new URI(nextPage.getUri()));
				if (nextProjects != null) {
					projectDetails.addAll(nextProjects);
				}
			}
			return projectDetails;

		} else {
			return null;
		}
	}
	
	public LocalDate getProcessDate(ClarityProcess process) throws ParseException {
		if (process == null || process.getDateRun() == null) {
			return null;
		}
		String dateString = process.getDateRun();
		return LocalDate.parse(dateString);
	}
	
}
