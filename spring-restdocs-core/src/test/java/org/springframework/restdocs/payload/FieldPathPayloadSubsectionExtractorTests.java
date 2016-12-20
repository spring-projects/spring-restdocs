/*
 * Copyright 2014-2016 the original author or authors.
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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.http.MediaType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
		assertThat(extracted.size(), is(equalTo(1)));
		assertThat(extracted.get("c"), is(equalTo((Object) 5)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractMultiElementArraySubsectionOfJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a")
				.extractSubsection("{\"a\":[{\"b\":5},{\"b\":4}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		List<Map<String, Object>> extracted = new ObjectMapper()
				.readValue(extractedPayload, List.class);
		assertThat(extracted.size(), is(equalTo(2)));
		assertThat(extracted.get(0).get("b"), is(equalTo((Object) 5)));
		assertThat(extracted.get(1).get("b"), is(equalTo((Object) 4)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void extractSingleElementArraySubsectionOfJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		byte[] extractedPayload = new FieldPathPayloadSubsectionExtractor("a.[]")
				.extractSubsection("{\"a\":[{\"b\":5}]}".getBytes(),
						MediaType.APPLICATION_JSON);
		List<Map<String, Object>> extracted = new ObjectMapper()
				.readValue(extractedPayload, List.class);
		assertThat(extracted.size(), is(equalTo(1)));
		assertThat(extracted.get(0).get("b"), is(equalTo((Object) 5)));
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
		assertThat(extracted.size(), is(equalTo(1)));
		assertThat(extracted.get("c"), is(equalTo((Object) 5)));
	}

	@Test
	public void extractMapSubsectionFromMultiElementArrayInAJsonMap()
			throws JsonParseException, JsonMappingException, IOException {
		this.thrown.expect(PayloadHandlingException.class);
		this.thrown.expectMessage(
				equalTo("a.[].b does not uniquely identify a subsection of the payload"));
		new FieldPathPayloadSubsectionExtractor("a.[].b").extractSubsection(
				"{\"a\":[{\"b\":{\"c\":5}},{\"b\":{\"c\":6}}]}".getBytes(),
				MediaType.APPLICATION_JSON);
	}

}
