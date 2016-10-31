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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.ModelCreationException;
import org.springframework.restdocs.snippet.TemplatedSnippet;

/**
 * Abstract {@link TemplatedSnippet} subclass that provides a base for snippets that
 * document a RESTful resource's request or response body.
 *
 * @author Andy Wilkinson
 */
public abstract class AbstractBodySnippet extends TemplatedSnippet {

	private final PayloadSubsectionExtractor<?> subsectionExtractor;

	/**
	 * Creates a new {@code AbstractBodySnippet} that will produce a snippet named
	 * {@code <type>-body} using a template named {@code <type>-body}. The snippet will
	 * contain the subsection of the body extracted by the given
	 * {@code subsectionExtractor}. The given {@code attributes} will be included in the
	 * model during template rendering
	 *
	 * @param type the type of the body
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 */
	protected AbstractBodySnippet(String type,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		this(type, type, subsectionExtractor, attributes);
	}

	/**
	 * Creates a new {@code AbstractBodySnippet} that will produce a snippet named
	 * {@code <name>-body} using a template named {@code <type>-body}. The snippet will
	 * contain the subsection of the body extracted by the given
	 * {@code subsectionExtractor}. The given {@code attributes} will be included in the
	 * model during template rendering
	 *
	 * @param name the name of the snippet
	 * @param type the type of the body
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 */
	protected AbstractBodySnippet(String name, String type,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		super(name + "-body"
				+ (subsectionExtractor != null
						? "-" + subsectionExtractor.getSubsectionId() : ""),
				type + "-body", attributes);
		this.subsectionExtractor = subsectionExtractor;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		try {
			MediaType contentType = getContentType(operation);
			byte[] content = getContent(operation);
			if (this.subsectionExtractor != null) {
				content = this.subsectionExtractor.extractSubsection(content,
						contentType);
			}
			Charset charset = extractCharset(contentType);
			String body = charset != null ? new String(content, charset)
					: new String(content);
			Map<String, Object> model = new HashMap<>();
			model.put("body", body);
			return model;
		}
		catch (IOException ex) {
			throw new ModelCreationException(ex);
		}
	}

	private Charset extractCharset(MediaType contentType) {
		if (contentType == null) {
			return null;
		}
		return contentType.getCharset();
	}

	/**
	 * Returns the content of the request or response extracted from the given
	 * {@code operation}.
	 *
	 * @param operation The operation
	 * @return The content
	 * @throws IOException if the content cannot be extracted
	 */
	protected abstract byte[] getContent(Operation operation) throws IOException;

	/**
	 * Returns the content type of the request or response extracted from the given
	 * {@code operation}.
	 *
	 * @param operation The operation
	 * @return The content type
	 */
	protected abstract MediaType getContentType(Operation operation);

}
