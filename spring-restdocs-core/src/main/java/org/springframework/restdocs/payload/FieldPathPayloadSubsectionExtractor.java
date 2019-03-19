/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldProcessor.ExtractedField;

/**
 * A {@link PayloadSubsectionExtractor} that extracts the subsection of the JSON payload
 * identified by a field path.
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 * @see PayloadDocumentation#beneathPath(String)
 */
public class FieldPathPayloadSubsectionExtractor
		implements PayloadSubsectionExtractor<FieldPathPayloadSubsectionExtractor> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final ObjectMapper prettyPrintingOjectMapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);

	private final String fieldPath;

	private final String subsectionId;

	/**
	 * Creates a new {@code FieldPathPayloadSubsectionExtractor} that will extract the
	 * subsection of the JSON payload beneath the given {@code fieldPath}. The
	 * {@code fieldPath} prefixed with {@code beneath-} with be used as the subsection ID.
	 * @param fieldPath the path of the field
	 */
	protected FieldPathPayloadSubsectionExtractor(String fieldPath) {
		this(fieldPath, "beneath-" + fieldPath);
	}

	/**
	 * Creates a new {@code FieldPathPayloadSubsectionExtractor} that will extract the
	 * subsection of the JSON payload beneath the given {@code fieldPath} and that will
	 * use the given {@code subsectionId} to identify the subsection.
	 * @param fieldPath the path of the field
	 * @param subsectionId the ID of the subsection
	 */
	protected FieldPathPayloadSubsectionExtractor(String fieldPath, String subsectionId) {
		this.fieldPath = fieldPath;
		this.subsectionId = subsectionId;
	}

	@Override
	public byte[] extractSubsection(byte[] payload, MediaType contentType) {
		try {
			ExtractedField extractedField = new JsonFieldProcessor().extract(
					this.fieldPath, objectMapper.readValue(payload, Object.class));
			Object value = extractedField.getValue();
			if (value instanceof List) {
				List<?> extractedList = (List<?>) value;
				Set<String> uncommonPaths = JsonFieldPaths.from(extractedList)
						.getUncommon();
				if (uncommonPaths.isEmpty()) {
					value = extractedList.get(0);
				}
				else {
					String message = this.fieldPath + " identifies multiple sections of "
							+ "the payload and they do not have a common structure. The "
							+ "following uncommon paths were found: ";
					List<String> prefixedPaths = new ArrayList<>();
					for (String uncommonPath : uncommonPaths) {
						prefixedPaths.add(this.fieldPath + "." + uncommonPath);
					}
					message += prefixedPaths;
					throw new PayloadHandlingException(message);
				}
			}
			return getObjectMapper(payload).writeValueAsBytes(value);
		}
		catch (IOException ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	@Override
	public String getSubsectionId() {
		return this.subsectionId;
	}

	/**
	 * Returns the path of the field that will be extracted.
	 * @return the path of the field
	 */
	protected String getFieldPath() {
		return this.fieldPath;
	}

	@Override
	public FieldPathPayloadSubsectionExtractor withSubsectionId(String subsectionId) {
		return new FieldPathPayloadSubsectionExtractor(this.fieldPath, subsectionId);
	}

	private ObjectMapper getObjectMapper(byte[] payload) {
		if (isPrettyPrinted(payload)) {
			return prettyPrintingOjectMapper;
		}
		return objectMapper;
	}

	private boolean isPrettyPrinted(byte[] payload) {
		for (byte b : payload) {
			if (b == '\n') {
				return true;
			}
		}
		return false;
	}

}
