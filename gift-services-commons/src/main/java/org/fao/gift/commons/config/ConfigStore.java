package org.fao.gift.commons.config;

public interface ConfigStore {

    String get(String key);

    String get(String key, String defaultValue);

    void set(String key, String value);
}
