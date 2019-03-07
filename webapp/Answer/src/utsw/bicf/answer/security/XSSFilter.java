package utsw.bicf.answer.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XSSFilter implements Filter {
	
	private FilterConfig filterConfig;

	@Override
	public void destroy() {
		this.filterConfig = null;

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
		chain.doFilter(new RequestWrapper((HttpServletRequest) request), responseWrapper);
		String servletResponse = new String(responseWrapper.toString());
		PrintWriter out = response.getWriter();
		out.write(servletResponse);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.filterConfig = arg0;

	}

}
