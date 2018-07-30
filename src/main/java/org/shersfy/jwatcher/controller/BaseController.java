package org.shersfy.jwatcher.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shersfy.jwatcher.beans.ResultCode;
import org.shersfy.jwatcher.filter.JWatcherFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseController {

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();
	
	/**处理成功**/
	protected static final int SUCESS 	= ResultCode.SUCESS;
	/**处理失败**/
	protected static final int FAIL 	= ResultCode.FAIL;
	
	@ModelAttribute
	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		THREAD_LOCAL_REQUEST.set(request);
		THREAD_LOCAL_RESPONSE.set(response);
	}

	public HttpServletRequest getRequest() {
		return THREAD_LOCAL_REQUEST.get();
	}

	public HttpServletResponse getResponse() {
		return THREAD_LOCAL_RESPONSE.get();
	}

	/**获取根路径**/
	protected String getBasePath() {
		HttpServletRequest request = getRequest();
		return request.getAttribute(JWatcherFilter.basepath).toString();
	}

}
