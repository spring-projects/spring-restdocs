package com.example

import grails.core.GrailsApplication
import grails.util.Environment

class IndexController {

    GrailsApplication grailsApplication

    def index() {
        render(contentType: 'application/json') {
            message = "Welcome to Grails!"
            environment = Environment.current.name
            appversion = grailsApplication.metadata['info.app.version']
            grailsversion = grailsApplication.metadata['info.app.grailsVersion']
            appprofile = grailsApplication.config.grails?.profile
            groovyversion = GroovySystem.getVersion()
            jvmversion = System.getProperty('java.version')
            controllers = array {
                for (c in grailsApplication.controllerClasses) {
                    controller([name: c.fullName])
                }
            }
            plugins = array {
                for (p in grailsApplication.mainContext.pluginManager.allPlugins) {
                    plugin([name: p.fullName])
                }
            }
        }
    }
}
