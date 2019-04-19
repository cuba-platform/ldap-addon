/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.web.sessionwatcher;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
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
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Stores all user sessions.<br>
 * Periodically, gets expired user sessions from the Middleware layer and sends notifications to users having expired sessions<br>
 * A session becomes expired if an access group or roles assigned to the user are changed since
 * the last LDAP synchronization or a user is deactivated via LDAP.
 */
@Component("ldap_ExpiredSessionWatcher")
public class ExpiredSessionWatcher {

    private final Logger logger = LoggerFactory.getLogger(ExpiredSessionWatcher.class);

    private final Set<WeakReference<VaadinSession>> sessions = new CopyOnWriteArraySet<>();

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private WebAuthConfig webAuthConfig;

    @Inject
    private TrustedClientService trustedClientService;

    @Inject
    private Messages messages;

    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;

    public void notifyExpiringSessions() {

        if (BooleanUtils.isFalse(ldapPropertiesConfig.getExpiringSessionsEnable())) {
            return;
        }

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

        List<WeakReference<VaadinSession>> closedSessions = new ArrayList<>();
        for (WeakReference<VaadinSession> wr : sessions) {
            VaadinSession session = wr.get();
            if (session == null) {
                closedSessions.add(wr);
                continue;
            }
            // obtain lock on session state
            session.accessSynchronously(() -> {
                if (session.getState() == VaadinSession.State.OPEN) {
                    // active app in this session
                    App app = App.getInstance();
                    Optional<ExpiredSession> expiredSession = expiringSessions.stream()
                            .filter(es -> es.getUuid().equals(app.getConnection().getSessionNN().getId()))
                            .findFirst();
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
                    closedSessions.add(wr);
                }
            });
        }
        sessions.removeAll(closedSessions);
    }

    @EventListener
    public void onAppStart(AppStartedEvent event) {
        if (BooleanUtils.isTrue(ldapPropertiesConfig.getExpiringSessionsEnable())) {
            sessions.add(new WeakReference<>(VaadinSession.getCurrent()));
        }
    }
}