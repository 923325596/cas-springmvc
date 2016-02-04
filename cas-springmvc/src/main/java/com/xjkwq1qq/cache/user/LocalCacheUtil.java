package com.xjkwq1qq.cache.user;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author 王强
 * @time 2015年4月1日 下午2:41:27
 */
public class LocalCacheUtil {
	private static ThreadLocal<Map<String, Object>> cache = new ThreadLocal<Map<String, Object>>();

	/**
	 * 设置变量
	 * 
	 * @param key
	 * @param value
	 */
	public static void put(String key, Object value) {
		Map<String, Object> params = cache.get();
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		params.put(key, value);
		cache.set(params);
	}

	/**
	 * 获取变量
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		Map<String, Object> params = cache.get();
		if (params == null)
			return null;
		return params.get(key);
	}

	public static void clear() {
		cache.remove();
	}

	/**
	 * 需要共享的变量 列举
	 * 
	 * @author Administrator
	 * 
	 */
	public static enum ParamsKey {
		userCache, // 用户信息
		businessState,// 日志状态缓存
		businessActionId //日志actionId缓存 
	}
}
