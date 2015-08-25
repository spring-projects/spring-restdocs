package org.springframework.restdocs.constraints;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;

/**
 * Tests for {@link ValidatorConstraintResolver}
 * 
 * @author Andy Wilkinson
 */
public class ValidatorConstraintResolverTests {

	private final ValidatorConstraintResolver resolver = new ValidatorConstraintResolver();

	@Test
	public void singleFieldConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("single",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(1));
		assertThat(constraints.get(0).getName(), is(NotNull.class.getName()));
	}

	@Test
	public void multipleFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("multiple",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(2));
		assertThat(constraints.get(0).getName(), is(NotNull.class.getName()));
		assertThat(constraints.get(1).getName(), is(Size.class.getName()));
		assertThat(constraints.get(1).getConfiguration().get("min"), is((Object) 8));
		assertThat(constraints.get(1).getConfiguration().get("max"), is((Object) 16));
	}

	@Test
	public void noFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("none",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(0));
	}

	@Test
	public void compositeConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("composite",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(1));
	}

	private static class ConstrainedFields {

		@NotNull
		private String single;

		@NotNull
		@Size(min = 8, max = 16)
		private String multiple;

		@SuppressWarnings("unused")
		private String none;

		@CompositeConstraint
		private String composite;
	}

	@ConstraintComposition(CompositionType.OR)
	@Null
	@NotBlank
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@javax.validation.Constraint(validatedBy = {})
	public @interface CompositeConstraint {

		String message() default "Must be null or not blank";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

	}

}
