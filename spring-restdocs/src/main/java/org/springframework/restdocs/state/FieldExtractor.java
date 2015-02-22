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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@code FieldExtractor} is used to extract {@link Field fields} from a JSON response.
 * The expected format of the links in the response is determined by the implementation.
 *
 * @author Andy Wilkinson
 *
 */
public class FieldExtractor {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Map<Path, Field> extractedFields = new HashMap<>();

	@SuppressWarnings("unchecked")
	public Map<Path, Field> extractFields(MockHttpServletRequest request)
			throws IOException {
		Map<String, Object> jsonContent = this.objectMapper.readValue(
				request.getInputStream(), Map.class);
		extractFieldsRecursively(jsonContent);
		return this.extractedFields;
	}

	@SuppressWarnings("unchecked")
	public Map<Path, Field> extractFields(MockHttpServletResponse response)
			throws IOException {
		String responseBody = response.getContentAsString();
		Assert.hasText(responseBody,
				"The response doesn't contain a body to extract fields from");
		Map<String, Object> jsonContent = this.objectMapper.readValue(responseBody,
				Map.class);
		extractFieldsRecursively(jsonContent);
		return this.extractedFields;
	}

	private void extractFieldsRecursively(Map<String, Object> jsonContent) {
		extractFieldsRecursively(null, jsonContent);
	}

	@SuppressWarnings("unchecked")
	private void extractFieldsRecursively(Path previousSteps,
			Map<String, Object> jsonContent) {
		for (Entry<String, Object> entry : jsonContent.entrySet()) {
			Path path;
			if (previousSteps == null) {
				path = new Path(entry.getKey());
			}
			else {
				path = new Path(previousSteps, entry.getKey());
			}
			this.extractedFields.put(path, new Field(path, entry.getValue()));
			if (entry.getValue() instanceof Map) {
				Map<String, Object> value = (Map<String, Object>) entry.getValue();
				extractFieldsRecursively(path, value);
			}
		}
	}
}
