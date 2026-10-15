/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.OperationResponse;

/**
 * A {@code LinkExtractor} is used to extract {@link Link links} from a JSON response. The
 * expected format of the links in the response is determined by the implementation.
 *
 * @author Andy Wilkinson
 *
 */
public interface LinkExtractor {

	/**
	 * Extract the links from the given {@code response}, returning a {@code Map} of links
	 * where the keys are the link rels.
	 * @param response the response from which the links are to be extracted
	 * @return the extracted links, keyed by rel
	 * @throws IOException if link extraction fails
	 */
	Map<String, List<Link>> extractLinks(OperationResponse response) throws IOException;

}
