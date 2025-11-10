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

To prevent this, the microservice template generates for each tenant subscription a scheduled task which uses a thread-pool, see DataSyncTaskScheduler and DataSyncTaskSchedulerConfig.

The log entries show the different TaskScheduler threads. For each tenant an different thread is used.

```
2020-12-29 21:10:44.577  INFO 10684 --- [subscriptions-0] c.c.m.t.m.service.DataSyncTaskScheduler  : Creating Scheduler task for tenant: t10334995
2020-12-29 21:10:44.579  INFO 10684 --- [TaskScheduler-1] c.c.m.t.m.service.DataSyncTaskScheduler  : START, scheduled task for tenant: t10334995
2020-12-29 21:10:44.580  INFO 10684 --- [subscriptions-0] c.c.m.t.m.service.DataSyncTaskScheduler  : Creating Scheduler task for tenant: t10979174
2020-12-29 21:10:44.580  INFO 10684 --- [TaskScheduler-1] c.c.m.t.m.service.DataSyncService        : All devices of tenant t10334995
2020-12-29 21:10:44.581  INFO 10684 --- [TaskScheduler-4] c.c.m.t.m.service.DataSyncTaskScheduler  : START, scheduled task for tenant: t10979174
2020-12-29 21:10:44.582  INFO 10684 --- [TaskScheduler-4] c.c.m.t.m.service.DataSyncService        : All devices of tenant t10979174
2020-12-29 21:10:45.045  INFO 10684 --- [TaskScheduler-4] c.c.m.t.m.service.DataSyncService        : Devices: [Soap Agent 1, Prosys Simulation Server, niclex-PC, Prosys Simulation Server, MyDevice (nsu=http://www.prosysopc.com/OPCUA/SampleAddressSpace;s=MyDevice), Prosys Simulation Server, test-opcua-gateway, MyDevice (nsu=http://www.prosysopc.com/OPCUA/SampleAddressSpace;s=MyDevice), Nordex Demo Server, NordexDevice (nsu=http://opcfoundation.org/UA/;i=84), Nordex Demo Server (Remote), NordexDeviceV21 (nsu=http://nordex-online.com/UA/IFD;s=NordexDeviceV2), Microservice-Exercise4, NC2 Windfarm 1, NC2 Windfarm 1, registration-dev] 
2020-12-29 21:10:45.045  INFO 10684 --- [TaskScheduler-4] c.c.m.t.m.service.DataSyncTaskScheduler  : END, scheduled task for tenant: t10979174
2020-12-29 21:10:45.082  INFO 10684 --- [TaskScheduler-1] c.c.m.t.m.service.DataSyncService        : Devices: [iPhone, templates-agent, template, Q211908 Lakelands, Q211937 Roche's Feed's, Qxxxxxx iBox Demo, A test device, Connex Spot Monitor] 
2020-12-29 21:10:45.082  INFO 10684 --- [TaskScheduler-1] c.c.m.t.m.service.DataSyncTaskScheduler  : END, scheduled task for tenant: t10334995
```

In contrast, annotate scheduled task, see DeviceService runs within one thread.
```
2020-12-29 21:11:15.389  INFO 10684 --- [TaskScheduler-3] c.c.m.t.m.service.DeviceService          : Found devices: [iPhone, templates-agent, template, Q211908 Lakelands, Q211937 Roche's Feed's, Qxxxxxx iBox Demo, A test device, Connex Spot Monitor]
2020-12-29 21:11:15.571  INFO 10684 --- [TaskScheduler-3] c.c.m.t.m.service.DeviceService          : Found devices: [Soap Agent 1, Prosys Simulation Server, niclex-PC, Prosys Simulation Server, MyDevice (nsu=http://www.prosysopc.com/OPCUA/SampleAddressSpace;s=MyDevice), Prosys Simulation Server, test-opcua-gateway, MyDevice (nsu=http://www.prosysopc.com/OPCUA/SampleAddressSpace;s=MyDevice), Nordex Demo Server, NordexDevice (nsu=http://opcfoundation.org/UA/;i=84), Nordex Demo Server (Remote), NordexDeviceV21 (nsu=http://nordex-online.com/UA/IFD;s=NordexDeviceV2), Microservice-Exercise4, NC2 Windfarm 1, NC2 Windfarm 1, registration-dev]
```

 

## How to run locally:

1. Build project

    mvn clean install

2. Create a new Microservice application on your Cumulocity IoT tenant

    `POST https://{base-url}/application/applications`
    
    Request body:
    
    ```json
    {
        "key": "templates-multisched",
        "name": "templates-multisched",
        "contextPath": "templates-multisched",
        "type": "MICROSERVICE",
        "manifest":{},	
        "requiredRoles": [
            "ROLE_INVENTORY_READ",
            "ROLE_INVENTORY_CREATE",
            "ROLE_INVENTORY_ADMIN"
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
        "name": "servicebootstrap_templates-multisched",
        "tenant": "<your tenant>"
    }
    ```
    Make sure to provide the correct authorization for the request.

5. Provide bootstrap credentials 

    Add the bootstrap credentials to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

    `java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-basic-0.0.1-SNAPSHOT.jar`
