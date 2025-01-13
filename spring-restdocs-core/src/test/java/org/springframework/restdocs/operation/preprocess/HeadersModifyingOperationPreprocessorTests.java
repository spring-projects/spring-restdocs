/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.operation.preprocess;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link HeadersModifyingOperationPreprocessor}.
 *
 * @author Jihoon Cha
 * @author Andy Wilkinson
 */
public class HeadersModifyingOperationPreprocessorTests {

	private final HeadersModifyingOperationPreprocessor preprocessor = new HeadersModifyingOperationPreprocessor();

	@Test
	public void addNewHeader() {
		this.preprocessor.add("a", "alpha");
		assertThat(this.preprocessor.preprocess(createRequest()).getHeaders().get("a"))
			.isEqualTo(Arrays.asList("alpha"));
		assertThat(this.preprocessor.preprocess(createResponse()).getHeaders().get("a"))
			.isEqualTo(Arrays.asList("alpha"));
	}

	@Test
	public void addValueToExistingHeader() {
		this.preprocessor.add("a", "alpha");
		assertThat(this.preprocessor.preprocess(createRequest((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerSet()).contains(entry("a", Arrays.asList("apple", "alpha")));
		assertThat(this.preprocessor.preprocess(createResponse((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerSet()).contains(entry("a", Arrays.asList("apple", "alpha")));
	}

	@Test
	public void setNewHeader() {
		this.preprocessor.set("a", "alpha", "avocado");
		assertThat(this.preprocessor.preprocess(createRequest()).getHeaders().headerSet())
			.contains(entry("a", Arrays.asList("alpha", "avocado")));
		assertThat(this.preprocessor.preprocess(createResponse()).getHeaders().headerSet())
			.contains(entry("a", Arrays.asList("alpha", "avocado")));
	}

	@Test
	public void setExistingHeader() {
		this.preprocessor.set("a", "alpha", "avocado");
		assertThat(this.preprocessor.preprocess(createRequest((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerSet()).contains(entry("a", Arrays.asList("alpha", "avocado")));
		assertThat(this.preprocessor.preprocess(createResponse((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerSet()).contains(entry("a", Arrays.asList("alpha", "avocado")));
	}

	@Test
	public void removeNonExistentHeader() {
		this.preprocessor.remove("a");
		assertThat(this.preprocessor.preprocess(createRequest()).getHeaders().headerNames()).doesNotContain("a");
		assertThat(this.preprocessor.preprocess(createResponse()).getHeaders().headerNames()).doesNotContain("a");
	}

	@Test
	public void removeHeader() {
		this.preprocessor.remove("a");
		assertThat(this.preprocessor.preprocess(createRequest((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerNames()).doesNotContain("a");
		assertThat(this.preprocessor.preprocess(createResponse((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerNames()).doesNotContain("a");
	}

	@Test
	public void removeHeaderValueForNonExistentHeader() {
		this.preprocessor.remove("a", "apple");
		assertThat(this.preprocessor.preprocess(createRequest()).getHeaders().headerNames()).doesNotContain("a");
		assertThat(this.preprocessor.preprocess(createResponse()).getHeaders().headerNames()).doesNotContain("a");
	}

	@Test
	public void removeHeaderValueWithMultipleValues() {
		this.preprocessor.remove("a", "apple");
		assertThat(
				this.preprocessor.preprocess(createRequest((headers) -> headers.addAll("a", List.of("apple", "alpha"))))
					.getHeaders()
					.headerSet())
			.contains(entry("a", Arrays.asList("alpha")));
		assertThat(this.preprocessor
			.preprocess(createResponse((headers) -> headers.addAll("a", List.of("apple", "alpha"))))
			.getHeaders()
			.headerSet()).contains(entry("a", Arrays.asList("alpha")));
	}

	@Test
	public void removeHeaderValueWithSingleValueRemovesEntryEntirely() {
		this.preprocessor.remove("a", "apple");
		assertThat(this.preprocessor.preprocess(createRequest((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerNames()).doesNotContain("a");
		assertThat(this.preprocessor.preprocess(createResponse((headers) -> headers.add("a", "apple")))
			.getHeaders()
			.headerNames()).doesNotContain("a");
	}

	@Test
	public void removeHeadersByNamePattern() {
		Consumer<HttpHeaders> headersCustomizer = (headers) -> {
			headers.add("apple", "apple");
			headers.add("alpha", "alpha");
			headers.add("avocado", "avocado");
			headers.add("bravo", "bravo");
		};
		this.preprocessor.removeMatching("^a.*");
		assertThat(this.preprocessor.preprocess(createRequest(headersCustomizer)).getHeaders().headerNames())
			.containsOnly("Host", "bravo");
		assertThat(this.preprocessor.preprocess(createResponse(headersCustomizer)).getHeaders().headerNames())
			.containsOnly("bravo");
	}

	private OperationRequest createRequest() {
		return createRequest(null);
	}

	private OperationRequest createRequest(Consumer<HttpHeaders> headersCustomizer) {
		HttpHeaders headers = new HttpHeaders();
		if (headersCustomizer != null) {
			headersCustomizer.accept(headers);
		}
		return new OperationRequestFactory().create(URI.create("http://localhost:8080"), HttpMethod.GET, new byte[0],
				headers, Collections.emptyList());
	}

	private OperationResponse createResponse() {
		return createResponse(null);
	}

	private OperationResponse createResponse(Consumer<HttpHeaders> headersCustomizer) {
		HttpHeaders headers = new HttpHeaders();
		if (headersCustomizer != null) {
			headersCustomizer.accept(headers);
		}
		return new OperationResponseFactory().create(HttpStatus.OK, headers, new byte[0]);
	}

}
