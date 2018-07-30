package org.shersfy.jwatcher.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
@WebFilter(urlPatterns="/*", filterName="jwFilter")
public class JWatcherFilter implements Filter{
	
	public static final String basepath = "basePath";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		this.setBasePath(httpReq);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
	private void setBasePath(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer(0);
		basePath.append(request.getScheme()).append("://");
		basePath.append(request.getServerName());
		if(request.getServerPort() != 80 && request.getServerPort() != 443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(request.getContextPath());
		request.setAttribute(basepath, basePath.toString());
	}

}
