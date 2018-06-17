package com.topmanager.oiltycoon.config;

import com.topmanager.oiltycoon.dao.impl.UserDaoImpl;
import com.topmanager.oiltycoon.security.ConnectionSignUpImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SocialConfig.class);

    private DataSource dataSource;

    private ConnectionSignUp connectionSignUp;

    @Autowired
    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer factoryConfigurer, Environment environment) {

        factoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(
                environment.getProperty("facebook.clientId"),
                environment.getProperty("facebook.clientSecret")));

        GoogleConnectionFactory googleConnectionFactory = new GoogleConnectionFactory(
                environment.getProperty("google.clientId"),
                environment.getProperty("google.clientSecret"));
        googleConnectionFactory.setScope(environment.getProperty("google.clientScope"));
        factoryConfigurer.addConnectionFactory(googleConnectionFactory);

    }

    @Override
    public UserIdSource getUserIdSource() {
        UserIdSource source = new AuthenticationNameUserIdSource();
        logger.error("GetUserIdSource");
        return source;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository connectionRepository = new JdbcUsersConnectionRepository(
                dataSource,
                connectionFactoryLocator,
                Encryptors.noOpText()
        );
        connectionRepository.setConnectionSignUp(connectionSignUp);
        return connectionRepository;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Facebook facebook(ConnectionRepository repository) {
        Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
        return connection != null ? connection.getApi() : null;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Google google(ConnectionRepository repository) {
        Connection<Google> connection = repository.findPrimaryConnection(Google.class);
        return connection != null ? connection.getApi() : null;
    }
}
