package com.fruits.orange.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

//token 配置单独管理
@Configuration
public class TokenConfig {

  //定义 jwt 对称密钥
  public static final String SIGNING_KEY = "my-certificate-key";

  @Bean
  public JwtAccessTokenConverter jwtAccessTokenConverter(){
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(SIGNING_KEY);
    return converter;
  }

  //JWT令牌存储方案
  @Bean
  public TokenStore tokenStore(){
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

}
