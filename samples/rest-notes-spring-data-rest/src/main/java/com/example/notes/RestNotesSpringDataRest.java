/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.notes;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManagerFactory;

@EnableWebMvc
@ComponentScan
@Configuration
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
public class RestNotesSpringDataRest {
	
	@Bean
	ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json().build();
	}
	
	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPackagesToScan("com.example.notes");
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setShowSql(true);
		jpaVendorAdapter.setGenerateDdl(true);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setDataSource(dataSource);
		return factory;
	}
	
	@Bean
	DataSource dataSource() {
		return new DriverManagerDataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
	}
	
	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
}
