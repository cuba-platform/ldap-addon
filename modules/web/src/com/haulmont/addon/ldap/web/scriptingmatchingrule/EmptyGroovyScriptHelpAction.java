package com.haulmont.addon.ldap.web.scriptingmatchingrule;

import com.haulmont.cuba.gui.components.AbstractAction;

public abstract class EmptyGroovyScriptHelpAction extends AbstractAction {

    public EmptyGroovyScriptHelpAction(String id) {
        super(id);
    }

    @Override
    public String getIcon() {
        return "icons/question-white.png";
    }


}
