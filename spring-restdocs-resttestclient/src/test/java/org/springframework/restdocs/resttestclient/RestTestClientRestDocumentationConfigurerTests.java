/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.resttestclient;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RestTestClientRestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
@ExtendWith(RestDocumentationExtension.class)
class RestTestClientRestDocumentationConfigurerTests {

	private RestTestClientRestDocumentationConfigurer configurer;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.configurer = new RestTestClientRestDocumentationConfigurer(restDocumentation);

	}

	@Test
	void configurationCanBeRetrievedButOnlyOnce() throws IOException {
		HttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("/test"));
		request.getHeaders().put(RestTestClient.RESTTESTCLIENT_REQUEST_ID, List.of("1"));
		ClientHttpRequestExecution requestExecution = mock(ClientHttpRequestExecution.class);
		this.configurer.intercept(request, new byte[0], requestExecution);
		assertThat(RestTestClientRestDocumentationConfigurer.retrieveConfiguration(request.getHeaders())).isNotNull();
		assertThatIllegalStateException()
			.isThrownBy(() -> RestTestClientRestDocumentationConfigurer.retrieveConfiguration(request.getHeaders()));
	}

	@Test
	void requestUriHasDefaultsAppliedWhenItHasNoHost() throws IOException {
		HttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("/test?foo=bar#baz"));
		request.getHeaders().put(RestTestClient.RESTTESTCLIENT_REQUEST_ID, List.of("1"));
		ClientHttpRequestExecution requestExecution = mock(ClientHttpRequestExecution.class);
		this.configurer.intercept(request, new byte[0], requestExecution);
		ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(requestExecution).execute(requestCaptor.capture(), any(byte[].class));
		assertThat(requestCaptor.getValue().getURI()).isEqualTo(URI.create("http://localhost:8080/test?foo=bar#baz"));
	}

	@Test
	void requestUriIsNotChangedWhenItHasAHost() throws IOException {
		HttpRequest request = new MockClientHttpRequest(HttpMethod.GET,
				URI.create("https://api.example.com:4567/test?foo=bar#baz"));
		request.getHeaders().put(RestTestClient.RESTTESTCLIENT_REQUEST_ID, List.of("1"));
		ClientHttpRequestExecution requestExecution = mock(ClientHttpRequestExecution.class);
		this.configurer.intercept(request, new byte[0], requestExecution);
		ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(requestExecution).execute(requestCaptor.capture(), any(byte[].class));
		assertThat(requestCaptor.getValue().getURI())
			.isEqualTo(URI.create("https://api.example.com:4567/test?foo=bar#baz"));
	}

}
