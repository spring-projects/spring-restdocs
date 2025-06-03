/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.testfixtures.jupiter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;

import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.util.StringUtils;

/**
 * AssertJ {@link AssertProvider} for asserting that the generated snippets are correct.
 *
 * @author Andy Wilkinson
 */
public class AssertableSnippets {

	private final File outputDirectory;

	private final String operationName;

	private final TemplateFormat templateFormat;

	AssertableSnippets(File outputDirectory, String operationName, TemplateFormat templateFormat) {
		this.outputDirectory = outputDirectory;
		this.operationName = operationName;
		this.templateFormat = templateFormat;
	}

	public File named(String name) {
		return getSnippetFile(name);
	}

	private File getSnippetFile(String name) {
		File snippetDir = new File(this.outputDirectory, this.operationName);
		return new File(snippetDir, name + "." + this.templateFormat.getFileExtension());
	}

	public CodeBlockSnippetAssertProvider curlRequest() {
		return new CodeBlockSnippetAssertProvider("curl-request");
	}

	public TableSnippetAssertProvider formParameters() {
		return new TableSnippetAssertProvider("form-parameters");
	}

	public CodeBlockSnippetAssertProvider httpieRequest() {
		return new CodeBlockSnippetAssertProvider("httpie-request");
	}

	public HttpRequestSnippetAssertProvider httpRequest() {
		return new HttpRequestSnippetAssertProvider("http-request");
	}

	public HttpResponseSnippetAssertProvider httpResponse() {
		return new HttpResponseSnippetAssertProvider("http-response");
	}

	public TableSnippetAssertProvider links() {
		return new TableSnippetAssertProvider("links");
	}

	public TableSnippetAssertProvider pathParameters() {
		return new TableSnippetAssertProvider("path-parameters");
	}

	public TableSnippetAssertProvider queryParameters() {
		return new TableSnippetAssertProvider("query-parameters");
	}

	public CodeBlockSnippetAssertProvider requestBody() {
		return new CodeBlockSnippetAssertProvider("request-body");
	}

	public CodeBlockSnippetAssertProvider requestBody(String suffix) {
		return new CodeBlockSnippetAssertProvider("request-body-%s".formatted(suffix));
	}

	public TableSnippetAssertProvider requestCookies() {
		return new TableSnippetAssertProvider("request-cookies");
	}

	public TableSnippetAssertProvider requestCookies(String suffix) {
		return new TableSnippetAssertProvider("request-cookies-%s".formatted(suffix));
	}

	public TableSnippetAssertProvider requestFields() {
		return new TableSnippetAssertProvider("request-fields");
	}

	public TableSnippetAssertProvider requestFields(String suffix) {
		return new TableSnippetAssertProvider("request-fields-%s".formatted(suffix));
	}

	public TableSnippetAssertProvider requestHeaders() {
		return new TableSnippetAssertProvider("request-headers");
	}

	public TableSnippetAssertProvider requestHeaders(String suffix) {
		return new TableSnippetAssertProvider("request-headers-%s".formatted(suffix));
	}

	public CodeBlockSnippetAssertProvider requestPartBody(String partName) {
		return new CodeBlockSnippetAssertProvider("request-part-%s-body".formatted(partName));
	}

	public CodeBlockSnippetAssertProvider requestPartBody(String partName, String suffix) {
		return new CodeBlockSnippetAssertProvider("request-part-%s-body-%s".formatted(partName, suffix));
	}

	public TableSnippetAssertProvider requestPartFields(String partName) {
		return new TableSnippetAssertProvider("request-part-%s-fields".formatted(partName));
	}

	public TableSnippetAssertProvider requestPartFields(String partName, String suffix) {
		return new TableSnippetAssertProvider("request-part-%s-fields-%s".formatted(partName, suffix));
	}

	public TableSnippetAssertProvider requestParts() {
		return new TableSnippetAssertProvider("request-parts");
	}

	public CodeBlockSnippetAssertProvider responseBody() {
		return new CodeBlockSnippetAssertProvider("response-body");
	}

	public CodeBlockSnippetAssertProvider responseBody(String suffix) {
		return new CodeBlockSnippetAssertProvider("response-body-%s".formatted(suffix));
	}

	public TableSnippetAssertProvider responseCookies() {
		return new TableSnippetAssertProvider("response-cookies");
	}

	public TableSnippetAssertProvider responseFields() {
		return new TableSnippetAssertProvider("response-fields");
	}

	public TableSnippetAssertProvider responseFields(String suffix) {
		return new TableSnippetAssertProvider("response-fields-%s".formatted(suffix));
	}

