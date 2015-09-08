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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.snippet.ModelCreationException;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

/**
 * A {@link Snippet} that documents a RESTful resource's links.
 *
 * @author Andy Wilkinson
 * @see HypermediaDocumentation#links(LinkDescriptor...)
 * @see HypermediaDocumentation#links(LinkExtractor, LinkDescriptor...)
 * @see HypermediaDocumentation#links(Map, LinkDescriptor...)
 * @see HypermediaDocumentation#links(LinkExtractor, Map, LinkDescriptor...)
 */
public class LinksSnippet extends TemplatedSnippet {

	private final Map<String, LinkDescriptor> descriptorsByRel = new LinkedHashMap<>();

	private final LinkExtractor linkExtractor;

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}.
	 * 
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 */
	protected LinksSnippet(LinkExtractor linkExtractor, List<LinkDescriptor> descriptors) {
		this(linkExtractor, descriptors, null);
	}

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}. The
	 * given {@code attributes} will be included in the model during template rendering.
	 * 
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 * @param attributes the additional attributes
	 */
	protected LinksSnippet(LinkExtractor linkExtractor, List<LinkDescriptor> descriptors,
			Map<String, Object> attributes) {
		super("links", attributes);
		this.linkExtractor = linkExtractor;
		for (LinkDescriptor descriptor : descriptors) {
			Assert.hasText(descriptor.getRel());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByRel.put(descriptor.getRel(), descriptor);
		}
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		OperationResponse response = operation.getResponse();
		try {
			validate(this.linkExtractor.extractLinks(response));
		}
		catch (IOException ex) {
			throw new ModelCreationException(ex);
		}
		Map<String, Object> model = new HashMap<>();
		model.put("links", createLinksModel());
		return model;
	}

	private void validate(Map<String, List<Link>> links) {
		Set<String> actualRels = links.keySet();

		Set<String> undocumentedRels = new HashSet<String>(actualRels);
		undocumentedRels.removeAll(this.descriptorsByRel.keySet());

		Set<String> requiredRels = new HashSet<>();
		for (Entry<String, LinkDescriptor> relAndDescriptor : this.descriptorsByRel
				.entrySet()) {
			if (!relAndDescriptor.getValue().isOptional()) {
				requiredRels.add(relAndDescriptor.getKey());
			}
		}

		Set<String> missingRels = new HashSet<String>(requiredRels);
		missingRels.removeAll(actualRels);

		if (!undocumentedRels.isEmpty() || !missingRels.isEmpty()) {
			String message = "";
			if (!undocumentedRels.isEmpty()) {
				message += "Links with the following relations were not documented: "
						+ undocumentedRels;
			}
			if (!missingRels.isEmpty()) {
				if (message.length() > 0) {
					message += ". ";
				}
				message += "Links with the following relations were not found in the "
						+ "response: " + missingRels;
			}
			throw new SnippetException(message);
		}
	}

	private List<Map<String, Object>> createLinksModel() {
		List<Map<String, Object>> model = new ArrayList<>();
		for (Entry<String, LinkDescriptor> entry : this.descriptorsByRel.entrySet()) {
			model.add(entry.getValue().toModel());
		}
		return model;
	}

	/**
	 * Returns a {@code Map} of {@link LinkDescriptor LinkDescriptors} keyed by their
	 * {@link LinkDescriptor#getRel() rels}.
	 * 
	 * @return the link descriptors
	 */
	protected final Map<String, LinkDescriptor> getDescriptorsByRel() {
		return this.descriptorsByRel;
	}

}