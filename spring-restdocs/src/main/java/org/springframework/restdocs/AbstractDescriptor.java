package org.springframework.restdocs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.Attributes.Attribute;

/**
 * Base class for descriptors. Provides the ability to associate arbitrary attributes with
 * a descriptor.
 * 
 * @author Andy Wilkinson
 *
 * @param <T> the type of the descriptor
 */
public abstract class AbstractDescriptor<T extends AbstractDescriptor<T>> {

	private Map<String, Object> attributes = new HashMap<>();

	/**
	 * Sets the descriptor's attributes
	 * 
	 * @param attributes the attributes
	 * @return the descriptor
	 */
	@SuppressWarnings("unchecked")
	public T attributes(Attribute... attributes) {
		for (Attribute attribute : attributes) {
			this.attributes.put(attribute.getKey(), attribute.getValue());
		}
		return (T) this;
	}

	/**
	 * Returns the descriptor's attributes
	 * 
	 * @return the attributes
	 */
	protected Map<String, Object> getAttributes() {
		return this.attributes;
	}

}
