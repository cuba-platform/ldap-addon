package com.haulmont.addon.ldap.web.extauth;

import com.haulmont.addon.ldap.service.AuthUserService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.auth.CubaAuthProvider;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import javax.servlet.*;
import java.io.IOException;
import java.util.Locale;

public class LdapComponentAuthProvider implements CubaAuthProvider {

    @Inject
    protected Messages messages;

    @Inject
    @Qualifier(UserSynchronizationService.NAME)
    private UserSynchronizationService userSynchronizationService;

    @Inject
    @Qualifier(AuthUserService.NAME)
    private AuthUserService authUserService;

    @Override
    public void authenticate(String login, String password, Locale messagesLocale) throws LoginException {
        //TODO: Для POC поддерживается аутентификация только по лдап
        //TODO: механизм принудительного обновления пользователей из лдап (с кубы в лдап, из лдап в кубу)
        if (!"admin".equalsIgnoreCase(login)) {//TODO:временно
            authUserService.ldapAuth(login, password, messagesLocale);
            userSynchronizationService.synchronizeUser(login);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
