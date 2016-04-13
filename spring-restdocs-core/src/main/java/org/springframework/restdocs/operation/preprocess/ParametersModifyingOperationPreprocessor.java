/*
 * Copyright 2012-2016 the original author or authors.
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

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.util.Assert;

/**
 * An {@link OperationPreprocessor} that can be used to modify a request's
 * {@link OperationRequest#getParameters()} by adding, setting, and removing parameters.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public final class ParametersModifyingOperationPreprocessor
		extends OperationPreprocessorAdapter {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final List<Modification> modifications = new ArrayList<>();

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		Parameters parameters = new Parameters();
		parameters.putAll(request.getParameters());
		for (Modification modification : this.modifications) {
			modification.apply(parameters);
		}
		return this.requestFactory.createFrom(request, parameters);
	}

	/**
	 * Adds a parameter with the given {@code name} and {@code value}.
	 *
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	public ParametersModifyingOperationPreprocessor add(String name, String value) {
		this.modifications.add(new AddParameterModification(name, value));
		return this;
	}

	/**
	 * Sets the parameter with the given {@code name} to have the given {@code values}.
	 *
	 * @param name the name
	 * @param values the values
	 * @return {@code this}
	 */
	public ParametersModifyingOperationPreprocessor set(String name, String... values) {
		Assert.notEmpty(values, "At least one value must be provided");
		this.modifications.add(new SetParameterModification(name, Arrays.asList(values)));
		return this;
	}

	/**
	 * Removes the parameter with the given {@code name}.
	 *
	 * @param name the name of the parameter
	 * @return {@code this}
	 */
	public ParametersModifyingOperationPreprocessor remove(String name) {
		this.modifications.add(new RemoveParameterModification(name));
		return this;
	}

	/**
	 * Removes the given {@code value} from the parameter with the given {@code name}.
	 *
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	public ParametersModifyingOperationPreprocessor remove(String name, String value) {
		this.modifications.add(new RemoveValueParameterModification(name, value));
		return this;
	}

	private interface Modification {

		void apply(Parameters parameters);

	}

	private static final class AddParameterModification implements Modification {

		private final String name;

		private final String value;

		private AddParameterModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void apply(Parameters parameters) {
			parameters.add(this.name, this.value);
		}

	}

	private static final class SetParameterModification implements Modification {

		private final String name;

		private final List<String> values;

		private SetParameterModification(String name, List<String> values) {
			this.name = name;
			this.values = values;
		}

		@Override
		public void apply(Parameters parameters) {
			parameters.put(this.name, this.values);
		}

	}

	private static final class RemoveParameterModification implements Modification {

		private final String name;

		private RemoveParameterModification(String name) {
			this.name = name;
		}

		@Override
		public void apply(Parameters parameters) {
			parameters.remove(this.name);
		}

	}

	private static final class RemoveValueParameterModification implements Modification {

		private final String name;

		private final String value;

		private RemoveValueParameterModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void apply(Parameters parameters) {
			List<String> values = parameters.get(this.name);
			if (values != null) {
				values.remove(this.value);
				if (values.isEmpty()) {
					parameters.remove(this.name);
				}
			}
		}

	}

}
