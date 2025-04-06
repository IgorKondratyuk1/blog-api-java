package org.development.blogApi.modules.securityDevice.exceptions;

public class SecurityDeviceNotFoundException extends RuntimeException {
    public SecurityDeviceNotFoundException() {
        super("Device not found");
    }
}
