package com.haulmont.addon.ldap.web.security.ldapcomponent;

import com.google.common.collect.ImmutableMap;
import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.service.AuthUserService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.ClientType;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.sys.ConditionalOnAppProperty;
import com.haulmont.cuba.security.auth.*;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import com.haulmont.cuba.web.security.LoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

import static com.haulmont.cuba.web.security.ExternalUserCredentials.EXTERNAL_AUTH_USER_SESSION_ATTRIBUTE;

/**
 * Login provider that used by LDAP addon.
 */
@ConditionalOnAppProperty(property = "cuba.web.ldap.enabled", value = "false", defaultValue = "false")
@ConditionalOnAppProperty(property = "ldap.addonEnabled", value = "true")
@ConditionalOnAppProperty(property = "cuba.web.externalAuthentication", value = "false", defaultValue = "false")
@Component("ldap_LdapAddonLoginProvider")
public class LdapAddonLoginProvider implements LoginProvider, Ordered {

    private final Logger log = LoggerFactory.getLogger(LdapAddonLoginProvider.class);

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private AuthUserService authUserService;

    @Inject
    private WebAuthConfig webAuthConfig;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;

    @Inject
    private Messages messages;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        if (!checkUserSupportsAddonLogin(credentials)) {
            return null;
        }

        if (RememberMeCredentials.class.isAssignableFrom(credentials.getClass())) {
            UserSynchronizationResultDto userSynchronizationResult =
                    userSynchronizationService.synchronizeUser(((RememberMeCredentials) credentials).getLogin(), true, null, null, null);
            if (userSynchronizationResult.isInactiveUser()) {
                throw new LoginException(messages.formatMessage(LdapAddonLoginProvider.class,
                        "LoginException.InactiveUserLoginAttempt", ((RememberMeCredentials) credentials).getLocale()));
            }
            return null;
        }

        LoginPasswordCredentials loginPasswordCredentials = (LoginPasswordCredentials) credentials;

        authUserService.ldapAuth(
                loginPasswordCredentials.getLogin(), loginPasswordCredentials.getPassword(), loginPasswordCredentials.getLocale());
        UserSynchronizationResultDto userSynchronizationResult
                = userSynchronizationService.synchronizeUser(loginPasswordCredentials.getLogin(), true, null, null, null);
        if (userSynchronizationResult.isInactiveUser()) {
            throw new LoginException(messages.formatMessage(LdapAddonLoginProvider.class,
                    "LoginException.InactiveUserLoginAttempt", loginPasswordCredentials.getLocale()));
        }

        TrustedClientCredentials tcCredentials = new TrustedClientCredentials(
                loginPasswordCredentials.getLogin(),
                webAuthConfig.getTrustedClientPassword(),
                loginPasswordCredentials.getLocale(),
                loginPasswordCredentials.getParams()
        );

        tcCredentials.setClientInfo(loginPasswordCredentials.getClientInfo());
        tcCredentials.setClientType(ClientType.WEB);
        tcCredentials.setIpAddress(loginPasswordCredentials.getIpAddress());
        tcCredentials.setOverrideLocale(loginPasswordCredentials.isOverrideLocale());
        tcCredentials.setSyncNewUserSessionReplication(loginPasswordCredentials.isSyncNewUserSessionReplication());

        Map<String, Serializable> sessionAttributes = ((AbstractClientCredentials) credentials).getSessionAttributes();
        Map<String, Serializable> targetSessionAttributes;
        if (sessionAttributes != null
                && !sessionAttributes.isEmpty()) {
            targetSessionAttributes = new HashMap<>(sessionAttributes);
            targetSessionAttributes.put(EXTERNAL_AUTH_USER_SESSION_ATTRIBUTE, true);
        } else {
            targetSessionAttributes = ImmutableMap.of(EXTERNAL_AUTH_USER_SESSION_ATTRIBUTE, true);
        }

        tcCredentials.setSessionAttributes(targetSessionAttributes);

        return loginMiddleware(tcCredentials);
    }

    private AuthenticationDetails loginMiddleware(Credentials credentials) throws LoginException {
        return authenticationService.login(credentials);
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return ldapPropertiesConfig.getLdapAddonEnabled()
                && (LoginPasswordCredentials.class.isAssignableFrom(credentialsClass) ||
                RememberMeCredentials.class.isAssignableFrom(credentialsClass));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PLATFORM_PRECEDENCE + 40;
    }

    private boolean checkUserSupportsAddonLogin(Credentials credentials) {
        String login = null;
        if (LoginPasswordCredentials.class.isAssignableFrom(credentials.getClass())) {
            login = ((LoginPasswordCredentials) (credentials)).getLogin();
        } else {
            login = ((RememberMeCredentials) (credentials)).getLogin();
        }

        if (webAuthConfig.getStandardAuthenticationUsers().contains(login)) {
            log.debug("User {} is not allowed to use external login");
            return false;
        }
        return true;
    }

}
