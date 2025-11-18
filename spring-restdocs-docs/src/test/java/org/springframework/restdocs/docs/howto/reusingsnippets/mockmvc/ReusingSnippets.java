/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.docs.howto.reusingsnippets.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.docs.howto.reusingsnippets.CommonSnippets;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReusingSnippets {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("example", CommonSnippets.pagingLinks.and(// <1>
					linkWithRel("alpha").description("Link to the alpha resource"),
					linkWithRel("bravo").description("Link to the bravo resource"))));
	}

}
