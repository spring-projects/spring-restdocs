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

package org.springframework.restdocs.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationConfigurer;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Tests for {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationConfigurerTests {

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void defaultConfiguration() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer()
				.beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "localhost", 8080);
	}

	@Test
	public void customScheme() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer()
				.withScheme("https").beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("https", "localhost", 8080);
	}

	@Test
	public void customHost() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().withHost(
				"api.example.com").beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "api.example.com", 8080);
	}

	@Test
	public void customPort() {
		RequestPostProcessor postProcessor = new RestDocumentationConfigurer().withPort(
				8081).beforeMockMvcCreated(null, null);
		postProcessor.postProcessRequest(this.request);

		assertUriConfiguration("http", "localhost", 8081);
	}

	private void assertUriConfiguration(String scheme, String host, int port) {
		assertEquals(scheme, this.request.getScheme());
		assertEquals(host, this.request.getRemoteHost());
		assertEquals(port, this.request.getRemotePort());
		assertEquals(port, this.request.getServerPort());
	}

}
