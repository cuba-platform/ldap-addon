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

package com.haulmont.addon.ldap.core.aop;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.ConditionalOnAppProperty;
import com.haulmont.cuba.security.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
@ConditionalOnAppProperty(property = "ldap.userSubstitutionCheckerEnabled", value = "true")
public class UserSubstitutionChecker {

    private static final Logger log = LoggerFactory.getLogger(UserSubstitutionChecker.class);

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    protected UserSessionSource userSessionSource;

    @Pointcut("execution(* com.haulmont.cuba.security.auth.AuthenticationServiceBean.substituteUser(..))")
    public void substituteUserPointcut() { }

    @Around("substituteUserPointcut()")
    public Object beforeUserSubstitution(ProceedingJoinPoint pjp) throws Throwable {
        Optional<User> userArg = getUserArg(pjp.getArgs());

        if (userArg.isPresent()) {
            User cubaUser = userArg.get();
            String userLogin = cubaUser.getLogin();
            // TODO: 24.03.2022 check when is it called
            LdapUser ldapUser = ldapUserDao.getLdapUser(userLogin, null);

            if (ldapUser != null && ldapUser.getDisabled()) {
                log.warn(String.format("Unable to switch to user '%s': the user is disabled", userLogin));
                return userSessionSource.getUserSession();
            }
        }

        return pjp.proceed();
    }

    private static Optional<User> getUserArg(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof User)
                .map(user -> (User) user)
                .findFirst();
    }
}
