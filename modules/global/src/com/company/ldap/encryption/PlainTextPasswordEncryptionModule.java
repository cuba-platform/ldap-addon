package com.company.ldap.encryption;

import com.haulmont.cuba.core.global.HashDescriptor;
import com.haulmont.cuba.core.sys.encryption.EncryptionModule;
import com.haulmont.cuba.security.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

import static com.company.ldap.encryption.PlainTextPasswordEncryptionModule.NAME;

/**
 * Задача данного класса НЕ хэшировать пароль передаваемый из ГУИ а передавать его в AuthenticationProvider как есть. По сути обертка над используемым механизмом хэширования
 * Далее в LdapExtensionLoginPasswordAuthenticationProvider перед проверкой пароля он будет принудительно захеширован используя getSuperPlainHash
 * Имя этого компонента должно быть прописано в app.properties и web-app.properties (cuba.passwordEncryptionModule)
 *
 */
@Component(NAME)
public class PlainTextPasswordEncryptionModule implements EncryptionModule {

    public static final String NAME ="ldap_PlainTextPasswordEncryptionModule";

    @Inject
    @Qualifier("cuba_Sha1EncryptionModule")
    private EncryptionModule encryptionModule;

    @Override
    public String getPlainHash(String content) {
        return content;
    }

    @Override
    public String getHashMethod() {
        return encryptionModule.getHashMethod();
    }

    @Override
    public HashDescriptor getHash(String content) {
        return encryptionModule.getHash(content);
    }

    @Override
    public String getPasswordHash(UUID userId, String password) {
        return encryptionModule.getPasswordHash(userId, password);
    }

    @Override
    public String getHash(String content, String salt) {
        return encryptionModule.getHash(content, salt);
    }

    @Override
    public boolean checkPassword(User user, String givenPassword) {
        return encryptionModule.checkPassword(user, givenPassword);
    }

    public String getSuperPlainHash(String content) {
        return encryptionModule.getPlainHash(content);
    }

}
