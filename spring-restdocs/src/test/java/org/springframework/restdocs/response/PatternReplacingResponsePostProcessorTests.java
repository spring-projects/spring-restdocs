/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Tests for {@link PatternReplacingResponsePostProcessor}.
 * 
 * @author Dewet Diener
 */
public class PatternReplacingResponsePostProcessorTests {

	@Test
	public void patternsAreReplaced() throws Exception {
		Pattern pattern = Pattern.compile(
				"([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})",
				Pattern.CASE_INSENSITIVE);
		PatternReplacingResponsePostProcessor postProcessor = new PatternReplacingResponsePostProcessor(
				pattern, "<<uuid>>");
		assertThat(
				postProcessor
						.modifyContent("{\"id\" : \"CA761232-ED42-11CE-BACD-00AA0057B223\"}"),
				is(equalTo("{\"id\" : \"<<uuid>>\"}")));
	}

}
