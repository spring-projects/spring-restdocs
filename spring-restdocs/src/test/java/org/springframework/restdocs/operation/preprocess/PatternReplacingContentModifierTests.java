package org.springframework.restdocs.operation.preprocess;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Tests for {@link PatternReplacingContentModifier}
 * 
 * @author Andy Wilkinson
 *
 */
public class PatternReplacingContentModifierTests {

	@Test
	public void patternsAreReplaced() throws Exception {
		Pattern pattern = Pattern.compile(
				"([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})",
				Pattern.CASE_INSENSITIVE);
		PatternReplacingContentModifier contentModifier = new PatternReplacingContentModifier(
				pattern, "<<uuid>>");
		assertThat(
				contentModifier.modifyContent("{\"id\" : \"CA761232-ED42-11CE-BACD-00AA0057B223\"}"
						.getBytes()), is(equalTo("{\"id\" : \"<<uuid>>\"}".getBytes())));
	}

}
