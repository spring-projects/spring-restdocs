/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.reusingfielddescriptors;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public final class BookPayload {

	// @fold:on
	private BookPayload() {

	}
	// @fold:off

	public static FieldDescriptor[] bookDescription() {
		return new FieldDescriptor[] { fieldWithPath("title").description("Title of the book"),
				fieldWithPath("author").description("Author of the book") };
	}

}
