package org.springframework.restdocs;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for descriptors. Provides the ability to associate arbitrary attributes with
 * a descriptor.
 * 
 * @author Andy Wilkinson
 *
 * @param <T> the type of the descriptor
 */
public abstract class AbstractDescriptor<T extends AbstractDescriptor<T>> {

	private final Map<String, Object> attributes = new HashMap<>();

	/**
	 * Sets an attribute with the given {@code name} and {@code value}.
	 * 
	 * @param name The name of the attribute
	 * @param value The value of the attribute
	 */
	@SuppressWarnings("unchecked")
	public T attribute(String name, Object value) {
		this.attributes.put(name, value);
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
