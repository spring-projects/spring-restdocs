class UrlMappings {

    static mappings = {
        "/"(controller: 'index')
        "500"(controller: 'InternalServerError')
        "404"(controller: 'NotFound')
    }
}
