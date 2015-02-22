/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.state;

import org.springframework.core.style.ToStringCreator;

/**
 * Representation of a field used in a Hypermedia-based API
 *
 * @author Andreas Evers
 */
public class Field {

	private final Path path;

	private final Object value;

	/**
	 * Creates a new {@code Field} with the given {@code path} and {@code value}
	 *
	 * @param path The field's path
	 * @param value The field's value
	 */
	public Field(Path path, Object value) {
		this.path = path;
		this.value = value;
	}

	/**
	 * Returns the field's {@code path}
	 * @return the field's {@code path}
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Returns the field's {@code value}
	 * @return the field's {@code value}
	 */
	public Object getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + this.path.hashCode();
		result = prime * result + this.value.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Field other = (Field) obj;
		if (!this.path.equals(other.path)) {
			return false;
		}
		if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("path", this.path)
				.append("value", this.value).toString();
	}

}
