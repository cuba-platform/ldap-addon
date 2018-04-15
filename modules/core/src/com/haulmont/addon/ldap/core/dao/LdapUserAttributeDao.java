package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.LdapUserAttribute;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.haulmont.addon.ldap.core.dao.LdapUserAttributeDao.NAME;

@Component(NAME)
public class LdapUserAttributeDao {

    public final static String NAME = "ldap_LdapUserAttributeDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private DaoHelper daoHelper;


    @Transactional(readOnly = true)
    public List<String> getLdapUserAttributesNames() {
        TypedQuery<String> query = persistence.getEntityManager()
                .createQuery("select lua.attributeName from ldap$LdapUserAttribute lua", String.class);
        return query.getResultList();

    }

    @Transactional
    public void saveLdapUserAttribute(LdapUserAttribute ldapUserAttribute) {
        daoHelper.persistOrMerge(ldapUserAttribute);
    }

    @Transactional
    public void refreshLdapUserAttributes(List<String> attributes) {
        persistence.getEntityManager().createQuery("delete from ldap$LdapUserAttribute lua").executeUpdate();
        for (String attribute : attributes) {
            LdapUserAttribute ldapUserAttribute = metadata.create(LdapUserAttribute.class);
            ldapUserAttribute.setAttributeName(attribute);
            saveLdapUserAttribute(ldapUserAttribute);
        }
    }
}
