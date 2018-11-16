package com.topmanager.oiltycoon.social.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServerConfig.class);

    private DefaultTokenServices defaultTokenServices;

    private TokenStore tokenStore;

    @Value("${security.jwt.resource-ids}")
    private String resourceId;

    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
        this.defaultTokenServices = defaultTokenServices;
    }

    @Autowired
    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Autowired
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .authenticationEntryPoint(authenticationEntryPoint)
                .resourceId(resourceId)
                .tokenServices(defaultTokenServices)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatcher(new OAuthRequestedMatcher())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private static class OAuthRequestedMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest httpServletRequest) {
            String auth = httpServletRequest.getHeader("Authorization");
            boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer");
            if(!haveOauth2Token) {
                logger.debug("Don't have oauth2Token");
            }
            boolean haveAccessToken = httpServletRequest.getParameter("access_token")!=null;
            if(!haveAccessToken) {
                logger.debug("Don't have access_token");
            }
            return haveOauth2Token || haveAccessToken;
        }
    }
}
