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

package org.springframework.restdocs.hypermedia;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

/**
 * A {@link SnippetWritingResultHandler} that produces a snippet documenting a RESTful
 * resource's links.
 * 
 * @author Andy Wilkinson
 */
public class LinkSnippetResultHandler extends SnippetWritingResultHandler {

	private final Map<String, LinkDescriptor> descriptorsByRel = new HashMap<String, LinkDescriptor>();

	private final LinkExtractor extractor;

	LinkSnippetResultHandler(String outputDir, LinkExtractor linkExtractor,
			List<LinkDescriptor> descriptors) {
		super(outputDir, "links");
		this.extractor = linkExtractor;
		for (LinkDescriptor descriptor : descriptors) {
			Assert.hasText(descriptor.getRel());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByRel.put(descriptor.getRel(), descriptor);
		}
	}

	@Override
	protected void handle(MvcResult result, DocumentationWriter writer) throws Exception {
		Map<String, List<Link>> links;
		if (this.extractor != null) {
			links = this.extractor.extractLinks(result.getResponse());
		}
		else {
			String contentType = result.getResponse().getContentType();
			LinkExtractor extractorForContentType = LinkExtractors
					.extractorForContentType(contentType);
			if (extractorForContentType != null) {
				links = extractorForContentType.extractLinks(result.getResponse());
			}
			else {
				throw new IllegalStateException(
						"No LinkExtractor has been provided and one is not available for the content type "
								+ contentType);
			}

		}

		Set<String> actualRels = links.keySet();
		Set<String> expectedRels = this.descriptorsByRel.keySet();

		Set<String> undocumentedRels = new HashSet<String>(actualRels);
		undocumentedRels.removeAll(expectedRels);

		Set<String> missingRels = new HashSet<String>(expectedRels);
		missingRels.removeAll(actualRels);

		if (!undocumentedRels.isEmpty() || !missingRels.isEmpty()) {
			String message = "";
			if (!undocumentedRels.isEmpty()) {
				message += "Links with the following relations were not documented: "
						+ undocumentedRels;
			}
			if (!missingRels.isEmpty()) {
				message += "Links with the following relations were not found in the response: "
						+ missingRels;
			}
			fail(message);
		}

		Assert.isTrue(actualRels.equals(expectedRels));

		writer.println("|===");
		writer.println("| Relation | Description");

		for (Entry<String, LinkDescriptor> entry : this.descriptorsByRel.entrySet()) {
			writer.println();
			writer.println("| " + entry.getKey());
			writer.println("| " + entry.getValue().getDescription());
		}

		writer.println("|===");
	}

}