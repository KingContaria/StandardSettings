package me.contaria.standardsettings;

import me.contaria.standardsettings.mixin.accessors.BakedModelManagerAccessor;
import me.contaria.standardsettings.options.StandardSetting;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StandardSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    public static StandardSettingsConfig config;

    @Nullable
    private static StandardSettingsCache settingsCache;

    public static void reset() {
        config.update();
        for (StandardSetting<?> setting : config.standardSettings) {
            setting.resetOption();
        }
        updateSettings(MinecraftClient.getInstance());
        LOGGER.info("Loaded StandardSettings");
    }

    private static void updateSettings(MinecraftClient client) {
        client.getWindow().applyVideoMode();

        LanguageManager languageManager = client.getLanguageManager();
        if (!languageManager.getLanguage().getCode().equals(client.options.language)) {
            languageManager.setLanguage(languageManager.getLanguage(client.options.language));
            languageManager.apply(client.getResourceManager());
        }

        BakedModelManagerAccessor bakedModelManager = (BakedModelManagerAccessor) client.getBakedModelManager();
        if (bakedModelManager.getMipmap() != client.options.mipmapLevels) {
            client.resetMipmapLevels(client.options.mipmapLevels);
            bakedModelManager.standardsettings$apply(bakedModelManager.standardsettings$prepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }

        KeyBinding.updateKeysByCode();
    }

    public static void onWorldJoin() {
        for (StandardSetting<?> setting : config.standardSettingsOnWorldJoin) {
            setting.resetOption();
        }
        LOGGER.info("Loaded StandardSettings on World Join");
    }

    public static void createCache(String worldName) {
        if (worldName != null) {
            settingsCache = new StandardSettingsCache(worldName);
            LOGGER.info("Cached options for '{}'", worldName);
        }
    }

    public static void loadCache(String worldName) {
        if (settingsCache != null && worldName.equals(settingsCache.getId())) {
            settingsCache.load();
            settingsCache = null;
            LOGGER.info("Restored cached options for '{}'", worldName);
        }
    }

    public static void saveToWorldFile(String worldName) {
        List<String> options = new ArrayList<>();
        for (StandardSetting<?> setting : config.standardSettings) {
            options.add(setting.getID() + ":" + setting.getOption());
        }
        for (StandardSetting<?> setting : config.standardSettingsOnWorldJoin) {
            options.add(setting.getID() + ":" + setting.get());
        }
        try {
            Files.write(MinecraftClient.getInstance().getLevelStorage().getSavesDirectory().resolve(worldName).resolve("standardoptions.txt"), options, StandardCharsets.UTF_8);
            LOGGER.info("Saved standardoptions to world file.");
        } catch (IOException e) {
            LOGGER.warn("Failed to save standardoptions to world file.");
        }
    }

    public static Text getTextWithoutPrefix(Text text, Text prefix) {
        if (!text.copy().equals(prefix.copy())) {
            return text;
        }

        List<Text> prefixSiblings = prefix.getSiblings();
        List<Text> textSiblings = text.getSiblings();

        if (prefixSiblings.size() >= textSiblings.size()) {
            return new LiteralText("");
        }

        List<Text> restText = textSiblings.subList(prefixSiblings.size(), textSiblings.size());

        MutableText newText = restText.remove(0).shallowCopy();
        for (Text t : restText) {
            newText.append(t);
        }
        return newText;
    }
}
