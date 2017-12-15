package org.fao.gift.config;

import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.forum.client.ForumConfigStore;
import org.fao.gift.notification.NotificationConfigStore;

import javax.inject.Inject;

public class ConfigStore implements ForumConfigStore, NotificationConfigStore {

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
