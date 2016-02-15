/*
 * Copyright 2014-2016 the original author or authors.
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

package com.example.restassured;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

@SpringApplicationConfiguration(classes=SampleRestAssuredApplication.class)
@WebIntegrationTest("server.port=0")
@RunWith(SpringJUnit4ClassRunner.class)
public class SampleRestAssuredApplicationTests {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");
	
	private RequestSpecification documentationSpec;
	
	@Value("${local.server.port}")
	private int port;

	@Before
	public void setUp() {
		this.documentationSpec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(restDocumentation)).build();
	}

	@Test
	public void sample() throws Exception {
		given(this.documentationSpec)
				.accept("text/plain")
				.filter(document("sample", 
						preprocessRequest(modifyUris()
								.scheme("https")
								.host("api.example.com")
								.removePort())))
		.when()
				.port(this.port)
				.get("/")
		.then()
				.assertThat().statusCode(is(200));
	}

}
