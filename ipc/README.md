# templates-ipc

## Summary
This Microservice template/example is a project, which showcases how you can create REST endpoints within your Microservice and call another Microservice REST API hosted in Cumulocity (Interprocess communication IPC)

There are multiple REST endpoints created by this Microservice. These are defined in the classes `DeviceController.class` and `MeasurementController.class`, which are both marked as `@RestController`. Both controllers define individual APIs and paths, e.g. to access any of endpoints from the DeviceController you have to call `/devices` followed by the path you want to query. 

The DeviceController defines three endpoints in total:
- `GET /devices/names` 

    will return a list of device names
    
- `GET /devices/{deviceId}` 

    replace the deviceId placeholder with any existing device id and the endpoint will return the managedObject for the corresponding device.

- `POST /devices` 
    
    lets you create a new device in Cumulocity. The request expects a JSON object containing the name and type of the device you want to create.
    
    ```json
    {
        "name" : "my device name",
        "type" : "device_type"
    }
    ```
  
The MeasurementController defines one endpoint:
- `GET /measurements/latest/{deviceId}`

    replacing the device id with an existing device id will return the latest measurement for the corresponding device as a JSON object.

In addition to those REST endpoints, you can also find an example for a simple scheduler in `DeviceService.class`, which triggers a request to Cumulocity in a background thread. The scheduler runs continuously in a specified interval which can be configured in the application.properties using the parameters `scheduled.delay.millis` and `scheduled.delay.millis`.

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