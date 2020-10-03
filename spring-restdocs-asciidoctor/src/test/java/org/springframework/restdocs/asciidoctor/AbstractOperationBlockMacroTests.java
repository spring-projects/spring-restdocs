/*
 * Copyright 2014-2019 the original author or authors.
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

package org.springframework.restdocs.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.util.FileSystemUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for tests for the Ruby operation block macro.
 *
 * @author Gerrit Meier
 * @author Andy Wilkinson
 */
public abstract class AbstractOperationBlockMacroTests {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	private Options options;

	private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();

	@Before
	public void setUp() throws IOException {
		prepareOperationSnippets(getBuildOutputLocation());
		this.options = OptionsBuilder.options().safe(SafeMode.UNSAFE).baseDir(getSourceLocation()).get();
		this.options.setAttributes(getAttributes());
		CapturingLogHandler.clear();
	}

	@After
	public void verifyLogging() {
		assertThat(CapturingLogHandler.getLogRecords()).isEmpty();
	}

	public void prepareOperationSnippets(File buildOutputLocation) throws IOException {
		File destination = new File(buildOutputLocation, "generated-snippets/some-operation");
		destination.mkdirs();
		FileSystemUtils.copyRecursively(new File("src/test/resources/some-operation"), destination);
	}

	@Test
	public void codeBlockSnippetInclude() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets='curl-request']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-simple"));
	}

	@Test
	public void operationWithParameterizedName() throws Exception {
		Attributes attributes = getAttributes();
		attributes.setAttribute("name", "some");
		this.options.setAttributes(attributes);
		String result = this.asciidoctor.convert("operation::{name}-operation[snippets='curl-request']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-simple"));
	}

	@Test
	public void codeBlockSnippetIncludeWithPdfBackend() throws Exception {
		File output = configurePdfOutput();
		this.asciidoctor.convert("operation::some-operation[snippets='curl-request']", this.options);
		assertThat(extractStrings(output)).containsExactly("Curl request", "$ curl 'http://localhost:8080/' -i", "1");
	}

	@Test
	public void tableSnippetInclude() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets='response-fields']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-table"));
	}

	@Test
	public void tableSnippetIncludeWithPdfBackend() throws Exception {
		File output = configurePdfOutput();
		this.asciidoctor.convert("operation::some-operation[snippets='response-fields']", this.options);
		assertThat(extractStrings(output)).containsExactly("Response fields", "Path", "Type", "Description", "a",
				"Object", "one", "a.b", "Number", "two", "a.c", "String", "three", "1");
	}

	@Test
	public void includeSnippetInSection() throws Exception {
		String result = this.asciidoctor.convert("= A\n:doctype: book\n:sectnums:\n\nAlpha\n\n== B\n\nBravo\n\n"
				+ "operation::some-operation[snippets='curl-request']\n\n== C\n", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-in-section"));
	}

	@Test
	public void includeSnippetInSectionWithAbsoluteLevelOffset() throws Exception {
		String result = this.asciidoctor
				.convert("= A\n:doctype: book\n:sectnums:\n:leveloffset: 1\n\nAlpha\n\n= B\n\nBravo\n\n"
						+ "operation::some-operation[snippets='curl-request']\n\n= C\n", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-in-section"));
	}

	@Test
	public void includeSnippetInSectionWithRelativeLevelOffset() throws Exception {
		String result = this.asciidoctor
				.convert("= A\n:doctype: book\n:sectnums:\n:leveloffset: +1\n\nAlpha\n\n= B\n\nBravo\n\n"
						+ "operation::some-operation[snippets='curl-request']\n\n= C\n", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("snippet-in-section"));
	}

	@Test
	public void includeSnippetInSectionWithPdfBackend() throws Exception {
		File output = configurePdfOutput();
		this.asciidoctor.convert("== Section\n" + "operation::some-operation[snippets='curl-request']", this.options);
		assertThat(extractStrings(output)).containsExactly("Section", "Curl request",
				"$ curl 'http://localhost:8080/' -i", "1");
	}

	@Test
	public void includeMultipleSnippets() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets='curl-request,http-request']",
				this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("multiple-snippets"));
	}

	@Test
	public void useMacroWithoutSnippetAttributeAddsAllSnippets() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[]", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("all-snippets"));
	}

	@Test
	public void useMacroWithEmptySnippetAttributeAddsAllSnippets() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets=]", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("all-snippets"));
	}

	@Test
	public void includingMissingSnippetAddsWarning() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets='missing-snippet']", this.options);
		assertThat(result).startsWith(getExpectedContentFromFile("missing-snippet"));
		assertThat(CapturingLogHandler.getLogRecords()).hasSize(1);
		assertThat(CapturingLogHandler.getLogRecords().get(0).getMessage())
				.contains("Snippet missing-snippet not found");
		assertThat(CapturingLogHandler.getLogRecords().get(0).getCursor().getLineNumber()).isEqualTo(1);
		CapturingLogHandler.getLogRecords().clear();
	}

	@Test
	public void defaultTitleIsProvidedForCustomSnippet() throws Exception {
		String result = this.asciidoctor.convert("operation::some-operation[snippets='custom-snippet']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("custom-snippet-default-title"));
	}

	@Test
	public void missingOperationIsHandledGracefully() throws Exception {
		String result = this.asciidoctor.convert("operation::missing-operation[]", this.options);
		assertThat(result).startsWith(getExpectedContentFromFile("missing-operation"));
		assertThat(CapturingLogHandler.getLogRecords()).hasSize(1);
		assertThat(CapturingLogHandler.getLogRecords().get(0).getMessage())
				.contains("No snippets were found for operation missing-operation");
		assertThat(CapturingLogHandler.getLogRecords().get(0).getCursor().getLineNumber()).isEqualTo(1);
		CapturingLogHandler.getLogRecords().clear();
	}

	@Test
	public void titleOfBuiltInSnippetCanBeCustomizedUsingDocumentAttribute() throws URISyntaxException, IOException {
		String result = this.asciidoctor.convert(":operation-curl-request-title: Example request\n"
				+ "operation::some-operation[snippets='curl-request']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("built-in-snippet-custom-title"));
	}

	@Test
	public void titleOfCustomSnippetCanBeCustomizedUsingDocumentAttribute() throws Exception {
		String result = this.asciidoctor.convert(":operation-custom-snippet-title: Customized title\n"
				+ "operation::some-operation[snippets='custom-snippet']", this.options);
		assertThat(result).isEqualTo(getExpectedContentFromFile("custom-snippet-custom-title"));
	}

	private String getExpectedContentFromFile(String fileName) throws URISyntaxException, IOException {
		Path filePath = Paths.get(this.getClass().getResource("/operations/" + fileName + ".html").toURI());
		String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
		if (isWindows()) {
			return content.replace("\r\n", "\n");
		}
		return content;
	}

	private boolean isWindows() {
		return File.separatorChar == '\\';
	}

	protected abstract Attributes getAttributes();

	protected abstract File getBuildOutputLocation();

	protected abstract File getSourceLocation();

	private File configurePdfOutput() {
		this.options.setBackend("pdf");
		File output = new File("build/output.pdf");
		this.options.setToFile(output.getAbsolutePath());
		return output;
	}

	private List<String> extractStrings(File pdfFile) throws IOException {
		PDDocument pdf = PDDocument.load(pdfFile);
		assertThat(pdf.getNumberOfPages()).isEqualTo(1);
		StringExtractor stringExtractor = new StringExtractor();
		stringExtractor.processPage(pdf.getPage(0));
		return stringExtractor.getStrings();
	}

	private static final class StringExtractor extends PDFStreamEngine {

		private final List<String> strings = new ArrayList<>();

		@Override
		protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
			if ("Tj".equals(operator.getName())) {
				for (COSBase operand : operands) {
					if (operand instanceof COSString) {
						this.strings.add((((COSString) operand).getASCII()));
					}
				}
			}
		}

		private List<String> getStrings() {
			return this.strings;
		}

	}

}
