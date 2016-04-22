/*
 * Copyright 2014-2016 the original author or authors.
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
import java.util.Arrays;
import java.util.Collections;
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

	private final boolean ignoreUndocumentedLinks;

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}.
	 * Undocumented links will trigger a failure.
	 *
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 */
	protected LinksSnippet(LinkExtractor linkExtractor,
			List<LinkDescriptor> descriptors) {
		this(linkExtractor, descriptors, null, false);
	}

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}. If
	 * {@code ignoreUndocumentedLinks} is {@code true}, undocumented links will be ignored
	 * and will not trigger a failure.
	 *
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 * @param ignoreUndocumentedLinks whether undocumented links should be ignored
	 */
	protected LinksSnippet(LinkExtractor linkExtractor, List<LinkDescriptor> descriptors,
			boolean ignoreUndocumentedLinks) {
		this(linkExtractor, descriptors, null, ignoreUndocumentedLinks);
	}

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}. The
	 * given {@code attributes} will be included in the model during template rendering.
	 * Undocumented links will trigger a failure.
	 *
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 * @param attributes the additional attributes
	 */
	protected LinksSnippet(LinkExtractor linkExtractor, List<LinkDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(linkExtractor, descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code LinksSnippet} that will extract links using the given
	 * {@code linkExtractor} and document them using the given {@code descriptors}. The
	 * given {@code attributes} will be included in the model during template rendering.
	 * If {@code ignoreUndocumentedLinks} is {@code true}, undocumented links will be
	 * ignored and will not trigger a failure.
	 *
	 * @param linkExtractor the link extractor
	 * @param descriptors the link descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedLinks whether undocumented links should be ignored
	 */
	protected LinksSnippet(LinkExtractor linkExtractor, List<LinkDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedLinks) {
		super("links", attributes);
		this.linkExtractor = linkExtractor;
		for (LinkDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getRel(), "Link descriptors must have a rel");
			this.descriptorsByRel.put(descriptor.getRel(), descriptor);
		}
		this.ignoreUndocumentedLinks = ignoreUndocumentedLinks;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		OperationResponse response = operation.getResponse();
		Map<String, List<Link>> links;
		try {
			links = this.linkExtractor.extractLinks(response);
			validate(links);
		}
		catch (IOException ex) {
			throw new ModelCreationException(ex);
		}
		Map<String, Object> model = new HashMap<>();
		model.put("links", createLinksModel(links));
		return model;
	}

	private void validate(Map<String, List<Link>> links) {
		Set<String> actualRels = links.keySet();

		Set<String> undocumentedRels;
		if (this.ignoreUndocumentedLinks) {
			undocumentedRels = Collections.emptySet();
		}
		else {
			undocumentedRels = new HashSet<>(actualRels);
			undocumentedRels.removeAll(this.descriptorsByRel.keySet());
		}

		Set<String> requiredRels = new HashSet<>();
		for (Entry<String, LinkDescriptor> relAndDescriptor : this.descriptorsByRel
				.entrySet()) {
			if (!relAndDescriptor.getValue().isOptional()) {
				requiredRels.add(relAndDescriptor.getKey());
			}
		}

		Set<String> missingRels = new HashSet<>(requiredRels);
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

	private List<Map<String, Object>> createLinksModel(Map<String, List<Link>> links) {
		List<Map<String, Object>> model = new ArrayList<>();
		for (Entry<String, LinkDescriptor> entry : this.descriptorsByRel.entrySet()) {
			LinkDescriptor descriptor = entry.getValue();
			if (!descriptor.isIgnored()) {
				if (descriptor.getDescription() == null) {
					descriptor = createDescriptor(
							getDescriptionFromLinkTitle(links, descriptor.getRel()),
							descriptor);
				}
				model.add(createModelForDescriptor(descriptor));
			}
		}
		return model;
	}

	private String getDescriptionFromLinkTitle(Map<String, List<Link>> links,
			String rel) {
		List<Link> linksForRel = links.get(rel);
		if (linksForRel != null) {
			for (Link link : linksForRel) {
				if (link.getTitle() != null) {
					return link.getTitle();
				}
			}
		}
		throw new SnippetException("No description was provided for the link with rel '"
				+ rel + "' and no title was available from the link in the payload");
	}

	private LinkDescriptor createDescriptor(String description, LinkDescriptor source) {
		LinkDescriptor newDescriptor = new LinkDescriptor(source.getRel())
				.description(description);
		if (source.isOptional()) {
			newDescriptor.optional();
		}
		if (source.isIgnored()) {
			newDescriptor.ignored();
		}
		return newDescriptor;
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

	/**
	 * Returns a model for the given {@code descriptor}.
	 *
	 * @param descriptor the descriptor
	 * @return the model
	 */
	protected Map<String, Object> createModelForDescriptor(LinkDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("rel", descriptor.getRel());
		model.put("description", descriptor.getDescription());
		model.put("optional", descriptor.isOptional());
		model.putAll(descriptor.getAttributes());
		return model;
	}

	/**
	 * Returns a new {@code RequestHeadersSnippet} configured with this snippet's link
	 * extractor and attributes, and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public LinksSnippet and(LinkDescriptor... additionalDescriptors) {
		List<LinkDescriptor> combinedDescriptors = new ArrayList<>();
		combinedDescriptors.addAll(this.descriptorsByRel.values());
		combinedDescriptors.addAll(Arrays.asList(additionalDescriptors));
		return new LinksSnippet(this.linkExtractor, combinedDescriptors, getAttributes());
	}

}
