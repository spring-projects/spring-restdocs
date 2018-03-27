/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.restassured3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.RequestCookie;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link RestAssuredRequestConverter}.
 *
 * @author Andy Wilkinson
 */
public class RestAssuredRequestConverterTests {

	@ClassRule
	public static TomcatServer tomcat = new TomcatServer();

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final RestAssuredRequestConverter factory = new RestAssuredRequestConverter();

	@Test
	public void requestUri() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.get("/foo/bar");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri(), is(equalTo(
				URI.create("http://localhost:" + tomcat.getPort() + "/foo/bar"))));
	}

	@Test
	public void requestMethod() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.head("/foo/bar");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getMethod(), is(equalTo(HttpMethod.HEAD)));
	}

	@Test
	public void queryStringParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.queryParam("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void queryStringFromUrlParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.get("/?foo=bar&foo=qix");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"),
				is(equalTo(Arrays.asList("bar", "qix"))));
	}

	@Test
	public void formParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.formParam("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void requestParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.param("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParameters().size(), is(1));
		assertThat(request.getParameters().get("foo"), is(equalTo(Arrays.asList("bar"))));
	}

	@Test
	public void headers() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.header("Foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getHeaders().toString(), request.getHeaders().size(), is(2));
		assertThat(request.getHeaders().get("Foo"), is(equalTo(Arrays.asList("bar"))));
		assertThat(request.getHeaders().get("Host"),
				is(equalTo(Arrays.asList("localhost:" + tomcat.getPort()))));
	}

	@Test
	public void headersWithCustomAccept() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.header("Foo", "bar").accept("application/json");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getHeaders().toString(), request.getHeaders().size(), is(3));
		assertThat(request.getHeaders().get("Foo"), is(equalTo(Arrays.asList("bar"))));
		assertThat(request.getHeaders().get("Accept"),
				is(equalTo(Arrays.asList("application/json"))));
		assertThat(request.getHeaders().get("Host"),
				is(equalTo(Arrays.asList("localhost:" + tomcat.getPort()))));
	}

	@Test
	public void cookies() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.cookie("cookie1", "cookieVal1").cookie("cookie2", "cookieVal2");
		requestSpec.get("/");
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getCookies().size(), is(equalTo(2)));

		Iterator<RequestCookie> cookieIterator = request.getCookies().iterator();
		RequestCookie cookie1 = cookieIterator.next();

		assertThat(cookie1.getName(), is(equalTo("cookie1")));
		assertThat(cookie1.getValue(), is(equalTo("cookieVal1")));

		RequestCookie cookie2 = cookieIterator.next();
		assertThat(cookie2.getName(), is(equalTo("cookie2")));
		assertThat(cookie2.getValue(), is(equalTo("cookieVal2")));
	}

	@Test
	public void multipart() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("a", "a.txt", "alpha", null)
				.multiPart("b", new ObjectBody("bar"), "application/json");
		requestSpec.post();
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
				.port(tomcat.getPort());
		requestSpec.post();
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	public void stringBody() {
		RequestSpecification requestSpec = RestAssured.given().body("body")
				.port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString(), is(equalTo("body")));
	}

	@Test
	public void objectBody() {
		RequestSpecification requestSpec = RestAssured.given().body(new ObjectBody("bar"))
				.port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString(), is(equalTo("{\"foo\":\"bar\"}")));
	}

	@Test
	public void byteArrayInputStreamBody() {
		RequestSpecification requestSpec = RestAssured.given()
				.body(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }))
				.port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContent(), is(equalTo(new byte[] { 1, 2, 3, 4 })));
	}

	@Test
	public void fileBody() {
		RequestSpecification requestSpec = RestAssured.given()
				.body(new File("src/test/resources/body.txt")).port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString(), is(equalTo("file")));
	}

	@Test
	public void fileInputStreamBody() throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream("src/test/resources/body.txt");
		RequestSpecification requestSpec = RestAssured.given().body(inputStream)
				.port(tomcat.getPort());
		requestSpec.post();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Cannot read content from input stream " + inputStream
				+ " due to reset() failure");
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	public void multipartWithByteArrayInputStreamBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("foo", "foo.txt", new ByteArrayInputStream("foo".getBytes()));
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString(),
				is(equalTo("foo")));
	}

	@Test
	public void multipartWithStringBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("control", "foo");
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString(),
				is(equalTo("foo")));
	}

	@Test
	public void multipartWithByteArrayBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("control", "file", "foo".getBytes());
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString(),
				is(equalTo("foo")));
	}

	@Test
	public void multipartWithFileBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart(new File("src/test/resources/body.txt"));
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString(),
				is(equalTo("file")));
	}

	@Test
	public void multipartWithFileInputStreamBody() throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream("src/test/resources/body.txt");
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("foo", "foo.txt", inputStream);
		requestSpec.post();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Cannot read content from input stream " + inputStream
				+ " due to reset() failure");
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	public void multipartWithObjectBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort())
				.multiPart("control", new ObjectBody("bar"));
		requestSpec.post();
		OperationRequest request = this.factory
				.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString(),
				is(equalTo("{\"foo\":\"bar\"}")));
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