	public TableSnippetAssertProvider responseHeaders() {
		return new TableSnippetAssertProvider("response-headers");
	}

	public final class TableSnippetAssertProvider implements AssertProvider<TableSnippetAssert> {

		private final String snippetName;

		private TableSnippetAssertProvider(String snippetName) {
			this.snippetName = snippetName;
		}

		@Override
		public TableSnippetAssert assertThat() {
			try {
				String content = Files
					.readString(new File(AssertableSnippets.this.outputDirectory, AssertableSnippets.this.operationName
							+ "/" + this.snippetName + "." + AssertableSnippets.this.templateFormat.getFileExtension())
						.toPath());
				return new TableSnippetAssert(content);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}

	}

	public final class TableSnippetAssert extends AbstractStringAssert<TableSnippetAssert> {

		private TableSnippetAssert(String actual) {
			super(actual, TableSnippetAssert.class);
		}

		public void isTable(UnaryOperator<Table<?>> tableOperator) {
			Table<?> table = tableOperator
				.apply(AssertableSnippets.this.templateFormat.equals(TemplateFormats.asciidoctor())
						? new AsciidoctorTable() : new MarkdownTable());
			table.getLinesAsString();
			Assertions.assertThat(this.actual).isEqualTo(table.getLinesAsString());
		}

	}

	public abstract class Table<T extends Table<T>> extends SnippetContent {

		public abstract T withHeader(String... columns);

		public abstract T withTitleAndHeader(String title, String... columns);

		public abstract T row(String... entries);

		public abstract T configuration(String string);

	}

	private final class AsciidoctorTable extends Table<AsciidoctorTable> {

		@Override
		public AsciidoctorTable withHeader(String... columns) {
			return withTitleAndHeader("", columns);
		}

		@Override
		public AsciidoctorTable withTitleAndHeader(String title, String... columns) {
			if (!title.isBlank()) {
				this.addLine("." + title);
			}
			this.addLine("|===");
			String header = "|" + StringUtils.collectionToDelimitedString(Arrays.asList(columns), "|");
			this.addLine(header);
			this.addLine("");
			this.addLine("|===");
			return this;
		}

		@Override
		public AsciidoctorTable row(String... entries) {
			for (String entry : entries) {
				this.addLine(-1, "|" + escapeEntry(entry));
			}
			this.addLine(-1, "");
			return this;
		}

		private String escapeEntry(String entry) {
			entry = entry.replace("|", "\\|");
			if (entry.startsWith("`") && entry.endsWith("`")) {
				return "`+" + entry.substring(1, entry.length() - 1) + "+`";
			}
			return entry;
		}

		@Override
		public AsciidoctorTable configuration(String configuration) {
			this.addLine(0, configuration);
			return this;
		}

	}

	private final class MarkdownTable extends Table<MarkdownTable> {

		@Override
		public MarkdownTable withHeader(String... columns) {
			return withTitleAndHeader("", columns);
		}

		@Override
		public MarkdownTable withTitleAndHeader(String title, String... columns) {
			if (StringUtils.hasText(title)) {
				this.addLine(title);
				this.addLine("");
			}
			String header = StringUtils.collectionToDelimitedString(Arrays.asList(columns), " | ");
			this.addLine(header);
			List<String> components = new ArrayList<>();
			for (String column : columns) {
				StringBuilder dashes = new StringBuilder();
				for (int i = 0; i < column.length(); i++) {
					dashes.append("-");
				}
				components.add(dashes.toString());
			}
			this.addLine(StringUtils.collectionToDelimitedString(components, " | "));
			this.addLine("");
			return this;
		}

		@Override
		public MarkdownTable row(String... entries) {
			this.addLine(-1, StringUtils.collectionToDelimitedString(Arrays.asList(entries), " | "));
			return this;
		}

		@Override
		public MarkdownTable configuration(String configuration) {
			throw new UnsupportedOperationException("Markdown tables do not support configuration");
		}

	}

	public final class CodeBlockSnippetAssertProvider implements AssertProvider<CodeBlockSnippetAssert> {

		private final String snippetName;

		private CodeBlockSnippetAssertProvider(String snippetName) {
			this.snippetName = snippetName;
		}

		@Override
		public CodeBlockSnippetAssert assertThat() {
			try {
				String content = Files
					.readString(new File(AssertableSnippets.this.outputDirectory, AssertableSnippets.this.operationName
							+ "/" + this.snippetName + "." + AssertableSnippets.this.templateFormat.getFileExtension())
						.toPath());
				return new CodeBlockSnippetAssert(content);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}

	}

