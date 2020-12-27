# templates-multischeduler

## Summary
If you have a multitenant microservice you often have to run a process for each tenant. In case of a scheduled task for example,
you have to use MicroserviceSubscriptionsService or ContextService<MicroserviceCredentials> to run in tenant context.
This Microservice template/example shows how you can schedule tasks which run for each tenant in a different thread.

It is important to know that:
```java
microserviceSubscriptionsService.runForEachTenant( ()->{
  String tenant= subscriptionsService.getTenant();		 
  //do something in tenant context;
  }	
```
the code inside is running synchronous in one thread (not in parallel). This means if one tenant is blocking, all others too.

To prevent this, the microservice template generates for each tenant subscription a scheduled task which uses a thread-pool, see DataSyncTaskScheduler.



## How to run locally:

1. Build project

    mvn clean install

2. Create a new Microservice application on your Cumulocity IoT tenant

    `POST https://{base-url}/application/applications`
    
    Request body:
    
    ```json
    {
        "key": "templates-basic",
        "name": "templates-basic",
        "contextPath": "templates-basic",
        "type": "MICROSERVICE",
        "manifest":{},	
        "requiredRoles": [
            "ROLE_INVENTORY_READ",
            "ROLE_INVENTORY_CREATE",
            "ROLE_INVENTORY_ADMIN",
            "ROLE_MEASUREMENT_READ"
        ],
        "roles": []
    }
    ```
    
    Make sure to provide the correct authorization for the request.

3. Subscribe the Microservice application to your tenant via UI

4. Acquire microservice bootstrap credentials

    `GET https://{base-url}/application/applications/{applicationId}/bootstrapUser`
    
    Response body:
    
    ```json
    {
        "password": "************************",
        "name": "servicebootstrap_templates-basic",
        "tenant": "<your tenant>"
    }
    ```
    Make sure to provide the correct authorization for the request.

5. Provide bootstrap credentials 

    Add the bootstrap credentials to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

    `java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-basic-0.0.1-SNAPSHOT.jar`

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.