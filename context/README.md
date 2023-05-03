# templates-context

## Summary

Template / Example for a microservice demonstrating the tenant and user context behavior.

## Use case

Very often microservice should not use the service user but the context of the authenticated user.
The documentation of this is very limited. Here is an example how to implement microservice in tenant and user context.

### All available user beans

```Java
        @UserScope
@Bean(name = "userCredentials")
public CumulocityCredentials credentials(){
        return delegate.getCumulocityCredentials();
        }

@Override
@UserScope
@Bean(name = "userInventoryApi")
public InventoryApi getInventoryApi()throws SDKException{
        return delegate.getInventoryApi();
        }

@Override
@UserScope
@Bean(name = "userIdentityApi")
public IdentityApi getIdentityApi()throws SDKException{
        return delegate.getIdentityApi();
        }

@Override
@UserScope
@Bean(name = "userMeasurementApi")
public MeasurementApi getMeasurementApi()throws SDKException{
        return delegate.getMeasurementApi();
        }

@Override
@UserScope
@Bean(name = "userDeviceControlApi")
public DeviceControlApi getDeviceControlApi()throws SDKException{
        return delegate.getDeviceControlApi();
        }

@Override
@UserScope
@Bean(name = "userAlarmApi")
public AlarmApi getAlarmApi()throws SDKException{
        return delegate.getAlarmApi();
        }

@Override
@UserScope
@Bean(name = "userEventApi")
public EventApi getEventApi()throws SDKException{
        return delegate.getEventApi();
        }

@Override
@UserScope
@Bean(name = "userAuditRecordApi")
public AuditRecordApi getAuditRecordApi()throws SDKException{
        return delegate.getAuditRecordApi();
        }

@Override
@UserScope
@Bean(name = "userDeviceCredentialsApi")
public DeviceCredentialsApi getDeviceCredentialsApi()throws SDKException{
        return delegate.getDeviceCredentialsApi();
        }

@Override
@UserScope
@Bean(name = "userBinariesApi")
public BinariesApi getBinariesApi()throws SDKException{
        return delegate.getBinariesApi();
        }

@Override
@UserScope
@Bean(name = "userUserApi")
public UserApi getUserApi()throws SDKException{
        return delegate.getUserApi();
        }

@Override
@UserScope
@Bean(name = "userTenantOptionApi")
public TenantOptionApi getTenantOptionApi()throws SDKException{
        return delegate.getTenantOptionApi();
        }

@Override
@UserScope
@Bean(name = "userSystemOptionApi")
public SystemOptionApi getSystemOptionApi()throws SDKException{
        return delegate.getSystemOptionApi();
        }

@Override
@UserScope
@Bean(name = "userTokenApi")
public TokenApi getTokenApi()throws SDKException{
        return delegate.getTokenApi();
        }

@Override
@UserScope
@Bean(name = "userNotificationSubscriptionApi")
public NotificationSubscriptionApi getNotificationSubscriptionApi()throws SDKException{
        return delegate.getNotificationSubscriptionApi();
        }
```

For a full list of tenant / user qualifier of beans, have a look
here: https://github.com/SoftwareAG/cumulocity-clients-java/blob/e113ec32b812252b73dc80cc289d822bed41bcfe/microservice/api/src/main/java/com/cumulocity/microservice/api/CumulocityClientFeature.java

## How to run locally:

1. Build project

mvn clean install

2. Create application at cumulocity

POST 'https://{base-url}/application/applications'

Request body:

```javascript
{
  "key": "templates-scope",
  "name": "templates-context",
  "contextPath": "templates-context",
  "type": "MICROSERVICE",
  "manifest":{},	
	"requiredRoles": [
		"ROLE_INVENTORY_READ",
		"ROLE_INVENTORY_CREATE",
		"ROLE_INVENTORY_ADMIN",
		"ROLE_MEASUREMENT_READ",
		"ROLE_MEASUREMENT_ADMIN",
		"ROLE_EVENT_READ",
		"ROLE_EVENT_ADMIN",
		"ROLE_ALARM_READ",
		"ROLE_ALARM_ADMIN",
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
    "name": "servicebootstrap_templates-context",
    "tenant": "<your tenant>"
}
```

5. Add bootstrap credentials

to your src/main/resources/application-dev.properties

6. Start microservice with spring profile "dev"

java -Dspring.profiles.active=dev -jar cumulocity-microservice-templates-context-0.0.1-SNAPSHOT.jar

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product
suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes
contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in
the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in
the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any
questions.