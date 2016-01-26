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
	 *
	 * @param <T> the type of the enumeration's elements
	 * @param enumeration The enumeration to expose as an {@code Iterable}
	 * @return the iterable
	 */
	static <T> Iterable<T> iterable(Enumeration<T> enumeration) {
		return new IterableEnumeration<>(enumeration);
	}

}
