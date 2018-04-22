package com.haulmont.addon.ldap.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ExpiredSession implements Serializable {
    private final UUID uuid;
    private final String login;
    private final long createTsMillis;

    public ExpiredSession(UUID uuid, String login, long createTsMillis) {
        this.uuid = uuid;
        this.login = login;
        this.createTsMillis = createTsMillis;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLogin() {
        return login;
    }

    public long getCreateTsMillis() {
        return createTsMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpiredSession that = (ExpiredSession) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
