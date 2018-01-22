package com.company.ldap.core.dao;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

import static com.company.ldap.core.dao.CubaUserDao.NAME;

@Service(NAME)
public class CubaUserDao {
    public final static String NAME = "ldap_CubaUserDao";

    @Inject
    private Persistence persistence;

    @Transactional(readOnly = true)
    public User getCubaUserByLogin(String login) {
        TypedQuery<User> query = persistence.getEntityManager().createQuery("select cu from sec$User cu " +
                "left join fetch cu.userRoles roles " +
                "left join fetch cu.group group " +
                "where cu.login = :login", User.class);
        query.setParameter("login", login);
        return query.getFirstResult();
    }

    @Transactional(readOnly = true)
    public List<User> getCubaUsers() {
        TypedQuery<User> query = persistence.getEntityManager().createQuery("select cu from sec$User cu " +
                "left join fetch cu.userRoles roles " +
                "left join fetch cu.group group", User.class);
        return query.getResultList();
    }

    @Transactional
    public void saveCubaUser(User cubaUser, boolean isNew) {
        EntityManager entityManager = persistence.getEntityManager();
        User mergedUser = isNew ? cubaUser : entityManager.merge(cubaUser);
        mergedUser.getUserRoles().forEach(entityManager::persist);
        entityManager.persist(mergedUser);

    }
}
