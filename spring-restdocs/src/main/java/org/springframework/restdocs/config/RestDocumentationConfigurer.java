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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link MockMvcConfigurer} that can be used to configure the documentation
 * 
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
 */
public class RestDocumentationConfigurer extends MockMvcConfigurerAdapter {

	/**
	 * The default scheme for documented URIs
	 * @see #withScheme(String)
	 */
	public static final String DEFAULT_SCHEME = "http";

	/**
	 * The defalt host for documented URIs
	 * @see #withHost(String)
	 */
	public static final String DEFAULT_HOST = "localhost";

	/**
	 * The default port for documented URIs
	 * @see #withPort(int)
	 */
	public static final int DEFAULT_PORT = 8080;

	/**
	 * The default context path for documented URIs
	 * @see #withContextPath(String)
	 */
	public static final String DEFAULT_CONTEXT_PATH = "";

	private String scheme = DEFAULT_SCHEME;

	private String host = DEFAULT_HOST;

	private int port = DEFAULT_PORT;

	private String contextPath = DEFAULT_CONTEXT_PATH;

	/**
	 * Configures any documented URIs to use the given {@code scheme}. The default is
	 * {@code http}.
	 * 
	 * @param scheme The URI scheme
	 * @return {@code this}
	 */
	public RestDocumentationConfigurer withScheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	/**
	 * Configures any documented URIs to use the given {@code host}. The default is
	 * {@code localhost}.
	 * 
	 * @param host The URI host
	 * @return {@code this}
	 */
	public RestDocumentationConfigurer withHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Configures any documented URIs to use the given {@code port}. The default is
	 * {@code 8080}.
	 * 
	 * @param port The URI port
	 * @return {@code this}
	 */
	public RestDocumentationConfigurer withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Configures any documented URIs to use the given {@code contextPath}. The default is
	 * an empty string.
	 * 
	 * @param contextPath The context path
	 * @return {@code this}
	 */
	public RestDocumentationConfigurer withContextPath(String contextPath) {
		this.contextPath = (StringUtils.hasText(contextPath) && !contextPath
				.startsWith("/")) ? "/" + contextPath : contextPath;
		return this;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(
					MockHttpServletRequest request) {
				RestDocumentationContext currentContext = RestDocumentationContext
						.currentContext();
				if (currentContext != null) {
					currentContext.getAndIncrementStepCount();
				}
				request.setScheme(RestDocumentationConfigurer.this.scheme);
				request.setServerPort(RestDocumentationConfigurer.this.port);
				request.setServerName(RestDocumentationConfigurer.this.host);
				request.setContextPath(RestDocumentationConfigurer.this.contextPath);
				configureContentLengthHeaderIfAppropriate(request);
				return request;
			}

			private void configureContentLengthHeaderIfAppropriate(
					MockHttpServletRequest request) {
				long contentLength = request.getContentLengthLong();
				if (contentLength > 0
						&& !StringUtils.hasText(request.getHeader("Content-Length"))) {
					request.addHeader("Content-Length", request.getContentLengthLong());
				}
			}

		};
	}
}
