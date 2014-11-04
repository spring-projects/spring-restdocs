/*
 * Copyright 2014 the original author or authors.
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

package com.example.notes;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.example.notes.NoteResourceAssembler.NoteResource;

@Component
public class NoteResourceAssembler extends ResourceAssemblerSupport<Note, NoteResource> {

	public NoteResourceAssembler() {
		super(NotesController.class, NoteResource.class);
	}

	@Override
	public NoteResource toResource(Note note) {
		NoteResource resource = createResourceWithId(note.getId(), note);
		resource.add(linkTo(NotesController.class).slash(note.getId()).slash("tags")
				.withRel("note-tags"));
		return resource;
	}

	@Override
	protected NoteResource instantiateResource(Note entity) {
		return new NoteResource(entity);
	}

	static class NoteResource extends Resource<Note> {

		public NoteResource(Note content) {
			super(content);
		}
	}

}
