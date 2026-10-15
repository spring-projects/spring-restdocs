/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.hypermedia.ignoringcommonlinks;

import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;

final class Hypermedia {

	// @fold: on
	private Hypermedia() {

	}
	// @fold: off

	static LinksSnippet ignoreSelfAndCuries(LinkDescriptor... descriptors) {
		return HypermediaDocumentation.links(linkWithRel("self").ignored().optional(), linkWithRel("curies").ignored())
			.and(descriptors);
	}

}
