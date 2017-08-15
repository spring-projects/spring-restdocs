/*
 * Copyright 2014-2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list of JSON fields. This list contains extracted values by {@code JsonFieldPath}
 * from the JSON payload.
 *
 * @param <E> the type of elements in this list
 *
 * @author Minhyeok Jeong
 */
final class JsonFieldList<E> extends ArrayList<E> {

	/**
	 * Constructs a list containing the elements of the specified collection, in the order
	 * they are returned by the collection's iterator.
	 *
	 * @param c the collection whose elements are to be placed into this list
	 * @throws NullPointerException if the specified collection is null
	 * @see ArrayList#ArrayList(Collection)
	 */
	public JsonFieldList(Collection<? extends E> c) {
		super(c);
	}

}
