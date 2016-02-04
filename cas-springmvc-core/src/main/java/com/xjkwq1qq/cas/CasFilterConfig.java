package com.xjkwq1qq.cas;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.springframework.util.Assert;

public class CasFilterConfig implements FilterConfig {

	private final String filterName;
	private final ServletContext servletContext;

	private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

	public CasFilterConfig() {
		this(null);
	}

	public CasFilterConfig(String filterName) {
		this(filterName, null);
	}

	public CasFilterConfig(String filterName, ServletContext servletContext) {
		super();
		this.filterName = filterName;
		this.servletContext = servletContext;
	}

	@Override
	public String getFilterName() {
		return filterName;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void addInitParameter(String name, String value) {
		Assert.notNull(name, "Parameter name must not be null");
		this.initParameters.put(name, value);
	}

	@Override
	public String getInitParameter(String name) {
		Assert.notNull(name, "Parameter name must not be null");
		return this.initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(this.initParameters.keySet());
	}

}
