/*
 * Copyright 2014-present the original author or authors.
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
