/*
 * Copyright 2014-2015 the original author or authors.
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.documentRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.documentResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.StubMvcResult;
import org.springframework.util.StringUtils;

/**
 * Tests for {@link PayloadDocumentation}
 * 
 * @author Andy Wilkinson
 */
public class PayloadDocumentationTests {

	private final File outputDir = new File("build/payload-documentation-tests");

	@Before
	public void setup() {
		System.setProperty("org.springframework.restdocs.outputDir",
				this.outputDir.getAbsolutePath());
	}

	@After
	public void cleanup() {
		System.clearProperty("org.springframework.restdocs.outputDir");
	}

	@Test
	public void requestWithFields() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setContent("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes());
		documentRequestFields("request-with-fields",
				fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three")).handle(
				new StubMvcResult(request, null));
		assertThat(
				snippet("request-with-fields", "request-fields"),
				is(asciidoctorTableWith(header("Path", "Type", "Description"),
						row("a.b", "Number", "one"), row("a.c", "String", "two"),
						row("a", "Object", "three"))));
	}

	@Test
	public void responseWithFields() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter()
				.append("{\"id\": 67,\"date\": \"2015-01-20\",\"assets\": [{\"id\":356,\"name\": \"sample\"}]}");
		documentResponseFields("response-with-fields",
				fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"),
				fieldWithPath("assets").description("three"),
				fieldWithPath("assets[]").description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")).handle(
				new StubMvcResult(new MockHttpServletRequest("GET", "/"), response));
		assertThat(
				snippet("response-with-fields", "response-fields"),
				is(asciidoctorTableWith(header("Path", "Type", "Description"),
						row("id", "Number", "one"), row("date", "String", "two"),
						row("assets", "Array", "three"),
						row("assets[]", "Object", "four"),
						row("assets[].id", "Number", "five"),
						row("assets[].name", "String", "six"))));
	}

	private Matcher<Iterable<? extends String>> asciidoctorTableWith(String[] header,
			String[]... rows) {
		Collection<Matcher<? super String>> matchers = new ArrayList<Matcher<? super String>>();
		for (String headerItem : header) {
			matchers.add(equalTo(headerItem));
		}

		for (String[] row : rows) {
			for (String rowItem : row) {
				matchers.add(equalTo(rowItem));
			}
		}

		matchers.add(equalTo("|==="));
		matchers.add(equalTo(""));

		return new IsIterableContainingInAnyOrder<String>(matchers);
	}

	private String[] header(String... columns) {
		String header = "|"
				+ StringUtils.collectionToDelimitedString(Arrays.asList(columns), "|");
		return new String[] { "", "|===", header, "" };
	}

	private String[] row(String... entries) {
		List<String> lines = new ArrayList<String>();
		for (String entry : entries) {
			lines.add("|" + entry);
		}
		lines.add("");
		return lines.toArray(new String[lines.size()]);
	}

	private List<String> snippet(String snippetName, String snippetType)
			throws IOException {
		File snippetDir = new File(this.outputDir, snippetName);
		File snippetFile = new File(snippetDir, snippetType + ".adoc");
		String line = null;
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(snippetFile));
		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		finally {
			reader.close();
		}
		return lines;
	}

}
