package com.xjkwq1qq.cas;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;

/**
 * cas过滤器容器 Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：CasFilterContext.java<br>
 * 摘要：简要描述本文件的内容<br>
 * -------------------------------------------------------<br>
 * 原作者：王强<br>
 * 完成日期：2015年6月17日<br>
 */
public class CasFilterContext {
	private ExtranetAuthenticationFilter authenticationFilter;

	private Cas20ProxyReceivingTicketValidationFilter validateFilter;

	private SingleSignOutFilter singleSignOutFilter;

	private String casServerUrlPrefix;

	public ExtranetAuthenticationFilter getAuthenticationFilter() {
		return authenticationFilter;
	}

	public void setAuthenticationFilter(ExtranetAuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

	public Cas20ProxyReceivingTicketValidationFilter getValidateFilter() {
		return validateFilter;
	}

	public void setValidateFilter(Cas20ProxyReceivingTicketValidationFilter validateFilter) {
		this.validateFilter = validateFilter;
	}

	public SingleSignOutFilter getSingleSignOutFilter() {
		return singleSignOutFilter;
	}

	public void setSingleSignOutFilter(SingleSignOutFilter singleSignOutFilter) {
		this.singleSignOutFilter = singleSignOutFilter;
	}

	public String getCasServerUrlPrefix() {
		return casServerUrlPrefix;
	}

	public void setCasServerUrlPrefix(String casServerUrlPrefix) {
		this.casServerUrlPrefix = casServerUrlPrefix;
	}

}
