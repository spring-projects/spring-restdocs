/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.mockmvc;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MockMvcRestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 */
@ExtendWith(RestDocumentationExtension.class)
class MockMvcRestDocumentationConfigurerTests {

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	void defaultConfiguration(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation)
			.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "localhost", 8080);
	}

	@Test
	void customScheme(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation).uris()
			.withScheme("https")
			.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("https", "localhost", 8080);
	}

	@Test
	void customHost(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation).uris()
			.withHost("api.example.com")
			.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "api.example.com", 8080);
	}

	@Test
	void customPort(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation).uris()
			.withPort(8081)
			.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "localhost", 8081);
	}

	@Test
	void noContentLengthHeaderWhenRequestHasNotContent(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation).uris()
			.withPort(8081)
			.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertThat(this.request.getHeader("Content-Length")).isNull();
	}

	@Test
	@SuppressWarnings("unchecked")
	void uriTemplateFromRequestAttribute(RestDocumentationContextProvider restDocumentation) {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation)
			.beforeMockMvcCreated(null, null);
		this.request.setAttribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "{a}/{b}");
		postProcessor.postProcessRequest(this.request);
		Map<String, Object> configuration = (Map<String, Object>) this.request
			.getAttribute(RestDocumentationResultHandler.ATTRIBUTE_NAME_CONFIGURATION);
		assertThat(configuration).containsEntry(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "{a}/{b}");
	}

	@Test
	@SuppressWarnings("unchecked")
	void uriTemplateFromRequest(RestDocumentationContextProvider restDocumentation) {
		Method setUriTemplate = ReflectionUtils.findMethod(MockHttpServletRequest.class, "setUriTemplate",
				String.class);
		Assumptions.assumeFalse(setUriTemplate == null);
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(restDocumentation)
			.beforeMockMvcCreated(null, null);
		ReflectionUtils.invokeMethod(setUriTemplate, this.request, "{a}/{b}");
		postProcessor.postProcessRequest(this.request);
		Map<String, Object> configuration = (Map<String, Object>) this.request
			.getAttribute(RestDocumentationResultHandler.ATTRIBUTE_NAME_CONFIGURATION);
		assertThat(configuration).containsEntry(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "{a}/{b}");
	}

	private void assertUriConfiguration(String scheme, String host, int port) {
		assertThat(scheme).isEqualTo(this.request.getScheme());
		assertThat(host).isEqualTo(this.request.getServerName());
		assertThat(port).isEqualTo(this.request.getServerPort());
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));
		try {
			UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentServletMapping().build();
			assertThat(scheme).isEqualTo(uriComponents.getScheme());
			assertThat(host).isEqualTo(uriComponents.getHost());
			assertThat(port).isEqualTo(uriComponents.getPort());
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}
	}

}
