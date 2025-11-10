# templates-basic

## Summary
This Microservice template/example is a really basic project, which showcases how you can create REST endpoints within your Microservice and how you can schedule tasks.

### REST endpoints
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

### Scheduler
In addition to those REST endpoints, you can also find an example for a simple scheduler in `DeviceService.class`, which triggers a request to Cumulocity in a background thread. The scheduler runs continuously in a specified interval which can be configured in the application.properties using the parameters `scheduled.delay.millis` and `scheduled.delay.millis`.

### Changing the Log Level dynamically
Furthermore we added the ability to change the log level at runtime by [exposing](src/main/resources/application-dev.properties#L5) the `loggers` endpoint. You can see all available loggers via:
- `GET /loggers`
- `GET /loggers/com.c8y.ms.templates.basic.service`

and change the log level via a POST request:
- `POST /loggers/com.c8y.ms.templates.basic.service` 
       
    ```json
    {
        "configuredLevel": "DEBUG"
    }
    ```

afterwards you should see the debug message from the [DeviceService](src/main/java/com/c8y/ms/templates/basic/service/DeviceService.java#L53) in the log file.

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
