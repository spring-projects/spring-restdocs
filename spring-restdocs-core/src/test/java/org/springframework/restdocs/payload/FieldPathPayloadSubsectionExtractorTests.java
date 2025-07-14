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

package org.springframework.restdocs.payload;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link FieldPathPayloadSubsectionExtractor}.
 *
 * @author Andy Wilkinson
 */
class FieldPathPayloadSubsectionExtractorTests {

	@Test
	@SuppressWarnings("unchecked")
	void extractMapSubsectionOfJsonMap() throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.b")
			.extractSubsection("{\"a\":{\"b\":{\"c\":5}}}".getBytes(), MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted.get("c")).isEqualTo(5);
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractSingleElementArraySubsectionOfJsonMap() throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[]")
			.extractSubsection("{\"a\":[{\"b\":5}]}".getBytes(), MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("b");
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractMultiElementArraySubsectionOfJsonMap() throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a")
			.extractSubsection("{\"a\":[{\"b\":5},{\"b\":4}]}".getBytes(), MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("b");
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractMapSubsectionFromSingleElementArrayInAJsonMap() throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b")
			.extractSubsection("{\"a\":[{\"b\":{\"c\":5}}]}".getBytes(), MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted.get("c")).isEqualTo(5);
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractMapSubsectionWithCommonStructureFromMultiElementArrayInAJsonMap() throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b")
			.extractSubsection("{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6}}]}".getBytes(), MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("c");
	}

	@Test
	void extractMapSubsectionWithVaryingStructureFromMultiElementArrayInAJsonMap() {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new FieldPathPayloadSubsectionExtractor("a.[].b").extractSubsection(
					"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6, \"d\": 7}}]}".getBytes(), MediaType.APPLICATION_JSON))
			.withMessageContaining("The following non-optional uncommon paths were found: [a.[].b.d]");
	}

	@Test
	void extractMapSubsectionWithVaryingStructureFromInconsistentJsonMap() {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new FieldPathPayloadSubsectionExtractor("*.d").extractSubsection(
					"{\"a\":{\"b\":1},\"c\":{\"d\":{\"e\":1,\"f\":2}}}".getBytes(), MediaType.APPLICATION_JSON))
			.withMessageContaining("The following non-optional uncommon paths were found: [*.d, *.d.e, *.d.f]");
	}

	@Test
	void extractMapSubsectionWithVaryingStructureFromInconsistentJsonMapWhereAllSubsectionFieldsAreOptional() {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new FieldPathPayloadSubsectionExtractor("*.d").extractSubsection(
					"{\"a\":{\"b\":1},\"c\":{\"d\":{\"e\":1,\"f\":2}}}".getBytes(), MediaType.APPLICATION_JSON,
					Arrays.asList(new FieldDescriptor("e").optional(), new FieldDescriptor("f").optional())))
			.withMessageContaining("The following non-optional uncommon paths were found: [*.d]");
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractMapSubsectionWithVaryingStructureDueToOptionalFieldsFromMultiElementArrayInAJsonMap()
			throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b").extractSubsection(
				"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6, \"d\": 7}}]}".getBytes(), MediaType.APPLICATION_JSON,
				Arrays.asList(new FieldDescriptor("d").optional()));
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("c");
	}

	@Test
	@SuppressWarnings("unchecked")
	void extractMapSubsectionWithVaryingStructureDueToOptionalParentFieldsFromMultiElementArrayInAJsonMap()
			throws JacksonException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b").extractSubsection(
				"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6, \"d\": { \"e\": 7}}}]}".getBytes(),
				MediaType.APPLICATION_JSON, Arrays.asList(new FieldDescriptor("d").optional()));
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload, Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("c");
	}

	@Test
	void extractedSubsectionIsPrettyPrintedWhenInputIsPrettyPrinted() throws JacksonException {
		ObjectMapper objectMapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
		byte[] prettyPrintedPayload = objectMapper
			.writeValueAsBytes(objectMapper.readValue("{\"a\": { \"b\": { \"c\": 1 }}}", Object.class));
		byte[] extractedSubsection = new FieldPathPayloadSubsectionExtractor("a.b")
			.extractSubsection(prettyPrintedPayload, MediaType.APPLICATION_JSON);
		byte[] prettyPrintedSubsection = objectMapper
			.writeValueAsBytes(objectMapper.readValue("{\"c\": 1 }", Object.class));
		assertThat(new String(extractedSubsection)).isEqualTo(new String(prettyPrintedSubsection));
	}

	@Test
	void extractedSubsectionIsNotPrettyPrintedWhenInputIsNotPrettyPrinted() throws JacksonException {
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] payload = objectMapper
			.writeValueAsBytes(objectMapper.readValue("{\"a\": { \"b\": { \"c\": 1 }}}", Object.class));
		byte[] extractedSubsection = new FieldPathPayloadSubsectionExtractor("a.b").extractSubsection(payload,
				MediaType.APPLICATION_JSON);
		byte[] subsection = objectMapper.writeValueAsBytes(objectMapper.readValue("{\"c\": 1 }", Object.class));
		assertThat(new String(extractedSubsection)).isEqualTo(new String(subsection));
	}

	@Test
	void extractNonExistentSubsection() {
		assertThatThrownBy(() -> new FieldPathPayloadSubsectionExtractor("a.c")
			.extractSubsection("{\"a\":{\"b\":{\"c\":5}}}".getBytes(), MediaType.APPLICATION_JSON))
			.isInstanceOf(PayloadHandlingException.class)
			.hasMessage("a.c does not identify a section of the payload");
	}

	@Test
	void extractEmptyArraySubsection() {
		assertThatThrownBy(() -> new FieldPathPayloadSubsectionExtractor("a").extractSubsection("{\"a\":[]}".getBytes(),
				MediaType.APPLICATION_JSON))
			.isInstanceOf(PayloadHandlingException.class)
			.hasMessage("a identifies an empty section of the payload");
	}

}
