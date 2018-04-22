package com.haulmont.addon.ldap.web.sessionwatcher;

import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import com.haulmont.cuba.web.security.events.AppStartedEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component("ldap_ExpiredSessionWatcher")
public class ExpiredSessionWatcher {

    private final Logger logger = LoggerFactory.getLogger(ExpiredSessionWatcher.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final List<VaadinSession> sessions = new ArrayList<>();

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private WebAuthConfig webAuthConfig;

    @Inject
    private TrustedClientService trustedClientService;

    @Inject
    private Messages messages;

    public void notifyExpiringSessions() {

        UserSession systemSession;
        try {
            systemSession = trustedClientService.getSystemSession(webAuthConfig.getTrustedClientPassword());
        } catch (LoginException e) {
            logger.error("Unable to get system session");
            return;
        }

        Set<ExpiredSession> expiringSessions;

        AppContext.setSecurityContext(new SecurityContext(systemSession));
        try {
            expiringSessions = userSynchronizationService.getExpiringSession();
        } finally {
            AppContext.setSecurityContext(null);
        }

        ArrayList<VaadinSession> activeSessions;
        lock.readLock().lock();
        try {
            activeSessions = new ArrayList<>(sessions);
        } finally {
            lock.readLock().unlock();
        }

        List<VaadinSession> closedSessions = new ArrayList<>();
        for (VaadinSession session : activeSessions) {
            // obtain lock on session state
            session.accessSynchronously(() -> {
                if (session.getState() == VaadinSession.State.OPEN) {
                    // active app in this session
                    App app = App.getInstance();
                    Optional<ExpiredSession> expiredSession = expiringSessions.stream().filter(es -> es.getUuid().equals(app.getConnection().getSessionNN().getId())).findFirst();
                    // user is logged in and session is expiring
                    if (app.getConnection().isAuthenticated() && expiredSession.isPresent()) {
                        // notify all opened web browser tabs
                        List<AppUI> appUIs = app.getAppUIs();
                        for (AppUI ui : appUIs) {
                            if (!ui.isClosing()) {
                                // work in context of UI
                                ui.accessSynchronously(() -> {
                                    new Notification(messages.getMainMessage("expiringSessionMessage"), Type.TRAY_NOTIFICATION)
                                            .show(ui.getPage());
                                });
                            }
                        }
                    }
                } else {
                    closedSessions.add(session);
                }
            });
        }

        lock.writeLock().lock();
        try {
            sessions.removeAll(closedSessions);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @EventListener
    public void onAppStart(AppStartedEvent event) {
        lock.writeLock().lock();
        try {
            sessions.add(VaadinSession.getCurrent());
        } finally {
            lock.writeLock().unlock();
        }
    }
}