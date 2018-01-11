package com.company.ldap.core.spring;

import com.company.ldap.config.LdapConfig;
import com.haulmont.cuba.core.sys.AppContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.company.ldap.core.spring.LdapContextSourceChangeSettingsListener.NAME;

@Component(NAME)
public class LdapContextSourceChangeSettingsListener implements AppContext.Listener {

    public static final String NAME = "ldap_LdapContextSourceSettingsListener";

    private Logger log = LoggerFactory.getLogger(LdapContextSourceChangeSettingsListener.class);

    @Inject
    private LdapConfig ldapConfig;

    public LdapContextSourceChangeSettingsListener() {
        AppContext.addListener(this);
    }

    /**
     * Invoked by the framework on application startup.
     */
    @Override
    public void applicationStarted() {
        try {
            ApplicationContext applicationContext = AppContext.getApplicationContext();
            LdapContextSource ldapContextSource = (LdapContextSource) ((TransactionAwareContextSourceProxy) (applicationContext.getBean("ldap_ldapContextSource"))).getTarget();
            ldapContextSource.setUrl(ldapConfig.getContextSourceUrl());
            ldapContextSource.setBase(ldapConfig.getContextSourceBase());
            ldapContextSource.setUserDn(ldapConfig.getContextSourceUserName());
            ldapContextSource.setPassword(ldapConfig.getContextSourcePassword());
            if (StringUtils.isEmpty(ldapConfig.getContextSourceUserName()) && StringUtils.isEmpty(ldapConfig.getContextSourcePassword())) {
                ldapContextSource.setAnonymousReadOnly(true);
            }

            //TODO: ssl self signed cert
            if (ldapConfig.getUseContextSourcePooling()){
                //TODO: switch to PoolingContextSource. Change ref in ldap template
            }

            ldapContextSource.afterPropertiesSet();
        } catch (Exception e) {
            //TODO: temporary
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoked by the framework on application shutdown.
     */
    @Override
    public void applicationStopped() {
    }

}