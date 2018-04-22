package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.LdapConfigDao.NAME;

@Component(NAME)
public class LdapConfigDao {

    public final static String NAME = "ldap_LdapConfigDao";

    @Inject
    private Persistence persistence;

    @Inject
    private LdapPropertiesConfig ldapContextConfig;

    @Transactional(readOnly = true)
    public LdapConfig getLdapConfig() {
        TypedQuery<LdapConfig> query = persistence.getEntityManager()
                .createQuery("select lc from ldap$LdapPropertiesConfig lc", LdapConfig.class);
        LdapConfig lc = query.getSingleResult();

        lc.setContextSourceUrl(ldapContextConfig.getContextSourceUrl());
        lc.setContextSourceBase(ldapContextConfig.getContextSourceBase());
        lc.setContextSourceUserName(ldapContextConfig.getContextSourceUserName());

        return lc;

    }
}
