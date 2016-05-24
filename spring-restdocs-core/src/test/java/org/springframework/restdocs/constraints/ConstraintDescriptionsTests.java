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

package org.springframework.restdocs.constraints;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ConstraintDescriptions}.
 *
 * @author Andy Wilkinson
 */
public class ConstraintDescriptionsTests {

	private final ConstraintResolver constraintResolver = mock(ConstraintResolver.class);

	private final ConstraintDescriptionResolver constraintDescriptionResolver = mock(
			ConstraintDescriptionResolver.class);

	private final ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(
			Constrained.class, this.constraintResolver,
			this.constraintDescriptionResolver);

	@Test
	public void descriptionsForConstraints() {
		Constraint constraint1 = new Constraint("constraint1",
				Collections.<String, Object>emptyMap());
		Constraint constraint2 = new Constraint("constraint2",
				Collections.<String, Object>emptyMap());
		given(this.constraintResolver.resolveForProperty("foo", Constrained.class))
				.willReturn(Arrays.asList(constraint1, constraint2));
		given(this.constraintDescriptionResolver.resolveDescription(constraint1))
				.willReturn("Bravo");
		given(this.constraintDescriptionResolver.resolveDescription(constraint2))
				.willReturn("Alpha");
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo"),
				contains("Alpha", "Bravo"));
	}

	@Test
	public void emptyListOfDescriptionsWhenThereAreNoConstraints() {
		given(this.constraintResolver.resolveForProperty("foo", Constrained.class))
				.willReturn(Collections.<Constraint>emptyList());
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo").size(),
				is(equalTo(0)));
	}

	private static class Constrained {

	}

}
