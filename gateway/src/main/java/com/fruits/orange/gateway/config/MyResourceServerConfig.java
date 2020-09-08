package com.fruits.orange.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

//这里不加 @Configuration 会导致 SecurityContextHolder 中无认证信息
@Configuration
public class MyResourceServerConfig {

  public static final String RESOURCE_ID = "res1";

  @Autowired
  private TokenStore tokenStore;


//  @Configuration
  @EnableResourceServer
  public class CertificateServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources){
      resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .antMatchers("/certificate/**").permitAll();
    }
  }



//  @Configuration
  @EnableResourceServer
  public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources){
      resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()
          .antMatchers("/resource/**").access("#oauth2.hasScope('ROLE_API')");
    }


  }

}
