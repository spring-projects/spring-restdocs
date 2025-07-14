/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.restassured;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify that the understanding of REST Assured's parameter handling behavior is
 * correct.
 *
 * @author Andy Wilkinson
 */
class RestAssuredParameterBehaviorTests {

	private static final MediaType APPLICATION_FORM_URLENCODED_ISO_8859_1 = MediaType
		.parseMediaType(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=ISO-8859-1");

	@RegisterExtension
	public static TomcatServer tomcat = new TomcatServer();

	private final RestAssuredRequestConverter factory = new RestAssuredRequestConverter();

	private OperationRequest request;

	private RequestSpecification spec = RestAssured.given()
		.port(tomcat.getPort())
		.filter((request, response, context) -> {
			this.request = this.factory.convert(request);
			return context.next(request, response);
		});

	@Test
	void queryParameterOnGet() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.get("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.GET);
	}

	@Test
	void queryParameterOnHead() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.head("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.HEAD);
	}

	@Test
	void queryParameterOnPost() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.post("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.POST);
	}

	@Test
	void queryParameterOnPut() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.put("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.PUT);
	}

	@Test
	void queryParameterOnPatch() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.patch("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.PATCH);
	}

	@Test
	void queryParameterOnDelete() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.delete("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.DELETE);
	}

	@Test
	void queryParameterOnOptions() {
		this.spec.queryParam("a", "alpha", "apple")
			.queryParam("b", "bravo")
			.options("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.OPTIONS);
	}

	@Test
	void paramOnGet() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").get("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.GET);
	}

	@Test
	void paramOnHead() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").head("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.HEAD);
	}

	@Test
	void paramOnPost() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").post("/form-url-encoded").then().statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.POST);
	}

	@Test
	void paramOnPut() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").put("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.PUT);
	}

	@Test
	void paramOnPatch() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").patch("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.PATCH);
	}

	@Test
	void paramOnDelete() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").delete("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.DELETE);
	}

	@Test
	void paramOnOptions() {
		this.spec.param("a", "alpha", "apple").param("b", "bravo").options("/query-parameter").then().statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.OPTIONS);
	}

	@Test
	void formParamOnGet() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.get("/query-parameter")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).hasQueryParametersWithMethod(HttpMethod.GET);
	}

	@Test
	void formParamOnHead() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.head("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.HEAD);
	}

	@Test
	void formParamOnPost() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.post("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.POST);
	}

	@Test
	void formParamOnPut() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.put("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.PUT);
	}

	@Test
	void formParamOnPatch() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.patch("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.PATCH);
	}

	@Test
	void formParamOnDelete() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.delete("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.DELETE);
	}

	@Test
	void formParamOnOptions() {
		this.spec.formParam("a", "alpha", "apple")
			.formParam("b", "bravo")
			.options("/form-url-encoded")
			.then()
			.statusCode(200);
		assertThatRequest(this.request).isFormUrlEncodedWithMethod(HttpMethod.OPTIONS);
	}

	private OperationRequestAssert assertThatRequest(OperationRequest request) {
		return new OperationRequestAssert(request);
	}

	private static final class OperationRequestAssert extends AbstractAssert<OperationRequestAssert, OperationRequest> {

		private OperationRequestAssert(OperationRequest actual) {
			super(actual, OperationRequestAssert.class);
		}

		private void isFormUrlEncodedWithMethod(HttpMethod method) {
			assertThat(this.actual.getMethod()).isEqualTo(method);
			assertThat(this.actual.getUri().getRawQuery()).isNull();
			assertThat(this.actual.getContentAsString()).isEqualTo("a=alpha&a=apple&b=bravo");
			assertThat(this.actual.getHeaders().getContentType()).isEqualTo(APPLICATION_FORM_URLENCODED_ISO_8859_1);
		}

		private void hasQueryParametersWithMethod(HttpMethod method) {
			assertThat(this.actual.getMethod()).isEqualTo(method);
			assertThat(this.actual.getUri().getRawQuery()).isEqualTo("a=alpha&a=apple&b=bravo");
			assertThat(this.actual.getContentAsString()).isEmpty();
		}

	}

}
