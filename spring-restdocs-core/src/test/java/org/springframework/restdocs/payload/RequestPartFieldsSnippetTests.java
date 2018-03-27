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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.templates.TemplateFormat;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for {@link RequestPartFieldsSnippet}.
 *
 * @author Mathieu Pousse
 * @author Andy Wilkinson
 */
public class RequestPartFieldsSnippetTests extends AbstractSnippetTests {

	public RequestPartFieldsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void mapRequestPartFields() throws IOException {
		this.snippets.expectRequestPartFields("one")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));

		new RequestPartFieldsSnippet("one", Arrays.asList(
				fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three"))).document(this.operationBuilder
						.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
	}

	@Test
	public void mapRequestPartSubsectionFields() throws IOException {
		this.snippets.expect("request-part-one-fields-beneath-a")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "one").row("`c`", "`String`", "two"));

		new RequestPartFieldsSnippet("one", beneathPath("a"), Arrays.asList(
				fieldWithPath("b").description("one"),
				fieldWithPath("c").description("two"))).document(this.operationBuilder
						.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
	}

	@Test
	public void multipleRequestParts() throws IOException {
		this.snippets.expectRequestPartFields("one");
		this.snippets.expectRequestPartFields("two");
		Operation operation = this.operationBuilder.request("http://localhost")
				.part("one", "{}".getBytes()).and().part("two", "{}".getBytes()).build();
		new RequestPartFieldsSnippet("one", Collections.<FieldDescriptor>emptyList())
				.document(operation);
		new RequestPartFieldsSnippet("two", Collections.<FieldDescriptor>emptyList())
				.document(operation);
	}

	@Test
	public void allUndocumentedRequestPartFieldsCanBeIgnored() throws IOException {
		this.snippets.expectRequestPartFields("one")
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b`",
						"`Number`", "Field b"));
		new RequestPartFieldsSnippet("one",
				Arrays.asList(fieldWithPath("b").description("Field b")), true)
						.document(this.operationBuilder.request("http://localhost")
								.part("one", "{\"a\": 5, \"b\": 4}".getBytes()).build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippets.expectRequestPartFields("one")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));

		PayloadDocumentation
				.requestPartFields("one", fieldWithPath("a.b").description("one"),
						fieldWithPath("a.c").description("two"))
				.and(fieldWithPath("a").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		this.snippets.expectRequestPartFields("one")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`Object`", "one").row("`a.b`", "`Number`", "two")
						.row("`a.c`", "`String`", "three"));

		PayloadDocumentation
				.requestPartFields("one", fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
	}

}
