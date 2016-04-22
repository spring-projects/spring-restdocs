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

package com.example;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class Payload {

	@SuppressWarnings("unused")
	public void bookFieldDescriptors() {
		// tag::book-descriptors[]
		FieldDescriptor[] book = new FieldDescriptor[] {
				fieldWithPath("title").description("Title of the book"),
				fieldWithPath("author").description("Author of the book") };
		// end::book-descriptors[]
	}

}
