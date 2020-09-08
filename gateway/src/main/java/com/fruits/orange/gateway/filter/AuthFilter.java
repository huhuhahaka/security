package com.fruits.orange.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.fruits.orange.gateway.common.EncryptUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class AuthFilter extends ZuulFilter {

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {

    //将 身份和权限信息 封装在 zuul 请求头中

    //获取请求上下文
    RequestContext requestContext = RequestContext.getCurrentContext();

    //从安全上下文中拿到用户身份对象
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(!(authentication instanceof OAuth2Authentication)){
      return null;
    }

    
    OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
    OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
    Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
    Map<String, Object> jsonToken = new HashMap<>(requestParameters);


    Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
    if(userAuthentication != null){
      //取出用户权限信息 放入 list 中
      List<String> authorities = new ArrayList<>();
      userAuthentication.getAuthorities().forEach(e -> authorities.add(e.getAuthority()));

      String principal = userAuthentication.getName();

      //把 身份信息和权限信息 放在 map 中
      jsonToken.put("principal", principal);
      jsonToken.put("authorities", authorities);

      //把 map 中的信息 封装在 zuul 请求头中

    }

    requestContext.addZuulRequestHeader("json-token", EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));

    return null;
  }
}
