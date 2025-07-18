/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.templates.Template;
import org.springframework.restdocs.templates.TemplateEngine;

/**
 * Base class for a {@link Snippet} that is produced using a {@link Template} and
 * {@link TemplateEngine}.
 *
 * @author Andy Wilkinson
 */
public abstract class TemplatedSnippet implements Snippet {

	private final Map<String, Object> attributes = new HashMap<>();

	private final String snippetName;

	private final String templateName;

	/**
	 * Creates a new {@code TemplatedSnippet} that will produce a snippet with the given
	 * {@code snippetName}. The {@code snippetName} will also be used as the name of the
	 * template. The given {@code attributes} will be included in the model during
	 * rendering of the template.
	 * @param snippetName the name of the snippet
	 * @param attributes the additional attributes
	 * @see #TemplatedSnippet(String, String, Map)
	 */
	protected TemplatedSnippet(String snippetName, @Nullable Map<String, Object> attributes) {
		this(snippetName, snippetName, attributes);
	}

	/**
	 * Creates a new {@code TemplatedSnippet} that will produce a snippet with the given
	 * {@code snippetName} using a template with the given {@code templateName}. The given
	 * {@code attributes} will be included in the model during rendering of the template.
	 * @param snippetName the name of the snippet
	 * @param templateName the name of the template
	 * @param attributes the additional attributes
	 */
	protected TemplatedSnippet(String snippetName, String templateName, @Nullable Map<String, Object> attributes) {
		this.templateName = templateName;
		this.snippetName = snippetName;
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	@Override
	public void document(Operation operation) throws IOException {
		RestDocumentationContext context = getRequiredAttribute(operation, RestDocumentationContext.class);
		WriterResolver writerResolver = getRequiredAttribute(operation, WriterResolver.class);
		Map<String, Object> model = createModel(operation);
		model.putAll(this.attributes);
		try (Writer writer = writerResolver.resolve(operation.getName(), this.snippetName, context)) {
			TemplateEngine templateEngine = getRequiredAttribute(operation, TemplateEngine.class);
			writer.append(templateEngine.compileTemplate(this.templateName).render(model));
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getRequiredAttribute(Operation operation, Class<T> type) {
		T attribute = (T) operation.getAttributes().get(type.getName());
		if (attribute == null) {
			throw new SnippetException("Operation must have a non-null " + type.getName() + " attribute");
		}
		return attribute;
	}

	/**
	 * Create the model that should be used during template rendering to document the
	 * given {@code operation}. Any additional attributes that were supplied when this
	 * {@code TemplatedSnippet} were created will be automatically added to the model
	 * prior to rendering.
	 * @param operation the operation
	 * @return the model
	 * @throws ModelCreationException if model creation fails
	 */
	protected abstract Map<String, Object> createModel(Operation operation);

	/**
	 * Returns the additional attributes that will be included in the model during
	 * template rendering.
	 * @return the additional attributes
	 */
	protected final Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns the name of the snippet that will be created.
	 * @return the snippet name
	 */
	protected final String getSnippetName() {
		return this.snippetName;
	}

}
