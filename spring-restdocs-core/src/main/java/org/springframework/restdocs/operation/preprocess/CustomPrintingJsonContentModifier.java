/*
 * Copyright 2014-2020 the original author or authors.
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

package org.springframework.restdocs.operation.preprocess;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

/**
 * A {@link ContentModifier} that modifies the JSON content by custom printing it.
 *
 * @author Takaaki Iida
 */
public class CustomPrintingJsonContentModifier implements ContentModifier {

	private final ObjectMapper objectMapper;

	CustomPrintingJsonContentModifier(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
		if (originalContent.length > 0) {
			try {
				return this.objectMapper.writeValueAsBytes(this.objectMapper.readTree(originalContent));
			}
			catch (Exception ex) {
				// Continue
			}
		}

		return originalContent;
	}

}
