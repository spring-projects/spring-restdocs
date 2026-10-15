/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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

	private static final class Constrained {

	}

}
