package org.development.blogApi.securityDevice.exceptions;

public class SecurityDeviceNotFoundException extends RuntimeException {
    public SecurityDeviceNotFoundException() {
        super("Device not found");
    }
}
