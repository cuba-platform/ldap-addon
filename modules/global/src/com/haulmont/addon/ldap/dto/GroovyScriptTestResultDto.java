package com.haulmont.addon.ldap.dto;

import java.io.Serializable;

public class GroovyScriptTestResultDto implements Serializable {
    private GroovyScriptTestResult result;
    private String errorText;

    public GroovyScriptTestResultDto(GroovyScriptTestResult result, String errorTetx) {
        this.result = result;
        this.errorText = errorTetx;
    }

    public GroovyScriptTestResult getResult() {
        return result;
    }

    public String getErrorText() {
        return errorText;
    }
}
