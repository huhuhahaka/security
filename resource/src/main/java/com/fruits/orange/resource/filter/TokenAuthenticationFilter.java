package com.fruits.orange.resource.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fruits.orange.resource.common.EncryptUtil;
import com.fruits.orange.resource.model.UserDTO;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    String token = httpServletRequest.getHeader("json-token");

    if (null != token){

      String json = EncryptUtil.decodeUTF8StringBase64(token);
      JSONObject jsonObject = JSON.parseObject(json);

      //用户信息
      UserDTO userDTO = JSON.parseObject(jsonObject.getString("principal"), UserDTO.class);

      //用户权限
      JSONArray authoritiesArray = jsonObject.getJSONArray("authorities");
      String[] authorities = authoritiesArray.toArray(new String[authoritiesArray.size()]);


      //将用户信息和权限填充 到用户身份token对象中
      UsernamePasswordAuthenticationToken authenticationToken
          = new UsernamePasswordAuthenticationToken(userDTO,null, AuthorityUtils.createAuthorityList(authorities));
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

      //将authenticationToken填充到安全上下文
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    }

    filterChain.doFilter(httpServletRequest,httpServletResponse);

  }
}
