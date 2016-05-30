package com.example

import grails.rest.Resource

@Resource(uri='/notes', formats = ['json', 'xml'])
class Note {
    Long id
    String title
    String body

    static hasMany = [tags: Tag]
    static mapping = {
        tags joinTable: [name: "mm_notes_tags", key: 'mm_note_id' ]
    }
}

