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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CustomPrintingJsonContentModifier}.
 *
 * @author Takaaki Iida
 */
public class CustomPrintingJsonContentModifierTests {

	@Test
	public void customPrint() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper().enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

		assertThat(new CustomPrintingJsonContentModifier(objectMapper).modifyContent("{\"num\":123}".getBytes(), null))
				.isEqualTo("{\"num\":\"123\"}".getBytes());
	}

	@Test
	public void emptyContentIsHandledGracefully() throws Exception {
		assertThat(new CustomPrintingJsonContentModifier(new ObjectMapper()).modifyContent("".getBytes(), null))
				.isEqualTo("".getBytes());
	}

	@Test
	public void nonJsonAndNonXmlContentIsHandledGracefully() throws Exception {
		assertThat(new CustomPrintingJsonContentModifier(new ObjectMapper()).modifyContent("abcdefg".getBytes(), null))
				.isEqualTo("abcdefg".getBytes());
	}

	@Test
	public void encodingIsPreserved() throws Exception {
		Map<String, String> input = new HashMap<>();
		input.put("japanese", "\u30b3\u30f3\u30c6\u30f3\u30c4");
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, String> output = objectMapper.readValue(new CustomPrintingJsonContentModifier(objectMapper)
				.modifyContent(objectMapper.writeValueAsBytes(input), null), Map.class);
		assertThat(output).isEqualTo(input);
	}

}
