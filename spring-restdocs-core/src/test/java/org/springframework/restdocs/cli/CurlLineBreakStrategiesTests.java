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

package org.springframework.restdocs.cli;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;

/**
 * Tests for {@link CurlLineBreakStrategies} and their interactions with
 * {@link CurlRequestSnippet}.
 *
 * @author Paul Samsotha
 */
@RunWith(Parameterized.class)
public class CurlLineBreakStrategiesTests extends AbstractSnippetTests {

	private static final String SEP = System.getProperty("line.separator");

	public CurlLineBreakStrategiesTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void defaultNoLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST"
				+ " -H 'X-Header-One: ONE'"
				+ " -d 'Some Content'";
		this.snippet.expectCurlRequest("default-no-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("default-no-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.none())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.content("Some Content")
				.build());
	}

	@Test
	public void headersOnlyLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST -d 'Some Content' \\"
				+ SEP
				+ " -H 'X-Header-One: ONE' \\" + SEP
				+ " -H 'X-Header-Two: TWO' \\" + SEP
				+ " -H 'X-Header-Three: THREE'";
		this.snippet.expectCurlRequest("headers-only-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("headers-only-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.headersOnly())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.header("X-Header-Two", "TWO")
				.header("X-Header-Three", "THREE")
				.content("Some Content")
				.build());
	}

	@Test
	public void partsOnlyLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST -H 'X-Header-One: ONE' \\"
				+ SEP
				+ " -F 'field1=Field1Data' \\" + SEP
				+ " -F 'field2=Field2Data'";
		this.snippet.expectCurlRequest("parts-only-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("parts-only-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.partsOnly())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.part("field1", "Field1Data".getBytes())
				.and()
				.part("field2", "Field2Data".getBytes())
				.build());
	}

	@Test
	public void headersAndPartsLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST \\" + SEP
				+ " -H 'X-Header-One: ONE' \\" + SEP
				+ " -H 'X-Header-Two: TWO' \\" + SEP
				+ " -H 'X-Header-Three: THREE' \\" + SEP
				+ " -F 'field1=Field1Data' \\" + SEP
				+ " -F 'field2=Field2Data'";
		this.snippet.expectCurlRequest("headers-and-parts-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("headers-and-parts-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.headersAndParts())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.header("X-Header-Two", "TWO")
				.header("X-Header-Three", "THREE")
				.part("field1", "Field1Data".getBytes())
				.and()
				.part("field2", "Field2Data".getBytes())
				.build());
	}

	@Test
	public void contentOnlyLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST -H 'X-Header-One: ONE' \\"
				+ SEP
				+ " -d 'a=aplha&b=bravo'";
		this.snippet.expectCurlRequest("content-only-break")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("content-only-break")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.contentOnly())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.param("a", "aplha")
				.param("b", "bravo")
				.build());
	}

	@Test
	public void headersAndContentLineBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST \\" + SEP
				+ " -H 'X-Header-One: ONE' \\" + SEP
				+ " -H 'X-Header-Two: TWO' \\" + SEP
				+ " -H 'X-Header-Three: THREE' \\" + SEP
				+ " -d 'Some Content'";
		this.snippet.expectCurlRequest("headers-and-content-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("headers-and-content-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						CurlLineBreakStrategies.headersAndContent())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.header("X-Header-Two", "TWO")
				.header("X-Header-Three", "THREE")
				.content("Some Content")
				.build());
	}

	@Test
	public void customPartsBeforeHeaderBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST \\" + SEP
				+ " -F 'field1=Field1Data' \\" + SEP
				+ " -F 'field2=Field2Data' \\" + SEP
				+ " -H 'X-Header-One: ONE' \\" + SEP
				+ " -H 'X-Header-Two: TWO' \\" + SEP
				+ " -H 'X-Header-Three: THREE'";
		this.snippet.expectCurlRequest("custom-parts-before-headers-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("custom-parts-before-headers-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						new PartsAndHeadersCurlLineBreakStrategy())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.header("X-Header-Two", "TWO")
				.header("X-Header-Three", "THREE")
				.part("field1", "Field1Data".getBytes())
				.and()
				.part("field2", "Field2Data".getBytes())
				.build());
	}

	@Test
	public void customContentBeforeHeadersBreaks() throws Exception {
		String expected = "$ curl 'http://localhost/foo' -i -X POST \\" + SEP
				+ " -d 'Some Content' \\" + SEP
				+ " -H 'X-Header-One: ONE' \\" + SEP
				+ " -H 'X-Header-Two: TWO' \\" + SEP
				+ " -H 'X-Header-Three: THREE'";
		this.snippet.expectCurlRequest("custom-content-before-headers-breaks")
				.withContents(codeBlock("bash").content(expected));
		new CurlRequestSnippet().document(
				operationBuilder("custom-content-before-headers-breaks")
				.attribute(CurlLineBreakStrategy.class.getName(),
						new ContentThenHeadersCurlLineBreakStrategy())
				.request("http://localhost/foo")
				.method("POST")
				.header("X-Header-One", "ONE")
				.header("X-Header-Two", "TWO")
				.header("X-Header-Three", "THREE")
				.content("Some Content")
				.build());
	}

	private static final class PartsAndHeadersCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLinesGroups() {
			CurlLineGroup allButHeaders = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.CONTENT);
			CurlLineGroup headers = new CurlLineGroup(CurlPart.HEADERS);
			CurlLineGroup multiparts = new CurlLineGroup(CurlPart.MULTIPARTS);
			return Arrays.asList(allButHeaders, multiparts, headers);
		}

		@Override
		public boolean splitHeaders() {
			return true;
		}

		@Override
		public boolean splitMultiParts() {
			return true;
		}
	}

	private static final class ContentThenHeadersCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLinesGroups() {
			CurlLineGroup allButHeaders = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.MULTIPARTS);
			CurlLineGroup headers = new CurlLineGroup(CurlPart.HEADERS);
			CurlLineGroup content = new CurlLineGroup(CurlPart.CONTENT);
			return Arrays.asList(allButHeaders, content, headers);
		}

		@Override
		public boolean splitHeaders() {
			return true;
		}

		@Override
		public boolean splitMultiParts() {
			return true;
		}
	}
}
