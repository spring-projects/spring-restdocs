/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import java.util.HashMap;
import java.util.Map;

/**
 * A fluent API for building a map of attributes.
 *
 * @author Andy Wilkinson
 */
public abstract class Attributes {

	private Attributes() {

	}

	/**
	 * Creates an attribute with the given {@code key}. A value for the attribute must
	 * still be specified.
	 * @param key the key of the attribute
	 * @return an {@code AttributeBuilder} to use to specify the value of the attribute
	 * @see AttributeBuilder#value(Object)
	 */
	public static AttributeBuilder key(String key) {
		return new AttributeBuilder(key);
	}

	/**
	 * Creates a {@code Map} of the given {@code attributes}.
	 * @param attributes the attributes
	 * @return a Map of the attributes
	 */
	public static Map<String, Object> attributes(Attribute... attributes) {
		Map<String, Object> attributeMap = new HashMap<>();
		for (Attribute attribute : attributes) {
			attributeMap.put(attribute.getKey(), attribute.getValue());
		}
		return attributeMap;
	}

	/**
	 * A simple builder for an attribute (key-value pair).
	 */
	public static final class AttributeBuilder {

		private final String key;

		private AttributeBuilder(String key) {
			this.key = key;
		}

		/**
		 * Configures the value of the attribute.
		 * @param value the attribute's value
		 * @return a newly created {@code Attribute}
		 */
		public Attribute value(Object value) {
			return new Attribute(this.key, value);
		}

	}

	/**
	 * An attribute (key-value pair).
	 */
	public static final class Attribute {

		private final String key;

		private final Object value;

		/**
		 * Creates a new attribute with the given {@code key} and {@code value}.
		 * @param key the key
		 * @param value the value
		 */
		public Attribute(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Returns the attribute's key.
		 * @return the key
		 */
		public String getKey() {
			return this.key;
		}

		/**
		 * Returns the attribute's value.
		 * @return the value
		 */
		public Object getValue() {
			return this.value;
		}

	}

}
