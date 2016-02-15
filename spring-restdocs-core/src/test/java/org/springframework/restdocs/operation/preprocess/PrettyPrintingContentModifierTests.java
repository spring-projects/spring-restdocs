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

package org.springframework.restdocs.operation.preprocess;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.restdocs.test.OutputCapture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link PrettyPrintingContentModifier}.
 *
 * @author Andy Wilkinson
 *
 */
public class PrettyPrintingContentModifierTests {

	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	@Test
	public void prettyPrintJson() throws Exception {
		assertThat(new PrettyPrintingContentModifier()
				.modifyContent("{\"a\":5}".getBytes(), null),
				equalTo(String.format("{%n  \"a\" : 5%n}").getBytes()));
	}

	@Test
	public void prettyPrintXml() throws Exception {
		assertThat(
				new PrettyPrintingContentModifier().modifyContent(
						"<one a=\"alpha\"><two b=\"bravo\"/></one>".getBytes(), null),
				equalTo(String
						.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n"
								+ "<one a=\"alpha\">%n    <two b=\"bravo\"/>%n</one>%n")
								.getBytes()));
	}

	@Test
	public void empytContentIsHandledGracefully() throws Exception {
		assertThat(new PrettyPrintingContentModifier().modifyContent("".getBytes(), null),
				equalTo("".getBytes()));
	}

	@Test
	public void nonJsonAndNonXmlContentIsHandledGracefully() throws Exception {
		String content = "abcdefg";
		this.outputCapture.expect(isEmptyString());
		assertThat(new PrettyPrintingContentModifier().modifyContent(content.getBytes(),
				null), equalTo(content.getBytes()));

	}

	@Test
	public void encodingIsPreserved() throws Exception {
		Map<String, String> input = new HashMap<>();
		input.put("japanese", "\u30b3\u30f3\u30c6\u30f3\u30c4");
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, String> output = objectMapper
				.readValue(new PrettyPrintingContentModifier().modifyContent(
						objectMapper.writeValueAsBytes(input), null), Map.class);
		assertThat(output, is(equalTo(input)));
	}
}
