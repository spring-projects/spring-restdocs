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

import org.springframework.restdocs.payload.JsonFieldType

import static com.jayway.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration

import com.jayway.restassured.builder.RequestSpecBuilder
import com.jayway.restassured.specification.RequestSpecification
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Specification

@Integration
@Rollback
class ApiDocumentationSpec extends Specification {

	@Rule
	JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

	@Value('${local.server.port}')
	Integer serverPort

	protected RequestSpecification documentationSpec

	void setup() {
		this.documentationSpec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(restDocumentation))
				.build()
	}

	void 'test and document get request for /index'() {
		expect:
		given(this.documentationSpec)
				.accept(MediaType.APPLICATION_JSON.toString())
				.filter(document('index-example',
				preprocessRequest(modifyUris()
						.host('api.example.com')
						.removePort()),
				preprocessResponse(prettyPrint()),
				responseFields(
						fieldWithPath('message').description('Welcome to Grails!'),
						fieldWithPath('environment').description("The running environment"),
						fieldWithPath('appversion').description('version of the app that is running'),
						fieldWithPath('grailsversion').description('the version of grails used in this project'),
						fieldWithPath('appprofile').description('the profile of grails used in this project'),
						fieldWithPath('groovyversion').description('the version of groovy used in this project'),
						fieldWithPath('jvmversion').description('the version of the jvm used in this project'),
						subsectionWithPath('controllers').type(JsonFieldType.ARRAY).description('the list of available controllers'),
						subsectionWithPath('plugins').type(JsonFieldType.ARRAY).description('the plugins active for this project'),
				)))
				.when()
				.port(this.serverPort)
				.get('/')
				.then()
				.assertThat()
				.statusCode(is(200))
	}

	void 'test and document notes list request'() {
		expect:
		given(this.documentationSpec)
				.accept(MediaType.APPLICATION_JSON.toString())
				.filter(document('notes-list-example',
				preprocessRequest(modifyUris()
						.host('api.example.com')
						.removePort()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath('[].class').description('the class of the resource'),
					fieldWithPath('[].id').description('the id of the note'),
					fieldWithPath('[].title').description('the title of the note'),
					fieldWithPath('[].body').description('the body of the note'),
					fieldWithPath('[].tags').type(JsonFieldType.ARRAY).description('the list of tags associated with the note'),
				)))
				.when()
				.port(this.serverPort)
				.get('/notes')
				.then()
				.assertThat()
				.statusCode(is(200))
	}

	void 'test and document create new note'() {
		expect:
		given(this.documentationSpec)
				.accept(MediaType.APPLICATION_JSON.toString())
				.contentType(MediaType.APPLICATION_JSON.toString())
				.filter(document('notes-create-example',
				preprocessRequest(modifyUris()
						.host('api.example.com')
						.removePort()),
				preprocessResponse(prettyPrint()),
				requestFields(
						fieldWithPath('title').description('the title of the note'),
						fieldWithPath('body').description('the body of the note'),
						subsectionWithPath('tags').type(JsonFieldType.ARRAY).description('a list of tags associated to the note')
				),
				responseFields(
					fieldWithPath('class').description('the class of the resource'),
					fieldWithPath('id').description('the id of the note'),
					fieldWithPath('title').description('the title of the note'),
					fieldWithPath('body').description('the body of the note'),
					subsectionWithPath('tags').type(JsonFieldType.ARRAY).description('the list of tags associated with the note')
				)))
				.body('{ "body": "My test example", "title": "Eureka!", "tags": [{"name": "testing123"}] }')
				.when()
				.port(this.serverPort)
				.post('/notes')
				.then()
				.assertThat()
				.statusCode(is(201))
	}

	void 'test and document getting specific note'() {
		expect:
		given(this.documentationSpec)
				.accept(MediaType.APPLICATION_JSON.toString())
				.filter(document('note-get-example',
				preprocessRequest(modifyUris()
						.host('api.example.com')
						.removePort()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath('class').description('the class of the resource'),
					fieldWithPath('id').description('the id of the note'),
					fieldWithPath('title').description('the title of the note'),
					fieldWithPath('body').description('the body of the note'),
					fieldWithPath('tags').type(JsonFieldType.ARRAY).description('the list of tags associated with the note'),
				)))
				.when()
				.port(this.serverPort)
				.get('/notes/1')
				.then()
				.assertThat()
				.statusCode(is(200))
	}

}
