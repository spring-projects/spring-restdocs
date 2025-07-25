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

package org.springframework.restdocs.hypermedia;

import java.io.IOException;

import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Stub implementation of {@code LinkExtractor} for testing.
 *
 * @author Andy Wilkinson
 */
class StubLinkExtractor implements LinkExtractor {

	private MultiValueMap<String, Link> linksByRel = new LinkedMultiValueMap<>();

	@Override
	public MultiValueMap<String, Link> extractLinks(OperationResponse response) throws IOException {
		return this.linksByRel;
	}

	StubLinkExtractor withLinks(Link... links) {
		for (Link link : links) {
			this.linksByRel.add(link.getRel(), link);
		}
		return this;
	}

}
