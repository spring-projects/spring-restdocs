# grails-spring-restdocs-example

## Overview

This is a sample project using Grails 3, Spock, and Spring REST docs.  For more information about the Grails framework 
see [grails.org](http://grails.org).

Grails is built on top of Spring Boot and Gradle so there are many different ways to run this project including:

### Gradle Command Line

Clean the project:

```
$ gradle clean
```

Build the project:

```
$ gradle build
```

Run the project:

```
$ gradle run
```

*Please note*, if you are including integration tests in Grails, they will not run as part of the `gradle test` task. 
Run them via the build task or individually as `gradle integrationTest`

### Gradle Wrapper (suggested)

The gradle wrapper allows a project to build without having gradle installed locally. The executable file will acquire 
the version of gradle and other dependencies recommended for this project.  This is especially important since some 
versions of gradle may cause conflicts with this project.

on Unix-like platforms such as Linux and Mac OS X:

```
$ ./gradlew run
```

on windows:

```
$ gradlew run
```

### Grails Command Line

Grails applications also have a command line feature useful for code generation and running projects locally. The 
command line is accessible by typing `grails` in the terminal at the root of the project.  Please ensure you are running 
the correct version of grails as specified in [gradle.properties](gradle.properties)

Similar to gradle clean, this task destroys the gradle build directory and cached assets.

```
grails> clean
```

The grails 'test-app' task runs all of the tests for the project.

```
grails> test-app
```

The `run-app` task is used to run the application locally.  By default, the project is run in development mode including 
automatic reloading and not caching static assets. It is not suggested to use this in production.

```
grails> run-app
```

### Building and Viewing the Docs

This is an example of the Grails API profile.  Therefore, there is no view layer to return the docs as static assets. 
The result of running asciidoctor or build is that the docs are sent to `/build/asciidoc/`.  You can then publish them 
to a destination of your choosing. I suggest publishing the docs using the 
[gradle github-pages](https://github.com/ajoberstar/gradle-git) plugin.

To just generate documentation and not run the application use:

```$ ./gradlew asciidoctor```
