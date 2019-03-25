//package utsw.bicf.answer.security;
//
//import java.io.IOException;
//import java.util.Collection;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.http.HttpHeaders;
//
//public class CookieFilter implements Filter {
//
//	@Override
//	public void init(FilterConfig filterConfig) throws ServletException {
//
//	}
//
//	
//	
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		chain.doFilter(request, response);
//		addSameSiteCookieAttribute((HttpServletRequest) request); // add SameSite=Lax cookie attribute
//	}
//
//	private void addSameSiteCookieAttribute(HttpServletResponse response) {
//		Collection<String> headers = response.getHeaders("Cookie");
//		boolean firstHeader = true;
//		for (String header : headers) { // there can be multiple Set-Cookie attributes
//			if (firstHeader) {
//				response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Lax"));
//				firstHeader = false;
//				continue;
//			}
//			response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Lax"));
//		}
//	}
//	
//	private void addSameSiteCookieAttribute(HttpServletRequest request) {
//		Cookie[] cookies = request.getCookies();
//		for (Cookie c : cookies) {
//			c.set
//		}
//		boolean firstHeader = true;
//		for (String header : headers) { // there can be multiple Set-Cookie attributes
//			if (firstHeader) {
//				response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Lax"));
//				firstHeader = false;
//				continue;
//			}
//			response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Lax"));
//		}
//	}
//
//	@Override
//	public void destroy() {
//
//	}
//}
