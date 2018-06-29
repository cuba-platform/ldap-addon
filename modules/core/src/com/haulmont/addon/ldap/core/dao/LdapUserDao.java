package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.utils.LdapConstants;
import com.haulmont.addon.ldap.core.utils.LdapUserMapper;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.global.LoginException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static com.haulmont.addon.ldap.core.dao.LdapUserDao.NAME;

@Component(NAME)
public class LdapUserDao {

    private final Logger logger = LoggerFactory.getLogger(LdapUserDao.class);

    public final static String NAME = "ldap_LdapUserDao";

    @Inject
    @Qualifier(LdapConstants.LDAP_TEMPLATE_BEAN_NAME)
    private LdapTemplate ldapTemplate;

    @Inject
    private Messages messages;

    @Inject
    private LdapConfigDao ldapConfigDao;

    public LdapUser getLdapUser(String login) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(createUserBaseAndLoginFilter(login));
        List<LdapUser> list = ldapTemplate.search(query, new LdapUserMapper(ldapConfig));
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return null;
        } else {
            throw new RuntimeException(messages.formatMessage(LdapUserDao.class, "multipleUsersWithLogin", login));
        }

    }

    public LdapUser findLdapUserByFilter(List<SimpleRuleCondition> conditions, String login) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
        Filter filter = parseSimpleRuleConditions(conditions);
        if (filter == null) {
            return null;
        }
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(addUserBaseAndLoginFilter(login, filter));
        List<LdapUser> list = ldapTemplate.search(query, new LdapUserMapper(ldapConfig));
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    public void authenticateLdapUser(String login, String password, Locale messagesLocale) throws LoginException {
        try {
            if (!ldapTemplate.authenticate(LdapUtils.emptyLdapName(), createUserBaseAndLoginFilter(login).encode(), password)) {
                String loginFailedMessage = messages.formatMessage(LdapUserDao.class, "LoginException.InvalidLoginOrPassword", messagesLocale, login);
                logger.warn(loginFailedMessage);
                throw new LoginException(loginFailedMessage);
            } else {
                String loginSuccessMessage = messages.formatMessage(LdapUserDao.class, "successLdapLogin", messagesLocale, login);
                logger.warn(loginSuccessMessage);
            }
        } catch (CommunicationException e) {
            String serverConnectionProblem = messages.formatMessage(LdapUserDao.class, "ldapServerConnectionProblem", messagesLocale, login);
            logger.error(serverConnectionProblem, e);
            throw new LoginException(serverConnectionProblem);
        }

    }


    private Filter createUserBaseAndLoginFilter(String login) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            return ef;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(ef);
        andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));

        return andFilter;
    }

    private Filter addUserBaseAndLoginFilter(String login, Filter filter) {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
        Filter resultFilter = null;
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
}

