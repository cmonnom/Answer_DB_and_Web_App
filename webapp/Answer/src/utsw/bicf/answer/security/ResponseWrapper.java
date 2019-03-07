package utsw.bicf.answer.security;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class ResponseWrapper extends HttpServletResponseWrapper {
	
	private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder().allowElements("br").toFactory();
	private CharArrayWriter output;

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new CharArrayWriter();
	}
	
	public String toString() {
        return cleanXSS(output.toString());
    }

	public PrintWriter getWriter() {
        return new PrintWriter(output);
    }
	
	private String cleanXSS(String value) {
		return HTML_POLICY.sanitize(value);
	}

}
