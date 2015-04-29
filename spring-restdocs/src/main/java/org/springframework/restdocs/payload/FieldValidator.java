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
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.snippet.SnippetGenerationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * {@code FieldValidator} is used to validate a payload's fields against the user-provided
 * {@link FieldDescriptor}s.
 * 
 * @author Andy Wilkinson
 */
class FieldValidator {

	private final FieldProcessor fieldProcessor = new FieldProcessor();

	private final ObjectMapper objectMapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);

	@SuppressWarnings("unchecked")
	void validate(Reader payloadReader, List<FieldDescriptor> fieldDescriptors)
			throws IOException {
		Map<String, Object> payload = this.objectMapper.readValue(payloadReader,
				Map.class);
		List<String> missingFields = findMissingFields(payload, fieldDescriptors);
		Map<String, Object> undocumentedPayload = findUndocumentedFields(payload,
				fieldDescriptors);

		if (!missingFields.isEmpty() || !undocumentedPayload.isEmpty()) {
			String message = "";
			if (!undocumentedPayload.isEmpty()) {
				message += String.format(
						"The following parts of the payload were not documented:%n%s",
						this.objectMapper.writeValueAsString(undocumentedPayload));
			}
			if (!missingFields.isEmpty()) {
				if (message.length() > 0) {
					message += String.format("%n");
				}
				message += "Fields with the following paths were not found in the payload: "
						+ missingFields;
			}
			throw new SnippetGenerationException(message);
		}
	}

	private List<String> findMissingFields(Map<String, Object> payload,
			List<FieldDescriptor> fieldDescriptors) {
		List<String> missingFields = new ArrayList<String>();

		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (!fieldDescriptor.isOptional()
					&& !this.fieldProcessor.hasField(
							FieldPath.compile(fieldDescriptor.getPath()), payload)) {
				missingFields.add(fieldDescriptor.getPath());
			}
		}

		return missingFields;
	}

	private Map<String, Object> findUndocumentedFields(Map<String, Object> payload,
			List<FieldDescriptor> fieldDescriptors) {
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			FieldPath path = FieldPath.compile(fieldDescriptor.getPath());
			this.fieldProcessor.remove(path, payload);
		}
		return payload;
	}

}
