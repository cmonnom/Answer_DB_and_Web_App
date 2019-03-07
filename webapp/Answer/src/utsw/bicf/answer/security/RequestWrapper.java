package utsw.bicf.answer.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class RequestWrapper extends HttpServletRequestWrapper {
	
	private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder().allowElements("br").toFactory();
	

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}

	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}

	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null)
			return null;
		return cleanXSS(value);
	}

	private String cleanXSS(String value) {
		return HTML_POLICY.sanitize(value);
	}

}
