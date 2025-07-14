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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest.Format;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for {@link RequestFieldsSnippet} that are specific to Asciidoctor.
 *
 * @author Andy Wilkinson
 */
class AsciidoctorRequestFieldsSnippetTests {

	@RenderedSnippetTest(format = Format.ASCIIDOCTOR)
	@SnippetTemplate(snippet = "request-fields", template = "request-fields-with-list-description")
	void requestFieldsWithListDescription(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description(Arrays.asList("one", "two"))))
			.document(operationBuilder.request("http://localhost").content("{\"a\": \"foo\"}").build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("a", "String", String.format(" - one%n - two"))
			.configuration("[cols=\"1,1,1a\"]"));
	}

}
