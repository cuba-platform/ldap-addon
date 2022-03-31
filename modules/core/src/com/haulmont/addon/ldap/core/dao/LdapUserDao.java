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

package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.spring.events.LdapFailedAuthenticationEvent;
import com.haulmont.addon.ldap.core.utils.LdapUserMapper;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.global.LoginException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import static com.haulmont.addon.ldap.core.dao.LdapUserDao.NAME;

@Component(NAME)
public class LdapUserDao {

    private final Logger logger = LoggerFactory.getLogger(LdapUserDao.class);

    public final static String NAME = "ldap_LdapUserDao";

    @Inject
    private Messages messages;
    @Inject
    private LdapConfigDao ldapConfigDao;
    @Inject
    private Events events;

    private final Map<String, ActiveDirectoryDomain> adDomainsCache = new HashMap<>();

    public LdapUser getLdapUser(String login, String tenantId) {
        List<LdapUser> userSearchResult = PreWindows2000Login.match(login)
                ? searchUserInDomain(login, tenantId)
                : searchUserContextSourceBase(login, tenantId);
        return userSearchResult.stream()
                .reduce(createOnlyOneObjectReducer(createMultipleLoginsException(login)))
                .orElse(null);
    }

    private List<LdapUser> searchUserInDomain(String login, String tenantId) {
        PreWindows2000Login oldStyleLogin = new PreWindows2000Login(login);
        return getActiveDirectoryDomain(oldStyleLogin.domainNetBiosName, tenantId, LdapUserDao::throwWrongDomainNameException)
                .searchUser(samAccountNameFilter(oldStyleLogin.samAccountName), tenantId);
    }

    private List<LdapUser> searchUserContextSourceBase(String login, String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(createUserBaseAndLoginFilter(login, ldapConfig));

        return ldapTemplate(tenantId).search(query, new LdapUserMapper(ldapConfig));
    }

    public LdapUser findLdapUserByFilter(List<SimpleRuleCondition> conditions, String login, String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        Filter filter = parseSimpleRuleConditions(conditions);
        if (filter == null) {
            return null;
        }
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(addUserBaseAndLoginFilter(login, filter, tenantId));
        List<LdapUser> list = ldapTemplate(tenantId).search(query, new LdapUserMapper(ldapConfig));
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    public void authenticateLdapUser(String login, String password, Locale messagesLocale, String tenantId) throws LoginException {
        try {
            boolean authenticated = isAuthenticated(login, password, tenantId);
            if (authenticated) {
                String loginSuccessMessage = messages.formatMessage(LdapUserDao.class, "successLdapLogin", messagesLocale, login);
                logger.warn(loginSuccessMessage);
            } else {
                String loginFailedMessage = messages.formatMessage(LdapUserDao.class, "LoginException.InvalidLoginOrPassword", messagesLocale, login);
                logger.warn(loginFailedMessage);
                events.publish(new LdapFailedAuthenticationEvent(this, StringUtils.lowerCase(login)));
                throw new LoginException(loginFailedMessage);
            }
        } catch (CommunicationException e) {
            if (e.getRootCause() instanceof UnknownHostException) {
                String unknownHost = e.getRootCause().getMessage();
                String unknownHostProblem = messages.formatMessage(LdapUserDao.class, "unknownHostProblem", messagesLocale, unknownHost);
                logger.error(unknownHostProblem, e);
                throw new LoginException(unknownHostProblem);
            } else {
                String serverConnectionProblem = messages.formatMessage(LdapUserDao.class, "ldapServerConnectionProblem", messagesLocale, login);
                logger.error(serverConnectionProblem, e);
                throw new LoginException(serverConnectionProblem);
            }

        }
    }

    private boolean isAuthenticated(String login, String password, String tenantId) {
        boolean authenticated;
        if (PreWindows2000Login.match(login)) {
            PreWindows2000Login oldStyleLogin = new PreWindows2000Login(login);
            authenticated =
                    getActiveDirectoryDomain(oldStyleLogin.domainNetBiosName, tenantId, LdapUserDao::throwWrongDomainNameException)
                            .authenticate(samAccountNameFilter(oldStyleLogin.samAccountName), password, tenantId);
        } else {
            authenticated = ldapTemplate(tenantId).authenticate(
                    LdapUtils.emptyLdapName(),
                    createUserBaseAndLoginFilter(login, ldapConfigDao.getLdapConfigByTenant(tenantId)).encode(),
                    password);
        }
        return authenticated;
    }

    private RuntimeException createMultipleLoginsException(String login) {
        return new RuntimeException(messages.formatMessage(LdapUserDao.class, "multipleUsersWithLogin", login));
    }

    private static <T, X extends RuntimeException> BinaryOperator<T> createOnlyOneObjectReducer(X multipleResultException) {
        return (l, r) -> {
            if (l != null) {
                throw multipleResultException;
            } else {
                return r;
            }
        };
    }

    private static String samAccountNameFilter(String samAccountName) {
        return new EqualsFilter("sAMAccountName", samAccountName).encode();
    }

    private static void throwWrongDomainNameException(String domainName) throws LoginException {
        throw new LoginException(String.format("Wrong '%s' domain name", domainName));
    }

    /**
     * Collect Active Directory domain info by it's NetBIOS name
     */
    private ActiveDirectoryDomain getActiveDirectoryDomain(String domainNetBiosName,
                                                           String tenantId,
                                                           @Nullable Consumer<String> notFoundCallback) {
        ActiveDirectoryDomain foundDomain = adDomainsCache.computeIfAbsent(domainNetBiosName, key -> {
            List<ActiveDirectoryDomain> searchResult = ldapTemplate(tenantId)
                    .search(
                            LdapUtils.newLdapName("CN=Partitions,CN=Configuration"),
                            new EqualsFilter("CN", key).encode(),
                            ActiveDirectoryDomain::new
                    );
            return searchResult.isEmpty() ? null : searchResult.get(0);
        });

        if (foundDomain == null && notFoundCallback != null) {
            notFoundCallback.accept(domainNetBiosName);
        }

        return foundDomain;
    }

    public List<LdapUser> getLdapUsers(List<String> logins, String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .filter(createUserBaseAndLoginsFilter(logins, tenantId));
        return ldapTemplate(tenantId).search(query, new LdapUserMapper(ldapConfig));
    }

    private Filter createUserBaseAndLoginFilter(String login, LdapConfig ldapConfig) {
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            return ef;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(ef);
        andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));

