/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.restassured;

import java.util.List;
import java.util.Map;

import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.TemplateEngine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RestAssuredRestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
public class RestAssuredRestDocumentationConfigurerTests {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(
			"build");

	private final FilterableRequestSpecification requestSpec = mock(
			FilterableRequestSpecification.class);

	private final FilterableResponseSpecification responseSpec = mock(
			FilterableResponseSpecification.class);

	private final FilterContext filterContext = mock(FilterContext.class);

	private final RestAssuredRestDocumentationConfigurer configurer = new RestAssuredRestDocumentationConfigurer(
			this.restDocumentation);

	@Test
	public void nextFilterIsCalled() {
		this.configurer.filter(this.requestSpec, this.responseSpec, this.filterContext);
		verify(this.filterContext).setValue(
				eq(RestDocumentationFilter.CONTEXT_KEY_CONFIGURATION), any(Map.class));
	}

	@Test
	public void configurationIsAddedToTheContext() {
		this.configurer.filter(this.requestSpec, this.responseSpec, this.filterContext);
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map> configurationCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.filterContext).setValue(
				eq(RestDocumentationFilter.CONTEXT_KEY_CONFIGURATION),
				configurationCaptor.capture());
		@SuppressWarnings("unchecked")
		Map<String, Object> configuration = configurationCaptor.getValue();
		assertThat(configuration, hasEntry(equalTo(TemplateEngine.class.getName()),
				instanceOf(TemplateEngine.class)));
		assertThat(configuration, hasEntry(equalTo(WriterResolver.class.getName()),
				instanceOf(WriterResolver.class)));
		assertThat(configuration,
				hasEntry(
						equalTo(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS),
						instanceOf(List.class)));
	}
}
