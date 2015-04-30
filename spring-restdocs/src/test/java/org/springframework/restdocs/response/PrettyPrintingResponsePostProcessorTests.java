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

package org.springframework.restdocs.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link PrettyPrintingResponsePostProcessor}
 * 
 * @author Andy Wilkinson
 *
 */
public class PrettyPrintingResponsePostProcessorTests {

	@Test
	public void prettyPrintJson() throws Exception {
		assertThat(new PrettyPrintingResponsePostProcessor().modifyContent("{\"a\":5}"),
				equalTo(String.format("{%n  \"a\" : 5%n}")));
	}

	@Test
	public void prettyPrintXml() throws Exception {
		assertThat(
				new PrettyPrintingResponsePostProcessor()
						.modifyContent("<one a=\"alpha\"><two b=\"bravo\"/></one>"),
				equalTo(String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n"
						+ "<one a=\"alpha\">%n    <two b=\"bravo\"/>%n</one>%n")));
	}

	@Test
	public void empytContentIsHandledGracefully() throws Exception {
		assertThat(new PrettyPrintingResponsePostProcessor().modifyContent(""),
				equalTo(""));
	}

	@Test
	public void nonJsonAndNonXmlContentIsHandledGracefully() throws Exception {
		String content = "abcdefg";
		assertThat(new PrettyPrintingResponsePostProcessor().modifyContent(content),
				equalTo(content));
	}
}
