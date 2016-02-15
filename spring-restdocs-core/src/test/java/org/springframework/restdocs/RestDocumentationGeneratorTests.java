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

package org.springframework.restdocs;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.snippet.Snippet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RestDocumentationGenerator}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationGeneratorTests {

	@SuppressWarnings("unchecked")
	private final RequestConverter<Object> requestConverter = mock(
			RequestConverter.class);

	@SuppressWarnings("unchecked")
	private final ResponseConverter<Object> responseConverter = mock(
			ResponseConverter.class);

	private final Object request = new Object();

	private final Object response = new Object();

	private final OperationRequest operationRequest = new OperationRequestFactory()
			.create(URI.create("http://localhost:8080"), null, null, new HttpHeaders(),
					null, null);

	private final OperationResponse operationResponse = new OperationResponseFactory()
			.create(null, null, null);

	private final Snippet snippet = mock(Snippet.class);

	@Test
	public void basicHandling() throws IOException {
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		HashMap<String, Object> configuration = new HashMap<>();
		new RestDocumentationGenerator<>("id", this.requestConverter,
				this.responseConverter, this.snippet).handle(this.request, this.response,
						configuration);
		verifySnippetInvocation(this.snippet, configuration);
	}

	@Test
	public void defaultSnippetsAreCalled() throws IOException {
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		HashMap<String, Object> configuration = new HashMap<>();
		Snippet defaultSnippet1 = mock(Snippet.class);
		Snippet defaultSnippet2 = mock(Snippet.class);
		configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS,
				Arrays.asList(defaultSnippet1, defaultSnippet2));
		new RestDocumentationGenerator<>("id", this.requestConverter,
				this.responseConverter, this.snippet).handle(this.request, this.response,
						configuration);
		verifySnippetInvocation(this.snippet, configuration);
		verifySnippetInvocation(defaultSnippet1, configuration);
		verifySnippetInvocation(defaultSnippet2, configuration);
	}

	@Test
	public void additionalSnippetsAreCalled() throws IOException {
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		Snippet additionalSnippet1 = mock(Snippet.class);
		Snippet additionalSnippet2 = mock(Snippet.class);
		RestDocumentationGenerator<Object, Object> generator = new RestDocumentationGenerator<>(
				"id", this.requestConverter, this.responseConverter, this.snippet);
		generator.addSnippets(additionalSnippet1, additionalSnippet2);
		HashMap<String, Object> configuration = new HashMap<>();
		generator.handle(this.request, this.response, configuration);
		verifySnippetInvocation(this.snippet, configuration);
		verifySnippetInvocation(additionalSnippet1, configuration);
		verifySnippetInvocation(additionalSnippet2, configuration);
	}

	private void verifySnippetInvocation(Snippet snippet, Map<String, Object> attributes)
			throws IOException {
		ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
		verify(snippet).document(operation.capture());
		assertThat(this.operationRequest, is(equalTo(operation.getValue().getRequest())));
		assertThat(this.operationResponse,
				is(equalTo(operation.getValue().getResponse())));
		assertThat(attributes, is(equalTo(operation.getValue().getAttributes())));
	}
}
