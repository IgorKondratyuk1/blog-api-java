package org.development.blogApi.exceptions.securityDeviceExceptions;

public class SecurityDeviceForbiddenException extends RuntimeException {
    public SecurityDeviceForbiddenException(String message) {
        super(message);
    }
}
