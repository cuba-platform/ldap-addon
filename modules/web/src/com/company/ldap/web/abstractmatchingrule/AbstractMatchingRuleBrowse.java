package com.company.ldap.web.abstractmatchingrule;

import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;

import javax.inject.Named;
import java.util.Map;

public class AbstractMatchingRuleBrowse extends AbstractLookup {

    @Named("abstractMatchingRulesTable.create")
    CreateAction createAction;

    @Named("abstractMatchingRulesTable.edit")
    EditAction editAction;

    @Named("abstractMatchingRulesTable.remove")
    RemoveAction removeAction;

    @Named("custTextField")
    TextField textField;

    @Named("abstractMatchingRulesTable")
    Table<AbstractMatchingRule> abstractMatchingRulesTable;

    public void customEditAction() {


        AbstractMatchingRule rule = abstractMatchingRulesTable.getSingleSelected();
        if (rule != null) {
            if (MatchingRuleType.FIXED.equals(rule.getRuleType())) {
                editAction.setWindowId("ldap$FixedMatchingRule.edit");
            }
            if (MatchingRuleType.SIMPLE.equals(rule.getRuleType())) {
                editAction.setWindowId("ldap$SimpleMatchingRule.edit");
            }

            editAction.actionPerform(this);
        }
    }

    public void customCreateAction() {
        if (textField.getValue().equals("0")) {
            createAction.setWindowId("ldap$FixedMatchingRule.edit");
        }
        if (textField.getValue().equals("1")) {
            createAction.setWindowId("ldap$SimpleMatchingRule.edit");
        }

        if (textField.getValue().equals("0")) {
            System.out.println("2328478298492");
        }

        if (textField.getValue().equals("0")) {
            System.out.println("2328478298492");
        }


        createAction.actionPerform(this);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);//listener в датасорс itemchangelistener
        abstractMatchingRulesTable.addAction(new EditAction(abstractMatchingRulesTable) {
            @Override
            public String getWindowId() {
                AbstractMatchingRule rule = abstractMatchingRulesTable.getSingleSelected();
                if (rule != null) {
                    if (MatchingRuleType.FIXED.equals(rule.getRuleType())) {
                        return ("ldap$FixedMatchingRule.edit");
                    }
                    if (MatchingRuleType.SIMPLE.equals(rule.getRuleType())) {
                        return ("ldap$SimpleMatchingRule.edit");
                    }
                }
                return "";
            }
        });//getwindowid //Metadata -> getMetClass
    }

}