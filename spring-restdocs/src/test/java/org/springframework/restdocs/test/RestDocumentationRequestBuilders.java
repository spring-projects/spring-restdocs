package org.springframework.restdocs.test;

import org.springframework.restdocs.config.RestDocumentationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RestDocumentationRequestBuilders {

	private RestDocumentationRequestBuilders() {

	}

	public static MockHttpServletRequestBuilder get(String urlTemplate) {
		return MockMvcRequestBuilders.get(urlTemplate).requestAttr(
				RestDocumentationContext.class.getName(), new RestDocumentationContext());
	}

}
