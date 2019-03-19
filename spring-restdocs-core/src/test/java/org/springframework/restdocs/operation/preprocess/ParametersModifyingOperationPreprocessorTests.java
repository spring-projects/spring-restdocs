/*
 * Copyright 2014-2018 the original author or authors.
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
 */
public class ParametersModifyingOperationPreprocessorTests {

	private final ParametersModifyingOperationPreprocessor preprocessor = new ParametersModifyingOperationPreprocessor();

	@Test
	public void addNewParameter() {
		Parameters parameters = new Parameters();
		assertThat(this.preprocessor.add("a", "alpha")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("alpha"));
	}

	@Test
	public void addValueToExistingParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		assertThat(this.preprocessor.add("a", "alpha")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("apple", "alpha"));
	}

	@Test
	public void setNewParameter() {
		Parameters parameters = new Parameters();
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void setExistingParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void removeNonExistentParameter() {
		Parameters parameters = new Parameters();
		assertThat(this.preprocessor.remove("a").preprocess(createRequest(parameters))
				.getParameters().size()).isEqualTo(0);
	}

	@Test
	public void removeParameter() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		assertThat(this.preprocessor.set("a", "alpha", "avocado")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("alpha", "avocado"));
	}

	@Test
	public void removeParameterValueForNonExistentParameter() {
		Parameters parameters = new Parameters();
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(parameters)).getParameters().size())
						.isEqualTo(0);
	}

	@Test
	public void removeParameterValueWithMultipleValues() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		parameters.add("a", "alpha");
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(parameters)).getParameters()).containsEntry("a",
						Arrays.asList("alpha"));
	}

	@Test
	public void removeParameterValueWithSingleValueRemovesEntryEntirely() {
		Parameters parameters = new Parameters();
		parameters.add("a", "apple");
		assertThat(this.preprocessor.remove("a", "apple")
				.preprocess(createRequest(parameters)).getParameters().size())
						.isEqualTo(0);
	}

	private OperationRequest createRequest(Parameters parameters) {
		return new OperationRequestFactory().create(URI.create("http://localhost:8080"),
				HttpMethod.GET, new byte[0], new HttpHeaders(), parameters,
				Collections.<OperationRequestPart>emptyList());
	}

}
