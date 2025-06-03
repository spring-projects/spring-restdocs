/*
 * Copyright 2014-2025 the original author or authors.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ContentTypeLinkExtractor}.
 *
 * @author Andy Wilkinson
 */
class ContentTypeLinkExtractorTests {

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final String halBody = "{ \"_links\" : { \"someRel\" : { \"href\" : \"someHref\" }} }";

	@Test
	void extractionFailsWithNullContentType() {
		assertThatIllegalStateException().isThrownBy(() -> new ContentTypeLinkExtractor()
			.extractLinks(this.responseFactory.create(HttpStatus.OK, new HttpHeaders(), null)));
	}

	@Test
	void extractorCalledWithMatchingContextType() throws IOException {
		Map<MediaType, LinkExtractor> extractors = new HashMap<>();
		LinkExtractor extractor = mock(LinkExtractor.class);
		extractors.put(MediaType.APPLICATION_JSON, extractor);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		OperationResponse response = this.responseFactory.create(HttpStatus.OK, httpHeaders, null);
		new ContentTypeLinkExtractor(extractors).extractLinks(response);
		verify(extractor).extractLinks(response);
	}

	@Test
	void extractorCalledWithCompatibleContextType() throws IOException {
		Map<MediaType, LinkExtractor> extractors = new HashMap<>();
		LinkExtractor extractor = mock(LinkExtractor.class);
		extractors.put(MediaType.APPLICATION_JSON, extractor);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.parseMediaType("application/json;foo=bar"));
		OperationResponse response = this.responseFactory.create(HttpStatus.OK, httpHeaders, null);
		new ContentTypeLinkExtractor(extractors).extractLinks(response);
		verify(extractor).extractLinks(response);
	}

	@Test
	void extractsLinksFromVndHalMediaType() throws IOException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.parseMediaType("application/vnd.hal+json"));
		OperationResponse response = this.responseFactory.create(HttpStatus.OK, httpHeaders, this.halBody.getBytes());
		Map<String, List<Link>> links = new ContentTypeLinkExtractor().extractLinks(response);
		assertThat(links).containsKey("someRel");
	}

	@Test
	void extractsLinksFromHalFormsMediaType() throws IOException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.parseMediaType("application/prs.hal-forms+json"));
		OperationResponse response = this.responseFactory.create(HttpStatus.OK, httpHeaders, this.halBody.getBytes());
		Map<String, List<Link>> links = new ContentTypeLinkExtractor().extractLinks(response);
		assertThat(links).containsKey("someRel");
	}

}
