/*
 * Copyright 2014-2024 the original author or authors.
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link GroupConstraintDescriptions}.
 *
 * @author Dmytro Nosan
 */
public class GroupConstraintDescriptionsTests {

	private final ConstraintResolver constraintResolver = mock(ConstraintResolver.class);

	private final ConstraintDescriptionResolver constraintDescriptionResolver = mock(
			ConstraintDescriptionResolver.class);

	private final GroupConstraintDescriptions constraintDescriptions = new GroupConstraintDescriptions(
			Constrained.class, this.constraintResolver, this.constraintDescriptionResolver);

	@Test
	public void descriptionsForConstraints() {
		Constraint alpha = new Constraint("alpha", Collections.emptyMap(), Set.of(Cloneable.class));
		Constraint bravo = new Constraint("bravo", Collections.emptyMap());
		Constraint delta = new Constraint("delta", Collections.emptyMap(), Set.of(Cloneable.class, Serializable.class));

		given(this.constraintResolver.resolveForProperty("foo", Constrained.class))
			.willReturn(Arrays.asList(alpha, bravo, delta));
		given(this.constraintDescriptionResolver.resolveDescription(alpha)).willReturn("alpha");
		given(this.constraintDescriptionResolver.resolveDescription(bravo)).willReturn("bravo");
		given(this.constraintDescriptionResolver.resolveDescription(delta)).willReturn("delta");

		assertThat(this.constraintDescriptions.descriptionsForProperty("foo", Cloneable.class)).containsExactly("alpha",
				"delta");
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo", Serializable.class, Cloneable.class))
			.containsExactly("alpha", "delta");
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo", Serializable.class))
			.containsExactly("delta");
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo")).containsExactly("bravo");
	}

	@Test
	public void emptyListOfDescriptionsWhenThereAreNoConstraints() {
		given(this.constraintResolver.resolveForProperty("foo", Constrained.class)).willReturn(Collections.emptyList());
		assertThat(this.constraintDescriptions.descriptionsForProperty("foo").size()).isEqualTo(0);
	}

	private static final class Constrained {

	}

}
