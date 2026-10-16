/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FormParameters}.
 *
 * @author chuuminggg
 */
class FormParametersTests {

	@Test
	void noContent() {
		FormParameters parameters = formParameters("");
		assertThat(parameters).isEmpty();
	}

	@Test
	void singleParameter() {
		FormParameters parameters = formParameters("a=alpha");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("alpha");
	}

	@Test
	void multipleParameters() {
		FormParameters parameters = formParameters("a=alpha&b=bravo&a=apple");
		assertThat(parameters).containsOnlyKeys("a", "b");
		assertThat(parameters.get("a")).containsExactly("alpha", "apple");
		assertThat(parameters.get("b")).containsExactly("bravo");
	}

	@Test
	void parameterWithNoValue() {
		FormParameters parameters = formParameters("a");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("");
	}

	@Test
	void parameterWithEmptyValue() {
		FormParameters parameters = formParameters("a=");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("");
	}

	@Test
	void encodedParameter() {
		FormParameters parameters = formParameters("a+b=c%26d");
		assertThat(parameters).containsOnlyKeys("a b");
		assertThat(parameters.get("a b")).containsExactly("c&d");
	}

	@Test
	void encodedParameterWithNoValue() {
		FormParameters parameters = formParameters("a+b");
		assertThat(parameters).containsOnlyKeys("a b");
		assertThat(parameters.get("a b")).containsExactly("");
	}

	@Test
	void parameterWithValueContainingEquals() {
		FormParameters parameters = formParameters("filter=name==bob");
		assertThat(parameters).containsOnlyKeys("filter");
		assertThat(parameters.get("filter")).containsExactly("name==bob");
	}

	@Test
	void parameterWithValueEndingWithEquals() {
		FormParameters parameters = formParameters("cursor=YWJj==");
		assertThat(parameters).containsOnlyKeys("cursor");
		assertThat(parameters.get("cursor")).containsExactly("YWJj==");
	}

	private FormParameters formParameters(String content) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return FormParameters.from(new OperationRequestFactory().create(URI.create("http://localhost"), HttpMethod.POST,
				content.getBytes(StandardCharsets.UTF_8), headers, Collections.emptyList()));
	}

}
