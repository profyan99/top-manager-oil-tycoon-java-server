package com.topmanager.oiltycoon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:mysql.jdbc.properties")
public class DataBaseProperties {

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Bean(name = "SimpleDataSource")
    @Primary
    public DataSource dataSource() throws Exception {
        return DataSourceBuilder
                .create()
                    .driverClassName(env.getProperty("jdbc.driverClassName"))
                    .url(env.getProperty("jdbc.url"))
                    .username(env.getProperty("jdbc.username"))
                    .password(env.getProperty("jdbc.password"))
                .build();
    }
}
