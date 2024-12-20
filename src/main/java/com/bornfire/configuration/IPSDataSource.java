package com.bornfire.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties("spring.datasource")
@EnableJpaRepositories(basePackages = "com.bornfire.entity", entityManagerFactoryRef = "datasrc", transactionManagerRef = "datasrcTransactionManager")
public class IPSDataSource {

	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String url;

	@Autowired
	Environment env;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Bean
	public LocalSessionFactoryBean datasrc() throws SQLException {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(srcdataSource());
		sessionFactory.setPackagesToScan("com.bornfire.entity");
		sessionFactory.setHibernateProperties(hibernateProperties());

		return sessionFactory;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		return hibernateProperties;
	}

	@Bean
	DataSource srcdataSource() throws SQLException {
		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setURL(url);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager datasrcTransactionManager() throws SQLException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(datasrc().getObject());
		return transactionManager;
	}

	@Bean
	public RestTemplate restTemplate() throws NoSuchAlgorithmException, CertificateException, FileNotFoundException,
			IOException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

}
