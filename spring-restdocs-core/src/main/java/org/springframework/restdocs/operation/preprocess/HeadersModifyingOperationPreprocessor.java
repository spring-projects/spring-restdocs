/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.util.Assert;

/**
 * An {@link OperationPreprocessor} that modifies a request or response by adding,
 * setting, or removing headers.
 *
 * @author Jihoon Cha
 * @author Andy Wilkinson
 * @since 3.0.0
 */
public class HeadersModifyingOperationPreprocessor implements OperationPreprocessor {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final List<Modification> modifications = new ArrayList<>();

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		return this.requestFactory.createFrom(request, preprocess(request.getHeaders()));
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return this.responseFactory.createFrom(response, preprocess(response.getHeaders()));
	}

	private HttpHeaders preprocess(HttpHeaders headers) {
		HttpHeaders modifiedHeaders = new HttpHeaders();
		modifiedHeaders.addAll(headers);
		for (Modification modification : this.modifications) {
			modification.applyTo(modifiedHeaders);
		}
		return modifiedHeaders;
	}

	/**
	 * Adds a header with the given {@code name} and {@code value}.
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	public HeadersModifyingOperationPreprocessor add(String name, String value) {
		this.modifications.add(new AddHeaderModification(name, value));
		return this;
	}

	/**
	 * Sets the header with the given {@code name} to have the given {@code values}.
	 * @param name the name
	 * @param values the values
	 * @return {@code this}
	 */
	public HeadersModifyingOperationPreprocessor set(String name, String... values) {
		Assert.notEmpty(values, "At least one value must be provided");
		this.modifications.add(new SetHeaderModification(name, Arrays.asList(values)));
		return this;
	}

	/**
	 * Removes the header with the given {@code name}.
	 * @param name the name of the parameter
	 * @return {@code this}
	 */
	public HeadersModifyingOperationPreprocessor remove(String name) {
		this.modifications.add(new RemoveHeaderModification(name));
		return this;
	}

	/**
	 * Removes the given {@code value} from the header with the given {@code name}.
	 * @param name the name
	 * @param value the value
	 * @return {@code this}
	 */
	public HeadersModifyingOperationPreprocessor remove(String name, String value) {
		this.modifications.add(new RemoveValueHeaderModification(name, value));
		return this;
	}

	/**
	 * Remove headers that match the given {@code namePattern} regular expression.
	 * @param namePattern the name pattern
	 * @return {@code this}
	 * @see Matcher#matches()
	 */
	public HeadersModifyingOperationPreprocessor removeMatching(String namePattern) {
		this.modifications.add(new RemoveHeadersByNamePatternModification(Pattern.compile(namePattern)));
		return this;
	}

	private interface Modification {

		void applyTo(HttpHeaders headers);

	}

	private static final class AddHeaderModification implements Modification {

		private final String name;

		private final String value;

		private AddHeaderModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void applyTo(HttpHeaders headers) {
			headers.add(this.name, this.value);
		}

	}

	private static final class SetHeaderModification implements Modification {

		private final String name;

		private final List<String> values;

		private SetHeaderModification(String name, List<String> values) {
			this.name = name;
			this.values = values;
		}

		@Override
		public void applyTo(HttpHeaders headers) {
			headers.put(this.name, this.values);
		}

	}

	private static final class RemoveHeaderModification implements Modification {

		private final String name;

		private RemoveHeaderModification(String name) {
			this.name = name;
		}

		@Override
		public void applyTo(HttpHeaders headers) {
			headers.remove(this.name);
		}

	}

	private static final class RemoveValueHeaderModification implements Modification {

		private final String name;

		private final String value;

		private RemoveValueHeaderModification(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void applyTo(HttpHeaders headers) {
			List<String> values = headers.get(this.name);
			if (values != null) {
				values.remove(this.value);
				if (values.isEmpty()) {
					headers.remove(this.name);
				}
			}
		}

	}

	private static final class RemoveHeadersByNamePatternModification implements Modification {

		private final Pattern namePattern;

		private RemoveHeadersByNamePatternModification(Pattern namePattern) {
			this.namePattern = namePattern;
		}

		@Override
		public void applyTo(HttpHeaders headers) {
			headers.headerNames().removeIf((name) -> this.namePattern.matcher(name).matches());
		}

	}

}
