/*
 * Copyright 2014 the original author or authors.
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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

public class RestDocumentationConfiguration extends MockMvcConfigurerAdapter {

	private String scheme = "http";

	private String host = "localhost";

	private int port = 8080;

	public RestDocumentationConfiguration withScheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	public RestDocumentationConfiguration withHost(String host) {
		this.host = host;
		return this;
	}

	public RestDocumentationConfiguration withPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(
					MockHttpServletRequest request) {
				request.setScheme(scheme);
				request.setRemotePort(port);
				request.setServerPort(port);
				request.setRemoteHost(host);
				return request;
			}
		};
	}

}
