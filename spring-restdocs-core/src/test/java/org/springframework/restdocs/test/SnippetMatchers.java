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

	public static SnippetMatcher snippet() {
		return new SnippetMatcher();
	}

	public static AsciidoctorTableMatcher tableWithTitleAndHeader(String title,
			String... headers) {
		return new AsciidoctorTableMatcher(title, headers);
	}

	public static AsciidoctorTableMatcher tableWithHeader(String... headers) {
		return new AsciidoctorTableMatcher(null, headers);
	}

	public static HttpRequestMatcher httpRequest(RequestMethod method, String uri) {
		return new HttpRequestMatcher(method, uri);
	}

	public static HttpResponseMatcher httpResponse(HttpStatus status) {
		return new HttpResponseMatcher(status);
	}

	@SuppressWarnings({ "rawtypes" })
	public static AsciidoctorCodeBlockMatcher<?> codeBlock(String language) {
		return new AsciidoctorCodeBlockMatcher(language);
	}

	private static abstract class AbstractSnippetContentMatcher extends
			BaseMatcher<String> {

		private List<String> lines = new ArrayList<String>();

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
			description.appendText("Asciidoctor snippet");
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
	 * A {@link Matcher} for an Asciidoctor code block.
	 *
	 * @param <T> The type of the matcher
	 */
	public static class AsciidoctorCodeBlockMatcher<T extends AsciidoctorCodeBlockMatcher<T>>
			extends AbstractSnippetContentMatcher {

		protected AsciidoctorCodeBlockMatcher(String language) {
			this.addLine("[source," + language + "]");
			this.addLine("----");
			this.addLine("----");
		}

		@SuppressWarnings("unchecked")
		public T content(String content) {
			this.addLine(-1, content);
			return (T) this;
		}

	}

	/**
	 * A {@link Matcher} for an HTTP request or response.
	 *
	 * @param <T> The type of the matcher
	 */
	public static abstract class HttpMatcher<T extends HttpMatcher<T>> extends
			AsciidoctorCodeBlockMatcher<HttpMatcher<T>> {

		private int headerOffset = 3;

		protected HttpMatcher() {
			super("http");
		}

		@SuppressWarnings("unchecked")
		public T header(String name, String value) {
			this.addLine(this.headerOffset++, name + ": " + value);
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T header(String name, long value) {
			this.addLine(this.headerOffset++, name + ": " + value);
			return (T) this;
		}

	}

	/**
	 * A {@link Matcher} for an HTTP response.
	 */
	public static final class HttpResponseMatcher extends
			HttpMatcher<HttpResponseMatcher> {

		private HttpResponseMatcher(HttpStatus status) {
			this.content("HTTP/1.1 " + status.value() + " " + status.getReasonPhrase());
			this.content("");
		}

	}

	/**
	 * A {@link Matcher} for an HTTP request.
	 */
	public static final class HttpRequestMatcher extends HttpMatcher<HttpRequestMatcher> {

		private HttpRequestMatcher(RequestMethod requestMethod, String uri) {
			this.content(requestMethod.name() + " " + uri + " HTTP/1.1");
			this.content("");
		}

	}

	/**
	 * A {@link Matcher} for an Asciidoctor table.
	 */
	public static final class AsciidoctorTableMatcher extends
			AbstractSnippetContentMatcher {

		private AsciidoctorTableMatcher(String title, String... columns) {
			if (StringUtils.hasText(title)) {
				this.addLine("." + title);
			}
			this.addLine("|===");
			String header = "|"
					+ StringUtils
							.collectionToDelimitedString(Arrays.asList(columns), "|");
			this.addLine(header);
			this.addLine("");
			this.addLine("|===");
		}

		public AsciidoctorTableMatcher row(String... entries) {
			for (String entry : entries) {
				this.addLine(-1, "|" + entry);
			}
			this.addLine(-1, "");
			return this;
		}

		public AsciidoctorTableMatcher configuration(String configuration) {
			this.addLine(0, configuration);
			return this;
		}
	}

	/**
	 * A {@link Matcher} for a snippet file.
	 */
	public static class SnippetMatcher extends BaseMatcher<File> {

		private Matcher<String> expectedContents;

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
			return FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(
					snippetFile), "UTF-8"));
		}

		@Override
		public void describeMismatch(Object item, Description description) {
			if (!snippetFileExists(item)) {
				description.appendText("The file " + item + " does not exist");
			}
			else if (this.expectedContents != null) {
				try {
					this.expectedContents
							.describeMismatch(read((File) item), description);
				}
				catch (IOException e) {
					description.appendText("The contents of " + item
							+ " cound not be read");
				}
			}
		}

		@Override
		public void describeTo(Description description) {
			if (this.expectedContents != null) {
				this.expectedContents.describeTo(description);
			}
			else {
				description.appendText("Asciidoctor snippet");
			}
		}

		public SnippetMatcher withContents(Matcher<String> matcher) {
			this.expectedContents = matcher;
			return this;
		}

	}
}
