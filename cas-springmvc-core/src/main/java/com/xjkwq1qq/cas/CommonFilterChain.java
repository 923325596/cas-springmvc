package com.xjkwq1qq.cas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * filter链<br>
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：CommonFilterChain.java<br>
 * 摘要：简要描述本文件的内容<br>
 * -------------------------------------------------------<br>
 * 原作者：王强<br>
 * 完成日期：2015年4月28日<br>
 */
public class CommonFilterChain implements FilterChain {
	// 拦截器列表
	private List<Filter> filters = new ArrayList<Filter>();
	private int index;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		index++;
		if (index <= filters.size()) {
			Filter filter = filters.get(index - 1);
			filter.doFilter(request, response, this);
		}
	}

	public void addFilter(Filter filter) {
		if (filter != null) {
			filters.add(filter);
		}

	}

	public boolean isPass() {
		if (index == filters.size() + 1) {
			// 全部通过
			return true;
		}
		return false;
	}

}
