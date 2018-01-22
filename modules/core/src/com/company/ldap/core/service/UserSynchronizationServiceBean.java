package com.company.ldap.core.service;

import com.company.ldap.core.dao.CubaUserDao;
import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.dao.MatchingRuleDao;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.rule.MatchingRuleApplierInitializer;
import com.company.ldap.core.spring.events.*;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.cuba.security.global.LoginException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service(UserSynchronizationService.NAME)
public class UserSynchronizationServiceBean implements UserSynchronizationService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(CubaUserDao.NAME)
    private CubaUserDao cubaUserDao;

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;

    @Inject
    @Qualifier(MatchingRuleApplierInitializer.NAME)
    private MatchingRuleApplierInitializer matchingRuleApplierInitializer;

    @Inject
    private Metadata metadata;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void synchronizeUser(String login, String password) throws LoginException {
        //ldapUserDao.authenticateLdapUser(login, password);
        ApplyMatchingRuleContext ldapUser = ldapUserDao.getLdapUserWrapper(login);
        User tmp = cubaUserDao.getCubaUserByLogin(login);
        User cubaUser = synchronizeCubaUserFromLdapUser(ldapUser, tmp, login, password);
        if (cubaUser.getActive()) {
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, ldapUser, cubaUser);
            applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
        }
        cubaUserDao.saveCubaUser(cubaUser, tmp == null);
    }

    private User synchronizeCubaUserFromLdapUser(ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser, String login, String password) {
        User cu = cubaUser == null ? metadata.create(User.class) : cubaUser;
        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new BeforeNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            cu.setLogin(login);
            cu.setPassword("1");//TODO:temporary
            cu.setUserRoles(new ArrayList<>());
        }
        cu.setEmail(applyMatchingRuleContext.getLdapUser().getEmail());
        cu.setFirstName(applyMatchingRuleContext.getLdapUser().getCn());
        cu.setLastName(applyMatchingRuleContext.getLdapUser().getSn());

        Boolean userDisabled = applyMatchingRuleContext.getLdapUser().getDisabled();
        cu.setActive(!userDisabled);
        if (userDisabled) {
            applicationEventPublisher.publishEvent(new BeforeUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            applicationEventPublisher.publishEvent(new AfterUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        }

        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new AfterNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        }
        return cu;
    }
}
