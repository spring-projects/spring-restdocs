/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.payload.JsonFieldPath.PathType;

/**
 * A {@code JsonFieldProcessor} processes a payload's fields, allowing them to be
 * extracted and removed.
 *
 * @author Andy Wilkinson
 *
 */
final class JsonFieldProcessor {

	boolean hasField(String path, Object payload) {
		HasFieldMatchCallback callback = new HasFieldMatchCallback();
		traverse(new ProcessingContext(payload, JsonFieldPath.compile(path)), callback);
		return callback.fieldFound();
	}

	ExtractedField extract(String path, Object payload) {
		JsonFieldPath compiledPath = JsonFieldPath.compile(path);
		final List<Object> matches = new ArrayList<>();
		traverse(new ProcessingContext(payload, compiledPath), new MatchCallback() {

			@Override
			public void foundMatch(Match match) {
				matches.add(match.getValue());
			}

		});
		if (matches.isEmpty()) {
			throw new FieldDoesNotExistException(path);
		}
		return new ExtractedField(
				compiledPath.getType() == PathType.SINGLE ? matches.get(0) : matches,
				compiledPath.getType());
	}

	void remove(String path, Object payload) {
		traverse(new ProcessingContext(payload, JsonFieldPath.compile(path)),
				new MatchCallback() {

					@Override
					public void foundMatch(Match match) {
						match.remove();
					}

				});
	}

	void removeSubsection(String path, Object payload) {
		traverse(new ProcessingContext(payload, JsonFieldPath.compile(path)),
				new MatchCallback() {

					@Override
					public void foundMatch(Match match) {
						match.removeSubsection();
					}

				});
	}

	private void traverse(ProcessingContext context, MatchCallback matchCallback) {
		String segment = context.getSegment();
		if (JsonFieldPath.isArraySegment(segment)) {
			if (context.getPayload() instanceof Collection) {
				handleCollectionPayload(context, matchCallback);
			}
		}
		else if (context.getPayload() instanceof Map) {
			handleMapPayload(context, matchCallback);
		}
	}

	private void handleCollectionPayload(ProcessingContext context,
			MatchCallback matchCallback) {
		handleCollectionPayload((Collection<?>) context.getPayload(), matchCallback,
				context);
	}

	private void handleCollectionPayload(Collection<?> collection,
			MatchCallback matchCallback, ProcessingContext context) {
		if (context.isLeaf()) {
			matchCallback.foundMatch(
					new LeafCollectionMatch(collection, context.getParentMatch()));
		}
		else {
			Iterator<?> items = collection.iterator();
			while (items.hasNext()) {
				Object item = items.next();
				traverse(context.descend(item, new CollectionMatch(items, collection,
						item, context.getParentMatch())), matchCallback);
			}
		}
	}

	private void handleWildcardPayload(Collection<?> collection,
			MatchCallback matchCallback, ProcessingContext context) {
		Iterator<?> items = collection.iterator();
		if (context.isLeaf()) {
			while (items.hasNext()) {
				Object item = items.next();
				matchCallback.foundMatch(new CollectionMatch(items, collection, item,
						context.getParentMatch()));
			}
		}
		else {
			while (items.hasNext()) {
				Object item = items.next();
				traverse(context.descend(item, new CollectionMatch(items, collection,
						item, context.getParentMatch())), matchCallback);
			}
		}
	}

	private void handleMapPayload(ProcessingContext context,
			MatchCallback matchCallback) {
		Map<?, ?> map = context.getPayload();
		if (map.containsKey(context.getSegment())) {
			Object item = map.get(context.getSegment());
			MapMatch mapMatch = new MapMatch(item, map, context.getSegment(),
					context.getParentMatch());
			if (context.isLeaf()) {
				matchCallback.foundMatch(mapMatch);
			}
			else {
				traverse(context.descend(item, mapMatch), matchCallback);
			}
		}
		else if ("*".equals(context.getSegment())) {
			handleWildcardPayload(map.values(), matchCallback, context);
		}
		else {
			matchCallback.absent();
		}
	}

	/**
	 * {@link MatchCallback} use to determine whether a payload has a particular field.
	 */
	private static final class HasFieldMatchCallback implements MatchCallback {

		private MatchType matchType = MatchType.NONE;

		@Override
		public void foundMatch(Match match) {
			this.matchType = this.matchType.combinedWith(
					match.getValue() == null ? MatchType.NULL : MatchType.NON_NULL);
		}

		@Override
		public void absent() {
			this.matchType = this.matchType.combinedWith(MatchType.ABSENT);
		}

		boolean fieldFound() {
			return this.matchType == MatchType.NON_NULL
					|| this.matchType == MatchType.NULL;
		}

		private enum MatchType {

			ABSENT, MIXED, NONE, NULL, NON_NULL;

			MatchType combinedWith(MatchType matchType) {
				if (this == NONE || this == matchType) {
					return matchType;
				}
				return MIXED;
			}

		}

	}

