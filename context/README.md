# templates-context

## Summary

Template / Example for a microservice demonstrating the tenant and user context behavior.

## Use case

Very often microservice should not use the service user but the context of the authenticated user.
The documentation of this is very limited. Here is an example how to implement microservice in tenant and user context.

### All available user beans

```Java
        @Override
        @UserScope
        @Bean(name = "userInventoryApi")
        public InventoryApi getInventoryApi() throws SDKException {
            return delegate.getInventoryApi();
        }

        @Override
        @UserScope
        @Bean(name = "userIdentityApi")
        public IdentityApi getIdentityApi() throws SDKException {
            return delegate.getIdentityApi();
        }

        @Override
        @UserScope
        @Bean(name = "userMeasurementApi")
        public MeasurementApi getMeasurementApi() throws SDKException {
            return delegate.getMeasurementApi();
        }

        @Override
        @UserScope
        @Bean(name = "userDeviceControlApi")
        public DeviceControlApi getDeviceControlApi() throws SDKException {
            return delegate.getDeviceControlApi();
        }

        @Override
        @UserScope
        @Bean(name = "userAlarmApi")
        public AlarmApi getAlarmApi() throws SDKException {
            return delegate.getAlarmApi();
        }

        @Override
        @UserScope
        @Bean(name = "userEventApi")
        public EventApi getEventApi() throws SDKException {
            return delegate.getEventApi();
        }

        @Override
        @UserScope
        @Bean(name = "userAuditRecordApi")
        public AuditRecordApi getAuditRecordApi() throws SDKException {
            return delegate.getAuditRecordApi();
        }

        @Override
        @UserScope
        @Bean(name = "userDeviceCredentialsApi")
        public DeviceCredentialsApi getDeviceCredentialsApi() throws SDKException {
            return delegate.getDeviceCredentialsApi();
        }

        @Override
        @UserScope
        @Bean(name = "userBinariesApi")
        public BinariesApi getBinariesApi() throws SDKException {
            return delegate.getBinariesApi();
        }

        @Override
        @UserScope
        @Bean(name = "userUserApi")
        public UserApi getUserApi() throws SDKException {
            return delegate.getUserApi();
        }

        @Override
        @UserScope
        @Bean(name = "userTenantOptionApi")
        public TenantOptionApi getTenantOptionApi() throws SDKException {
            return delegate.getTenantOptionApi();
        }

        @Override
        @UserScope
        @Bean(name = "userSystemOptionApi")
        public SystemOptionApi getSystemOptionApi() throws SDKException {
            return delegate.getSystemOptionApi();
        }

        @Override
        @UserScope
        @Bean(name = "userTokenApi")
        public TokenApi getTokenApi() throws SDKException {
            return delegate.getTokenApi();
        }

        @Override
        @UserScope
        @Bean(name = "userNotificationSubscriptionApi")
        public NotificationSubscriptionApi getNotificationSubscriptionApi() throws SDKException {
            return delegate.getNotificationSubscriptionApi();
        }
```

For a full list of tenant / user qualifier of beans, have a look
here: https://github.com/Cumulocity-IoT/cumulocity-clients-java/blob/develop/microservice/api/src/main/java/com/cumulocity/microservice/api/CumulocityClientFeature.java

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
