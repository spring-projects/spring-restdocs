/*
 * Copyright 2014-2017 the original author or authors.
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

package org.springframework.restdocs.asciidoctor;

import java.io.File;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link RestDocsIncludeProcessor}.
 *
 * @author Gerrit Meier
 */
public class RestDocsIncludeProcessorTest {

	private final Options options = new Options();
	private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();

	@Before
	public void setUp() {
		this.options.setAttributes(getAttributes());
	}

	private Attributes getAttributes() {
		Attributes attributes = new Attributes();
		attributes.setAttribute("projectdir", new File(".").getAbsolutePath());
		return attributes;
	}

	@Test
	public void insertsNothingIfNoSnippetIsDefined() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[]", this.options);

		assertEquals("", result);
	}

	@Test
	public void includeWarningMessageIfCustomSnippetNotFound() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='test']", this.options);

		assertTrue(result.contains("Snippet not found at"));
		assertTrue(result.contains("/build/generated-snippets/some-operation/test.adoc"));
	}

	@Test
	public void includeWarningMessageIfCommonSnippetNotFound() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='httpie-request']", this.options);

		assertTrue(result.contains("Snippet not found at"));
		assertTrue(result.contains("/build/generated-snippets/some-operation/httpie-request.adoc"));
	}

	@Test
	public void includesSingleSnippet() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='curl-request']", this.options);

		assertTrue(result.contains("curl 'http://localhost:8080/' -i"));
	}

	@Test
	public void includesCustomSnippet() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='custom-snippet']", this.options);

		assertTrue(result.contains("Custom snippet"));
		assertTrue(result.contains("mycustomsnippet"));
		assertFalse(result.contains("Snippet not found at"));
	}

	@Test
	public void includesMultipleSnippetsInTheRightOrder() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='curl-request, http-request']", this.options);
		String curlSnippet = "curl 'http://localhost:8080/' -i";
		assertTrue(result.contains(curlSnippet));
		assertTrue(result.split(curlSnippet)[1].contains("GET / HTTP/1.1"));
	}

	@Test
	public void ignoresBlankSnippets() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='curl-request, ']", this.options);

		assertTrue(result.contains("curl 'http://localhost:8080/' -i"));
		assertFalse(result.contains("Snippet not found at"));
	}

	@Test
	public void sectionLevelDefaultsToFour() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='curl-request']", this.options);

		assertTrue(result.contains("<h4 id=\"_curl_request\">curl request</h4>"));
	}

	@Test
	public void customSectionLevel() {
		String result = this.asciidoctor.convert(
				"include::restdocs:some-operation[snippets='curl-request', level=2]", this.options);

		assertTrue(result.contains("<h2 id=\"_curl_request\">curl request</h2>"));
	}


}
