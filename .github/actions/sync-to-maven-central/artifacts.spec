{
  "files": [
    {
      "aql": {
        "items.find": {
          "$and": [
            {
              "@build.name": "${buildName}",
              "@build.number": "${buildNumber}",
              "path": {
                "$nmatch": "org/springframework/restdocs/spring-restdocs-docs/*"
              }
            }
          ]
        }
      },
      "target": "nexus/"
    }
  ]
}
