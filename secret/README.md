# templates-secret

## Summary
Template / Example for a microservice using tenant options to store a secret encrypted on the Cumulocity platform.

Cumulocity tenant options: https://cumulocity.com/guides/reference/tenants/#option


## Use case

Often used for cloud 2 cloud integration when credentials of other API must be stored encrypted at Cumulocity. 

## How to run locally:

1. Build project

mvn clean install

2. Create application at cumulocity

POST 'https://{base-url}/application/applications'

Request body:

```javascript
{
  "key": "templates-secret",
  "name": "templates-secret",
  "contextPath": "templates-secret",
  "type": "MICROSERVICE",
  "manifest":{},	
	"requiredRoles": [
        "ROLE_OPTION_MANAGEMENT_READ",
        "ROLE_OPTION_MANAGEMENT_ADMIN"
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
    "name": "servicebootstrap_templates-secret",
    "tenant": "<your tenant>"
}
```

5. Add bootstrap credentials 

to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-secret-0.0.1-SNAPSHOT.jar

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.
