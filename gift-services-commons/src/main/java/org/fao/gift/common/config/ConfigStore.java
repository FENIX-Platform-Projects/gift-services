package org.fao.gift.common.config;

public interface ConfigStore {

    String get(String key);

    String get(String key, String defaultValue);

    void set(String key, String value);
}
