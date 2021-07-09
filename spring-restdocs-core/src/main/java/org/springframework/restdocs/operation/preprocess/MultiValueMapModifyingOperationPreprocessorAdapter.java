/*
 * Copyright 2014-2019 the original author or authors.
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

package org.springframework.restdocs.operation.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * An {@link OperationPreprocessorAdapter} that supports modification of {@link T}.
 *
 * @param <S> the concrete type of this preprocessor, to be returned from methods that
 * support chaining
 * @param <T> the type to which the modification applies
 * @author Andy Wilkinson
 * @author Jihun Cha
 * @see ParametersModifyingOperationPreprocessor
 * @see HeadersModifyingOperationPreprocessor
 */
abstract class MultiValueMapModifyingOperationPreprocessorAdapter<S extends MultiValueMapModifyingOperationPreprocessorAdapter<S, T>, T extends MultiValueMap<String, String>>
		extends OperationPreprocessorAdapter {

	private final List<Modification<T>> modifications = new ArrayList<>();

	/**
	 * Adds a {@link T} with the given {@code name} and {@code value}.
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public S add(String name, String value) {
		this.modifications.add(new AddModification<>(name, value));
		return (S) this;
	}

	/**
	 * Sets the {@link T} with the given {@code name} to have the given {@code values}.
	 * @param name the name
	 * @param values the values
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public S set(String name, String... values) {
		Assert.notEmpty(values, "At least one value must be provided");
		this.modifications.add(new SetModification<>(name, Arrays.asList(values)));
		return (S) this;
	}

	/**
	 * Removes the {@link T} with the given {@code name}.
	 * @param name the name of the {@link T}
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public S remove(String name) {
		this.modifications.add(new RemoveModification<>(name));
		return (S) this;
	}

	/**
	 * Removes the {@link T} with the given pattern of the {@code name}.
	 * @param name the pattern of the name
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public S remove(Pattern name) {
		this.modifications.add(new PatternRemoveModification<>(name));
		return (S) this;
	}

	/**
	 * Removes the given {@code value} from the {@link T} with the given {@code name}.
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public S remove(String name, String value) {
		this.modifications.add(new RemoveValueModification<>(name, value));
		return (S) this;
	}

	/**
	 * Returns a list of the modifications.
	 * @return the list of modifications
	 */
	List<Modification<T>> getModifications() {
		return this.modifications;
	}

	/**
	 * A {@code Modification} is used to apply the modification to {@link T}.
	 *
	 * @param <T> the type to which the modification applies
	 */
	interface Modification<T extends MultiValueMap<String, String>> {

		void apply(T map);

	}

	private static final class AddModification<T extends MultiValueMap<String, String>>
			implements Modification<T> {

		private final String name;

		private final String value;

		private AddModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void apply(T map) {
			map.add(this.name, this.value);
		}

	}

	private static final class SetModification<T extends MultiValueMap<String, String>>
			implements Modification<T> {

		private final String name;

		private final List<String> values;

		private SetModification(String name, List<String> values) {
			this.name = name;
			this.values = values;
		}

		@Override
		public void apply(T map) {
			map.put(this.name, this.values);
		}

	}

	private static final class RemoveModification<T extends MultiValueMap<String, String>>
			implements Modification<T> {

		private final String name;

		private RemoveModification(String name) {
			this.name = name;
		}

		@Override
		public void apply(T map) {
			map.remove(this.name);
		}

	}

	private static final class PatternRemoveModification<T extends MultiValueMap<String, String>>
			implements Modification<T> {

		private final Pattern name;

		private PatternRemoveModification(Pattern name) {
			this.name = name;
		}

		@Override
		public void apply(T map) {
			map.keySet().removeIf((key) -> this.name.matcher(key).matches());
		}

	}

	private static final class RemoveValueModification<T extends MultiValueMap<String, String>>
			implements Modification<T> {

		private final String name;

		private final String value;

		private RemoveValueModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void apply(T map) {
			List<String> values = map.get(this.name);
			if (values != null) {
				values.remove(this.value);
				if (values.isEmpty()) {
					map.remove(this.name);
				}
			}
		}

	}

}
