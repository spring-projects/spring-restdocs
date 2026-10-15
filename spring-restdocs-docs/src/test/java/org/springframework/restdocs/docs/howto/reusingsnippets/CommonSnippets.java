/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.reusingsnippets;

import org.springframework.restdocs.hypermedia.LinksSnippet;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;

public final class CommonSnippets {

	// @fold:on
	private CommonSnippets() {

	}
	// @fold:off

	public static final LinksSnippet pagingLinks = links(
			linkWithRel("first").optional().description("The first page of results"),
			linkWithRel("last").optional().description("The last page of results"),
			linkWithRel("next").optional().description("The next page of results"),
			linkWithRel("prev").optional().description("The previous page of results"));

}
