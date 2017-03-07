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
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.util.FileSystemUtils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * Tests for Ruby operation block macro.
 *
 * @author Gerrit Meier
 * @author Andy Wilkinson
 */
public class OperationBlockMacroTests {

	private final Options options = new Options();

	private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();

	@BeforeClass
	public static void prepareOperationSnippets() throws IOException {
		File destination = new File("build/generated-snippets/some-operation");
		destination.mkdirs();
		FileSystemUtils.copyRecursively(new File("src/test/resources/some-operation"),
				destination);
	}

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
	public void simpleSnippetInclude() throws Exception {
		String result = this.asciidoctor.convert(
				"operation::some-operation[snippets='curl-request']", this.options);
		assertThat(result, equalTo(getExpectedContentFromFile("snippet-simple")));
	}

	@Test
	public void includeSnippetInSection() throws Exception {
		String result = this.asciidoctor.convert(
				"[[bruce]]\n== Section\n"
						+ "operation::some-operation[snippets='curl-request']",
				this.options);
		assertThat(result, equalTo(getExpectedContentFromFile("snippet-in-section")));
	}

	@Test
	public void includeMultipleSnippets() throws Exception {
		String result = this.asciidoctor.convert(
				"operation::some-operation[snippets='curl-request,http-request']",
				this.options);
		assertThat(result, equalTo(getExpectedContentFromFile("multiple-snippets")));
	}

	@Test
	public void useMacroWithoutSnippetAttributeAddsAllSnippets() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[]",
				this.options);
		assertThat(result, equalTo(getExpectedContentFromFile("all-snippets")));
	}

	@Test
	public void useMacroWithEmptySnippetAttributeAddsAllSnippets() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets=]",
				this.options);
		assertThat(result, equalTo(getExpectedContentFromFile("all-snippets")));
	}

	@Test
	public void includingUnknownSnippetAddsWarning() throws Exception {
		String result = this.asciidoctor.convert(
				"operation::some-operation[snippets='unknown-snippet']", this.options);
		assertThat(result, startsWith(getExpectedContentFromFile("missing-snippet")));
	}

	@Test
	public void includingCustomSnippetCreatesCustomTitle() throws Exception {
		String result = this.asciidoctor.convert(
				"operation::some-operation[snippets='custom-snippet']", this.options);
		assertThat(result,
				containsString(getExpectedContentFromFile("snippet-custom-title")));
	}

	@Test
	public void nonExistentOperationIsHandledGracefully() throws Exception {
		String result = this.asciidoctor.convert("operation::non-existent-operation[]",
				this.options);
		assertThat(result, startsWith(getExpectedContentFromFile("missing-operation")));
	}

	private String getExpectedContentFromFile(String fileName)
			throws URISyntaxException, IOException {
		Path filePath = Paths.get(
				this.getClass().getResource("/operations/" + fileName + ".html").toURI());
		String content = new String(Files.readAllBytes(filePath));
		if (isWindows()) {
			return content.replace("\n", "\r\n");
		}
		return content;
	}

	private boolean isWindows() {
		return File.separatorChar == '\\';
	}

}
