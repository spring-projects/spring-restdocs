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
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FieldPathPayloadSubsectionExtractor}.
 *
 * @author Andy Wilkinson
 */
public class FieldPathPayloadSubsectionExtractorTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	@SuppressWarnings("unchecked")
	public void extractMapSubsectionOfJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.b")
				.extractSubsection("{\"a\":{\"b\":{\"c\":5}}}".getBytes(),
						MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload,
				Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted.get("c")).isEqualTo(5);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractSingleElementArraySubsectionOfJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[]")
				.extractSubsection("{\"a\":[{\"b\":5}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload,
				Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("b");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractMultiElementArraySubsectionOfJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a")
				.extractSubsection("{\"a\":[{\"b\":5},{\"b\":4}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload,
				Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("b");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractMapSubsectionFromSingleElementArrayInAJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b")
				.extractSubsection("{\"a\":[{\"b\":{\"c\":5}}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload,
				Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted.get("c")).isEqualTo(5);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractMapSubsectionWithCommonStructureFromMultiElementArrayInAJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[].b")
				.extractSubsection(
						"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6}}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		Map<String, Object> extracted = new ObjectMapper().readValue(extractedPayload,
				Map.class);
		assertThat(extracted.size()).isEqualTo(1);
		assertThat(extracted).containsOnlyKeys("c");
	}

	@Test
	public void extractMapSubsectionWithVaryingStructureFromMultiElementArrayInAJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		this.thrown.expect(PayloadHandlingException.class);
		this.thrown.expectMessage("The following uncommon paths were found: [a.[].b.d]");
		new FieldPathPayloadSubsectionExtractor("a.[].b").extractSubsection(
				"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6, \"d\": 7}}]}".getBytes(),
				MediaType.APPLICATION_JSON);
	}

	@Test
	public void extractedSubsectionIsPrettyPrintedWhenInputIsPrettyPrinted()
			throws JsonParseException, JsonMappingException, JsonProcessingException,
			IOException {
		ObjectMapper objectMapper = new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);
		byte[] prettyPrintedPayload = objectMapper.writeValueAsBytes(
				objectMapper.readValue("{\"a\": { \"b\": { \"c\": 1 }}}", Object.class));
		byte[] extractedSubsection = new FieldPathPayloadSubsectionExtractor("a.b")
				.extractSubsection(prettyPrintedPayload, MediaType.APPLICATION_JSON);
		byte[] prettyPrintedSubsection = objectMapper
				.writeValueAsBytes(objectMapper.readValue("{\"c\": 1 }", Object.class));
		assertThat(new String(extractedSubsection))
				.isEqualTo(new String(prettyPrintedSubsection));
	}

	@Test
	public void extractedSubsectionIsNotPrettyPrintedWhenInputIsNotPrettyPrinted()
			throws JsonParseException, JsonMappingException, JsonProcessingException,
			IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] payload = objectMapper.writeValueAsBytes(
				objectMapper.readValue("{\"a\": { \"b\": { \"c\": 1 }}}", Object.class));
		byte[] extractedSubsection = new FieldPathPayloadSubsectionExtractor("a.b")
				.extractSubsection(payload, MediaType.APPLICATION_JSON);
		byte[] subsection = objectMapper
				.writeValueAsBytes(objectMapper.readValue("{\"c\": 1 }", Object.class));
		assertThat(new String(extractedSubsection)).isEqualTo(new String(subsection));
	}

}
