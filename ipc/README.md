# templates-ipc

## Summary
This Microservice template/example is a project, which showcases how you can create REST endpoints within your Microservice and call another Microservice REST API hosted in Cumulocity (Interprocess communication IPC)

There is one REST endpoint created by this Microservice. This is defined in the classes `DeviceController.class`. This controller defines the individual API of the microservice.

The DeviceController defines one endpoint:
- `GET /devices/names` 

    will return a list of device names
    

This DeviceController uses another REST API of another Microservice [basic](../basic) to accomplish the task (response). Instead of using REST templates a declarative way of defining REST clients is used in that example. [Spring cloud - Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)

However, this example can also easily changed to rest templates. The integral part of this template you will find in `TemplatesBasicClientConfig`. This configuration contains the request interceptor which handles in that case the authorization (Autorization header) and setting the right base URL. If you want to call another microservice which is not hosted in Cumulocity you can modify the request interceptor to do a OAuth2 authorization for example. [See](https://www.baeldung.com/spring-cloud-feign-oauth-token).




## How to run locally:

1. Build project

    mvn clean install

2. Create a new Microservice application on your Cumulocity IoT tenant

    `POST https://{base-url}/application/applications`
    
    Request body:
    
    ```json
    {
        "key": "templates-ipc",
        "name": "templates-ipc",
        "contextPath": "templates-ipc",
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
        "name": "servicebootstrap_templates-ipc",
        "tenant": "<your tenant>"
    }
    ```
    Make sure to provide the correct authorization for the request.

5. Provide bootstrap credentials 

    Add the bootstrap credentials to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

    `java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-ipc-0.0.1-SNAPSHOT.jar`

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.