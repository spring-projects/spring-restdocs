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

package org.springframework.restdocs.constraints;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ConstraintDescriptions}.
 *
 * @author Andy Wilkinson
 */
class ConstraintDescriptionsTests {

	private final ConstraintResolver constraintResolver = mock(ConstraintResolver.class);

	private final ConstraintDescriptionResolver constraintDescriptionResolver = mock(
			ConstraintDescriptionResolver.class);

	private final ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(Constrained.class,
			this.constraintResolver, this.constraintDescriptionResolver);

	@Test
	void descriptionsForConstraints() {
		Constraint constraint1 = new Constraint("constraint1", Collections.<String, Object>emptyMap());
		Constraint constraint2 = new Constraint("constraint2", Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForProperty("foo", Constrained.class))
			.willReturn(Arrays.asList(constraint1, constraint2));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1)).willReturn("Bravo");
		given(this.constraintDescriptionResolver.resolveDescription(constraint2)).willReturn("Alpha");
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo")).containsExactly("Alpha", "Bravo");
	}

	@Test
	void emptyListOfDescriptionsWhenThereAreNoConstraints() {
		given(this.constraintResolver.resolveForProperty("foo", Constrained.class))
			.willReturn(Collections.<Constraint>emptyList());
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo").size()).isEqualTo(0);
	}

	@Test
	void descriptionsForMethodParameterConstraints() throws NoSuchMethodException {
		Method method = Constrained.class.getDeclaredMethod("foo", String.class);
		Constraint constraint1 = new Constraint("constraint1", Collections.<String, Object>emptyMap());
		Constraint constraint2 = new Constraint("constraint2", Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForMethodParameter(method, 0))
			.willReturn(Arrays.asList(constraint1, constraint2));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1)).willReturn("Bravo");
		given(this.constraintDescriptionResolver.resolveDescription(constraint2)).willReturn("Alpha");
		assertThat(this.constraintDescriptions.descriptionsForMethodParameter(method, 0)).containsExactly("Alpha",
				"Bravo");
	}

	@Test
	void descriptionsForMethodParameterConstraintsUsingName() throws NoSuchMethodException {
		Method method = Constrained.class.getDeclaredMethod("foo", String.class);
		Constraint constraint1 = new Constraint("constraint1", Collections.<String, Object>emptyMap());
		Constraint constraint2 = new Constraint("constraint2", Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForMethodParameter(method, 0))
			.willReturn(Arrays.asList(constraint1, constraint2));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1)).willReturn("Bravo");
		given(this.constraintDescriptionResolver.resolveDescription(constraint2)).willReturn("Alpha");
		assertThat(this.constraintDescriptions.descriptionsForMethodParameter("foo", 0, String.class))
			.containsExactly("Alpha", "Bravo");
	}

	@Test
	void descriptionsForMethodParameterConstraintsWithNoTypesUsingName() throws NoSuchMethodException {
		Method method = Constrained.class.getDeclaredMethod("bar");
		Constraint constraint1 = new Constraint("constraint1", Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForMethodParameter(method, 0)).willReturn(Arrays.asList(constraint1));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1)).willReturn("Alpha");
		assertThat(this.constraintDescriptions.descriptionsForMethodParameter("bar", 0)).containsExactly("Alpha");
	}

	@Test
	void descriptionsForInheritedMethodParameterConstraintsUsingName() throws NoSuchMethodException {
		Method method = Constrained.class.getDeclaredMethod("foo", String.class);
		Constraint constraint1 = new Constraint("constraint1", Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForMethodParameter(method, 0)).willReturn(Arrays.asList(constraint1));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1)).willReturn("Alpha");

		ConstraintDescriptions subclassDescriptions = new ConstraintDescriptions(SubclassConstrained.class,
				this.constraintResolver, this.constraintDescriptionResolver);
		assertThat(subclassDescriptions.descriptionsForMethodParameter("foo", 0, String.class))
			.containsExactly("Alpha");
	}

	@Test
	void descriptionsForNonExistentMethodParameter() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> this.constraintDescriptions.descriptionsForMethodParameter("baz", 0));
	}

	private static class Constrained {

		void foo(String foo) {
		}

		void bar() {
		}

	}

	private static final class SubclassConstrained extends Constrained {

	}

}
