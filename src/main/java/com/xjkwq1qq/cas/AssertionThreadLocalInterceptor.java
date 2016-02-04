package com.xjkwq1qq.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户登录信息封装<br>
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：AssertionThreadLocalInterceptor.java<br>
 * 摘要：将用户信息从session或者request里面取出，设置到当前线程中<br>
 * -------------------------------------------------------<br>
 * 原作者：王强<br>
 * 完成日期：2015年4月28日<br>
 */
public class AssertionThreadLocalInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final HttpSession session = request.getSession(false);
		final Assertion assertion = (Assertion) (session == null ? request.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : session
				.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION));
		AssertionHolder.setAssertion(assertion);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		AssertionHolder.clear();
	}

}
