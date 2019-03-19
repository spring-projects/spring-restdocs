/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;

import org.springframework.http.HttpStatus;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Condition Conditions} for verify the contents of generated documentation
 * snippets.
 *
 * @author Andy Wilkinson
 */
public final class SnippetConditions {

	private SnippetConditions() {

	}

	public static TableCondition<?> tableWithHeader(TemplateFormat format,
			String... headers) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorTableCondition(null, headers);
		}
		return new MarkdownTableCondition(null, headers);
	}

	public static TableCondition<?> tableWithTitleAndHeader(TemplateFormat format,
			String title, String... headers) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorTableCondition(title, headers);
		}
		return new MarkdownTableCondition(title, headers);
	}

	public static HttpRequestCondition httpRequest(TemplateFormat format,
			RequestMethod requestMethod, String uri) {
		if ("adoc".equals(format.getFileExtension())) {
			return new HttpRequestCondition(requestMethod, uri,
					new AsciidoctorCodeBlockCondition<>("http", "nowrap"), 3);
		}
		return new HttpRequestCondition(requestMethod, uri,
				new MarkdownCodeBlockCondition<>("http"), 2);
	}

	public static HttpResponseCondition httpResponse(TemplateFormat format,
			HttpStatus status) {
		if ("adoc".equals(format.getFileExtension())) {
			return new HttpResponseCondition(status,
					new AsciidoctorCodeBlockCondition<>("http", "nowrap"), 3);
		}
		return new HttpResponseCondition(status, new MarkdownCodeBlockCondition<>("http"),
				2);
	}

	@SuppressWarnings({ "rawtypes" })
	public static CodeBlockCondition<?> codeBlock(TemplateFormat format,
			String language) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorCodeBlockCondition(language, null);
		}
		return new MarkdownCodeBlockCondition(language);
	}

	@SuppressWarnings({ "rawtypes" })
	public static CodeBlockCondition<?> codeBlock(TemplateFormat format, String language,
			String options) {
		if ("adoc".equals(format.getFileExtension())) {
			return new AsciidoctorCodeBlockCondition(language, options);
		}
		return new MarkdownCodeBlockCondition(language);
	}

	private abstract static class AbstractSnippetContentCondition
			extends Condition<String> {

		private List<String> lines = new ArrayList<>();

		protected AbstractSnippetContentCondition() {
			as(new Description() {

				@Override
				public String value() {
					return getLinesAsString();
				}

			});
		}

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

		@Override
		public boolean matches(String content) {
			return getLinesAsString().equals(content);
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
	 * Base class for code block Conditions.
	 *
	 * @param <T> The type of the Condition
	 */
	public static class CodeBlockCondition<T extends CodeBlockCondition<T>>
			extends AbstractSnippetContentCondition {

		@SuppressWarnings("unchecked")
		public T withContent(String content) {
			this.addLine(-1, content);
			return (T) this;
		}

	}

	/**
	 * A {@link Condition} for an Asciidoctor code block.
	 *
	 * @param <T> The type of the Condition
	 */
	public static class AsciidoctorCodeBlockCondition<T extends AsciidoctorCodeBlockCondition<T>>
			extends CodeBlockCondition<T> {

		protected AsciidoctorCodeBlockCondition(String language, String options) {
			this.addLine("[source" + ((language != null) ? "," + language : "")
					+ ((options != null) ? ",options=\"" + options + "\"" : "") + "]");
			this.addLine("----");
			this.addLine("----");
		}

	}

	/**
	 * A {@link Condition} for a Markdown code block.
	 *
	 * @param <T> The type of the Condition
	 */
	public static class MarkdownCodeBlockCondition<T extends MarkdownCodeBlockCondition<T>>
			extends CodeBlockCondition<T> {

		protected MarkdownCodeBlockCondition(String language) {
			this.addLine("```" + ((language != null) ? language : ""));
			this.addLine("```");
		}

	}

	/**
	 * A {@link Condition} for an HTTP request or response.
	 *
	 * @param <T> The type of the Condition
	 */
	public abstract static class HttpCondition<T extends HttpCondition<T>>
			extends Condition<String> {

		private final CodeBlockCondition<?> delegate;

		private int headerOffset;

		protected HttpCondition(CodeBlockCondition<?> delegate, int headerOffset) {
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
		public boolean matches(String item) {
			return this.delegate.matches(item);
		}

	}

	/**
	 * A {@link Condition} for an HTTP response.
	 */
	public static final class HttpResponseCondition
			extends HttpCondition<HttpResponseCondition> {

		private HttpResponseCondition(HttpStatus status, CodeBlockCondition<?> delegate,
				int headerOffset) {
			super(delegate, headerOffset);
			this.content("HTTP/1.1 " + status.value() + " " + status.getReasonPhrase());
			this.content("");
		}

	}

	/**
	 * A {@link Condition} for an HTTP request.
	 */
	public static final class HttpRequestCondition
			extends HttpCondition<HttpRequestCondition> {

		private HttpRequestCondition(RequestMethod requestMethod, String uri,
				CodeBlockCondition<?> delegate, int headerOffset) {
			super(delegate, headerOffset);
			this.content(requestMethod.name() + " " + uri + " HTTP/1.1");
			this.content("");
		}

	}

	/**
	 * Base class for table Conditions.
	 *
	 * @param <T> The concrete type of the Condition
	 */
	public abstract static class TableCondition<T extends TableCondition<T>>
			extends AbstractSnippetContentCondition {

		public abstract T row(String... entries);

		public abstract T configuration(String configuration);

	}

	/**
	 * A {@link Condition} for an Asciidoctor table.
	 */
	public static final class AsciidoctorTableCondition
			extends TableCondition<AsciidoctorTableCondition> {

		private AsciidoctorTableCondition(String title, String... columns) {
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
		public AsciidoctorTableCondition row(String... entries) {
			for (String entry : entries) {
				this.addLine(-1, "|" + escapeEntry(entry));
			}
			this.addLine(-1, "");
			return this;
		}

		private String escapeEntry(String entry) {
			if (entry.startsWith("`") && entry.endsWith("`")) {
				return "`+" + entry.substring(1, entry.length() - 1) + "+`";
			}
			return entry;
		}

		@Override
		public AsciidoctorTableCondition configuration(String configuration) {
			this.addLine(0, configuration);
			return this;
		}

	}

	/**
	 * A {@link Condition} for a Markdown table.
	 */
	public static final class MarkdownTableCondition
			extends TableCondition<MarkdownTableCondition> {

		private MarkdownTableCondition(String title, String... columns) {
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
		public MarkdownTableCondition row(String... entries) {
			this.addLine(-1, StringUtils
					.collectionToDelimitedString(Arrays.asList(entries), " | "));
			return this;
		}

		@Override
		public MarkdownTableCondition configuration(String configuration) {
			throw new UnsupportedOperationException(
					"Markdown does not support table configuration");
		}

	}

}