	public final class CodeBlockSnippetAssert extends AbstractStringAssert<CodeBlockSnippetAssert> {

		private CodeBlockSnippetAssert(String actual) {
			super(actual, CodeBlockSnippetAssert.class);
		}

		public void isCodeBlock(UnaryOperator<CodeBlock<?>> codeBlockOperator) {
			CodeBlock<?> codeBlock = codeBlockOperator
				.apply(AssertableSnippets.this.templateFormat.equals(TemplateFormats.asciidoctor())
						? new AsciidoctorCodeBlock() : new MarkdownCodeBlock());
			Assertions.assertThat(this.actual).isEqualTo(codeBlock.getLinesAsString());
		}

	}

	public abstract class CodeBlock<T extends CodeBlock<T>> extends SnippetContent {

		public abstract T withLanguage(String language);

		public abstract T withOptions(String options);

		public abstract T withLanguageAndOptions(String language, String options);

		public abstract T content(String string);

	}

	private final class AsciidoctorCodeBlock extends CodeBlock<AsciidoctorCodeBlock> {

		@Override
		public AsciidoctorCodeBlock withLanguage(String language) {
			addLine("[source,%s]".formatted(language));
			return this;
		}

		@Override
		public AsciidoctorCodeBlock withOptions(String options) {
			addLine("[source,options=\"%s\"]".formatted(options));
			return this;
		}

		@Override
		public AsciidoctorCodeBlock withLanguageAndOptions(String language, String options) {
			addLine("[source,%s,options=\"%s\"]".formatted(language, options));
			return this;
		}

		@Override
		public AsciidoctorCodeBlock content(String content) {
			addLine("----");
			addLine(content);
			addLine("----");
			return this;
		}

	}

	private final class MarkdownCodeBlock extends CodeBlock<MarkdownCodeBlock> {

		@Override
		public MarkdownCodeBlock withLanguage(String language) {
			addLine("```%s".formatted(language));
			return this;
		}

		@Override
		public MarkdownCodeBlock withOptions(String options) {
			addLine("```");
			return this;
		}

		@Override
		public MarkdownCodeBlock withLanguageAndOptions(String language, String options) {
			addLine("```%s".formatted(language));
			return this;
		}

		@Override
		public MarkdownCodeBlock content(String content) {
			addLine(content);
			addLine("```");
			return this;
		}

	}

	public final class HttpRequestSnippetAssertProvider implements AssertProvider<HttpRequestSnippetAssert> {

		private final String snippetName;

		private HttpRequestSnippetAssertProvider(String snippetName) {
			this.snippetName = snippetName;
		}

		@Override
		public HttpRequestSnippetAssert assertThat() {
			try {
				String content = Files
					.readString(new File(AssertableSnippets.this.outputDirectory, AssertableSnippets.this.operationName
							+ "/" + this.snippetName + "." + AssertableSnippets.this.templateFormat.getFileExtension())
						.toPath());
				return new HttpRequestSnippetAssert(content);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}

	}

	public final class HttpRequestSnippetAssert extends AbstractStringAssert<HttpRequestSnippetAssert> {

		private HttpRequestSnippetAssert(String actual) {
			super(actual, HttpRequestSnippetAssert.class);
		}

		public void isHttpRequest(UnaryOperator<HttpRequest<?>> operator) {
			HttpRequest<?> codeBlock = operator
				.apply(AssertableSnippets.this.templateFormat.equals(TemplateFormats.asciidoctor())
						? new AsciidoctorHttpRequest() : new MarkdownHttpRequest());
			Assertions.assertThat(this.actual).isEqualTo(codeBlock.getLinesAsString());
		}

	}

	public abstract class HttpRequest<T extends HttpRequest<T>> extends SnippetContent {

		public T get(String uri) {
			return request("GET", uri);
		}

		public T post(String uri) {
			return request("POST", uri);
		}

		public T put(String uri) {
			return request("PUT", uri);
		}

		public T patch(String uri) {
			return request("PATCH", uri);
		}

		public T delete(String uri) {
			return request("DELETE", uri);
		}

		protected abstract T request(String method, String uri);

		public abstract T header(String name, Object value);

		@SuppressWarnings("unchecked")
		public T content(String content) {
			addLine(-1, content);
			return (T) this;
		}

	}

	private final class AsciidoctorHttpRequest extends HttpRequest<AsciidoctorHttpRequest> {

		private int headerOffset = 3;

