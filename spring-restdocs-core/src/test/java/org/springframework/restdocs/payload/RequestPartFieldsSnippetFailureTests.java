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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for failures when rendering {@link RequestPartFieldsSnippet} due to missing or
 * undocumented fields.
 *
 * @author Mathieu Pousse
 * @author Andy Wilkinson
 */
public class RequestPartFieldsSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(
			TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedRequestPartField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				startsWith("The following parts of the payload were not documented:"));
		new RequestPartFieldsSnippet("part", Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
						.part("part", "{\"a\": 5}".getBytes()).build());
	}

	@Test
	public void missingRequestPartField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				startsWith("The following parts of the payload were not documented:"));
		new RequestPartFieldsSnippet("part",
				Arrays.asList(fieldWithPath("b").description("one")))
						.document(this.operationBuilder.request("http://localhost")
								.part("part", "{\"a\": 5}".getBytes()).build());
	}

	@Test
	public void missingRequestPart() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				equalTo("A request part named 'another' was not found in the request"));
		new RequestPartFieldsSnippet("another",
				Arrays.asList(fieldWithPath("a.b").description("one")))
						.document(this.operationBuilder.request("http://localhost")
								.part("part", "{\"a\": {\"b\": 5}}".getBytes()).build());
	}

}
