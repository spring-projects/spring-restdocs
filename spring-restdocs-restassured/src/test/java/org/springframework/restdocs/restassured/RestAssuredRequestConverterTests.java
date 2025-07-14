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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.RequestCookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link RestAssuredRequestConverter}.
 *
 * @author Andy Wilkinson
 */
class RestAssuredRequestConverterTests {

	@RegisterExtension
	public static TomcatServer tomcat = new TomcatServer();

	private final RestAssuredRequestConverter factory = new RestAssuredRequestConverter();

	@Test
	void requestUri() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.get("/foo/bar");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:" + tomcat.getPort() + "/foo/bar"));
	}

	@Test
	void requestMethod() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.head("/foo/bar");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getMethod()).isEqualTo(HttpMethod.HEAD);
	}

	@Test
	void queryStringParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort()).queryParam("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri().getRawQuery()).isEqualTo("foo=bar");
	}

	@Test
	void queryStringFromUrlParameters() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort());
		requestSpec.get("/?foo=bar&foo=qix");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri().getRawQuery()).isEqualTo("foo=bar&foo=qix");
	}

	@Test
	void paramOnGetRequestIsMappedToQueryString() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort()).param("foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getUri().getRawQuery()).isEqualTo("foo=bar");
	}

	@Test
	void headers() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort()).header("Foo", "bar");
		requestSpec.get("/");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getHeaders().headerSet()).containsOnly(entry("Foo", Collections.singletonList("bar")),
				entry("Host", Collections.singletonList("localhost:" + tomcat.getPort())));
	}

	@Test
	void headersWithCustomAccept() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.header("Foo", "bar")
			.accept("application/json");
		requestSpec.get("/");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getHeaders().headerSet()).containsOnly(entry("Foo", Collections.singletonList("bar")),
				entry("Accept", Collections.singletonList("application/json")),
				entry("Host", Collections.singletonList("localhost:" + tomcat.getPort())));
	}

	@Test
	void cookies() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.cookie("cookie1", "cookieVal1")
			.cookie("cookie2", "cookieVal2");
		requestSpec.get("/");
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getCookies().size()).isEqualTo(2);

		Iterator<RequestCookie> cookieIterator = request.getCookies().iterator();
		RequestCookie cookie1 = cookieIterator.next();

		assertThat(cookie1.getName()).isEqualTo("cookie1");
		assertThat(cookie1.getValue()).isEqualTo("cookieVal1");

		RequestCookie cookie2 = cookieIterator.next();
		assertThat(cookie2.getName()).isEqualTo("cookie2");
		assertThat(cookie2.getValue()).isEqualTo("cookieVal2");
	}

	@Test
	void multipart() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart("a", "a.txt", "alpha", null)
			.multiPart("b", new ObjectBody("bar"), "application/json");
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		Collection<OperationRequestPart> parts = request.getParts();
		assertThat(parts).hasSize(2);
		assertThat(parts).extracting("name").containsExactly("a", "b");
		assertThat(parts).extracting("submittedFileName").containsExactly("a.txt", "file");
		assertThat(parts).extracting("contentAsString").containsExactly("alpha", "{\"foo\":\"bar\"}");
		assertThat(parts).map((part) -> part.getHeaders().get(HttpHeaders.CONTENT_TYPE))
			.containsExactly(Collections.singletonList(MediaType.TEXT_PLAIN_VALUE),
					Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void byteArrayBody() {
		RequestSpecification requestSpec = RestAssured.given().body("body".getBytes()).port(tomcat.getPort());
		requestSpec.post();
		this.factory.convert((FilterableRequestSpecification) requestSpec);
	}

	@Test
	void stringBody() {
		RequestSpecification requestSpec = RestAssured.given().body("body").port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString()).isEqualTo("body");
	}

	@Test
	void objectBody() {
		RequestSpecification requestSpec = RestAssured.given().body(new ObjectBody("bar")).port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString()).isEqualTo("{\"foo\":\"bar\"}");
	}

	@Test
	void byteArrayInputStreamBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.body(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }))
			.port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContent()).isEqualTo(new byte[] { 1, 2, 3, 4 });
	}

	@Test
	void fileBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.body(new File("src/test/resources/body.txt"))
			.port(tomcat.getPort());
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getContentAsString()).isEqualTo("file");
	}

	@Test
	void fileInputStreamBody() throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream("src/test/resources/body.txt");
		RequestSpecification requestSpec = RestAssured.given().body(inputStream).port(tomcat.getPort());
		requestSpec.post();
		assertThatIllegalStateException()
			.isThrownBy(() -> this.factory.convert((FilterableRequestSpecification) requestSpec))
			.withMessage("Cannot read content from input stream " + inputStream + " due to reset() failure");
	}

	@Test
	void multipartWithByteArrayInputStreamBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart("foo", "foo.txt", new ByteArrayInputStream("foo".getBytes()));
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString()).isEqualTo("foo");
	}

	@Test
	void multipartWithStringBody() {
		RequestSpecification requestSpec = RestAssured.given().port(tomcat.getPort()).multiPart("control", "foo");
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString()).isEqualTo("foo");
	}

	@Test
	void multipartWithByteArrayBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart("control", "file", "foo".getBytes());
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString()).isEqualTo("foo");
	}

	@Test
	void multipartWithFileBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart(new File("src/test/resources/body.txt"));
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString()).isEqualTo("file");
	}

	@Test
	void multipartWithFileInputStreamBody() throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream("src/test/resources/body.txt");
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart("foo", "foo.txt", inputStream);
		requestSpec.post();
		assertThatIllegalStateException()
			.isThrownBy(() -> this.factory.convert((FilterableRequestSpecification) requestSpec))
			.withMessage("Cannot read content from input stream " + inputStream + " due to reset() failure");
	}

	@Test
	void multipartWithObjectBody() {
		RequestSpecification requestSpec = RestAssured.given()
			.port(tomcat.getPort())
			.multiPart("control", new ObjectBody("bar"));
		requestSpec.post();
		OperationRequest request = this.factory.convert((FilterableRequestSpecification) requestSpec);
		assertThat(request.getParts().iterator().next().getContentAsString()).isEqualTo("{\"foo\":\"bar\"}");
	}

	/**
	 * Sample object body to verify JSON serialization.
	 */
	public static class ObjectBody {

		private final String foo;

		ObjectBody(String foo) {
			this.foo = foo;
		}

		public String getFoo() {
			return this.foo;
		}

	}

}
