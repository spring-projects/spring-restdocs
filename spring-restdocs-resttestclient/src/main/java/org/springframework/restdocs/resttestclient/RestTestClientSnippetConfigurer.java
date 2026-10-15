/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.resttestclient;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.restdocs.config.SnippetConfigurer;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * A {@link SnippetConfigurer} for {@link RestTestClient} that can be used to configure
 * the generated documentation snippets.
 *
 * @author Andy Wilkinson
 * @since 4.1.0
 */
public class RestTestClientSnippetConfigurer
		extends SnippetConfigurer<RestTestClientRestDocumentationConfigurer, RestTestClientSnippetConfigurer>
		implements ClientHttpRequestInterceptor {

	RestTestClientSnippetConfigurer(RestTestClientRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		return and().intercept(request, body, execution);
	}

}
