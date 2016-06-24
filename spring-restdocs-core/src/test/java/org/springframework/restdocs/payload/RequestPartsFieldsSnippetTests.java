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

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for {@link RequestPartFieldsSnippet}.
 *
 * @author Mathieu Pousse
 */
public class RequestPartsFieldsSnippetTests extends AbstractSnippetTests {

	public RequestPartsFieldsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void mapRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("map-request-parts-with-fields")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));

		new RequestPartFieldsSnippet("part", Arrays.asList(fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three")))
						.document(operationBuilder("map-request-parts-with-fields")
								.request("http://localhost")
								.part("part", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
								.build());
	}

}
