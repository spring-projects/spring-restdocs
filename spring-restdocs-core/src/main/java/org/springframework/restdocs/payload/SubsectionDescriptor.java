/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

/**
 * A description of a subsection, i.e. a field and all of its descendants, in a request or
 * response payload.
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public class SubsectionDescriptor extends FieldDescriptor {

	/**
	 * Creates a new {@code SubsectionDescriptor} describing the subsection with the given
	 * {@code path}.
	 * @param path the path
	 */
	protected SubsectionDescriptor(String path) {
		super(path);
	}

}
