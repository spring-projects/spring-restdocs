/*
 * Copyright 2014-2016 the original author or authors.
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

package com.example

import grails.rest.Resource

@Resource(uri='/tags')
class Tag {

	Long id

	String name

	static hasMany = [notes: Note]

	static belongsTo = Note

	static mapping = {
		notes joinTable: [name: "mm_notes_tags", key: 'mm_tag_id']
	}

}
