package me.contaria.standardsettings;

import me.contaria.standardsettings.options.StandardSetting;

import java.util.HashSet;
import java.util.Set;

public class StandardSettingsCache {
    private final String id;
    private final Set<Entry<?>> cache;

    public StandardSettingsCache(String id) {
        this.id = id;
        this.cache = new HashSet<>();
        for (StandardSetting<?> setting : StandardSettings.config.standardSettings) {
            this.cache.add(new Entry<>(setting));
        }
    }

    public String getId() {
        return this.id;
    }

    public void load() {
        for (Entry<?> cacheEntry : this.cache) {
            cacheEntry.load();
        }
    }

    private static class Entry<T> {
        private final StandardSetting<T> setting;
        private final T value;

        private Entry(StandardSetting<T> setting) {
            this.setting = setting;
            this.value = setting.getOption();
        }

        private void load() {
            this.setting.setOption(this.value);
        }
    }
}
