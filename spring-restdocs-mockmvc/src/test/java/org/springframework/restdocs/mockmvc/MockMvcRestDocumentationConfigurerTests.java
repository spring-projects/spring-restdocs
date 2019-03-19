/*
 * Copyright 2014-2018 the original author or authors.
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

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;

import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MockMvcRestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 */
public class MockMvcRestDocumentationConfigurerTests {

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Test
	public void defaultConfiguration() {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(
				this.restDocumentation).beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "localhost", 8080);
	}

	@Test
	public void customScheme() {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(
				this.restDocumentation).uris().withScheme("https")
						.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("https", "localhost", 8080);
	}

	@Test
	public void customHost() {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(
				this.restDocumentation).uris().withHost("api.example.com")
						.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "api.example.com", 8080);
	}

	@Test
	public void customPort() {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(
				this.restDocumentation).uris().withPort(8081).beforeMockMvcCreated(null,
						null);
		postProcessor.postProcessRequest(this.request);
		assertUriConfiguration("http", "localhost", 8081);
	}

	@Test
	public void noContentLengthHeaderWhenRequestHasNotContent() {
		RequestPostProcessor postProcessor = new MockMvcRestDocumentationConfigurer(
				this.restDocumentation).uris().withPort(8081).beforeMockMvcCreated(null,
						null);
		postProcessor.postProcessRequest(this.request);
		assertThat(this.request.getHeader("Content-Length")).isNull();
	}

	private void assertUriConfiguration(String scheme, String host, int port) {
		assertThat(scheme).isEqualTo(this.request.getScheme());
		assertThat(host).isEqualTo(this.request.getServerName());
		assertThat(port).isEqualTo(this.request.getServerPort());
		RequestContextHolder
				.setRequestAttributes(new ServletRequestAttributes(this.request));
		try {
			URI uri = BasicLinkBuilder.linkToCurrentMapping().toUri();
			assertThat(scheme).isEqualTo(uri.getScheme());
			assertThat(host).isEqualTo(uri.getHost());
			assertThat(port).isEqualTo(uri.getPort());
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}
	}

}
