package com.haulmont.addon.ldap.datatype;

import com.haulmont.chile.core.annotations.JavaClass;
import com.haulmont.chile.core.datatypes.impl.IntegerDatatype;
import org.dom4j.Element;

import java.text.ParseException;
import java.util.Locale;

@JavaClass(Integer.class)
public class PositiveNumberDataType extends IntegerDatatype {

    public PositiveNumberDataType(Element element) {
        super(element);
    }

    @Override
    public Integer parse(String value, Locale locale) throws ParseException {
        Integer val = super.parse(value);
        if (val != null && val <= 0) {
            throw new ParseException(String.format("Value must be positive: \"%s\"", value), 0);
        }
        return val;
    }
}
