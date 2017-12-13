package org.fao.gift.config;

import org.fao.gift.common.dto.MainConfig;

import javax.inject.Inject;

public class ForumConfigStore implements org.fao.gift.forum.client.ForumConfigStore {

    @Inject
    MainConfig config;

    @Override
    public String get(String key) {
        return config.get(key);
    }

    @Override
    public String get(String key, String defaultValue) {
        return config.get(key) != null ? config.get(key) : defaultValue;
    }

    @Override
    public void set(String key, String value) {
        config.add(key, value);
    }
}
