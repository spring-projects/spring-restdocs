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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.DocumentationWriter.TableAction;
import org.springframework.restdocs.snippet.DocumentationWriter.TableWriter;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link SnippetWritingResultHandler} that produces a snippet documenting a RESTful
 * resource's request or response fields.
 * 
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public abstract class FieldSnippetResultHandler extends SnippetWritingResultHandler {

	private final Map<String, FieldDescriptor> descriptorsByPath = new LinkedHashMap<String, FieldDescriptor>();

	private final FieldTypeResolver fieldTypeResolver = new FieldTypeResolver();

	private final FieldExtractor fieldExtractor = new FieldExtractor();

	private final FieldValidator fieldValidator = new FieldValidator();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private List<FieldDescriptor> fieldDescriptors;

	FieldSnippetResultHandler(String outputDir, String filename,
			List<FieldDescriptor> descriptors) {
		super(outputDir, filename + "-fields");
		for (FieldDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getPath());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByPath.put(descriptor.getPath(), descriptor);
		}
		this.fieldDescriptors = descriptors;
	}

	@Override
	protected void handle(MvcResult result, DocumentationWriter writer)
			throws IOException {

		this.fieldValidator.validate(getPayloadReader(result), this.fieldDescriptors);

		final Map<String, Object> payload = extractPayload(result);

		List<String> missingFields = new ArrayList<String>();

		for (FieldDescriptor fieldDescriptor : this.fieldDescriptors) {
			if (!fieldDescriptor.isOptional()) {
				Object field = this.fieldExtractor.extractField(
						fieldDescriptor.getPath(), payload);
				if (field == null) {
					missingFields.add(fieldDescriptor.getPath());
				}
			}
		}

		writer.table(new TableAction() {

			@Override
			public void perform(TableWriter tableWriter) throws IOException {
				tableWriter.headers("Path", "Type", "Description");
				for (Entry<String, FieldDescriptor> entry : FieldSnippetResultHandler.this.descriptorsByPath
						.entrySet()) {
					FieldDescriptor descriptor = entry.getValue();
					FieldType type = descriptor.getType() != null ? descriptor.getType()
							: FieldSnippetResultHandler.this.fieldTypeResolver
									.resolveFieldType(descriptor.getPath(), payload);
					tableWriter.row(entry.getKey().toString(), type.toString(), entry
							.getValue().getDescription());
				}

			}

		});

	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractPayload(MvcResult result) throws IOException {
		Reader payloadReader = getPayloadReader(result);
		try {
			return this.objectMapper.readValue(payloadReader, Map.class);
		}
		finally {
			payloadReader.close();
		}
	}

	protected abstract Reader getPayloadReader(MvcResult result) throws IOException;

}