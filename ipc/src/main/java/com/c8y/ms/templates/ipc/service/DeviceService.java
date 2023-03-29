package com.c8y.ms.templates.ipc.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;

/**
 * This is an example service. This should be removed for your real project!
 *
 * @author APES
 */
@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final ContextService<MicroserviceCredentials> contextService;

    private final MicroserviceSubscriptionsService subscriptions;

    private final BasicMicroserviceClient basicApi;
    
    @Autowired
    public DeviceService(BasicMicroserviceClient basicApi, ContextService<MicroserviceCredentials> contextService,
                         MicroserviceSubscriptionsService subscriptions) {
        this.basicApi = basicApi;
        this.contextService = contextService;
        this.subscriptions = subscriptions;
    }

    /**
     * Query a list of devices from the Basic Microservice api
     **/
    public List<String> getAllDeviceNames() {
        List<String> allDeviceNames = basicApi.getDeviceNames();
        return allDeviceNames;
    }

}
