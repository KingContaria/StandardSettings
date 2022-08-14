package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4107;
import net.minecraft.class_4117;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final int[] version = new int[]{1,2,1,0};
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final class_4117 window = client.field_19944;
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static int renderDistanceOnWorldJoin;
    private static double fovOnWorldJoin;
    private static int guiScaleOnWorldJoin;
    public static OptionsCache optionsCache = new OptionsCache(client);
    public static String lastQuitWorld;
    public static String[] standardoptionsCache;
    private static Map<File, Long> filesLastModifiedMap;

    public static void load() {
        long start = System.nanoTime();

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;
        guiScaleOnWorldJoin = -1;
        changeOnResize = false;

        try {
            if (!standardoptionsFile.exists()) {
                standardoptionsCache = null;
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            if (lastQuitWorld != null) {
                optionsCache.save(lastQuitWorld);
                lastQuitWorld = null;
            }

            if (standardoptionsCache == null || wereFilesModified(filesLastModifiedMap)) {
                LOGGER.info("Reloading & caching StandardSettings...");
                List<String> lines = resolveGlobalFile(standardoptionsFile);
                if (lines == null) {
                    LOGGER.error("standardoptions.txt is empty");
                    return;
                }
                if (filesLastModifiedMap.size() > 1) {
                    LOGGER.info("Using global standardoptions file");
                }
                standardoptionsCache = lines.toArray(new String[0]);
            }
            load(standardoptionsCache);
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (Exception e) {
            standardoptionsCache = null;
            LOGGER.error("Failed to load StandardSettings", e);
        }
    }

    private static boolean wereFilesModified(Map<File, Long> map) {
        if (map == null) {
            return false;
        }
        AtomicBoolean wasModified = new AtomicBoolean(false);
        map.forEach((file, lastModified) -> wasModified.set(file.lastModified() != lastModified || !file.exists() || wasModified.get()));
        return wasModified.get();
    }

    private static List<String> resolveGlobalFile(File file) {
        filesLastModifiedMap = new HashMap<>();
        List<String> lines = null;
        do {
            filesLastModifiedMap.put(file, file.lastModified());
            try {
                lines = Files.readLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                break;
            }
        } while (lines != null && lines.size() > 0 && (file = new File(lines.get(0))).exists() && !filesLastModifiedMap.containsKey(file));
        return lines;
    }

    private static void load(String[] lines) {
        for (String line : lines) {
            try {
                String[] strings = line.split(":", 2);
                if (strings.length < 2 || (strings[1] = strings[1].trim()).equals("") && !strings[0].equals("fullscreenResolution")) {
                    continue;
                }
                String[] string0_split = strings[0].split("_", 2);
                switch (string0_split[0]) {
                    case "autoJump": options.field_14902 = Boolean.parseBoolean(strings[1]); break;
                    case "autoSuggestions": options.field_19978 = Boolean.parseBoolean(strings[1]); break;
                    case "chatColors": options.chatColor = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinks": options.chatLink = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinksPrompt": options.chatLinkPrompt = Boolean.parseBoolean(strings[1]); break;
                    case "enableVsync":
                        options.field_19991 = Boolean.parseBoolean(strings[1]);
                        client.field_19944.method_18306(); break;
                    case "useVbo": options.vbo = Boolean.parseBoolean(strings[1]); break;
                    case "entityShadows": options.entityShadows = Boolean.parseBoolean(strings[1]); break;
                    case "forceUnicodeFont": client.method_9391().method_18454(options.forceUnicode = Boolean.parseBoolean(strings[1])); break;
                    case "invertYMouse": options.invertYMouse = Boolean.parseBoolean(strings[1]); break;
                    case "reducedDebugInfo": options.reducedDebugInfo = Boolean.parseBoolean(strings[1]); break;
                    case "showSubtitles": options.field_13292 = Boolean.parseBoolean(strings[1]); break;
                    case "touchscreen": options.touchScreen = Boolean.parseBoolean(strings[1]); break;
                    case "fullscreen":
                        if (window.method_18316() != Boolean.parseBoolean(strings[1])) {
                            if (client.isWindowFocused()) {
                                window.method_18313();
                            } else {
                                LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        } break;
                    case "bobView": options.bobView = Boolean.parseBoolean(strings[1]); break;
                    case "mouseSensitivity": options.field_19988 = Double.parseDouble(strings[1]); break;
                    case "fov": options.field_19984 = Double.parseDouble(strings[1]) < 5 ? Double.parseDouble(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1]); break;
                    case "gamma": options.field_19985 = Double.parseDouble(strings[1]); break;
                    case "renderDistance": options.viewDistance = Integer.parseInt(strings[1]); break;
                    case "guiScale": options.guiScale = Integer.parseInt(strings[1]); break;
                    case "particles": options.particle = Integer.parseInt(strings[1]); break;
                    case "maxFps": options.maxFramerate = Integer.parseInt(strings[1]); break;
                    case "fancyGraphics": options.fancyGraphics = Boolean.parseBoolean(strings[1]); break;
                    case "ao": options.ao = Integer.parseInt(strings[1]); break;
                    case "renderClouds": options.cloudMode = strings[1].equals("true") ? 2 : strings[1].equals("false") ? 0 : 1; break;
                    case "attackIndicator": options.field_13290 = Integer.parseInt(strings[1]); break;
                    case "lang":
                        if (!options.language.equals(strings[1]) && ((LanguageManagerAccessor)client.getLanguageManager()).getLanguageDefs().containsKey(strings[1])) {
                            client.getLanguageManager().method_5939(((LanguageManagerAccessor)client.getLanguageManager()).getLanguageDefs().get(options.language = strings[1]));
                            client.getLanguageManager().reload(client.getResourceManager());
                        } break;
                    case "chatVisibility": options.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(Integer.parseInt(strings[1])); break;
                    case "chatOpacity": options.field_19989 = Double.parseDouble(strings[1]); break;
                    case "advancedItemTooltips": options.field_19992 = Boolean.parseBoolean(strings[1]); break;
                    case "pauseOnLostFocus": options.field_19973 = Boolean.parseBoolean(strings[1]); break;
                    case "chatHeightFocused": options.field_19977 = Double.parseDouble(strings[1]); break;
                    case "chatHeightUnfocused": options.field_19976 = Double.parseDouble(strings[1]); break;
                    case "chatScale": options.field_19974 = Double.parseDouble(strings[1]); break;
                    case "chatWidth": options.field_19975 = Double.parseDouble(strings[1]); break;
                    case "mipmapLevels":
                        if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = Integer.parseInt(strings[1]));
                            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
                            client.method_18222().reload(client.getResourceManager());
                        } break;
                    case "mainHand": options.field_13289 = "left".equalsIgnoreCase(strings[1]) ? HandOption.LEFT : HandOption.RIGHT; break;
                    case "narrator": options.field_15879 = Integer.parseInt(strings[1]); break;
                    case "biomeBlendRadius": options.field_19979 = Integer.parseInt(strings[1]); break;
                    case "mouseWheelSensitivity": options.field_19980 = Double.parseDouble(strings[1]); break;
                    case "key":
                        for (KeyBinding keyBinding : options.keysAll) {
                            if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                keyBinding.method_18170(class_4107.method_18156(strings[1])); break;
                            }
                        } break;
                    case "soundCategory":
                        for (SoundCategory soundCategory : SoundCategory.values()) {
                            if (string0_split[1].equals(soundCategory.getName())) {
                                options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                            }
                        } break;
                    case "modelPart":
                        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                            if (string0_split[1].equals(playerModelPart.getName())) {
                                options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                            }
                        } break;
                    case "chunkborders":
                        if (client.field_13282.method_13451() != Boolean.parseBoolean(strings[1])) {
                            client.field_13282.method_13451();
                        } break;
                    case "hitboxes": client.getEntityRenderManager().method_10205(Boolean.parseBoolean(strings[1])); break;
                    case "perspective": options.perspective = Integer.parseInt(strings[1]) % 3; break;
                    case "piedirectory":
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace('.','\u001e')); break;
                    case "f1": options.field_19987 = Boolean.parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin": fovOnWorldJoin = Double.parseDouble(strings[1]) < 5 ? Double.parseDouble(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1]); break;
                    case "guiScaleOnWorldJoin": guiScaleOnWorldJoin = Integer.parseInt(strings[1]); break;
                    case "renderDistanceOnWorldJoin": renderDistanceOnWorldJoin = Integer.parseInt(strings[1]); break;
                    case "changeOnResize": changeOnResize = Boolean.parseBoolean(strings[1]); break;
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                }
            } catch (Exception e) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.viewDistance = renderDistanceOnWorldJoin;
        }
        if (fovOnWorldJoin != 0) {
            options.field_19984 = fovOnWorldJoin;
        }
        if (guiScaleOnWorldJoin != -1) {
            options.guiScale = guiScaleOnWorldJoin;
            window.method_18314();
        }
        if (fovOnWorldJoin != 0 || guiScaleOnWorldJoin != -1 || renderDistanceOnWorldJoin != 0) {
            fovOnWorldJoin = renderDistanceOnWorldJoin = 0;
            guiScaleOnWorldJoin = -1;
            options.save();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.field_19988 = check("Sensitivity", options.field_19988 * 2, 0, 2, true) / 2;
        options.field_19984 = Math.round(check("FOV", options.field_19984, 30, 110, false));
        options.field_19985 = check("Brightness", options.field_19985, 0, 5, true);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = check("GUI Scale", options.guiScale, 0, Integer.MAX_VALUE);
        options.maxFramerate = check("Max Framerate", options.maxFramerate, 1, 260);
        options.field_19979 = check("Biome Blend", options.field_19979, 0, 7);
        options.field_19989 = check("Chat Text Opacity", options.field_19989, 0, 1, true);
        options.field_19977 = check("(Chat) Focused Height", options.field_19977, 0, 1, false);
        options.field_19976 = check("(Chat) Unfocused Height", options.field_19976, 0, 1, false);
        options.field_19974 = check("(Chat) Scale", options.field_19974, 0, 1, true);
        options.field_19975 = check("(Chat) Width", options.field_19975, 0, 1, false);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels);
            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
            client.method_18222().reload(client.getResourceManager());
        }
        options.field_19980 = check("Scroll Sensitivity", options.field_19980, 0.01, 10, false);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + SoundCategoryName.valueOf(soundCategory.name()).assignedName, options.getSoundVolume(soundCategory), 0, 1, true));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110, false));
        }
        if (guiScaleOnWorldJoin != -1) {
            guiScaleOnWorldJoin = check("GUI Scale (On World Join)", guiScaleOnWorldJoin, 0, Integer.MAX_VALUE);
        }

        window.method_18314();
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double check(String settingName, double setting, double min, double max, boolean percent) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", percent ? asPercent(setting) : setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", percent ? asPercent(setting) : setting);
            return max;
        }
        return setting;
    }

    private static float check(String settingName, float setting, float min, float max, boolean percent) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", percent ? asPercent(setting) : setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", percent ? asPercent(setting) : setting);
            return max;
        }
        return setting;
    }

    private static int check(String settingName, int setting, int min, int max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }

    private static String asPercent(double value) {
        return value * 100 == (int) (value * 100) ? (int) (value * 100) + "%" : value * 100 + "%";
    }

    private enum SoundCategoryName {
        MASTER("Master Volume"),
        MUSIC("Music"),
        RECORDS("Jukebox/Note Blocks"),
        WEATHER("Weather"),
        BLOCKS("Blocks"),
        HOSTILE("Hostile Creatures"),
        NEUTRAL("Friendly Creatures"),
        PLAYERS("Players"),
        AMBIENT("Ambient/Environment"),
        VOICE("Voice/Speech");

        private final String assignedName;
        SoundCategoryName(String name) {
            this.assignedName = name;
        }
    }

    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder("autoJump:" + options.field_14902 + l +
                "autoSuggestions:" + options.field_19978 + l +
                "chatColors:" + options.chatColor + l +
                "chatLinks:" + options.chatLink + l +
                "chatLinksPrompt:" + options.chatLinkPrompt + l +
                "enableVsync:" + options.field_19991 + l +
                "useVbo:" + options.vbo + l +
                "entityShadows:" + options.entityShadows + l +
                "forceUnicodeFont:" + options.forceUnicode + l +
                "invertYMouse:" + options.invertYMouse + l +
                "reducedDebugInfo:" + options.reducedDebugInfo + l +
                "showSubtitles:" + options.field_13292 + l +
                "touchscreen:" + options.touchScreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "mouseSensitivity:" + options.field_19988 + l +
                "fov:" + (options.field_19984 - 70.0f) / 40.0f + l +
                "gamma:" + options.field_19985 + l +
                "renderDistance:" + options.viewDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.particle + l +
                "maxFps:" + options.maxFramerate + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.ao + l +
                "renderClouds:" + (options.cloudMode == 1 ? "fast" : options.cloudMode == 2) + l +
                "attackIndicator:" + options.field_13290 + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.chatVisibilityType.getId() + l +
                "chatOpacity:" + options.field_19989 + l +
                "advancedItemTooltips:" + options.field_19992 + l +
                "pauseOnLostFocus:" + options.field_19973 + l +
                "chatHeightFocused:" + options.field_19977 + l +
                "chatHeightUnfocused:" + options.field_19976 + l +
                "chatScale:" + options.field_19974 + l +
                "chatWidth:" + options.field_19975 + l +
                "mipmapLevels:" + options.mipmapLevels + l +
                "mainHand:" + (options.field_13289 == HandOption.LEFT ? "left" : "right") + l +
                "narrator:" + options.field_15879 + l +
                "biomeBlendRadius:" + options.field_19979 + l +
                "mouseWheelSensitivity:" + options.field_19980 + l);
        for (KeyBinding keyBinding : options.keysAll) {
            string.append("key_").append(keyBinding.getTranslationKey()).append(":").append(keyBinding.method_18176()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.getEnabledPlayerModelParts().contains(playerModelPart)).append(l);
        }
        string.append("chunkborders:").append(l).append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("changeOnResize:false");

        return string.toString();
    }

    public static List<String> checkVersion(int[] fileVersion, List<String> existingLines) {
        if (compareVersions(fileVersion, version)) {
            LOGGER.warn("standardoptions.txt was marked with an outdated StandardSettings version ({}), updating now...", String.join(".", Arrays.stream(fileVersion).mapToObj(String::valueOf).toArray(String[]::new)));
        } else {
            return null;
        }

        int i = 0;
        if (existingLines != null) {
            for (String line : existingLines) {
                existingLines.set(i++, line.split(":", 2)[0]);
            }
        }

        List<String> lines = new ArrayList<>();

        if (compareVersions(fileVersion, new int[]{1,2,1,-1000})) {
            if (existingLines != null && (existingLines.contains("entityCulling") || existingLines.contains("f1") || existingLines.contains("guiScaleOnWorldJoin") || existingLines.contains("changeOnResize"))) {
                LOGGER.info("Didn't find anything to update, good luck on the runs!");
                return lines;
            }
            lines.add("f1:");
            lines.add("guiScaleOnWorldJoin:");
            lines.add("changeOnResize:false");
        }
        if (lines.size() == 0) {
            LOGGER.info("Didn't find anything to update, good luck on the runs!");
            return null;
        }
        return lines;
    }

    // returns true when versionToCheck is older than versionToCompareTo
    public static boolean compareVersions(int[] versionToCheck, int[] versionToCompareTo) {
        int i = 0;
        for (int v1 : versionToCheck) {
            int v2 = versionToCompareTo[i++];
            if (v1 == v2) continue;
            return v1 < v2;
        }
        return false;
    }

    public static String getVersion() {
        return String.join(".", Arrays.stream(version).mapToObj(String::valueOf).toArray(String[]::new));
    }
}