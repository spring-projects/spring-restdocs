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

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * Creates a new {@code TemplatedSnippet} that will produce a snippet with the given
	 * {@code snippetName}. The given {@code attributes} will be included in the model
	 * during rendering of the template.
	 *
	 * @param snippetName The name of the snippet
	 * @param attributes The additional attributes
	 */
	protected TemplatedSnippet(String snippetName, Map<String, Object> attributes) {
		this.snippetName = snippetName;
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	@Override
	public void document(Operation operation) throws IOException {
		RestDocumentationContext context = (RestDocumentationContext) operation
				.getAttributes().get(RestDocumentationContext.class.getName());
		WriterResolver writerResolver = (WriterResolver) operation.getAttributes()
				.get(WriterResolver.class.getName());
		try (Writer writer = writerResolver.resolve(operation.getName(), this.snippetName,
				context)) {
			Map<String, Object> model = createModel(operation);
			model.putAll(this.attributes);
			TemplateEngine templateEngine = (TemplateEngine) operation.getAttributes()
					.get(TemplateEngine.class.getName());
			writer.append(templateEngine.compileTemplate(this.snippetName).render(model));
		}
	}

	/**
	 * Create the model that should be used during template rendering to document the
	 * given {@code operation}. Any additional attributes that were supplied when this
	 * {@code TemplatedSnippet} were created will be automatically added to the model
	 * prior to rendering.
	 *
	 * @param operation The operation
	 * @return the model
	 * @throws ModelCreationException if model creation fails
	 */
	protected abstract Map<String, Object> createModel(Operation operation);

	/**
	 * Returns the additional attributes that will be included in the model during
	 * template rendering.
	 *
	 * @return the additional attributes
	 */
	protected final Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns the name of the snippet that will be created.
	 *
	 * @return the snippet name
	 */
	protected final String getSnippetName() {
		return this.snippetName;
	}

}
