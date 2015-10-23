package org.springframework.restdocs.restassured;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.curl.CurlDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationIntegrationTests.TestApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class RestAssuredRestDocumentationIntegrationTests {

	@Rule
	public RestDocumentation restDocumentation = new RestDocumentation(
			"build/generated-snippets");

	@Value("${local.server.port}")
	private int port;

	@Test
	public void test() {
		given().port(this.port)
				.filter(document(this.restDocumentation, "test1",
								CurlDocumentation.curlRequest(),
								HttpDocumentation.httpResponse()
						)

				).get("/").then().statusCode(200);
	}


	@Test
	public void requestParametersSnippet() {
		given()
			.port(this.port)
			.filter(document(this.restDocumentation, "test2",
					CurlDocumentation.curlRequest(),
					HttpDocumentation.httpResponse(),
					requestParameters(
							parameterWithName("page").description("The page to retrieve"),
							parameterWithName("size").description("The page size"))))
			.param("page", 2)
			.param("size", 20)
			.get("/")
		.then().statusCode(200);

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/test2"),
				"http-response.adoc", "curl-request.adoc",
				"request-parameters.adoc");
	}


	@Test
	public void requestQueryParametersSnippet() {
		given()
			.port(this.port)
			.filter(document(this.restDocumentation, "test3",
					CurlDocumentation.curlRequest(),
					HttpDocumentation.httpResponse(),
					requestParameters(
							parameterWithName("page").description("The page to retrieve"),
							parameterWithName("size").description("The page size"))))
			.queryParam("page", 2)
			.queryParam("size", 20)
			.get("/")
		.then().statusCode(200);

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/test3"),
				"http-response.adoc", "curl-request.adoc",
				"request-parameters.adoc");
	}


	@Test
	public void requestPathParametersSnippet() {
		final String path = "/do/{param}";
		given()
			.port(this.port)
			.filter(document(this.restDocumentation, "test4",
					CurlDocumentation.curlRequest(),
					HttpDocumentation.httpResponse(),
					pathParameters(path, parameterWithName("param").description("The parameter"))))
			.pathParam("param", "foo")
			.get(path)
				.then().statusCode(200);

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/test4"),
				"http-response.adoc", "curl-request.adoc",
				"path-parameters.adoc");
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			assertTrue(new File(directory, snippet).isFile());
		}
	}



	@SpringBootApplication
	static class TestApplication {

	}

	@RestController
	static class TestController {

		@RequestMapping("/")
		public void index() {

		}

		@RequestMapping(value = "/do/{param}")
		public void param(@PathVariable("param") String brand) {
		}

	}

}
