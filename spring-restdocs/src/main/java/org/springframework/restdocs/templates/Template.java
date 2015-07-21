package org.springframework.restdocs.templates;

import java.util.Map;

/**
 * A compiled {@code Template} that can be rendered to a {@link String}.
 * 
 * @author Andy Wilkinson
 *
 */
public interface Template {

	/**
	 * Renders the template to a {@link String} using the given {@code context} for
	 * variable/property resolution.
	 * 
	 * @param context The context to use
	 * @return The rendered template
	 */
	String render(Map<String, Object> context);

}
