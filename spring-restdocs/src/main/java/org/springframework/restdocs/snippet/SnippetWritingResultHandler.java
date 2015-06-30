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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * Base class for a {@link ResultHandler} that writes a documentation snippet
 *
 * @author Andy Wilkinson
 */
public abstract class SnippetWritingResultHandler implements ResultHandler {

	private final Map<String, Object> attributes = new HashMap<>();

	private final String identifier;

	private final String snippetName;

	protected SnippetWritingResultHandler(String identifier, String snippetName,
			Map<String, Object> attributes) {
		this.identifier = identifier;
		this.snippetName = snippetName;
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	protected abstract void handle(MvcResult result, PrintWriter writer)
			throws IOException;

	@Override
	public void handle(MvcResult result) throws IOException {
		WriterResolver writerResolver = (WriterResolver) result.getRequest()
				.getAttribute(WriterResolver.class.getName());
		try (Writer writer = writerResolver.resolve(this.identifier, this.snippetName)) {
			handle(result, new PrintWriter(writer));
		}
	}

	protected Map<String, Object> getAttributes() {
		return this.attributes;
	}

}