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

package org.springframework.restdocs.mockmvc;

import java.util.Arrays;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.curl.CurlDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.WriterResolver;

/**
 * A configurer that can be used to configure the generated documentation snippets.
 *
 * @author Andy Wilkinson
 */
public class SnippetConfigurer extends
		AbstractNestedConfigurer<RestDocumentationMockMvcConfigurer> {

	private List<Snippet> defaultSnippets = Arrays.asList(
			CurlDocumentation.curlRequest(), HttpDocumentation.httpRequest(),
			HttpDocumentation.httpResponse());

	/**
	 * The default encoding for documentation snippets.
	 *
	 * @see #withEncoding(String)
	 */
	public static final String DEFAULT_SNIPPET_ENCODING = "UTF-8";

	private String snippetEncoding = DEFAULT_SNIPPET_ENCODING;

	SnippetConfigurer(RestDocumentationMockMvcConfigurer parent) {
		super(parent);
	}

	/**
	 * Configures any documentation snippets to be written using the given
	 * {@code encoding}. The default is UTF-8.
	 *
	 * @param encoding the encoding
	 * @return {@code this}
	 */
	public SnippetConfigurer withEncoding(String encoding) {
		this.snippetEncoding = encoding;
		return this;
	}

	@Override
	void apply(MockHttpServletRequest request) {
		((WriterResolver) request.getAttribute(WriterResolver.class.getName()))
				.setEncoding(this.snippetEncoding);
		request.setAttribute("org.springframework.restdocs.mockmvc.defaultSnippets",
				this.defaultSnippets);
	}

	/**
	 * Configures the documentation snippets that will be produced by default.
	 *
	 * @param defaultSnippets the default snippets
	 * @return {@code this}
	 */
	public SnippetConfigurer withDefaults(Snippet... defaultSnippets) {
		this.defaultSnippets = Arrays.asList(defaultSnippets);
		return this;
	}
}
