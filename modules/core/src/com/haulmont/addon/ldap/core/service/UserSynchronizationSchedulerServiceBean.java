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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service(UserSynchronizationSchedulerService.NAME)
public class UserSynchronizationSchedulerServiceBean implements UserSynchronizationSchedulerService {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Set<ExpiredSession> expiredSessions = new HashSet<>();

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
                lock.writeLock().lock();
                try {
                    expiredSessions.add(new ExpiredSession(use.getUuid(), use.getLogin(), timeSource.currentTimeMillis()));
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }

    public void killExpiredSessions() {
        for (ExpiredSession es : getExpiredSessionsSnapshot()) {
            boolean killSession = (timeSource.currentTimeMillis() - es.getCreateTsMillis()) >= ldapPropertiesConfig.getSessionExpiringPeriodSec() * 1000;
            if (killSession) {
                lock.writeLock().lock();
                try {
                    expiredSessions.remove(es);
                } finally {
                    lock.writeLock().unlock();
                }
                userSessionService.killSession(es.getUuid());
            }
        }
    }

    public Set<ExpiredSession> getExpiredSessions() {
        return getExpiredSessionsSnapshot();
    }

    private Set<ExpiredSession> getExpiredSessionsSnapshot() {
        Set<ExpiredSession> tempExpiredSessions;
        lock.readLock().lock();
        try {
            tempExpiredSessions = new HashSet<>(expiredSessions);
        } finally {
            lock.readLock().unlock();
        }
        return tempExpiredSessions;
    }
}
