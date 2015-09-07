package org.springframework.restdocs.restassured;

import static com.jayway.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.curl.CurlDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationIntegrationTests.TestApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
				.filter(document(this.restDocumentation, "test",
						CurlDocumentation.curlRequest())).get("/").then().statusCode(200);
	}

	@SpringBootApplication
	static class TestApplication {

	}

	@RestController
	static class TestController {

		@RequestMapping("/")
		public void index() {

		}

	}

}
