/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.testfixtures.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;

/**
 * Signals that a method is a template for a test that renders a snippet. The test will be
 * executed once for each of the two supported snippet formats (Asciidoctor and Markdown).
 * <p>
 * A rendered snippet test method can inject the following types:
 * <ul>
 * <li>{@link OperationBuilder}</li>
 * <li>{@link AssertableSnippets}</li>
 * </ul>
 *
 * @author Andy Wilkinson
 */
@TestTemplate
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RenderedSnippetTestExtension.class)
public @interface RenderedSnippetTest {

	/**
	 * The snippet formats to render.
	 * @return the formats
	 */
	Format[] format() default { Format.ASCIIDOCTOR, Format.MARKDOWN };

	enum Format {

		/**
		 * Asciidoctor snippet format.
		 */
		ASCIIDOCTOR(TemplateFormats.asciidoctor()),

		/**
		 * Markdown snippet format.
		 */
		MARKDOWN(TemplateFormats.markdown());

		private final TemplateFormat templateFormat;

		Format(TemplateFormat templateFormat) {
			this.templateFormat = templateFormat;
		}

		TemplateFormat templateFormat() {
			return this.templateFormat;
		}

	}

}
