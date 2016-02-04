package com.xjkwq1qq.cas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * cas权限认证拦截器<br>
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：CasAuthenticationInterceptor.java<br>
 * 摘要：简要描述本文件的内容<br>
 * -------------------------------------------------------<br>
 * 原作者：王强<br>
 * 完成日期：2015年4月28日<br>
 */
public class CasAuthenticationInterceptor implements HandlerInterceptor {
	public static final Logger logger = LoggerFactory.getLogger(CasAuthenticationInterceptor.class);

	public final static String DEFAULT_CAS_CONFIG = "default_cas_config";

	private Map<String, CasFilterContext> configMapping;// 多网段配置映射

	public final static String SERVERLOGINURL = "cas.serverLoginUrl";// 登录地址

	public final static String SERVERURLPREFIX = "cas.serverUrlPrefix";// 验证地址

	public final static String SERVERNAME = "cas.serverName";// 服务地址

	public final static String LOCALSERVERNAME = "cas.localServerName";// 内网服务地址

	public final static String LOGOUTURL = "cas.logoutUrl";// 登出地址

	private boolean useExtranet;

	private static CasAuthenticationInterceptor instance;

	public static CasAuthenticationInterceptor getInstance() {
		return instance;
	}

	public String getLogoutUrl(HttpServletRequest request) {
		CasFilterContext casFilterContext = getCasFilterContext(request);
		return casFilterContext.getCasServerUrlPrefix();
	}

	/**
	 * 初始化，将cas配置文件预置成对象映射
	 * 
	 * @date 2015年8月25日 下午5:16:30
	 * @author 王强
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {
		configMapping = new HashMap<String, CasFilterContext>();
		Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application-cas.properties"));
		try {
			useExtranet = Boolean.valueOf(properties.getProperty("cas.useExtranet", "false"));
		} catch (Exception e) {
			logger.error("cas.useExtranet 必须是boolean值，异常配置" + properties.getProperty("cas.useExtranet", "false") + "，采用默认值false");
		}
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String entity = (String) entry.getKey();
			if (StringUtils.isNotBlank(entity)) {
				entity = entity.trim();
				if (entity.startsWith(SERVERLOGINURL)) {
					if (SERVERLOGINURL.equals(entity)) {// 默认配置
						CasFilterContext casFilterContext = new CasFilterContext();
						initCasFilterContext(casFilterContext, properties.getProperty(SERVERLOGINURL), properties.getProperty(SERVERURLPREFIX),
								properties.getProperty(SERVERNAME), properties.getProperty(SERVERNAME));
						casFilterContext.setCasServerUrlPrefix(properties.getProperty(LOGOUTURL));
						configMapping.put(DEFAULT_CAS_CONFIG, casFilterContext);
					} else {// 非默认映射配置
						// 获取ip地址
						String ip = entity.substring(SERVERLOGINURL.length());
						CasFilterContext casFilterContext = new CasFilterContext();

						initCasFilterContext(casFilterContext, properties.getProperty(SERVERLOGINURL + ip),
								properties.getProperty(SERVERURLPREFIX + ip), properties.getProperty(SERVERNAME + ip),
								properties.getProperty(LOCALSERVERNAME + ip));
						casFilterContext.setCasServerUrlPrefix(properties.getProperty(LOGOUTURL + ip));
						configMapping.put(ip.substring(1), casFilterContext);
					}
				}
			}
		}
		instance = this;
	}

	/**
	 * 将cas配置信息转换为CasFilterContext对象
	 * 
	 * @date 2015年8月25日 下午5:30:11
	 * @author 王强
	 * @param casFilterContext
	 * @param casServerLoginUrl
	 *            登录地址
	 * @param casServerUrlPrefix
	 *            验证地址
	 * @param serverName
	 *            服务地址
	 * @param localServerName
	 *            本地服务地址
	 * @throws ServletException
	 */
	private void initCasFilterContext(CasFilterContext casFilterContext, String casServerLoginUrl, String casServerUrlPrefix, String serverName,
			String localServerName) throws ServletException {
		MockFilterConfig filterConfig = new MockFilterConfig();
		filterConfig.addInitParameter("casServerLoginUrl", casServerLoginUrl);
		filterConfig.addInitParameter("casServerUrlPrefix", casServerUrlPrefix);
		filterConfig.addInitParameter("serverName", serverName);
		if (StringUtils.isNotBlank(localServerName)) {
			filterConfig.addInitParameter("localServerName", localServerName);
		}
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.init(filterConfig);
		casFilterContext.setSingleSignOutFilter(singleSignOutFilter);
		ExtranetAuthenticationFilter authenticationFilter = new ExtranetAuthenticationFilter();
		authenticationFilter.init(filterConfig);
		casFilterContext.setAuthenticationFilter(authenticationFilter);
		Cas20ProxyReceivingTicketValidationFilter validateFilter = new Cas20ProxyReceivingTicketValidationFilter();
		validateFilter.init(filterConfig);
		casFilterContext.setValidateFilter(validateFilter);
	}

	private CasFilterContext getCasFilterContext(HttpServletRequest request) {
		if (useExtranet) {
			String url = getRequestIp(request);
			String ip = url;
			if (url.indexOf(":") > 0) {
				ip = url.substring(0, url.indexOf(":"));
			}
			CasFilterContext context = configMapping.get(ip);
			if (context != null) {
				return context;
			}
		}
		return configMapping.get(DEFAULT_CAS_CONFIG);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String url = request.getRequestURI();

		if (validateExceptionUrl(url)) {
			return true;
		} else if (validateRestUrl(url)) {
			return true;
		} else {
			return validateCas(request, response);
		}
	}

	private boolean validateExceptionUrl(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * cas认证
	 * 
	 * @date 2015年8月26日 上午9:53:08
	 * @author 王强
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	private boolean validateCas(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		CommonFilterChain filterChain = new CommonFilterChain();
		CasFilterContext filterContext = getCasFilterContext(request);
		filterChain.addFilter(filterContext.getSingleSignOutFilter());
		filterChain.addFilter(filterContext.getAuthenticationFilter());
		filterChain.addFilter(filterContext.getValidateFilter());
		filterChain.doFilter(request, response);
		return filterChain.isPass();
	}

	private String getRequestIp(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return url.substring(7, url.indexOf("/", 7));
	}

	private boolean validateRestUrl(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		CasFilterContext filterContext = getCasFilterContext(request);
		filterContext.getValidateFilter().destroy();
		filterContext.getAuthenticationFilter().destroy();
		filterContext.getSingleSignOutFilter().destroy();
	}

}
