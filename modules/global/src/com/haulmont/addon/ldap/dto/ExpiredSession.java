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
