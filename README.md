# Spring REST Docs [![Build status][1]][2] [![Revved up by Develocity][23]][24]

## Overview

The primary goal of this project is to make it easy to document RESTful services by combining content that's been hand-written using [Asciidoctor][3] with auto-generated examples produced with the [Spring MVC Test][4] framework.
The result is intended to be an easy-to-read user guide, akin to [GitHub's API documentation][5] for example, rather than the fully automated, dense API documentation produced by tools like [Swagger][6].

For a broader introduction see the Documenting RESTful APIs presentation.
Both the [slides][7] and a [video recording][8] are available.

## Learning more

To learn more about Spring REST Docs, please consult the [reference documentation][9].

## Building from source

You will need Java 17 or later to build Spring REST Docs.
It is built using [Gradle][10]:

```
./gradlew build
```

## Contributing

Contributors to this project agree to uphold its [code of conduct][11].

There are several that you can contribute to Spring REST Docs:

 - Open a [pull request][12]. Please see the [contributor guidelines][13] for details
 - Ask and answer questions on Stack Overflow using the [`spring-restdocs`][15] tag

## Third-party extensions

| Name | Description |
| ---- | ----------- |
| [restdocs-wiremock][16] | Auto-generate WireMock stubs as part of documenting your RESTful API |
| [restdocsext-jersey][17] | Enables Spring REST Docs to be used with [Jersey's test framework][18] |
| [spring-auto-restdocs][19] | Uses introspection and Javadoc to automatically document request and response parameters |
| [restdocs-api-spec][20] | A Spring REST Docs extension that adds API specification support. It currently supports [OpenAPI 2][21] and [OpenAPI 3][22] |

## Licence

Spring REST Docs is open source software released under the [Apache 2.0 license][14].

[1]: https://github.com/spring-projects/spring-restdocs/actions/workflows/build-and-deploy-snapshot.yml/badge.svg?branch=main (Build status)
[2]: https://github.com/spring-projects/spring-restdocs/actions/workflows/build-and-deploy-snapshot.yml
[3]: https://asciidoctor.org
[4]: https://docs.spring.io/spring-framework/docs/4.1.x/spring-framework-reference/htmlsingle/#spring-mvc-test-framework
[5]: https://developer.github.com/v3/
[6]: https://swagger.io
[7]: https://speakerdeck.com/ankinson/documenting-restful-apis-webinar
[8]: https://www.youtube.com/watch?v=knH5ihPNiUs&feature=youtu.be
[9]: https://docs.spring.io/spring-restdocs/docs/
[10]: https://gradle.org
[11]: CODE_OF_CONDUCT.md
[12]: https://help.github.com/articles/using-pull-requests/
[13]: CONTRIBUTING.md
[14]: https://www.apache.org/licenses/LICENSE-2.0.html
[15]: https://stackoverflow.com/tags/spring-restdocs
[16]: https://github.com/ePages-de/restdocs-wiremock
[17]: https://github.com/RESTDocsEXT/restdocsext-jersey
[18]: https://jersey.java.net/documentation/latest/test-framework.html
[19]: https://github.com/ScaCap/spring-auto-restdocs
[20]: https://github.com/ePages-de/restdocs-api-spec
[21]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md
[22]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md
[23]: https://img.shields.io/badge/Revved%20up%20by-Develocity-06A0CE?logo=Gradle&labelColor=02303A
[24]: https://ge.spring.io/scans?search.rootProjectNames=spring-restdocs
