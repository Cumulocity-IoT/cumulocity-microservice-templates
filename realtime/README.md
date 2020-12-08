# templates-realtime

## Summary

This Microservice demonstrates how listeners for Cumulocity realtime notifications can be implemented. Realtime notifications enable the user to listen to updates for certain Cumulocity domain objects such as alarms, operations, measurements or managedObjects.

This template showcases how to listen for:
- updates on a specific device
- new measurements created for a specific device
- new operations created for a specific device

In order for this template to work you will need to have a device registered/created in your Cumulocity tenant. It doesn't need to be a physical device it can also be a device, which has been created manually using the REST api. In the `application-dev.properties` file you have to set the device id (`device.id`) for which you want to listen for realtime notifications. If you have specified the device id and start the Microservice you will see a similar output on the console, indicating that the Microservice could properly subscribe for realtime notifications:
```text
Successfully subscribed: /managedobjects/<your_device_id>
Successfully subscribed: /measurements/<your_device_id>
Successfully subscribed: /<your_device_id>
```

To test the different types of realtime notifications, it's recommended to use a REST client (e.g. Postman) to send sample requests.
- Example to update the device representation to trigger a realtime notification for the device:

    `PUT {{url}}/inventory/managedObjects/{{deviceId}}`
    
    ```json
    {
        "c8y_Position": {
            "alt": 67,
            "lng": 6.95173,
            "lat": 51.151977 
        }
    }
    ```
  
    this request will update the geolocation for your device and will trigger a realtime notification.
    
- Example to receive a notification once a measurement has been created for your device:

    `POST {{url}}/measurement/measurements`
    
    ```json
    {
        "c8y_TemperatureMeasurement": {
            "T": { 
                "value": 25,
                "unit": "C" 
            }
        },
        "time": "{{timestamp}}", 
        "source": {
            "id":"{{deviceId}}" 
        }, 
        "type": "c8y_TemperatureMeasurement"
    }
    ``` 
  
    This request will create a measurement for your device. The Microservice receives the incoming measurement as a realtime notification.
    
- Example to receive an operation:

    `POST {{url}}/devicecontrol/operations/`
    
    ```json
    {
        "deviceId" : "{{deviceId}}",
        "c8y_Restart" : {},
        "description": "Restart device"
    }
    ```

    This request will create an operation for your device. The Microservice receives this operation and will update the status from `PENDING` to `EXECUTING` to `SUCCESSFUL`.

## How to run locally:

1. Build project

    mvn clean install

2. Create a new Microservice application on your Cumulocity IoT tenant

    `POST https://{base-url}/application/applications`
    
    Request body:
    
    ```json
    {
        "key": "templates-realtime",
        "name": "templates-realtime",
        "contextPath": "templates-realtime",
        "type": "MICROSERVICE",
        "manifest":{},	
        "requiredRoles": [
            "ROLE_INVENTORY_READ",
            "ROLE_INVENTORY_CREATE",
            "ROLE_INVENTORY_ADMIN",
            "ROLE_MEASUREMENT_READ",
            "ROLE_DEVICE_CONTROL_READ",
            "ROLE_DEVICE_CONTROL_ADMIN"
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
        "name": "servicebootstrap_templates-realtime",
        "tenant": "<your tenant>"
    }
    ```
    Make sure to provide the correct authorization for the request.

5. Provide bootstrap credentials 

    Add the bootstrap credentials to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

    `java -Dspring.profiles.active=dev -jar cumulocity-microservice-realtime-basic-0.0.1-SNAPSHOT.jar`

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.