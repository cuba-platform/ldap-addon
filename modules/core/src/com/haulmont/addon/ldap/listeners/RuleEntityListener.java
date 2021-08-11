package com.haulmont.addon.ldap.listeners;

import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.cuba.security.entity.Group;
import org.springframework.stereotype.Component;

@Component("ldap_RuleEntityListener")
public class RuleEntityListener implements BeforeInsertEntityListener<AbstractDbStoredMatchingRule>,
        BeforeUpdateEntityListener<AbstractDbStoredMatchingRule> {

    @Override
    public void onBeforeInsert(AbstractDbStoredMatchingRule entity, EntityManager entityManager) {
        setGroupName(entity);
    }

    @Override
    public void onBeforeUpdate(AbstractDbStoredMatchingRule entity, EntityManager entityManager) {
        setGroupName(entity);
    }

    private void setGroupName(AbstractDbStoredMatchingRule entity) {
        Group accessGroup = entity.getAccessGroup();
        if (accessGroup != null && !accessGroup.isPredefined()) {
            entity.setAccessGroupName(null);
        } else if (accessGroup != null) {
            String accessGroupName = accessGroup.getName();
            entity.setAccessGroupName(accessGroupName);
            entity.setAccessGroup(null);
        }
    }
}
