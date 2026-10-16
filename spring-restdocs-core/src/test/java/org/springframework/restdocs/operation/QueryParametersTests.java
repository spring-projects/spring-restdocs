/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link QueryParameters}.
 *
 * @author chuuminggg
 */
class QueryParametersTests {

	@Test
	void noQueryString() {
		QueryParameters parameters = queryParameters("http://localhost");
		assertThat(parameters).isEmpty();
	}

	@Test
	void singleParameter() {
		QueryParameters parameters = queryParameters("http://localhost?a=alpha");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("alpha");
	}

	@Test
	void multipleParameters() {
		QueryParameters parameters = queryParameters("http://localhost?a=alpha&b=bravo&a=apple");
		assertThat(parameters).containsOnlyKeys("a", "b");
		assertThat(parameters.get("a")).containsExactly("alpha", "apple");
		assertThat(parameters.get("b")).containsExactly("bravo");
	}

	@Test
	void parameterWithNoValue() {
		QueryParameters parameters = queryParameters("http://localhost?a");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("");
	}

	@Test
	void parameterWithEmptyValue() {
		QueryParameters parameters = queryParameters("http://localhost?a=");
		assertThat(parameters).containsOnlyKeys("a");
		assertThat(parameters.get("a")).containsExactly("");
	}

	@Test
	void encodedParameter() {
		QueryParameters parameters = queryParameters("http://localhost?a%20b=c%26d");
		assertThat(parameters).containsOnlyKeys("a b");
		assertThat(parameters.get("a b")).containsExactly("c&d");
	}

	@Test
	void encodedParameterWithNoValue() {
		QueryParameters parameters = queryParameters("http://localhost?a%20b");
		assertThat(parameters).containsOnlyKeys("a b");
		assertThat(parameters.get("a b")).containsExactly("");
	}

	@Test
	void parameterWithValueContainingEquals() {
		QueryParameters parameters = queryParameters("http://localhost?filter=name==bob");
		assertThat(parameters).containsOnlyKeys("filter");
		assertThat(parameters.get("filter")).containsExactly("name==bob");
	}

	@Test
	void parameterWithValueEndingWithEquals() {
		QueryParameters parameters = queryParameters("http://localhost?cursor=YWJj==");
		assertThat(parameters).containsOnlyKeys("cursor");
		assertThat(parameters.get("cursor")).containsExactly("YWJj==");
	}

	private QueryParameters queryParameters(String uri) {
		return QueryParameters.from(new OperationRequestFactory().create(URI.create(uri), HttpMethod.GET, new byte[0],
				new HttpHeaders(), Collections.emptyList()));
	}

}
