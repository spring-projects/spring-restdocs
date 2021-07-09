/*
 * Copyright 2014-2020 the original author or authors.
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
import java.util.regex.Pattern;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ParametersModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 * @author Jihun Cha
 */
public class ParametersModifyingOperationPreprocessorTests {

	private final ParametersModifyingOperationPreprocessor preprocessor = new ParametersModifyingOperationPreprocessor();

	@Test
	public void addNewParameter() {
		Parameters parameters = new Parameters();
		OperationRequest request = this.preprocessor.add("a", "alpha").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("alpha"));
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?a=alpha"));
	}

	@Test
	public void addValueToExistingParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		OperationRequest request = this.preprocessor.add("a", "alpha").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("apple", "alpha"));
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?a=apple&a=alpha"));
	}

	@Test
	public void setNewParameter() {
		Parameters parameters = new Parameters();
		OperationRequest request = this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("alpha", "avocado"));
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?a=alpha&a=avocado"));
	}

	@Test
	public void setExistingParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		OperationRequest request = this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("alpha", "avocado"));
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?a=alpha&a=avocado"));
	}

	@Test
	public void removeNonExistentParameter() {
		Parameters parameters = new Parameters();
		OperationRequest request = this.preprocessor.remove("a").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters().size()).isEqualTo(0);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080"));
	}

	@Test
	public void removeParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		OperationRequest request = this.preprocessor.remove("a").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).isEmpty();
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080"));
	}

	@Test
	public void removePatternParameter() {
		Parameters parameters = new Parameters();
		parameters.add("apple", "apple");
		parameters.add("alpha", "alpha");
		parameters.add("avocado", "avocado");
		parameters.add("bravo", "bravo");
		assertThat(this.preprocessor.remove(Pattern.compile("^a.*"))
				.preprocess(createRequest(parameters)).getParameters().size())
						.isEqualTo(1);
	}

	@Test
	public void removeParameterValueForNonExistentParameter() {
		Parameters parameters = new Parameters();
		OperationRequest request = this.preprocessor.remove("a", "apple").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).isEmpty();
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080"));
	}

	@Test
	public void removeParameterValueWithMultipleValues() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		parameters.add("a", "alpha");
		parameters.add("b", "bravo");
		OperationRequest request = this.preprocessor.remove("a", "apple").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("alpha"));
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?a=alpha&b=bravo"));
	}

	@Test
	public void removeParameterValueWithSingleValueRemovesEntryEntirely() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		parameters.add("b", "bravo");
		OperationRequest request = this.preprocessor.remove("a", "apple").preprocess(createGetRequest(parameters));
		assertThat(request.getParameters()).doesNotContainKey("a");
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080?b=bravo"));
	}

	@Test
	public void whenParametersOfANonGetRequestAreModifiedThenTheQueryStringIsUnaffected() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		parameters.add("b", "bravo");
		OperationRequest request = this.preprocessor.remove("a", "apple").preprocess(createPostRequest(parameters));
		assertThat(request.getParameters()).doesNotContainKey("a");
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080"));
	}

	private OperationRequest createGetRequest(Parameters parameters) {
		return new OperationRequestFactory().create(
				URI.create("http://localhost:8080" + (parameters.isEmpty() ? "" : "?" + parameters.toQueryString())),
				HttpMethod.GET, new byte[0], new HttpHeaders(), parameters,
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createPostRequest(Parameters parameters) {
		return new OperationRequestFactory().create(URI.create("http://localhost:8080"), HttpMethod.POST, new byte[0],
				new HttpHeaders(), parameters, Collections.<OperationRequestPart>emptyList());
	}

}
