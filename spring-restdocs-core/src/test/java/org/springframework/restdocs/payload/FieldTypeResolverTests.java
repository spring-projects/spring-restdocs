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

import org.junit.Test;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link FieldTypeResolver}.
 *
 * @author Mathias Düsterhöft
 */
public class FieldTypeResolverTests {

	@Test
	public void returnJsonFieldTypeResolver() {
		assertThat(FieldTypeResolver.create("{\"field\": \"value\"}".getBytes(),
				MediaType.APPLICATION_JSON)).isInstanceOf(JsonFieldTypeResolver.class);
	}

	@Test
	public void returnXmlContentHandler() {
		assertThat(FieldTypeResolver.create("<a><b>5</b></a>".getBytes(),
				MediaType.APPLICATION_XML)).isInstanceOf(XmlContentHandler.class);
	}

	@Test
	public void throwOnInvalidContent() {
		assertThatThrownBy(() -> FieldTypeResolver.create("some".getBytes(),
				MediaType.APPLICATION_XML)).isInstanceOf(PayloadHandlingException.class);
	}

}
