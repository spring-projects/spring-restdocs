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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Matcher Matchers} for verify the contents of generated documentation snippets.
 * 
 * @author Andy Wilkinson
 */
public class SnippetMatchers {

	public static AsciidoctorTableMatcher tableWithHeader(String... headers) {
		return new AsciidoctorTableMatcher(headers);
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

	private static abstract class AbstractSnippetMatcher extends BaseMatcher<String> {

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
			for (String line : this.lines) {
				writer.append(String.format("%s%n", line));
			}
			return writer.toString();
		}
	}

	public static class AsciidoctorCodeBlockMatcher<T extends AsciidoctorCodeBlockMatcher<T>>
			extends AbstractSnippetMatcher {

		protected AsciidoctorCodeBlockMatcher(String language) {
			this.addLine("");
			this.addLine("[source," + language + "]");
			this.addLine("----");
			this.addLine("----");
			this.addLine("");
		}

		@SuppressWarnings("unchecked")
		public T content(String content) {
			this.addLine(-2, content);
			return (T) this;
		}

	}

	public static abstract class HttpMatcher<T extends HttpMatcher<T>> extends
			AsciidoctorCodeBlockMatcher<HttpMatcher<T>> {

		private int headerOffset = 4;

		protected HttpMatcher() {
			super("http");
		}

		@SuppressWarnings("unchecked")
		public T header(String name, String value) {
			this.addLine(this.headerOffset++, name + ": " + value);
			return (T) this;
		}

	}

	public static class HttpResponseMatcher extends HttpMatcher<HttpResponseMatcher> {

		public HttpResponseMatcher(HttpStatus status) {
			this.content("HTTP/1.1 " + status.value() + " " + status.getReasonPhrase());
			this.content("");
		}

	}

	public static class HttpRequestMatcher extends HttpMatcher<HttpRequestMatcher> {

		public HttpRequestMatcher(RequestMethod requestMethod, String uri) {
			this.content(requestMethod.name() + " " + uri + " HTTP/1.1");
			this.content("");
		}

	}

	public static class AsciidoctorTableMatcher extends AbstractSnippetMatcher {

		private AsciidoctorTableMatcher(String... columns) {
			this.addLine("");
			this.addLine("|===");
			String header = "|"
					+ StringUtils
							.collectionToDelimitedString(Arrays.asList(columns), "|");
			this.addLine(header);
			this.addLine("");
			this.addLine("|===");
			this.addLine("");
		}

		public AsciidoctorTableMatcher row(String... entries) {
			for (String entry : entries) {
				this.addLine(-2, "|" + entry);
			}
			this.addLine(-2, "");
			return this;
		}
	}
}
