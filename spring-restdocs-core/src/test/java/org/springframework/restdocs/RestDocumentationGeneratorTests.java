/*
 * Copyright 2014-2017 the original author or authors.
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
import org.mockito.InOrder;
import org.mockito.Mockito;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Snippet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests for {@link RestDocumentationGenerator}.
 *
 * @author Andy Wilkinson
 * @author Filip Hrisafov
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

	private final OperationPreprocessor requestPreprocessor = mock(
			OperationPreprocessor.class);

	private final OperationPreprocessor responsePreprocessor = mock(
			OperationPreprocessor.class);

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
		InOrder inOrder = Mockito.inOrder(defaultSnippet1, defaultSnippet2, this.snippet);
		verifySnippetInvocation(inOrder, defaultSnippet1, configuration);
		verifySnippetInvocation(inOrder, defaultSnippet2, configuration);
		verifySnippetInvocation(inOrder, this.snippet, configuration);
	}

	@Test
	public void defaultOperationRequestPreprocessorsAreCalled() throws IOException {
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		HashMap<String, Object> configuration = new HashMap<>();
		OperationPreprocessor defaultPreprocessor1 = mock(OperationPreprocessor.class);
		OperationPreprocessor defaultPreprocessor2 = mock(OperationPreprocessor.class);
		configuration.put(
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_REQUEST_PREPROCESSOR,
				Preprocessors.preprocessRequest(defaultPreprocessor1,
						defaultPreprocessor2));
		OperationRequest first = createRequest();
		OperationRequest second = createRequest();
		OperationRequest third = createRequest();
		given(this.requestPreprocessor.preprocess(this.operationRequest))
				.willReturn(first);
		given(defaultPreprocessor1.preprocess(first)).willReturn(second);
		given(defaultPreprocessor2.preprocess(second)).willReturn(third);
		new RestDocumentationGenerator<>("id", this.requestConverter,
				this.responseConverter,
				Preprocessors.preprocessRequest(this.requestPreprocessor), this.snippet)
						.handle(this.request, this.response, configuration);
		verifySnippetInvocation(this.snippet, third, this.operationResponse,
				configuration, 1);
	}

	@Test
	public void defaultOperationResponsePreprocessorsAreCalled() throws IOException {
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		HashMap<String, Object> configuration = new HashMap<>();
		OperationPreprocessor defaultPreprocessor1 = mock(OperationPreprocessor.class);
		OperationPreprocessor defaultPreprocessor2 = mock(OperationPreprocessor.class);
		configuration.put(
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_RESPONSE_PREPROCESSOR,
				Preprocessors.preprocessResponse(defaultPreprocessor1,
						defaultPreprocessor2));
		OperationResponse first = createResponse();
		OperationResponse second = createResponse();
		OperationResponse third = new OperationResponseFactory()
				.createFrom(this.operationResponse, new HttpHeaders());
		given(this.responsePreprocessor.preprocess(this.operationResponse))
				.willReturn(first);
		given(defaultPreprocessor1.preprocess(first)).willReturn(second);
		given(defaultPreprocessor2.preprocess(second)).willReturn(third);
		new RestDocumentationGenerator<>("id", this.requestConverter,
				this.responseConverter,
				Preprocessors.preprocessResponse(this.responsePreprocessor), this.snippet)
						.handle(this.request, this.response, configuration);
		verifySnippetInvocation(this.snippet, this.operationRequest, third, configuration,
				1);
	}

	@Test
	public void newGeneratorOnlyCallsItsSnippets() throws IOException {
		OperationRequestPreprocessor requestPreprocessor = mock(
				OperationRequestPreprocessor.class);
		OperationResponsePreprocessor responsePreprocessor = mock(
				OperationResponsePreprocessor.class);
		given(this.requestConverter.convert(this.request))
				.willReturn(this.operationRequest);
		given(this.responseConverter.convert(this.response))
				.willReturn(this.operationResponse);
		given(requestPreprocessor.preprocess(this.operationRequest))
				.willReturn(this.operationRequest);
		given(responsePreprocessor.preprocess(this.operationResponse))
				.willReturn(this.operationResponse);
		Snippet additionalSnippet1 = mock(Snippet.class);
		Snippet additionalSnippet2 = mock(Snippet.class);
		RestDocumentationGenerator<Object, Object> generator = new RestDocumentationGenerator<>(
				"id", this.requestConverter, this.responseConverter, requestPreprocessor,
				responsePreprocessor, this.snippet);
		HashMap<String, Object> configuration = new HashMap<>();
		generator.withSnippets(additionalSnippet1, additionalSnippet2)
				.handle(this.request, this.response, configuration);
		verifyNoMoreInteractions(this.snippet);
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

	private void verifySnippetInvocation(Snippet snippet,
			OperationRequest operationRequest, OperationResponse operationResponse,
			Map<String, Object> attributes, int times) throws IOException {
		ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
		verify(snippet, Mockito.times(times)).document(operation.capture());
		assertThat(operationRequest, is(equalTo(operation.getValue().getRequest())));
		assertThat(operationResponse, is(equalTo(operation.getValue().getResponse())));
	}

	private void verifySnippetInvocation(InOrder inOrder, Snippet snippet,
			Map<String, Object> attributes) throws IOException {
		ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
		inOrder.verify(snippet).document(operation.capture());
		assertThat(this.operationRequest, is(equalTo(operation.getValue().getRequest())));
		assertThat(this.operationResponse,
				is(equalTo(operation.getValue().getResponse())));
		assertThat(attributes, is(equalTo(operation.getValue().getAttributes())));
	}

	private static OperationRequest createRequest() {
		return new OperationRequestFactory().create(URI.create("http://localhost:8080"),
				null, null, new HttpHeaders(), null, null);
	}

	private static OperationResponse createResponse() {
		return new OperationResponseFactory().create(null, null, null);
	}

}
