/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.mockmvc;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * An adapter to expose an {@link Enumeration} as an {@link Iterable}.
 *
 * @param <T> the type of the Enumeration's contents
 * @author Andy Wilkinson
 */
final class IterableEnumeration<T> implements Iterable<T> {

	private final Enumeration<T> enumeration;

	private IterableEnumeration(Enumeration<T> enumeration) {
		this.enumeration = enumeration;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return IterableEnumeration.this.enumeration.hasMoreElements();
			}

			@Override
			public T next() {
				return IterableEnumeration.this.enumeration.nextElement();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Creates an {@code Iterable} that will iterate over the given {@code enumeration}.
	 * @param <T> the type of the enumeration's elements
	 * @param enumeration the enumeration to expose as an {@code Iterable}
	 * @return the iterable
	 */
	static <T> Iterable<T> of(Enumeration<T> enumeration) {
		return new IterableEnumeration<>(enumeration);
	}

}
