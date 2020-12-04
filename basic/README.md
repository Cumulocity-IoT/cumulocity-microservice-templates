# templates-basic

## Summary
This Microservice template/example is a really basic project, which showcases how you can create REST endpoints within your Microservice and how you can schedule tasks.

There are multiple REST endpoints created by this Microservice. These are defined in the classes DeviceController.class and MeasurementController.class, which are both marked as @RestController. Both controllers define individual APIs and paths, e.g. to access any of endpoints from the DeviceController you have to call /devices followed by the path you want to query. 

The DeviceContoller defines three endpoints in total:
- GET /devices/names -> will return a list of device names
- GET /devices/{deviceId} -> replace the deviceId placeholder with any existing device id and the endpoint will return the managedObject for the corresponding device
- POST /devices -> lets you create a new device in Cumulocity. The request expects a JSON object containing the name and type of the device you want to create.

## How to run locally:

1. Build project

    mvn clean install

2. Create a new Microservice application on your Cumulocity IoT tenant

    POST _https://{base-url}/application/applications_
    
    Request body:
    
    ```javascript
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

    GET _https://{base-url}/application/applications/{applicationId}/bootstrapUser_
    
    Response body:
    
    ```javascript
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

    java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-basic-0.0.1-SNAPSHOT.jar

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.