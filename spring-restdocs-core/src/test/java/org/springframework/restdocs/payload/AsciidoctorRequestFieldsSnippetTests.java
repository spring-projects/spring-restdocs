/*
 * Copyright 2012-2016 the original author or authors.
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

import org.junit.Rule;
import org.junit.Test;

import org.springframework.core.io.FileSystemResource;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;

/**
 * Tests for {@link RequestFieldsSnippet} that are specific to Asciidoctor.
 *
 * @author Andy Wilkinson
 */
public class AsciidoctorRequestFieldsSnippetTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet(TemplateFormats.asciidoctor());

	@Test
	public void requestFieldsWithListDescription() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-list-description"));
		this.snippet.expectRequestFields("request-fields-with-list-description")
				.withContents(
						tableWithHeader(asciidoctor(), "Path", "Type", "Description")
								//
								.row("a", "String", String.format(" - one%n - two"))
								.configuration("[cols=\"1,1,1a\"]"));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a").description(Arrays.asList("one", "two")))).document(
						new OperationBuilder("request-fields-with-list-description",
								this.snippet.getOutputDirectory())
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.request("http://localhost")
										.content("{\"a\": \"foo\"}").build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource(
				"src/test/resources/custom-snippet-templates/asciidoctor/" + name
						+ ".snippet");
	}

}
