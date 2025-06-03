/*
 * Copyright 2014-2025 the original author or authors.
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

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link FieldTypeResolver}.
 *
 * @author Mathias Düsterhöft
 */
public class FieldTypeResolverTests {

	@Test
	public void whenForContentWithDescriptorsCalledWithJsonContentThenReturnsJsonFieldTypeResolver() {
		assertThat(FieldTypeResolver.forContentWithDescriptors("{\"field\": \"value\"}".getBytes(),
				MediaType.APPLICATION_JSON, Collections.emptyList()))
			.isInstanceOf(JsonContentHandler.class);
	}

	@Test
	public void whenForContentWithDescriptorsCalledWithXmlContentThenReturnsXmlContentHandler() {
		assertThat(FieldTypeResolver.forContentWithDescriptors("<a><b>5</b></a>".getBytes(), MediaType.APPLICATION_XML,
				Collections.emptyList()))
			.isInstanceOf(XmlContentHandler.class);
	}

	@Test
	public void whenForContentWithDescriptorsIsCalledWithInvalidContentThenExceptionIsThrown() {
		assertThatExceptionOfType(PayloadHandlingException.class).isThrownBy(() -> FieldTypeResolver
			.forContentWithDescriptors("some".getBytes(), MediaType.APPLICATION_XML, Collections.emptyList()));
	}

}
