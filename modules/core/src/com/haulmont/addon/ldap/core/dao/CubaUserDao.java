package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.haulmont.addon.ldap.core.dao.CubaUserDao.NAME;

@Service(NAME)
public class CubaUserDao {
    public final static String NAME = "ldap_CubaUserDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSynchronizationLogDao userSynchronizationLogDao;

    @Transactional(readOnly = true)
    public User getCubaUserByLogin(String login) {
        TypedQuery<User> query = persistence.getEntityManager().createQuery("select distinct cu from sec$User cu where cu.login = :login", User.class);
        query.setParameter("login", login);
        query.setViewName("sec-user-view-with-group-roles");

        User cubaUser = query.getFirstResult();
        if (cubaUser == null) {
            cubaUser = metadata.create(User.class);
            cubaUser.setUserRoles(new ArrayList<>());
        }
        return cubaUser;
    }

    @Transactional(readOnly = true)
    public List<User> getCubaUsers() {
        TypedQuery<User> query = persistence.getEntityManager().createQuery("select distinct cu from sec$User cu where cu.login = :login", User.class);
        query.setViewName("sec-user-view-with-group-roles");
        return query.getResultList();
    }

    @Transactional
    public void saveCubaUser(User cubaUser, User originalUser, ApplyMatchingRuleContext applyMatchingRuleContext) {
        EntityManager entityManager = persistence.getEntityManager();
        User mergedUser = PersistenceHelper.isNew(cubaUser) ? cubaUser : entityManager.merge(cubaUser);
        originalUser.getUserRoles().forEach(entityManager::remove);
        mergedUser.getUserRoles().forEach(entityManager::persist);
        entityManager.persist(mergedUser);
        userSynchronizationLogDao.logUserSynchronization(applyMatchingRuleContext, originalUser);
    }
}
