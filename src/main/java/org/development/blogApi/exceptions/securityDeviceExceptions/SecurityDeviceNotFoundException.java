package org.development.blogApi.exceptions.securityDeviceExceptions;

public class SecurityDeviceNotFoundException extends RuntimeException {
    public SecurityDeviceNotFoundException() {
        super("Security device not found");
    }
}
