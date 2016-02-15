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

package org.springframework.restdocs.restassured;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.restassured.RestAssuredRequestConverterTests.TestApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link RestAssuredRequestConverter}.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class RestAssuredRequestConverterTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final RestAssuredRequestConverter factory = new RestAssuredRequestConverter();

	@Value("${local.server.port}")
	private int port;

	@Test
	public void requestUri() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port);
		requestSpec.get("/foo/bar");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri(),
				is(equalTo(URI.create("http://localhost:" + this.port + "/foo/bar"))));
	}

	@Test
	public void requestMethod() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port);
		requestSpec.head("/foo/bar");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getMethod(), is(equalTo(HttpMethod.HEAD)));
	}

	@Test
	public void queryStringParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port)
				.queryParam("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void queryStringFromUrlParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port);
		requestSpec.get("/?foo=bar");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void formParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port)
				.formParameter("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void requestParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port)
				.parameter("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void headers() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port)
				.header("Foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getHeaders().toString(), request.getHeaders().size(), is(3));
		assertThat(request.getHeaders().get("Foo"), is(equalTo(Arrays.asList("bar"))));
		assertThat(request.getHeaders().get("Accept"), is(equalTo(Arrays.asList("*/*"))));
		assertThat(request.getHeaders().get("Host"),
				is(equalTo(Arrays.asList("localhost"))));
	}

	@Test
	public void multipart() {
		RequestSpecification requestSpec = RestAssured.given().port(this.port)
				.multiPart("a", "a.txt", "alpha", null)
				.multiPart("b", new ObjectBody("bar"), "application/json");
		requestSpec.post().then().statusCode(200);
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		Collection<OperationRequestPart> parts = request.getParts();
		assertThat(parts.size(), is(2));
		Iterator<OperationRequestPart> iterator = parts.iterator();
		OperationRequestPart part = iterator.next();
		assertThat(part.getName(), is(equalTo("a")));
		assertThat(part.getSubmittedFileName(), is(equalTo("a.txt")));
		assertThat(part.getContentAsString(), is(equalTo("alpha")));
		assertThat(part.getHeaders().getContentType(), is(equalTo(MediaType.TEXT_PLAIN)));
		part = iterator.next();
		assertThat(part.getName(), is(equalTo("b")));
		assertThat(part.getSubmittedFileName(), is(equalTo("file")));
		assertThat(part.getContentAsString(), is(equalTo("{\"foo\":\"bar\"}")));
		assertThat(part.getHeaders().getContentType(),
				is(equalTo(MediaType.APPLICATION_JSON)));
	}

	@Test
	public void byteArrayBody() {
		RequestSpecification requestSpec = RestAssured.given().body("body".getBytes())
				.port(this.port);
		requestSpec.post();
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	public void stringBody() {
		RequestSpecification requestSpec = RestAssured.given().body("body")
				.port(this.port);
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString(), is(equalTo("body")));
	}

	@Test
	public void objectBody() {
		RequestSpecification requestSpec = RestAssured.given().body(new ObjectBody("bar"))
				.port(this.port);
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString(), is(equalTo("{\"foo\":\"bar\"}")));
	}

	@Test
	public void inputStreamBodyIsNotSupported() {
		RequestSpecification requestSpec = RestAssured.given()
				.body(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }))
				.port(this.port);
		requestSpec.post();
		this.thrown.expectMessage(
				equalTo("Unsupported request content: java.io.ByteArrayInputStream"));
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	public void fileBodyIsNotSupported() {
		RequestSpecification requestSpec = RestAssured.given()
				.body(new File("src/test/resources/body.txt")).port(this.port);
		requestSpec.post();
		this.thrown.expectMessage(equalTo("Unsupported request content: java.io.File"));
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	/**
	 * Minimal test web application.
	 */
	@Configuration
	@EnableAutoConfiguration
	@RestController
	static class TestApplication {

		@RequestMapping("/")
		void handle() {
		}

	}

	/**
	 * Sample object body to verify JSON serialization.
	 */
	static class ObjectBody {

		private final String foo;

		ObjectBody(String foo) {
			this.foo = foo;
		}

		public String getFoo() {
			return this.foo;
		}

	}

}
