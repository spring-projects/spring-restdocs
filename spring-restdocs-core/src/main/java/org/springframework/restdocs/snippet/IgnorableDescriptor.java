/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

/**
 * Base class for descriptors for items that can be ignored.
 *
 * @param <T> the type of the descriptor
 * @author Andy Wilkinson
 */
public abstract class IgnorableDescriptor<T extends IgnorableDescriptor<T>> extends AbstractDescriptor<T> {

	private boolean ignored = false;

	/**
	 * Marks the described item as being ignored. Ignored items are not included in the
	 * generated documentation.
	 * @return the descriptor
	 */
	@SuppressWarnings("unchecked")
	public final T ignored() {
		this.ignored = true;
		return (T) this;
	}

	/**
	 * Returns whether or not the item being described should be ignored and, therefore,
	 * should not be included in the documentation.
	 * @return {@code true} if the item should be ignored, otherwise {@code false}.
	 */
	public final boolean isIgnored() {
		return this.ignored;
	}

}
