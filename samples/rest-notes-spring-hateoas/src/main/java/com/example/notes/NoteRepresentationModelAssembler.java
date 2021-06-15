/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.notes;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.example.notes.NoteRepresentationModelAssembler.NoteModel;

@Component
class NoteRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Note, NoteModel> {

	NoteRepresentationModelAssembler() {
		super(NotesController.class, NoteModel.class);
	}

	@Override
	public NoteModel toModel(Note entity) {
		NoteModel noteModel = createModelWithId(entity.getId(), entity);
		noteModel.add(linkTo(methodOn(NotesController.class).noteTags(entity.getId())).withRel("note-tags"));
		return noteModel;
	}
	
	@Override
	protected NoteModel instantiateModel(Note entity) {
		return new NoteModel(entity);
	}

	@Relation(collectionRelation = "notes", itemRelation = "note")
	static class NoteModel extends RepresentationModel<NoteModel> {
		
		private final Note note;

		NoteModel(Note note) {
			this.note = note;
		}
		
		public String getTitle() {
			return this.note.getTitle();
		}
		
		public String getBody() {
			return this.note.getBody();
		}
		
	}

}
