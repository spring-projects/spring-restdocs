/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import java.util.Enumeration;
import java.util.Iterator;

public final class IterableEnumeration<T> implements Iterable<T> {

	private final Enumeration<T> enumeration;

	public IterableEnumeration(Enumeration<T> enumeration) {
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

		};
	}

	public static <T> Iterable<T> iterable(Enumeration<T> enumeration) {
		return new IterableEnumeration<T>(enumeration);
	}

}