		@Override
		protected AsciidoctorHttpRequest request(String method, String uri) {
			addLine("[source,http,options=\"nowrap\"]");
			addLine("----");
			addLine("%s %s HTTP/1.1".formatted(method, uri));
			addLine("");
			addLine("----");
			return this;
		}

		@Override
		public AsciidoctorHttpRequest header(String name, Object value) {
			addLine(this.headerOffset++, "%s: %s".formatted(name, value));
			return this;
		}

	}

	private final class MarkdownHttpRequest extends HttpRequest<MarkdownHttpRequest> {

		private int headerOffset = 2;

		@Override
		public MarkdownHttpRequest request(String method, String uri) {
			addLine("```http");
			addLine("%s %s HTTP/1.1".formatted(method, uri));
			addLine("");
			addLine("```");
			return this;
		}

		@Override
		public MarkdownHttpRequest header(String name, Object value) {
			addLine(this.headerOffset++, "%s: %s".formatted(name, value));
			return this;
		}

	}

	public final class HttpResponseSnippetAssertProvider implements AssertProvider<HttpResponseSnippetAssert> {

		private final String snippetName;

		private HttpResponseSnippetAssertProvider(String snippetName) {
			this.snippetName = snippetName;
		}

		@Override
		public HttpResponseSnippetAssert assertThat() {
			try {
				String content = Files
					.readString(new File(AssertableSnippets.this.outputDirectory, AssertableSnippets.this.operationName
							+ "/" + this.snippetName + "." + AssertableSnippets.this.templateFormat.getFileExtension())
						.toPath());
				return new HttpResponseSnippetAssert(content);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}

	}

	public final class HttpResponseSnippetAssert extends AbstractStringAssert<HttpResponseSnippetAssert> {

		private HttpResponseSnippetAssert(String actual) {
			super(actual, HttpResponseSnippetAssert.class);
		}

		public void isHttpResponse(UnaryOperator<HttpResponse<?>> operator) {
			HttpResponse<?> httpResponse = operator
				.apply(AssertableSnippets.this.templateFormat.equals(TemplateFormats.asciidoctor())
						? new AsciidoctorHttpResponse() : new MarkdownHttpResponse());
			Assertions.assertThat(this.actual).isEqualTo(httpResponse.getLinesAsString());
		}

	}

	public abstract class HttpResponse<T extends HttpResponse<T>> extends SnippetContent {

		public T ok() {
			return status("200 OK");
		}

		public T badRequest() {
			return status("400 Bad Request");
		}

		public T status(int status) {
			return status("%d ".formatted(status));
		}

		protected abstract T status(String status);

		public abstract T header(String name, Object value);

		@SuppressWarnings("unchecked")
		public T content(String content) {
			addLine(-1, content);
			return (T) this;
		}

	}

	private final class AsciidoctorHttpResponse extends HttpResponse<AsciidoctorHttpResponse> {

		private int headerOffset = 3;

		@Override
		protected AsciidoctorHttpResponse status(String status) {
			addLine("[source,http,options=\"nowrap\"]");
			addLine("----");
			addLine("HTTP/1.1 %s".formatted(status));
			addLine("");
			addLine("----");
			return this;
		}

		@Override
		public AsciidoctorHttpResponse header(String name, Object value) {
			addLine(this.headerOffset++, "%s: %s".formatted(name, value));
			return this;
		}

	}

	private final class MarkdownHttpResponse extends HttpResponse<MarkdownHttpResponse> {

		private int headerOffset = 2;

		@Override
		public MarkdownHttpResponse status(String status) {
			addLine("```http");
			addLine("HTTP/1.1 %s".formatted(status));
			addLine("");
			addLine("```");
			return this;
		}

		@Override
		public MarkdownHttpResponse header(String name, Object value) {
			addLine(this.headerOffset++, "%s: %s".formatted(name, value));
			return this;
		}

	}

	private static class SnippetContent {

		private List<String> lines = new ArrayList<>();

		protected void addLine(String line) {
			this.lines.add(line);
		}

		protected void addLine(int index, String line) {
			this.lines.add(determineIndex(index), line);
		}

		private int determineIndex(int index) {
			if (index >= 0) {
				return index;
			}
			return index + this.lines.size();
		}

		protected String getLinesAsString() {
			StringWriter writer = new StringWriter();
			Iterator<String> iterator = this.lines.iterator();
			while (iterator.hasNext()) {
				writer.append(String.format("%s", iterator.next()));
				if (iterator.hasNext()) {
					writer.append(String.format("%n"));
				}
			}
			return writer.toString();
		}

	}

}
