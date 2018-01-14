package com.company.ldap.core.dao;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.utils.LdapConstants;
import com.company.ldap.core.utils.LdapUserMapper;
import com.company.ldap.core.utils.LdapUserValidator;
import com.company.ldap.core.utils.LdapUserWrapperMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.company.ldap.core.dao.LdapUserDao.NAME;

@Service(NAME)
public class LdapUserDao {

    public final static String NAME = "ldap_LdapUserDao";

    @Inject
    @Qualifier(LdapConstants.LDAP_TEMPLATE_BEAN_NAME)
    private LdapTemplate ldapTemplate;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    @Qualifier(LdapUserValidator.NAME)
    private LdapUserValidator ldapUserValidator;

    public ApplyMatchingRuleContext getLdapUserWrapper(String login) {
        //TODO:timelimit
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .countLimit(2)
                .filter(createUserBaseAndLoginFilter(login));
        List<LdapUserWrapper> list = ldapTemplate.search(query, new LdapUserWrapperMapper());
        LdapUserWrapper ldapUserWrapper = ldapUserValidator.validateLdapUserResult(login, list);
        return new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes());
    }

    public LdapUser findLdapUserByFilter(String filter, String login) {
        //TODO:timelimit
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .countLimit(2)
                .filter(addUserBaseAndLoginFilter(login, new HardcodedFilter(LdapEncoder.nameEncode(filter))));
        List<LdapUser> list = ldapTemplate.search(query, new LdapUserMapper());
        return ldapUserValidator.validateLdapUserResult(login, list);
    }


    //TODO: ldap injection
    private Filter createUserBaseAndLoginFilter(String login) {
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            return ef;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(ef);
        andFilter.and(new HardcodedFilter(LdapEncoder.nameEncode(ldapConfig.getUserBase())));

        return andFilter;
    }

    private Filter addUserBaseAndLoginFilter(String login, Filter filter) {
        Filter resultFilter = null;
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        resultFilter = ef;
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            AndFilter andFilter = new AndFilter();
            andFilter.and(ef);
            andFilter.and(new HardcodedFilter(LdapEncoder.nameEncode(ldapConfig.getUserBase())));
            resultFilter = andFilter;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(resultFilter);
        andFilter.and(filter);
        return andFilter;
    }
}

