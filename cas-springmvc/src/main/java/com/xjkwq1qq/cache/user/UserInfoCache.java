package com.xjkwq1qq.cache.user;

import com.xjkwq1qq.cache.user.LocalCacheUtil.ParamsKey;

public class UserInfoCache {
	public static void put(UserInfo userInfo) {
		LocalCacheUtil.put(ParamsKey.userCache.name(), userInfo);
	}

	public static UserInfo getCurrentUser() {
		Object user = LocalCacheUtil.get(ParamsKey.userCache.name());
		if(user!=null && user instanceof UserInfo){
			return (UserInfo) LocalCacheUtil.get(ParamsKey.userCache.name());
		}
		return null;
	}
}
