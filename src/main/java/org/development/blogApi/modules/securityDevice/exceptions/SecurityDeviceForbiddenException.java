package org.development.blogApi.modules.securityDevice.exceptions;

public class SecurityDeviceForbiddenException extends RuntimeException {
    public SecurityDeviceForbiddenException(String message) {
        super(message);
    }
}
