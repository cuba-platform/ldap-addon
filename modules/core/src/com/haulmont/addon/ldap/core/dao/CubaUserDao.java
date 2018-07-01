package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.dao.CubaUserDao.NAME;

@Component(NAME)
public class CubaUserDao {
    public final static String NAME = "ldap_CubaUserDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSynchronizationLogDao userSynchronizationLogDao;

    @Inject
    private DaoHelper daoHelper;

    @Transactional(readOnly = true)
    public User getCubaUserByLogin(String login) {
        TypedQuery<User> query = persistence.getEntityManager()
                .createQuery("select cu from sec$User cu where cu.login = :login", User.class);
        query.setParameter("login", login);
        query.setViewName("sec-user-view-with-group-roles");

        User cubaUser = query.getFirstResult();
        if (cubaUser == null) {
            cubaUser = metadata.create(User.class);
            cubaUser.setUserRoles(new ArrayList<>());
            cubaUser.setLogin(login);
        }
        return cubaUser;
    }

    @Transactional(readOnly = true)
    public List<String> getActiveCubaUsersLogins() {
        TypedQuery<String> query = persistence.getEntityManager()
                .createQuery("select cu.login from sec$User cu where cu.active = true", String.class);
        return query.getResultList();
    }

    @Transactional
    public void saveCubaUser(User cubaUser, User beforeRulesApplyUserState, LdapMatchingRuleContext ldapMatchingRuleContext) {
        if (beforeRulesApplyUserState.getActive() || cubaUser.getActive()) {
            EntityManager entityManager = persistence.getEntityManager();
            User mergedUser = daoHelper.persistOrMerge(cubaUser);
            List<Role> newRoles = mergedUser.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .collect(Collectors.toList());
            beforeRulesApplyUserState.getUserRoles().stream()
                    .filter(ur -> !newRoles.contains(ur.getRole()))
                    .forEach(entityManager::remove);
            mergedUser.getUserRoles().forEach(entityManager::persist);
        }
        userSynchronizationLogDao.logUserSynchronization(ldapMatchingRuleContext, beforeRulesApplyUserState);
    }

    @Transactional(readOnly = true)
    public List<User> getCubaUsersByLogin(List<String> logins) {
        TypedQuery<User> query = persistence.getEntityManager()
                .createQuery("select cu from sec$User cu where cu.login in :logins", User.class);
        query.setParameter("logins", logins);
        query.setViewName("sec-user-view-with-group-roles");

        return query.getResultList();
    }

    @Transactional
    public void save(User cubaUser) {
        daoHelper.persistOrMerge(cubaUser);
    }
}
