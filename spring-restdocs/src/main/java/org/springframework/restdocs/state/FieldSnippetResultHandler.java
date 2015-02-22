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

package org.springframework.restdocs.state;

import static org.springframework.restdocs.state.FieldSnippetResultHandler.Type.REQUEST;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

/**
 * A {@link SnippetWritingResultHandler} that produces a snippet documenting a RESTful
 * resource's request or response fields.
 * 
 * @author Andreas Evers
 */
public class FieldSnippetResultHandler extends SnippetWritingResultHandler {

	private final Map<Path, FieldDescriptor> descriptorsByName = new HashMap<Path, FieldDescriptor>();

	private final Type type;

	private FieldExtractor extractor = new FieldExtractor();

	private StateDocumentationValidator validator;

	enum Type {
		REQUEST, RESPONSE;
	}

	FieldSnippetResultHandler(String outputDir, FieldSnippetResultHandler.Type type,
			List<FieldDescriptor> descriptors) {
		super(outputDir, type.toString().toLowerCase() + "fields");
		this.type = type;
		for (FieldDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getPath());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByName.put(descriptor.getPath(), descriptor);
		}
		this.validator = new StateDocumentationValidator(type);
	}

	@Override
	protected void handle(MvcResult result, DocumentationWriter writer)
			throws IOException {
		Map<Path, Field> fields;
		if (this.type == REQUEST) {
			fields = this.extractor.extractFields(result.getRequest());
		}
		else {
			fields = this.extractor.extractFields(result.getResponse());
		}

		SortedSet<Path> actualFields = new TreeSet<Path>(fields.keySet());
		SortedSet<Path> expectedFields = new TreeSet<Path>(
				this.descriptorsByName.keySet());

		this.validator.validateFields(actualFields, expectedFields);

		writer.println("|===");
		writer.println("| Path | Description | Type | Required | Constraints");

		for (Entry<Path, FieldDescriptor> entry : this.descriptorsByName.entrySet()) {
			writer.println();
			writer.println("| " + entry.getKey());
			writer.println("| " + entry.getValue().getDescription());
			writer.println("| " + entry.getValue().getType());
			writer.println("| " + entry.getValue().isRequired());
			writer.println("| " + entry.getValue().getConstraints());
		}

		writer.println("|===");
	}

}