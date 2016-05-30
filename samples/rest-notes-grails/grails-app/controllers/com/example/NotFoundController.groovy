package com.example

class NotFoundController {

    def index() {
        render(contentType: 'application/json') {
            error = 404
            message = "Not Found"
        }
    }
}
