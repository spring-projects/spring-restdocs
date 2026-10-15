/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PatternReplacingContentModifier}.
 *
 * @author Andy Wilkinson
 */
class PatternReplacingContentModifierTests {

	@Test
	void patternsAreReplaced() {
		Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
				Pattern.CASE_INSENSITIVE);
		PatternReplacingContentModifier contentModifier = new PatternReplacingContentModifier(pattern, "<<uuid>>");
		assertThat(
				contentModifier.modifyContent("{\"id\" : \"CA761232-ED42-11CE-BACD-00AA0057B223\"}".getBytes(), null))
			.isEqualTo("{\"id\" : \"<<uuid>>\"}".getBytes());
	}

	@Test
	void contentThatDoesNotMatchIsUnchanged() {
		Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
				Pattern.CASE_INSENSITIVE);
		PatternReplacingContentModifier contentModifier = new PatternReplacingContentModifier(pattern, "<<uuid>>");
		assertThat(contentModifier.modifyContent("{\"id\" : \"CA76-ED42-11CE-BACD\"}".getBytes(), null))
			.isEqualTo("{\"id\" : \"CA76-ED42-11CE-BACD\"}".getBytes());
	}

	@Test
	void encodingIsPreservedUsingCharsetFromContentType() {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		Pattern pattern = Pattern.compile("[0-9]+");
		PatternReplacingContentModifier contentModifier = new PatternReplacingContentModifier(pattern, "<<number>>");
		assertThat(contentModifier.modifyContent((japaneseContent + " 123").getBytes(),
				new MediaType("text", "plain", Charset.forName("UTF-8"))))
			.isEqualTo((japaneseContent + " <<number>>").getBytes());
	}

	@Test
	void encodingIsPreservedUsingFallbackCharset() {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		Pattern pattern = Pattern.compile("[0-9]+");
		PatternReplacingContentModifier contentModifier = new PatternReplacingContentModifier(pattern, "<<number>>",
				StandardCharsets.UTF_8);
		assertThat(contentModifier.modifyContent((japaneseContent + " 123").getBytes(), new MediaType("text", "plain")))
			.isEqualTo((japaneseContent + " <<number>>").getBytes());
	}

}
