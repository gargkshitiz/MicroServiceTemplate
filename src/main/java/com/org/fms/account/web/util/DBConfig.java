package com.org.fms.account.web.util;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Configures Datasource bean
 * @author Kshitiz Garg
 */
@Configuration

@EntityScan("com.org.fms.account.model")
@EnableJpaRepositories("com.org.fms.account.model")

@PropertySource("classpath:sql-server.properties")
public class DBConfig {
	
	protected Logger logger = LoggerFactory.getLogger(DBConfig.class);
	
	@Value("${jdbcUrl}")
	private String jdbcUrl;
	@Value("${userId}")
	private String userId;
	@Value("${password}")
	private String password;
	@Value("${initialPoolSize:5}")
	private int initialPoolSize;
	@Value("${maxPoolSize:50}")
	private int maxPoolSize;
	@Value("${acquireIncrement:1}")
	private int acquireIncrement;
	@Value("${maxStatements:50}")
	private int maxStatements;
	@Value("${idleConnectionTestPeriod:3000}")
	private int idleConnectionTestPeriod;
	@Value("${loginTimeout:3000}")
	private int loginTimeout;
	
	@Bean
	public DataSource dataSource() throws Exception {

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("net.sourceforge.jtds.jdbc.Driver");
			dataSource.setJdbcUrl(jdbcUrl);
			dataSource.setUser(userId);
			dataSource.setPassword(password);
			dataSource.setInitialPoolSize(initialPoolSize);
			dataSource.setMaxPoolSize(maxPoolSize);
			dataSource.setAcquireIncrement(acquireIncrement);
			dataSource.setMaxStatements(maxStatements);
			dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
			dataSource.setLoginTimeout(loginTimeout);
		} 
		catch (PropertyVetoException | SQLException e) {
			logger.error("Exception while creating datasource", e);
			throw e;
		}
		return dataSource;
	}

}