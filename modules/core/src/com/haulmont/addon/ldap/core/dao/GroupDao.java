package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.security.entity.Group;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.GroupDao.NAME;


@Component(NAME)
public class GroupDao {
    public final static String NAME = "ldap_GroupDao";

    @Inject
    private Persistence persistence;

    @Inject
    private LdapConfigDao ldapConfigDao;

    @Transactional(readOnly = true)
    public Group getDefaultGroup() {
        LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
        TypedQuery<Group> query = persistence.getEntityManager()
                .createQuery("select gr from sec$Group gr where gr.name = :name", Group.class);
        query.setParameter("name", ldapConfig.getDefaultAccessGroupName());
        return query.getSingleResult();
    }
}
