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

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.dao.UserSynchronizationLogDao.NAME;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.*;
import static com.haulmont.addon.ldap.entity.UserSynchronizationResultEnum.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;

@Component(NAME)
public class UserSynchronizationLogDao {

    public final static String NAME = "ldap_UserSynchronizationLogDao";

    private static final String INDENT = "   ";
    private static final String PASSWORD = "PASSWORD";
    private static final String PASSWORD_VALUE = "*****";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private DaoHelper daoHelper;


    public void saveUserSynchronizationLog(UserSynchronizationLog userSynchronizationLog) {
        daoHelper.persistOrMerge(userSynchronizationLog);
    }

    @Transactional
    public void logUserSynchronization(LdapMatchingRuleContext ldapMatchingRuleContext, User originalUser) {
        User cubaUser = ldapMatchingRuleContext.getCubaUser();
        UserSynchronizationLog userSynchronizationLog = metadata.create(UserSynchronizationLog.class);
        userSynchronizationLog.setLogin(cubaUser.getLogin());
        UserSynchronizationResultEnum result = (originalUser.getActive() || cubaUser.getActive()) ? SUCCESS_SYNC : DISABLED_USER_TRY_LOGIN;
        userSynchronizationLog.setResult(result);
        userSynchronizationLog.setLdapAttributes(ldapMatchingRuleContext.getLdapUser() == null ?
                null : getLdapAttributes(ldapMatchingRuleContext.getLdapUser().getUnmodifiableLdapAttributeMap()));
        userSynchronizationLog.setAccessGroupBefore(originalUser.getGroup() == null ? null : originalUser.getGroup().getName());
        userSynchronizationLog.setAccessGroupAfter(cubaUser.getGroup() == null ? null : cubaUser.getGroup().getName());
        userSynchronizationLog.setRolesBefore(getRolesField(originalUser.getUserRoles()));
        userSynchronizationLog.setRolesAfter(getRolesField(cubaUser.getUserRoles()));
        userSynchronizationLog.setUserInfoBefore(getUserInfoField(originalUser));
        userSynchronizationLog.setUserInfoAfter(getUserInfoField(cubaUser));
        userSynchronizationLog.setAppliedRules(getAppliedRulesField(ldapMatchingRuleContext.getAppliedRules()));
        userSynchronizationLog.setIsNewUser(PersistenceHelper.isNew(cubaUser));
        if (FALSE.equals(cubaUser.getActive()) && TRUE.equals(originalUser.getActive())) {
            userSynchronizationLog.setIsDeactivated(TRUE);
        }
        saveUserSynchronizationLog(userSynchronizationLog);
    }

    @Transactional
    public void logDisabledDuringSync(LdapUser ldapUser) {
        UserSynchronizationLog userSynchronizationLog = metadata.create(UserSynchronizationLog.class);
        userSynchronizationLog.setLogin(ldapUser.getLogin());
        userSynchronizationLog.setResult(USER_DISABLED_DURING_LDAP_SYNC);
        userSynchronizationLog.setLdapAttributes(getLdapAttributes(ldapUser.getUnmodifiableLdapAttributeMap()));
        userSynchronizationLog.setIsDeactivated(TRUE);
        saveUserSynchronizationLog(userSynchronizationLog);
    }

    @Transactional
    public void logEnabledDuringSync(LdapUser ldapUser) {
        UserSynchronizationLog userSynchronizationLog = metadata.create(UserSynchronizationLog.class);
        userSynchronizationLog.setLogin(ldapUser.getLogin());
        userSynchronizationLog.setResult(USER_ENABLED_DURING_LDAP_SYNC);
        userSynchronizationLog.setLdapAttributes(getLdapAttributes(ldapUser.getUnmodifiableLdapAttributeMap()));
        userSynchronizationLog.setIsDeactivated(FALSE);
        saveUserSynchronizationLog(userSynchronizationLog);
    }

    @Transactional
    public void logSynchronizationError(String login, Exception e) {
        UserSynchronizationLog userSynchronizationLog = metadata.create(UserSynchronizationLog.class);
        userSynchronizationLog.setLogin(login);
        userSynchronizationLog.setResult(ERROR_SYNC);
        userSynchronizationLog.setErrorText(ExceptionUtils.getStackTrace(e));
        saveUserSynchronizationLog(userSynchronizationLog);
    }

    @Transactional(readOnly = true)
    public List<UserSynchronizationLog> getByLogin(String login) {
        TypedQuery<UserSynchronizationLog> query = persistence.getEntityManager()
                .createQuery("select usl from ldap$UserSynchronizationLog usl where usl.login = :login", UserSynchronizationLog.class);
        query.setParameter("login", login);
        return query.getResultList();
    }

