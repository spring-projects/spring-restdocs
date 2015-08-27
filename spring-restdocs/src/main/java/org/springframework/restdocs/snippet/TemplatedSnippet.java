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

	protected TemplatedSnippet(String snippetName, Map<String, Object> attributes) {
		this.snippetName = snippetName;
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	@Override
	public void document(Operation operation) throws IOException {
		WriterResolver writerResolver = (WriterResolver) operation.getAttributes().get(
				WriterResolver.class.getName());
		try (Writer writer = writerResolver
				.resolve(operation.getName(), this.snippetName)) {
			Map<String, Object> model = createModel(operation);
			model.putAll(this.attributes);
			TemplateEngine templateEngine = (TemplateEngine) operation.getAttributes()
					.get(TemplateEngine.class.getName());
			writer.append(templateEngine.compileTemplate(this.snippetName).render(model));
		}
	}

	protected abstract Map<String, Object> createModel(Operation operation)
			throws IOException;

}