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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.templates.TemplateFormat;

import static org.assertj.core.api.Assertions.assertThat;
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
		new RequestPartFieldsSnippet("one", Arrays.asList(
				fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three"))).document(this.operationBuilder
						.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
		assertThat(this.generatedSnippets.requestPartFields("one"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));
	}

	@Test
	public void mapRequestPartSubsectionFields() throws IOException {
		new RequestPartFieldsSnippet("one", beneathPath("a"), Arrays.asList(
				fieldWithPath("b").description("one"),
				fieldWithPath("c").description("two"))).document(this.operationBuilder
						.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
		assertThat(this.generatedSnippets.snippet("request-part-one-fields-beneath-a"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "one").row("`c`", "`String`", "two"));
	}

	@Test
	public void multipleRequestParts() throws IOException {
		Operation operation = this.operationBuilder.request("http://localhost")
				.part("one", "{}".getBytes()).and().part("two", "{}".getBytes()).build();
		new RequestPartFieldsSnippet("one", Collections.<FieldDescriptor>emptyList())
				.document(operation);
		new RequestPartFieldsSnippet("two", Collections.<FieldDescriptor>emptyList())
				.document(operation);
		assertThat(this.generatedSnippets.requestPartFields("one")).isNotNull();
		assertThat(this.generatedSnippets.requestPartFields("two")).isNotNull();
	}

	@Test
	public void allUndocumentedRequestPartFieldsCanBeIgnored() throws IOException {
		new RequestPartFieldsSnippet("one",
				Arrays.asList(fieldWithPath("b").description("Field b")), true)
						.document(this.operationBuilder.request("http://localhost")
								.part("one", "{\"a\": 5, \"b\": 4}".getBytes()).build());
		assertThat(this.generatedSnippets.requestPartFields("one"))
				.is(tableWithHeader("Path", "Type", "Description").row("`b`", "`Number`",
						"Field b"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		PayloadDocumentation
				.requestPartFields("one", fieldWithPath("a.b").description("one"),
						fieldWithPath("a.c").description("two"))
				.and(fieldWithPath("a").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
		assertThat(this.generatedSnippets.requestPartFields("one"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		PayloadDocumentation
				.requestPartFields("one", fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
						.build());
		assertThat(this.generatedSnippets.requestPartFields("one"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`Object`", "one").row("`a.b`", "`Number`", "two")
						.row("`a.c`", "`String`", "three"));
	}

}