        return andFilter;
    }

    private Filter addUserBaseAndLoginFilter(String login, Filter filter, String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        Filter resultFilter;
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        resultFilter = ef;
        if (StringUtils.isNotEmpty(ldapConfig.getUserBase())) {
            AndFilter andFilter = new AndFilter();
            andFilter.and(ef);
            andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));
            resultFilter = andFilter;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(resultFilter);
        andFilter.and(filter);
        return andFilter;
    }

    private Filter parseSimpleRuleConditions(List<SimpleRuleCondition> conditions) {
        Filter prevFilter = null;
        for (SimpleRuleCondition simpleRuleCondition : conditions) {
            String attributeName = simpleRuleCondition.getAttribute();
            if (StringUtils.isNotEmpty(attributeName)) {
                Filter ef = new EqualsFilter(attributeName, simpleRuleCondition.getAttributeValue());
                if (prevFilter != null) {
                    AndFilter andFilter = new AndFilter();
                    andFilter.and(ef);
                    andFilter.and(prevFilter);
                    prevFilter = andFilter;
                } else {
                    prevFilter = ef;
                }
            }
        }
        return prevFilter;
    }

    private Filter createUserBaseAndLoginsFilter(List<String> logins, String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where(ldapConfig.getLoginAttribute()).is(logins.get(0));
        for (String login : logins.subList(1, logins.size())) {
            containerCriteria = containerCriteria.or(ldapConfig.getLoginAttribute()).is(login);
        }

        Filter ef = containerCriteria.filter();
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            return ef;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(ef);
        andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));

        return andFilter;
    }

    /**
     * Represents old-style windows login i.e. "DOMAIN\\user" form
     */
    static class PreWindows2000Login {
        final String domainNetBiosName;
        final String samAccountName;

        PreWindows2000Login(String loginValue) throws LoginException {
            String[] domainUser = loginValue.split("\\\\");
            if (domainUser.length != 2) {
                throw new LoginException("Login may contain only one '\\' sign");
            }

            domainNetBiosName = domainUser[0];
            samAccountName = domainUser[1];
        }

        /**
         * @return true if specified login string is pre-Windows2000 login string
         */
        static boolean match(String loginString) {
            return loginString.contains("\\");
        }
    }

    /**
     * A class for operations on AD domain
     */
    class ActiveDirectoryDomain {

        private static final String CN_USERS = "CN=Users";

        final String nETBIOSName;
        final String nCName;
        final String dnsRoot;

        private LdapContextSource ldapContextSource;
        private LdapTemplate ldapTemplate;

        ActiveDirectoryDomain(Attributes domainAttributes) throws NamingException {
            this.nETBIOSName = domainAttributes.get("nETBIOSName").get().toString();
            this.nCName = domainAttributes.get("nCName").get().toString();
            this.dnsRoot = domainAttributes.get("dnsRoot").get().toString();
        }

        String getUrl() {
            return "ldap://" + dnsRoot;
        }

        LdapContextSource getLdapContextSource(String tenantId) {
            if (ldapContextSource == null) {
                LdapConfig ldapConfigByTenant = ldapConfigDao.getLdapConfigByTenant(tenantId);
                ldapContextSource = new LdapContextSource();
                ldapContextSource.setUserDn(ldapConfigByTenant.getContextSourceUserName());
                ldapContextSource.setPassword(ldapConfigByTenant.getContextSourcePassword());
                ldapContextSource.setUrl(getUrl());
                ldapContextSource.setBase(nCName);
                ldapContextSource.afterPropertiesSet();
            }
            return ldapContextSource;
        }

        LdapTemplate getLdapTemplate(String tenantId) {
            if (ldapTemplate == null) {
                ldapTemplate = new LdapTemplate(getLdapContextSource(tenantId));
            }
            return ldapTemplate;
        }

        List<LdapUser> searchUser(String query, String tenantId) {
            return getLdapTemplate(tenantId)
                    .search(CN_USERS, query, new LdapUserMapper(ldapConfigDao.getLdapConfigByTenant(tenantId)));
        }

        boolean authenticate(String filter, String password, String tenantId) throws LoginException {
            return getLdapTemplate(tenantId)
                    .authenticate(
                            CN_USERS,
                            filter,
                            password,
                            (ctx, ldapEntryIdentification) -> {
                            },
                            e -> logger.error(String.format("Could not auth user by query: %s", filter), e));
        }
    }

    private LdapTemplate ldapTemplate(String tenantId) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfigByTenant(tenantId);
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUserDn(ldapConfig.getContextSourceUserName());
        ldapContextSource.setPassword(ldapConfig.getContextSourcePassword());
        ldapContextSource.setUrl(ldapConfig.getContextSourceUrl());
        ldapContextSource.setBase(ldapConfig.getUserBase());
        ldapContextSource.afterPropertiesSet();

        return new LdapTemplate(ldapContextSource);
    }

}

