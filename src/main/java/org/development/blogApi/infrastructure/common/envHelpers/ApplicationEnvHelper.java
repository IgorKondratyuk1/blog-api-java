package org.development.blogApi.infrastructure.common.envHelpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEnvHelper {
    private static int emailConfirmationExpirationHours;

    @Value("${email-confirmation.expiration-hours}")
    public void setEmailConfirmationExpirationHours(int emailConfirmationExpirationHours) {
        ApplicationEnvHelper.emailConfirmationExpirationHours = emailConfirmationExpirationHours;
    }

    public static int getEmailConfirmationExpirationHours() {
        return emailConfirmationExpirationHours;
    }
}
