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

package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.test.ExpectedSnippets;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;

/**
 * Tests for failures when rendering {@link RequestPartsSnippet} due to missing or
 * undocumented request parts.
 *
 * @author Andy Wilkinson
 */
public class RequestPartsSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(asciidoctor());

	@Rule
	public ExpectedSnippets snippets = new ExpectedSnippets(asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedPart() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo(
				"Request parts with the following names were" + " not documented: [a]"));
		new RequestPartsSnippet(Collections.<RequestPartDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
						.part("a", "alpha".getBytes()).build());
	}

	@Test
	public void missingPart() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Request parts with the following names were"
				+ " not found in the request: [a]"));
		new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one")))
				.document(this.operationBuilder.request("http://localhost").build());
	}

	@Test
	public void undocumentedAndMissingParts() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Request parts with the following names were"
				+ " not documented: [b]. Request parts with the following"
				+ " names were not found in the request: [a]"));
		new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.part("b", "bravo".getBytes()).build());
	}

}
