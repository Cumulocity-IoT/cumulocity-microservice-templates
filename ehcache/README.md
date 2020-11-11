# templates-ehcache

## Summary
Template / Example for a microservice using spring cache abstraction and EHCACHE library.

Spring Cache Abstraction: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/cache.html

EHCACHE: https://www.ehcache.org/

## Use case

For device / asset integration the external id (unique external identifier) is used for correlate devices and assets.
To keep the communication less frequent to the platform a managed cache can be used. Be aware server side (microserivce)
integration has fewer problems with frequent platform communication. This approach is more interesting for agent and edge development, where bandwidth to cloud isn't always high. 

## How to run locally:

1. Build project

mvn clean install

2. Create application at comulocity

POST 'https://{base-url}/application/applications'

Request body:

```javascript
{
  "key": "templates-ehcache",
  "name": "templates-ehcache",
  "contextPath": "templates-ehcache",
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
    "name": "servicebootstrap_templates-ehcache",
    "tenant": "<your tenant>"
}
```

5. Add bootstrap credentials 

to your src/main/resources/application-dev.properties

6. Set environment variable

C8Y_MICROSERVICE_ISOLATION=MULTI_TENANT

7. Start microservice with spring profile "dev"

java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-ehcache-0.0.1-SNAPSHOT.jar

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.
