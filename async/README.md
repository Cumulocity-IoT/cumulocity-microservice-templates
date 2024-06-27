# templates-async

## Summary
Cumulocity Microservice template demonstrating async capabilities with Java.
The main goal is to demonstrate how high throughput microservice can be implemented that parallelize logic.

In an example 4 methods demonstrate different ways how this achieved on an example calling C8Y Event API.

## Use case

4 basic flavors of async programming will be demonstrated starting with the common blocking code style.

0. Blocking RESTController calling blocking Service
1. Blocking RESTController calling C8Y async using CompletableFuture 
2. Non-Blocking RESTController calling C8Y async using CompletableFuture
3. Non-Blocking RESTController calling C8Y async using Executor Service
4. Non-Blocking RESTController calling C8Y async combining Virtual Threads + CompletableFuture


## How to run locally:

1. Build project

mvn clean install

2. Create application at cumulocity

POST 'https://{base-url}/application/applications'

Request body:

```javascript
{
  "key": "templates-async",
  "name": "templates-async",
  "contextPath": "templates-async",
  "type": "MICROSERVICE",
  "manifest":{},	
	"requiredRoles": [
		"ROLE_INVENTORY_READ",
		"ROLE_INVENTORY_CREATE",
		"ROLE_INVENTORY_ADMIN",
		"ROLE_IDENTITY_READ",
		"ROLE_IDENTITY_ADMIN"
	],
	"roles": [
	]
}
```

3. Subscribe application to your tenant via UI

4. Acquire microservice bootstrap credentials

GET 'https://{base-url}/application/applications/{applicationId}/bootstrapUser

Response body:

```javascript
{
    "password": "************************",
    "name": "servicebootstrap_templates-async",
    "tenant": "<your tenant>"
}
```

5. Add bootstrap credentials 

to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-ehcache-0.0.1-SNAPSHOT.jar

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.
