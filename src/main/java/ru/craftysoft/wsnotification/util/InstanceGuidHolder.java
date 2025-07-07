package ru.craftysoft.wsnotification.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class InstanceGuidHolder {

    private final String instanceGuid = UUID.randomUUID().toString();

    public String getInstanceGuid() {
        return instanceGuid;
    }

}