    private String getRolesField(List<UserRole> originalRoles) {
        return originalRoles.stream()
                .map(ur -> ur.getRole() != null ? ur.getRole().getName() : ur.getRoleName())
                .distinct()
                .collect(Collectors.joining("\n"));
    }

    private String getUserInfoField(User originalUser) {
        StringBuilder sb = new StringBuilder();
        sb.append("First name: ");
        sb.append(originalUser.getFirstName());
        sb.append("\n");

        sb.append("Middle name: ");
        sb.append(originalUser.getMiddleName());
        sb.append("\n");

        sb.append("Last name: ");
        sb.append(originalUser.getLastName());
        sb.append("\n");

        sb.append("Full name: ");
        sb.append(originalUser.getName());
        sb.append("\n");

        sb.append("Email: ");
        sb.append(originalUser.getEmail());
        sb.append("\n");

        sb.append("Position: ");
        sb.append(originalUser.getPosition());
        sb.append("\n");

        sb.append("Language: ");
        sb.append(originalUser.getLanguage());
        sb.append("\n");

        sb.append("Active: ");
        sb.append(originalUser.getActive());

        return sb.toString();
    }

    private String getAppliedRulesField(Set<CommonMatchingRule> appliedRules) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (CommonMatchingRule cmr : appliedRules) {
            sb.append(i);
            sb.append(")");
            sb.append(cmr.getRuleType().getName());
            sb.append(". ");
            sb.append(cmr.getDescription());
            sb.append("(");
            sb.append(cmr.getMatchingRuleId());
            sb.append(")");
            sb.append("\n");
            if (!(CUSTOM == cmr.getRuleType())) {
                AbstractDbStoredMatchingRule dbRule = (AbstractDbStoredMatchingRule) cmr;
                sb.append(INDENT);
                sb.append("Override existing group: ");
                sb.append(dbRule.getIsOverrideExistingAccessGroup());
                sb.append(". ");

                sb.append("Override existing roles: ");
                sb.append(dbRule.getIsOverrideExistingRoles());
                sb.append(". ");

                sb.append("Terminal rule: ");
                sb.append(dbRule.getIsOverrideExistingRoles());
                sb.append(".");

                sb.append("\n");
                sb.append(INDENT);
                sb.append("Group: ");
                sb.append(dbRule.getAccessGroup() == null ? StringUtils.EMPTY : dbRule.getAccessGroup().getName());
                sb.append(".");

                sb.append("\n");
                sb.append(INDENT);
                sb.append("Roles: ");
                sb.append(dbRule.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(",")));
                sb.append(".");

                if (!(DEFAULT == cmr.getRuleType())) {
                    sb.append("\n");
                    sb.append(INDENT);
                    sb.append("Additional rule info: ");
                    sb.append(getAdditionalRuleInfo(dbRule));
                }


            }
            sb.append("\n");
            i++;
        }
        return sb.toString();
    }

    private String getAdditionalRuleInfo(AbstractDbStoredMatchingRule dbRule) {
        StringBuilder sb = new StringBuilder();
        if (SIMPLE == dbRule.getRuleType()) {
            SimpleMatchingRule smr = (SimpleMatchingRule) dbRule;
            sb.append(smr.getConditions().stream()
                    .sorted(Comparator.comparing(SimpleRuleCondition::getAttribute))
                    .map(SimpleRuleCondition::getAttributePair)
                    .collect(Collectors.joining(",")));
        } else if (SCRIPTING == dbRule.getRuleType()) {
            ScriptingMatchingRule smr = (ScriptingMatchingRule) dbRule;
            sb.append(smr.getScriptingCondition());
        }
        return sb.toString();
    }

    private String getLdapAttributes(Map<String, Object> ldapAttributes) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> me : ldapAttributes.entrySet()) {
            String attrName = me.getKey();
            sb.append(attrName);
            sb.append(": ");
            if (me.getValue() instanceof List) {
                List<Object> listValue = (List<Object>) me.getValue();
                List<String> list = listValue.stream()
                        .map(v -> v == null ? "null" : v.toString())
                        .collect(toList());
                sb.append(list.stream().collect(Collectors.joining(",")));
            } else {
                String attrValue = attrName.toUpperCase().contains(PASSWORD) ? PASSWORD_VALUE : me.getValue().toString();
                sb.append(attrValue);
            }
            sb.append("\n");
        }
        return sb.toString().replaceAll("\0", "");
    }
}
