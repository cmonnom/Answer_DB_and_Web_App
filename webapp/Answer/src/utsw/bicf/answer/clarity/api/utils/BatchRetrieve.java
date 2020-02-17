//package utsw.bicf.answer.clarity.api.utils;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class BatchRetrieve {
//	
//	private static final String HEADER = "<ri:links xmlns:ri=\"http://genologics.com/ri\">\n";
//	String objectType;
//	List<String> uris;
//	String url;
//	
//	public BatchRetrieve(String objectType, List<String> uris) {
//		super();
//		this.objectType = objectType;
//		this.uris = uris;
//		if (uris != null && !uris.isEmpty())
//		this.url = uris.get(0).substring(0, uris.get(0).lastIndexOf("/")) + "/batch/retrieve";
////		this.url = "https://clarity.biohpc.swmed.edu/api/v2/artifacts/batch/retrieve";
//	}
//	
//	public String buildXML() {
//		StringBuilder sb = new StringBuilder(HEADER);
//		if (uris == null) {
//			return null;
//		}
//		for (String uri : uris) {
//			sb.append(buildUri(uri));
//		}
//		sb.append("</ri:links>"); //close the xml
//		return sb.toString();
//	}
//
//	private String buildUri(String uri) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<link uri=\"").append(uri).append("\" rel=\"").append(objectType).append("\"/>\n");
//		return sb.toString();
//	}
//
//	public String getObjectType() {
//		return objectType;
//	}
//
//	public void setObjectType(String objectType) {
//		this.objectType = objectType;
//	}
//
//	public List<String> getUris() {
//		return uris;
//	}
//
//	public void setUris(List<String> uris) {
//		this.uris = uris;
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//	
//	public void printUris() {
//		System.out.println(uris.stream().collect(Collectors.joining("\n")));
//	}
//	
//}
