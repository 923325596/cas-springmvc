# cas-springmvc
·
实现功能：
* 单点登录cas和springmvc集成，通过interceptor来实现
* 单点登录cas支持多网段映射
* 单点登录支持内外网隔离
·

##cas和springmvc集成步骤
###1）maven依赖
```xml 
<dependency>
	<groupId>com.xjkwq1qq</groupId>
	<artifactId>cas-springmvc</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```	
###2）springmvc的配置
<b>在springmvc的interceptor中配置拦截器，参考test/resources/springMVC-servlet</b>
com.xjkwq1qq.cas.CasAuthenticationInterceptor进行拦截认证<br>
com.xjkwq1qq.cas.AssertionThreadLocalInterceptor将原本cas的过程转化为interceptor，将用户信息从session或者request里面取出，设置到当前线程中<br>
com.web.interceptor.UserInfoInitInterceptor用户信息封装，用户自定义的intercptor，用于将用户信息封装为可用的信息（这块用户可以根据自己情况进行调整）<br>

<b>具体配置如下</b>
```xml
<mvc:interceptors>
	<mvc:interceptor>
		<mvc:mapping path="/**" />
		<mvc:exclude-mapping path="/common/**" />
		<mvc:exclude-mapping path="/css/**" />
		<bean class="com.xjkwq1qq.cas.CasAuthenticationInterceptor"></bean>
	</mvc:interceptor>
	<bean class="com.xjkwq1qq.cas.AssertionThreadLocalInterceptor"></bean>
	<bean class="com.web.interceptor.UserInfoInitInterceptor"></bean>
</mvc:interceptors>
```
###3）cas单点配置
<b>多网段支持，这个默认是使用ip的（非域名访问的情况，如果用域名可能就不涉及到多个网段了）</b>
```
#default
cas.serverLoginUrl=http://172.168.100.133/cas-server/login
cas.serverUrlPrefix=http://172.168.100.133/cas-server
cas.serverName=http://172.168.100.133:80
cas.logoutUrl=http://172.168.100.133/cas-server/logout?index=http%3A%2F%2F172.168.100.133:80%3A8080%2Fpublic-opinion

#default false
cas.useExtranet=true

#127.0.0.1
cas.serverLoginUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/login
cas.serverUrlPrefix_127.0.0.1=http://192.168.0.244:8080/cas-server
cas.serverName_127.0.0.1=http://127.0.0.1
cas.localServerName_127.0.0.1=http://192.168.0.192
cas.logoutUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/logout?index=http%3A%2F%2F127.0.0.1%2Fpublic-opinion

#192.168.0.192
cas.serverLoginUrl_192.168.0.192=http://192.168.0.244:8080/cas-server/login
cas.serverUrlPrefix_192.168.0.192=http://192.168.0.244:8080/cas-server
cas.serverName_192.168.0.192=http://192.168.0.192
cas.localServerName_192.168.0.192=http://192.168.0.192
cas.logoutUrl_192.168.0.192=http://192.168.0.244:8080/cas-server/logout?index=http%3A%2F%2F192.168.0.192%3A80%2Fpublic-opinion
```

上面配置配置了3中映射关系<br>
配置加上_ip的方式<br>
如果浏览器中输入项目地址为127.0.0.1，那么他会使用
```
#127.0.0.1
cas.serverLoginUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/login
cas.serverUrlPrefix_127.0.0.1=http://192.168.0.244:8080/cas-server
cas.serverName_127.0.0.1=http://127.0.0.1
cas.localServerName_127.0.0.1=http://192.168.0.192
cas.logoutUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/logout?index=http%3A%2F%2F127.0.0.1%2Fpublic-opinion
```
如果浏览器中输入项目地址已192.168.0.192开始，那么他会使用配置
```
#192.168.0.192
cas.serverLoginUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/login
cas.serverUrlPrefix_127.0.0.1=http://192.168.0.244:8080/cas-server
cas.serverName_127.0.0.1=http://127.0.0.1
cas.localServerName_127.0.0.1=http://192.168.0.192
cas.logoutUrl_127.0.0.1=http://192.168.0.244:8080/cas-server/logout?index=http%3A%2F%2F127.0.0.1%2Fpublic-opinion
```
其他都会走默认配置

<b>内外网隔离的情况下（内网服务器无法访问外网ip的情况），这个时候需要注意配置才能够实现单点访问</b>
比如项目外部地址ip1:port1，cas服务外部地址ip2:port2，项目内部地址ip3:port3，cas服务内部地址ip4:prot4
```
cas.serverLoginUrl_ip1=http://ip2:port2/cas-server/login
cas.serverUrlPrefix_ip1=http://ip4:port4/cas-server
cas.serverName_ip1=http://ip1:port1
cas.localServerName_ip1=http://ip3:port3
cas.logoutUrl_ip1=http://ip2:port2/cas-server/logout?index=http%3A%2F%2F<b>ip1:port1</b>%2Fpublic-opinion
```


