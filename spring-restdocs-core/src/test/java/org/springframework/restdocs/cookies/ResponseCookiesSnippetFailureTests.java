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

package org.springframework.restdocs.cookies;

import java.io.IOException;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.OperationBuilder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for failures when rendering {@link ResponseCookiesSnippet} due to missing or
 * undocumented cookies.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
public class ResponseCookiesSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(TemplateFormats.asciidoctor());

	@Test
	public void missingResponseCookie() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseCookiesSnippet(Collections
						.singletonList(CookieDocumentation.cookieWithName("Content-Type").description("one")))
								.document(this.operationBuilder.response().build()))
				.withMessage("Cookies with the following names were not found" + " in the response: [Content-Type]");
	}

	@Test
	public void undocumentedResponseCookieAndMissingResponseHeader() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseCookiesSnippet(Collections
						.singletonList(CookieDocumentation.cookieWithName("Content-Type").description("one")))
								.document(this.operationBuilder.response().cookie("X-Test", "test").build()))
				.withMessageEndingWith(
						"Cookies with the following names were not found" + " in the response: [Content-Type]");
	}

}
