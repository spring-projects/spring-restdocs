/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Tests for {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 */
public class RestDocumentationConfigurerTests {

	private MockHttpServletRequest request = new MockHttpServletRequest();

	private RestDocumentationContext context = new RestDocumentationContext(null);

	@Before
	public void establishContext() {
		RestDocumentationContextHolder.setCurrentContext(this.context);
	}

	@After
	public void clearContext() {
		RestDocumentationContextHolder.removeCurrentContext();
	}

	@Test
	public void defaultConfiguration() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer()
				.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "localhost", 8080);
	}

	@Test
	public void customScheme() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().uris()
				.withScheme("https").beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("https", "localhost", 8080);
	}

	@Test
	public void customHost() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().uris()
				.withHost("api.example.com").beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "api.example.com", 8080);
	}

	@Test
	public void customPort() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().uris()
				.withPort(8081).beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "localhost", 8081);
	}

	@Test
	public void noContentLengthHeaderWhenRequestHasNotContent() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().uris()
				.withPort(8081).beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);
		assertThat(this.request.getHeader("Content-Length"), is(nullValue()));
	}

	@Test
	public void contentLengthHeaderIsSetWhenRequestHasContent() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer()
				.beforeMockMvcCreated(null, null);
		byte[] content = "Hello, world".getBytes();
		this.request.setContent(content);
		postProcessor.postProcessRequest(this.request);
		assertThat(this.request.getHeader("Content-Length"),
				is(equalTo(Integer.toString(content.length))));
	}

	private void assertUriConfiguration(String scheme, String host, int port) {
		assertEquals(scheme, this.request.getScheme());
		assertEquals(host, this.request.getServerName());
		assertEquals(port, this.request.getServerPort());
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
				this.request));
		try {
			URI uri = BasicLinkBuilder.linkToCurrentMapping().toUri();
			assertEquals(scheme, uri.getScheme());
			assertEquals(host, uri.getHost());
			assertEquals(port, uri.getPort());
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}
	}

}
