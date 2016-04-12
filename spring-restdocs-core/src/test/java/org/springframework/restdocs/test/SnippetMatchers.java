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

package org.springframework.restdocs.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import org.springframework.http.HttpStatus;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Matcher Matchers} for verify the contents of generated documentation snippets.
 *
 * @author Andy Wilkinson
 */
public final class SnippetMatchers {

	private SnippetMatchers() {

	}

	public static SnippetMatcher snippet(TemplateFormat templateFormat) {
		return new SnippetMatcher(templateFormat);
	}

	public static TableMatcher<?> tableWithHeader(TemplateFormat format,
			String... headers) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorTableMatcher(null, headers);
		}
		return new MarkdownTableMatcher(null, headers);
	}

	public static TableMatcher<?> tableWithTitleAndHeader(TemplateFormat format,
			String title, String... headers) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorTableMatcher(title, headers);
		}
		return new MarkdownTableMatcher(title, headers);
	}

	public static HttpRequestMatcher httpRequest(TemplateFormat format,
			RequestMethod requestMethod, String uri) {
		if ("adoc".equals(format.getFileExtension())) {
			return new HttpRequestMatcher(requestMethod, uri,
					new AsciidoctorCodeBlockMatcher<>("http", "nowrap"), 3);
		}
		return new HttpRequestMatcher(requestMethod, uri,
				new MarkdownCodeBlockMatcher<>("http"), 2);
	}

	public static HttpResponseMatcher httpResponse(TemplateFormat format,
			HttpStatus status) {
		if ("adoc".equals(format.getFileExtension())) {
			return new HttpResponseMatcher(status,
					new AsciidoctorCodeBlockMatcher<>("http", "nowrap"), 3);
		}
		return new HttpResponseMatcher(status, new MarkdownCodeBlockMatcher<>("http"), 2);
	}

	@SuppressWarnings({ "rawtypes" })
	public static CodeBlockMatcher<?> codeBlock(TemplateFormat format, String language) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorCodeBlockMatcher(language, null);
		}
		return new MarkdownCodeBlockMatcher(language);
	}

	private static abstract class AbstractSnippetContentMatcher
			extends BaseMatcher<String> {

		private final TemplateFormat templateFormat;

		private List<String> lines = new ArrayList<>();

		protected AbstractSnippetContentMatcher(TemplateFormat templateFormat) {
			this.templateFormat = templateFormat;
		}

		protected void addLine(String line) {
			this.lines.add(line);
		}

		protected void addLine(int index, String line) {
			if (index < 0) {
				index = index + this.lines.size();
			}
			this.lines.add(index, line);
		}

		@Override
		public boolean matches(Object item) {
			return getLinesAsString().equals(item);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(this.templateFormat.getFileExtension() + " snippet");
			description.appendText(getLinesAsString());
		}

		@Override
		public void describeMismatch(Object item, Description description) {
			description.appendText("was:");
			if (item instanceof String) {
				description.appendText((String) item);
			}
			else {
				description.appendValue(item);
			}
		}

		private String getLinesAsString() {
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

	/**
	 * Base class for code block matchers.
	 *
	 * @param <T> The type of the matcher
	 */
	public static class CodeBlockMatcher<T extends CodeBlockMatcher<T>>
			extends AbstractSnippetContentMatcher {

		protected CodeBlockMatcher(TemplateFormat templateFormat) {
			super(templateFormat);
		}

		@SuppressWarnings("unchecked")
		public T content(String content) {
			this.addLine(-1, content);
			return (T) this;
		}

	}

	/**
	 * A {@link Matcher} for an Asciidoctor code block.
	 *
	 * @param <T> The type of the matcher
	 */
	public static class AsciidoctorCodeBlockMatcher<T extends AsciidoctorCodeBlockMatcher<T>>
			extends CodeBlockMatcher<T> {

		protected AsciidoctorCodeBlockMatcher(String language, String options) {
			super(TemplateFormats.asciidoctor());
			this.addLine("[source," + language
					+ (options == null ? "" : ",options=\"" + options + "\"") + "]");
			this.addLine("----");
			this.addLine("----");
		}

	}

	/**
	 * A {@link Matcher} for a Markdown code block.
	 *
	 * @param <T> The type of the matcher
	 */
	public static class MarkdownCodeBlockMatcher<T extends MarkdownCodeBlockMatcher<T>>
			extends CodeBlockMatcher<T> {

		protected MarkdownCodeBlockMatcher(String language) {
			super(TemplateFormats.markdown());
			this.addLine("```" + language);
			this.addLine("```");
		}

	}

	/**
	 * A {@link Matcher} for an HTTP request or response.
	 *
	 * @param <T> The type of the matcher
	 */
	public static abstract class HttpMatcher<T extends HttpMatcher<T>>
			extends BaseMatcher<String> {

		private final CodeBlockMatcher<?> delegate;

		private int headerOffset;

		protected HttpMatcher(CodeBlockMatcher<?> delegate, int headerOffset) {
			this.delegate = delegate;
			this.headerOffset = headerOffset;
		}

		@SuppressWarnings("unchecked")
		public T header(String name, String value) {
			this.delegate.addLine(this.headerOffset++, name + ": " + value);
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T header(String name, long value) {
			this.delegate.addLine(this.headerOffset++, name + ": " + value);
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T content(String content) {
			this.delegate.addLine(-1, content);
			return (T) this;
		}

		@Override
		public boolean matches(Object item) {
			return this.delegate.matches(item);
		}

		@Override
		public void describeTo(Description description) {
			this.delegate.describeTo(description);
		}

	}

	/**
	 * A {@link Matcher} for an HTTP response.
	 */
	public static final class HttpResponseMatcher
			extends HttpMatcher<HttpResponseMatcher> {

		private HttpResponseMatcher(HttpStatus status, CodeBlockMatcher<?> delegate,
				int headerOffset) {
			super(delegate, headerOffset);
			this.content("HTTP/1.1 " + status.value() + " " + status.getReasonPhrase());
			this.content("");
		}

	}

	/**
	 * A {@link Matcher} for an HTTP request.
	 */
	public static final class HttpRequestMatcher extends HttpMatcher<HttpRequestMatcher> {

		private HttpRequestMatcher(RequestMethod requestMethod, String uri,
				CodeBlockMatcher<?> delegate, int headerOffset) {
			super(delegate, headerOffset);
			this.content(requestMethod.name() + " " + uri + " HTTP/1.1");
			this.content("");
		}

	}

	/**
	 * Base class for table matchers.
	 *
	 * @param <T> The concrete type of the matcher
	 */
	public static abstract class TableMatcher<T extends TableMatcher<T>>
			extends AbstractSnippetContentMatcher {

		protected TableMatcher(TemplateFormat templateFormat) {
			super(templateFormat);
		}

		public abstract T row(String... entries);

		public abstract T configuration(String configuration);

	}

	/**
	 * A {@link Matcher} for an Asciidoctor table.
	 */
	public static final class AsciidoctorTableMatcher
			extends TableMatcher<AsciidoctorTableMatcher> {

		private AsciidoctorTableMatcher(String title, String... columns) {
			super(TemplateFormats.asciidoctor());
			if (StringUtils.hasText(title)) {
				this.addLine("." + title);
			}
			this.addLine("|===");
			String header = "|" + StringUtils
					.collectionToDelimitedString(Arrays.asList(columns), "|");
			this.addLine(header);
			this.addLine("");
			this.addLine("|===");
		}

		@Override
		public AsciidoctorTableMatcher row(String... entries) {
			for (String entry : entries) {
				this.addLine(-1, "|" + entry);
			}
			this.addLine(-1, "");
			return this;
		}

		@Override
		public AsciidoctorTableMatcher configuration(String configuration) {
			this.addLine(0, configuration);
			return this;
		}

	}

	/**
	 * A {@link Matcher} for a Markdown table.
	 */
	public static final class MarkdownTableMatcher
			extends TableMatcher<MarkdownTableMatcher> {

		private MarkdownTableMatcher(String title, String... columns) {
			super(TemplateFormats.asciidoctor());
			if (StringUtils.hasText(title)) {
				this.addLine(title);
				this.addLine("");
			}
			String header = StringUtils
					.collectionToDelimitedString(Arrays.asList(columns), " | ");
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
		}

		@Override
		public MarkdownTableMatcher row(String... entries) {
			this.addLine(-1, StringUtils
					.collectionToDelimitedString(Arrays.asList(entries), " | "));
			return this;
		}

		@Override
		public MarkdownTableMatcher configuration(String configuration) {
			throw new UnsupportedOperationException(
					"Markdown does not support table configuration");
		}

	}

	/**
	 * A {@link Matcher} for a snippet file.
	 */
	public static final class SnippetMatcher extends BaseMatcher<File> {

		private final TemplateFormat templateFormat;

		private Matcher<String> expectedContents;

		private SnippetMatcher(TemplateFormat templateFormat) {
			this.templateFormat = templateFormat;
		}

		@Override
		public boolean matches(Object item) {
			if (snippetFileExists(item)) {
				if (this.expectedContents != null) {
					try {
						return this.expectedContents.matches(read((File) item));
					}
					catch (IOException e) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		private boolean snippetFileExists(Object item) {
			return item instanceof File && ((File) item).isFile();
		}

		private String read(File snippetFile) throws IOException {
			return FileCopyUtils.copyToString(
					new InputStreamReader(new FileInputStream(snippetFile), "UTF-8"));
		}

		@Override
		public void describeMismatch(Object item, Description description) {
			if (!snippetFileExists(item)) {
				description.appendText("The file " + item + " does not exist");
			}
			else if (this.expectedContents != null) {
				try {
					this.expectedContents.describeMismatch(read((File) item),
							description);
				}
				catch (IOException e) {
					description
							.appendText("The contents of " + item + " cound not be read");
				}
			}
		}

		@Override
		public void describeTo(Description description) {
			if (this.expectedContents != null) {
				this.expectedContents.describeTo(description);
			}
			else {
				description
						.appendText(this.templateFormat.getFileExtension() + " snippet");
			}
		}

		public SnippetMatcher withContents(Matcher<String> matcher) {
			this.expectedContents = matcher;
			return this;
		}

	}

}
