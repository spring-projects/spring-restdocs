/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates.mustache;

import java.io.IOException;
import java.io.StringWriter;

import com.samskivert.mustache.Template.Fragment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link AsciidoctorTableCellContentLambda}.
 *
 * @author Andy Wilkinson
 */
class AsciidoctorTableCellContentLambdaTests {

	@Test
	void verticalBarCharactersAreEscaped() throws IOException {
		Fragment fragment = mock(Fragment.class);
		given(fragment.execute()).willReturn("|foo|bar|baz|");
		StringWriter writer = new StringWriter();
		new AsciidoctorTableCellContentLambda().execute(fragment, writer);
		assertThat(writer.toString()).isEqualTo("\\|foo\\|bar\\|baz\\|");
	}

	@Test
	void escapedVerticalBarCharactersAreNotEscapedAgain() throws IOException {
		Fragment fragment = mock(Fragment.class);
		given(fragment.execute()).willReturn("\\|foo|bar\\|baz|");
		StringWriter writer = new StringWriter();
		new AsciidoctorTableCellContentLambda().execute(fragment, writer);
		assertThat(writer.toString()).isEqualTo("\\|foo\\|bar\\|baz\\|");
	}

}
