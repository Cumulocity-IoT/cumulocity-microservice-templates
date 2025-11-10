# templates-metrics

## Summary
This Microservice template/example is a based on the "basic" template to show how you can use the Micrometer Framework for custom metrics. Custom metrics can help monitoring your custom business logic and the related microservice source code.

Several JVM metrics are enabled and available by default, this section is only about adding custom metrics.

In general metrics can be visualized with e.g. with a Prometheus + Grafana stack. 

The Micrometer Framework is supported in the Cumulocity Microservice SDK version higher than 10.6.6.

### Usage of the Micrometer Framework

There are 3 ways shown how "Timer" of the Micrometer framework can be used. Checkout [Micrometer Concepts]( https://micrometer.io/docs/concepts) for more information, e.g. how to add a description and tags to metrics.

#### Record block of code

[Reference: Micrometer Concepts: Record block of code](https://micrometer.io/docs/concepts#_recording_blocks_of_code)

This mechanism use showed in `DeviceService.getAllDeviceNames()`.
The timer is named `devices.all.inventory.requests`.

#### Record code with Timer.Sample

[Reference: Micrometer Concepts: Record code with Timer.Sample](https://micrometer.io/docs/concepts#_storing_start_state_in_timer_sample)

This mechanism use showed in `DeviceService.getDeviceRepresentation()`.
The timer is named `device.inventory.requests`.

#### Record code with `@Timed`-Annotation

[Reference: Micrometer Concepts: Timed-Annotation](https://micrometer.io/docs/concepts#_storing_start_state_in_timer_sample)

This mechanism use showed in `MeasurementService.getLatestMeasurement(String)`.
The timer is named `measurements.latest.request`.

### Accessing metrics

The Cumulocity Microservice SDK by default uses the Spring Boot Actuator endpoint to provide the metrics via API.

#### `/prometheus` Prometheus specific endpoint

[Reference: Prometheus](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-export-prometheus)

This endpoint can be used easily together with a Prometheus and Grafana setup, which tremendously helps setting up an easy monitoring solution for microservices. As another advantage [Grafana even provides a dashboard](https://grafana.com/grafana/dashboards/4701) out of the box, to visualize the default metrics of the Micrometer Framework.

#### `/metrics` generic endpoint

[Reference: Metrics endpoint](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-endpoint)

<details><summary>Response of /metrics</summary>
<p>

See example output below, showing many default metrics.
Please also notice the custom metrics `devices.all.inventory.requests` and `device.inventory.requests`.

To access the values the metrics need to be queried individually, like:
`GET /metrics/device.inventory.requests`


```json
{
    "names": [
        "jvm.threads.states",
        "jvm.gc.memory.promoted",
        "jvm.memory.max",
        "jvm.memory.used",
        "jvm.gc.max.data.size",
        "jvm.memory.committed",
        "system.cpu.count",
        "logback.events",
        "jvm.buffer.memory.used",
        "tomcat.sessions.created",
        "jvm.threads.daemon",
        "system.cpu.usage",
        "jvm.gc.memory.allocated",
        "devices.all.inventory.requests",
        "http.server.requests",
        "tomcat.sessions.expired",
        "jvm.threads.live",
        "jvm.threads.peak",
        "process.uptime",
        "tomcat.sessions.rejected",
        "process.cpu.usage",
        "jvm.classes.loaded",
        "jvm.classes.unloaded",
        "tomcat.sessions.active.current",
        "tomcat.sessions.alive.max",
        "jvm.gc.live.data.size",
        "jvm.gc.pause",
        "device.inventory.requests",
        "jvm.buffer.count",
        "jvm.buffer.total.capacity",
        "tomcat.sessions.active.max",
        "process.start.time"
    ]
}
```

</p>
</details>

## How to run locally

see [basic/README.md](../basic/README.md)
