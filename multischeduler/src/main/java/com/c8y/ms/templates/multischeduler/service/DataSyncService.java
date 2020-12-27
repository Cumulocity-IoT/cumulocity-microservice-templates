package com.c8y.ms.templates.multischeduler.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;

@Service
public class DataSyncService {
    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);

    private final ContextService<MicroserviceCredentials> contextService;

    private final DeviceService deviceService;

    @Autowired
    public DataSyncService(ContextService<MicroserviceCredentials> contextService, DeviceService deviceService) {
        this.contextService = contextService;
        this.deviceService = deviceService;
    }

    public Boolean syncDataWithinContext(MicroserviceCredentials context) {
        Boolean callWithinContext = contextService.callWithinContext(context, (Callable<Boolean>) () -> {
            try {
                log.info("All devices of tenant {}", context.getTenant());
                List<String> allDeviceNames = deviceService.getAllDeviceNames();
                log.info("Devices: {} ", allDeviceNames);
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("Get all devices for tenant {} failed with message {}", context.getTenant(), e.getMessage(), e);
            }
            return Boolean.FALSE;
        });
        return callWithinContext;
    }
}