	private static final class MapMatch implements Match {

		private final Object item;

		private final Map<?, ?> map;

		private final String segment;

		private final Match parent;

		private MapMatch(Object item, Map<?, ?> map, String segment, Match parent) {
			this.item = item;
			this.map = map;
			this.segment = segment;
			this.parent = parent;
		}

		@Override
		public Object getValue() {
			return this.item;
		}

		@Override
		public void remove() {
			Object removalCandidate = this.map.get(this.segment);
			if (isMapWithEntries(removalCandidate)
					|| isCollectionWithNonScalarEntries(removalCandidate)) {
				return;
			}
			this.map.remove(this.segment);
			if (this.map.isEmpty() && this.parent != null) {
				this.parent.remove();
			}
		}

		@Override
		public void removeSubsection() {
			this.map.remove(this.segment);
			if (this.map.isEmpty() && this.parent != null) {
				this.parent.removeSubsection();
			}
		}

		private boolean isMapWithEntries(Object object) {
			return object instanceof Map && !((Map<?, ?>) object).isEmpty();
		}

		private boolean isCollectionWithNonScalarEntries(Object object) {
			if (!(object instanceof Collection)) {
				return false;
			}
			for (Object entry : (Collection<?>) object) {
				if (entry instanceof Map || entry instanceof Collection) {
					return true;
				}
			}
			return false;
		}

	}

	private static final class CollectionMatch implements Match {

		private final Iterator<?> items;

		private final Collection<?> collection;

		private final Object item;

		private final Match parent;

		private CollectionMatch(Iterator<?> items, Collection<?> collection, Object item,
				Match parent) {
			this.items = items;
			this.collection = collection;
			this.item = item;
			this.parent = parent;
		}

		@Override
		public Object getValue() {
			return this.item;
		}

		@Override
		public void remove() {
			if (!itemIsEmpty()) {
				return;
			}
			this.items.remove();
			if (this.collection.isEmpty() && this.parent != null) {
				this.parent.remove();
			}
		}

		@Override
		public void removeSubsection() {
			this.items.remove();
			if (this.collection.isEmpty() && this.parent != null) {
				this.parent.removeSubsection();
			}
		}

		private boolean itemIsEmpty() {
			return !isMapWithEntries(this.item) && !isCollectionWithEntries(this.item);
		}

		private boolean isMapWithEntries(Object object) {
			return object instanceof Map && !((Map<?, ?>) object).isEmpty();
		}

		private boolean isCollectionWithEntries(Object object) {
			return object instanceof Collection && !((Collection<?>) object).isEmpty();
		}

	}

	private static final class LeafCollectionMatch implements Match {

		private final Collection<?> collection;

		private final Match parent;

		private LeafCollectionMatch(Collection<?> collection, Match parent) {
			this.collection = collection;
			this.parent = parent;
		}

		@Override
		public Collection<?> getValue() {
			return this.collection;
		}

		@Override
		public void remove() {
			if (containsOnlyScalars(this.collection)) {
				this.collection.clear();
				if (this.parent != null) {
					this.parent.remove();
				}
			}
		}

		@Override
		public void removeSubsection() {
			this.collection.clear();
			if (this.parent != null) {
				this.parent.removeSubsection();
			}
		}

		private boolean containsOnlyScalars(Collection<?> collection) {
			for (Object item : collection) {
				if (item instanceof Collection || item instanceof Map) {
					return false;
				}
			}
			return true;
		}

	}

	private interface MatchCallback {

		void foundMatch(Match match);

		default void absent() {
		}

	}

	private interface Match {

		Object getValue();

		void remove();

		void removeSubsection();

	}

	private static final class ProcessingContext {

		private final Object payload;

		private final List<String> segments;

		private final Match parent;

		private final JsonFieldPath path;

		private ProcessingContext(Object payload, JsonFieldPath path) {
			this(payload, path, null, null);
		}

		private ProcessingContext(Object payload, JsonFieldPath path,
				List<String> segments, Match parent) {
			this.payload = payload;
			this.path = path;
			this.segments = segments == null ? path.getSegments() : segments;
			this.parent = parent;
		}

		private String getSegment() {
			return this.segments.get(0);
		}

		@SuppressWarnings("unchecked")
		private <T> T getPayload() {
			return (T) this.payload;
		}

		private boolean isLeaf() {
			return this.segments.size() == 1;
		}

		private Match getParentMatch() {
			return this.parent;
		}

		private ProcessingContext descend(Object payload, Match match) {
			return new ProcessingContext(payload, this.path,
					this.segments.subList(1, this.segments.size()), match);
		}

	}

	/**
	 * A field that has been extracted from a JSON payload.
	 */
	static class ExtractedField {

		private final Object value;

		private final PathType type;

		ExtractedField(Object value, PathType type) {
			this.value = value;
			this.type = type;
		}

		Object getValue() {
			return this.value;
		}

		PathType getType() {
			return this.type;
		}

	}

}
