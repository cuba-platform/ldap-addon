package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.security.app.UserSessionService;
import com.haulmont.cuba.security.entity.UserSessionEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service(UserSynchronizationSchedulerService.NAME)
public class UserSynchronizationSchedulerServiceBean implements UserSynchronizationSchedulerService {

    private final Set<ExpiredSession> expiredSessions = new CopyOnWriteArraySet<>();

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private UserSessionService userSessionService;

    @Inject
    private TimeSource timeSource;

    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;

    public void checkExpiredSessions() {
        List<String> standardAuthenticationUsers = ldapPropertiesConfig.getStandardAuthenticationUsers() == null ? new ArrayList<>() : ldapPropertiesConfig.getStandardAuthenticationUsers();
        List<UserSessionEntity> activeSessions = userSessionService.loadUserSessionEntities(UserSessionService.Filter.ALL).stream()
                .filter(userSession -> !userSession.getSystem())
                .filter(userSession -> !standardAuthenticationUsers.contains(userSession.getLogin()))
                .collect(Collectors.toList());
        for (UserSessionEntity use : activeSessions) {
            boolean isSessionExpire = userSynchronizationService.synchronizeUser(use.getLogin(), false);
            if (isSessionExpire) {
                expiredSessions.add(new ExpiredSession(use.getUuid(), use.getLogin(), timeSource.currentTimeMillis()));
            }
        }
    }

    public void killExpiredSessions() {
        for (ExpiredSession es : expiredSessions) {
            boolean killSession = (timeSource.currentTimeMillis() - es.getCreateTsMillis()) >= ldapPropertiesConfig.getSessionExpiringPeriodSec() * 1000;
            if (killSession) {
                expiredSessions.remove(es);
                userSessionService.killSession(es.getUuid());
            }
        }
    }

    public Set<ExpiredSession> getExpiredSessions() {
        return new HashSet<>(expiredSessions);
    }

}
