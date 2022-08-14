package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

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
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static int renderDistanceOnWorldJoin;
    private static float fovOnWorldJoin;
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
                if (strings.length < 2 || (strings[1] = strings[1].trim()).equals("")) {
                    continue;
                }
                String[] string0_split = strings[0].split("_", 2);
                switch (string0_split[0]) {
                    case "chatColors": options.chatColor = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinks": options.chatLink = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinksPrompt": options.chatLinkPrompt = Boolean.parseBoolean(strings[1]); break;
                    case "enableVsync": Display.setVSyncEnabled(options.vsync = Boolean.parseBoolean(strings[1])); break;
                    case "useVbo": options.vbo = Boolean.parseBoolean(strings[1]); break;
                    case "entityShadows": options.entityShadows = Boolean.parseBoolean(strings[1]); break;
                    case "forceUnicodeFont": client.textRenderer.method_960(client.getLanguageManager().method_5938() || (options.forceUnicode = Boolean.parseBoolean(strings[1]))); break;
                    case "invertYMouse": options.invertYMouse = Boolean.parseBoolean(strings[1]); break;
                    case "reducedDebugInfo": options.reducedDebugInfo = Boolean.parseBoolean(strings[1]); break;
                    case "showSubtitles": options.field_13292 = Boolean.parseBoolean(strings[1]); break;
                    case "touchscreen": options.touchScreen = Boolean.parseBoolean(strings[1]); break;
                    case "fullscreen":
                        if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                            if (Display.isActive()) {
                                client.toggleFullscreen();
                            } else {
                                LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        } break;
                    case "bobView": options.bobView = Boolean.parseBoolean(strings[1]); break;
                    case "anaglyph3d":
                        if (options.anaglyph3d != (options.anaglyph3d = Boolean.parseBoolean(strings[1]))) {
                            client.getTextureManager().reload(client.getResourceManager());
                        } break;
                    case "mouseSensitivity": options.sensitivity = Float.parseFloat(strings[1]); break;
                    case "fov": options.fov = Float.parseFloat(strings[1]) < 5 ? Float.parseFloat(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1]); break;
                    case "gamma": options.gamma = Float.parseFloat(strings[1]); break;
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
                    case "chatOpacity": options.chatOpacity = Float.parseFloat(strings[1]); break;
                    case "advancedItemTooltips": options.advancedItemTooltips = Boolean.parseBoolean(strings[1]); break;
                    case "pauseOnLostFocus": options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]); break;
                    case "chatHeightFocused": options.chatHeightFocused = Float.parseFloat(strings[1]); break;
                    case "chatHeightUnfocused": options.chatHeightUnfocused = Float.parseFloat(strings[1]); break;
                    case "chatScale": options.chatScale = Float.parseFloat(strings[1]); break;
                    case "chatWidth": options.chatWidth = Float.parseFloat(strings[1]); break;
                    case "mipmapLevels":
                        if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = Integer.parseInt(strings[1]));
                            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
                            ((MinecraftClientAccessor) client).getModelManager().reload(client.getResourceManager());
                        } break;
                    case "mainHand": options.field_13289 = "left".equalsIgnoreCase(strings[1]) ? HandOption.LEFT : HandOption.RIGHT; break;
                    case "key":
                        for (KeyBinding keyBinding : options.keysAll) {
                            if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                keyBinding.setCode(Integer.parseInt(strings[1])); break;
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
                    case "hitboxes": client.getEntityRenderManager().method_10205(Boolean.parseBoolean(strings[1])); break;
                    case "perspective": options.perspective = Integer.parseInt(strings[1]) % 3; break;
                    case "piedirectory":
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace('.','\u001e')); break;
                    case "f1": options.hudHidden = Boolean.parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin": fovOnWorldJoin = Float.parseFloat(strings[1]) < 5 ? Float.parseFloat(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1]); break;
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
            options.fov = fovOnWorldJoin;
        }
        if (guiScaleOnWorldJoin != -1) {
            options.guiScale = guiScaleOnWorldJoin;
            if (client.currentScreen != null) {
                Window window = new Window(client);
                int j = window.getWidth();
                int k = window.getHeight();
                client.currentScreen.init(client, j, k);
            }
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

        options.sensitivity = check("Sensitivity", options.sensitivity * 2, 0, 2, true) / 2;
        options.fov = Math.round(check("FOV", options.fov, 30, 110, false));
        options.gamma = check("Brightness", options.gamma, 0, 5, true);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = check("GUI Scale", options.guiScale, 0, Integer.MAX_VALUE);
        options.maxFramerate = check("Max Framerate", options.maxFramerate, 1, 260);
        options.chatOpacity = check("Chat Text Opacity", options.chatOpacity, 0, 1, true);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0, 1, false);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1, false);
        options.chatScale = check("(Chat) Scale", options.chatScale, 0, 1, true);
        options.chatWidth = check("(Chat) Width", options.chatWidth, 0, 1, false);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels);
            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
            ((MinecraftClientAccessor) client).getModelManager().reload(client.getResourceManager());
        }
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

        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
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
        StringBuilder string = new StringBuilder("chatColors:" + options.chatColor + l +
                "chatLinks:" + options.chatLink + l +
                "chatLinksPrompt:" + options.chatLinkPrompt + l +
                "enableVsync:" + options.vsync + l +
                "useVbo:" + options.vbo + l +
                "entityShadows:" + options.entityShadows + l +
                "forceUnicodeFont:" + options.forceUnicode + l +
                "invertYMouse:" + options.invertYMouse + l +
                "reducedDebugInfo:" + options.reducedDebugInfo + l +
                "showSubtitles:" + options.field_13292 + l +
                "touchscreen:" + options.touchScreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "anaglyph3d:" + options.anaglyph3d + l +
                "mouseSensitivity:" + options.sensitivity + l +
                "fov:" + (options.fov - 70.0f) / 40.0f + l +
                "gamma:" + options.gamma + l +
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
                "chatOpacity:" + options.chatOpacity + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "chatHeightFocused:" + options.chatHeightFocused + l +
                "chatHeightUnfocused:" + options.chatHeightUnfocused + l +
                "chatScale:" + options.chatScale + l +
                "chatWidth:" + options.chatWidth + l +
                "mipmapLevels:" + options.mipmapLevels + l +
                "mainHand:" + (options.field_13289 == HandOption.LEFT ? "left" : "right") + l);
        for (KeyBinding keyBinding : options.keysAll) {
            string.append("key_").append(keyBinding.getTranslationKey()).append(":").append(keyBinding.getCode()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.getEnabledPlayerModelParts().contains(playerModelPart)).append(l);
        }
        string.append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("changeOnResize:false");

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