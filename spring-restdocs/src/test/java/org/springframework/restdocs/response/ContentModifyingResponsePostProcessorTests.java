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

package org.springframework.restdocs.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletResponse;

public class ContentModifyingResponsePostProcessorTests {

	private final MockHttpServletResponse original = new MockHttpServletResponse();

	private final ContentModifyingReponsePostProcessor postProcessor = new TestContentModifyingResponsePostProcessor();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void contentCanBeModified() throws Exception {
		MockHttpServletResponse modified = this.postProcessor.postProcess(this.original);
		assertThat(modified.getContentAsString(), is(equalTo("modified")));
		assertThat(modified.getContentAsByteArray(), is(equalTo("modified".getBytes())));
	}

	@Test
	public void nonContentMethodsAreDelegated() throws Exception {
		this.original.addHeader("a", "alpha");
		MockHttpServletResponse modified = this.postProcessor.postProcess(this.original);
		assertThat(modified.getHeader("a"), is(equalTo("alpha")));
	}

	private static final class TestContentModifyingResponsePostProcessor extends
			ContentModifyingReponsePostProcessor {

		@Override
		protected String modifyContent(String originalContent) throws Exception {
			return "modified";
		}

	}

}
