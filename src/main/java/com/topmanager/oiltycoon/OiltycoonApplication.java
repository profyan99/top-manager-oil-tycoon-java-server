package com.topmanager.oiltycoon;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.InputStream;

@SpringBootApplication
public class OiltycoonApplication {
	public static void main(String[] args) {
		SpringApplication.run(OiltycoonApplication.class, args);
	}

	@Bean(name = "SimpleSqlFactory")
	@Primary
	public SqlSessionFactory mysqlSessionFactory() throws Exception {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		return new SqlSessionFactoryBuilder().build(inputStream);
	}

	@Bean(name = "SimpleSqlSession")
	@Primary
	public SqlSessionTemplate sqlSession() throws Exception {
		return new SqlSessionTemplate(mysqlSessionFactory());
	}

}
