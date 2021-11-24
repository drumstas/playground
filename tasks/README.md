### Decisions made:
* Java 17 - assumed that the use of latest LTS is preferred

* Single table design - inferred that a SubTask is also a Task and they naturally fit as one entity

* Command pattern - seemed to fit in this use case, provides logic encapsulation
and better adheres to entities having single responsibilities than say a service class, enables to write new logic without impacting old one, can be further improve to increase resiliency... cons: more boilerplate

* Reusing DTOs and "business layer" Result objects for different commands 
(pros: less code, cons: the objects are universal and hence not fully utilised by domain logic, might prove tedious when app grows)

* Cascade remove operation, subtask deletion together with parent seemed reasonable, might change with different requirements

### Things to improve if given more time:

* Containerize the app
* Proper typesafe validation for enum types
* Accept/return a human-readable metric for time spent on task
* Improve/refactor filtering functionality to make it more dynamic and testable, and not depend on fixed fields, also check for more than just equality (this part was done in a real hurry). Could introduce hibernate metamodel for typesafe criteria
* Implement some security/authentication
* Split request/response/result objects per commands (i.e. TaskDeleteRequest, TaskUpdateRequest)
* Improve update functionality to be more granular instead of just using the fields from an update request (maybe a REST resource per type or at least for specific fields like assignee and timeSpent)
* Assignee/TaskGroup could become separate domain entities
* Audit changes to entities
* Could introduce command queueing with a message broker
* Could introduce a concept of command history and undo/replay functionality
* Cleanup HAL resources and maybe show them only when the client accepts 'hal+json'
* Separate integration tests into another source set and have src/test and src/test-integration
* Could create a separate subtask controller if the app grows
* Analyze common query patterns and create db indexes for task columns (potential candidates could be assignee, group)
* Write more thorough tests

### How to run locally
Need to have JDK 17 (I use sdkman to manage that) and docker (for integration tests)

Either run `./gradlew build` and then launch `src/test/run/DevApplication` from your editor.

Or just run `./gradlew bootRunDev`

### Misc
When ran swagger is available at `localhost:8080/swagger-ui.html`

Included Postman collection for exercising api endpoints in `postman_collection.json`
