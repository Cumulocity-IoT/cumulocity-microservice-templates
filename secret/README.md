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

