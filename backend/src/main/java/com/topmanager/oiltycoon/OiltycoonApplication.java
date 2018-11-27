package com.topmanager.oiltycoon;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@MapperScan(basePackages = {"com.topmanager.oiltycoon.social.dao.mapper", "com.topmanger.oiltycoon.mapper"})
@EnableAsync
@EnableScheduling
public class OiltycoonApplication {

	private Environment env;

	@Autowired
	public void setEnv(Environment env) {
		this.env = env;
	}

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

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getProperty("mail.host"));
		mailSender.setPort(Integer.parseInt(env.getProperty("mail.port")));

		mailSender.setUsername(env.getProperty("mail.username"));
		mailSender.setPassword(env.getProperty("mail.password"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", env.getProperty("mail.protocol"));
		props.put("mail.smtp.auth", env.getProperty("mail.auth"));
		props.put("mail.smtp.starttls.enable", env.getProperty("mail.tls"));
		props.put("mail.debug", env.getProperty("mail.debug"));
		return mailSender;
	}



}
