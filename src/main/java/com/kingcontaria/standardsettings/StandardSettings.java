package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.accessors.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_4107;
import net.minecraft.class_4115;
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

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final int[] version = new int[]{1,2,2,0};
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final class_4117 window = client.field_19944;
    public static final File standardoptionsFile = new File(FabricLoader.getInstance().getConfigDir().resolve("standardoptions.txt").toUri());
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static Optional<Integer> renderDistanceOnWorldJoin = Optional.empty();
    private static Optional<Double> fovOnWorldJoin = Optional.empty();
    private static Optional<Integer> guiScaleOnWorldJoin = Optional.empty();
    public static OptionsCache optionsCache = new OptionsCache(client);
    public static String lastWorld;
    public static String[] standardoptionsCache;
    public static Map<File, Long> filesLastModifiedMap;

    public static void load() {
        long start = System.nanoTime();

        emptyOnWorldJoinOptions();

        try {
            if (!standardoptionsFile.exists()) {
                standardoptionsCache = null;
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            // caches options for last world before applying standardoptions to reload later if necessary
            // allows for verifiability when rejoining a world after accidentally quitting with Atum
            if (lastWorld != null) {
                optionsCache.save(lastWorld);
                lastWorld = null;
            }

            // reload and cache standardoptions if necessary
            if (standardoptionsCache == null || wereFilesModified(filesLastModifiedMap)) {
                LOGGER.info("Reloading & caching StandardSettings...");
                List<String> lines = resolveGlobalFile(standardoptionsFile);
                if (lines == null) {
                    LOGGER.error("standardoptions.txt is empty");
                    return;
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

    // checks if standardoptions file chain has been modified
    private static boolean wereFilesModified(Map<File, Long> map) {
        if (map == null) {
            return true;
        }
        boolean wereFilesModified = false;
        for (Map.Entry<File, Long> entry : map.entrySet()) {
            wereFilesModified |= !entry.getKey().exists() || entry.getKey().lastModified() != entry.getValue();
        }
        return wereFilesModified;
    }

    // creates a standardoptions file chain by checking if the first line of a file points to another file directory
    public static List<String> resolveGlobalFile(File file) {
        filesLastModifiedMap = new HashMap<>();
        List<String> lines = null;
        do {
            // save the last modified time of each file to be checked later
            filesLastModifiedMap.put(file, file.lastModified());

            try {
                lines = Files.readLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                break;
            }
        } while (lines != null && lines.size() > 0 && (file = new File(lines.get(0))).exists() && !filesLastModifiedMap.containsKey(file));
        return lines;
    }

    // load standardoptions from cache, the heart of the mod if you will
    private static void load(String[] lines) {
        for (String line : lines) {
            try {
                String[] strings = line.split(":", 2);

                // skip line if value is empty
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
                    case "fullscreenResolution":
                        if (!strings[1].equals(window.method_18310().isPresent() ? window.method_18310().get().method_18287() : "")) {
                            try {
                                String fullscreenResolution = class_4115.method_18281(strings[1]).get().toString();
                                String resolution = null;
                                for (int i = 0; true; i++) {
                                    String newResolution = window.method_18294(i);
                                    if (newResolution.equals(resolution)) {
                                        window.method_18303(0);
                                        break;
                                    }
                                    resolution = newResolution;
                                    if (resolution.equals(fullscreenResolution)) {
                                        window.method_18303(i + 1);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                window.method_18303(0);
                            }
                            window.method_18312();
                        } break;
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
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]); break;
                    case "f1": options.field_19987 = Boolean.parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin": fovOnWorldJoin = Optional.of(Double.parseDouble(strings[1]) < 5 ? Double.parseDouble(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1])); break;
                    case "guiScaleOnWorldJoin": guiScaleOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "renderDistanceOnWorldJoin": renderDistanceOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "changeOnResize": changeOnResize = Boolean.parseBoolean(strings[1]); break;
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                    // also has a few extra settings that can be reset that Minecraft doesn't save to options.txt, but are important in speedrunning
                }
            } catch (Exception e) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    // load OnWorldJoin options if present
    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        renderDistanceOnWorldJoin.ifPresent(viewDistance -> {
            options.viewDistance = viewDistance;
            client.worldRenderer.scheduleTerrainUpdate();
        });
        fovOnWorldJoin.ifPresent(fov -> options.field_19984 = fov);
        guiScaleOnWorldJoin.ifPresent(guiScale -> {
            options.guiScale = guiScale;
            window.method_18314();
        });

        if (fovOnWorldJoin.isPresent() || guiScaleOnWorldJoin.isPresent() || renderDistanceOnWorldJoin.isPresent()) {
            emptyOnWorldJoinOptions();
            options.save();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    // resets OnWorldJoin options to their default (empty) state
    private static void emptyOnWorldJoinOptions() {
        fovOnWorldJoin = Optional.empty();
        guiScaleOnWorldJoin = Optional.empty();
        renderDistanceOnWorldJoin = Optional.empty();
        changeOnResize = false;
        changeOnWindowActivation = false;
    }

    // makes sure the values are within the boundaries of vanilla minecraft / the speedrun.com rule set
    public static void checkSettings() {
        long start = System.nanoTime();

        options.field_19988 = check("Sensitivity", options.field_19988 * 2, 0, 2, true) / 2;
        options.field_19984 = (int) (check("FOV", options.field_19984, 30, 110, false));
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

        if (renderDistanceOnWorldJoin.isPresent()) {
            renderDistanceOnWorldJoin = Optional.of(check("Render Distance (On World Join)", renderDistanceOnWorldJoin.get(), 2, 32));
        }
        if (fovOnWorldJoin.isPresent()) {
            fovOnWorldJoin = Optional.of((double) (int) check("FOV (On World Join)", fovOnWorldJoin.get(), 30, 110, false));
        }
        if (guiScaleOnWorldJoin.isPresent()) {
            guiScaleOnWorldJoin = Optional.of(check("GUI Scale (On World Join)", guiScaleOnWorldJoin.get(), 0, Integer.MAX_VALUE));
        }

        window.method_18314();
        options.save();

        LOGGER.info("Finished checking and saving Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    // check methods return the value of the setting, adjusted to be in the given bounds
    // if a setting is outside the bounds, it also gives a log output to signal the value has been corrected
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

    // returns the contents for a new standardoptions.txt file
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
                "fullscreenResolution:" + (options.field_19990 == null ? "" : options.field_19990) + l +
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

        // remove the values from the lines
        if (existingLines != null) {
            existingLines.replaceAll(line -> line.split(":", 2)[0]);
        }

        List<String> lines = new ArrayList<>();

        checking:
        {
            // add lines add in StandardSettings v1.2.2-pre1
            if (compareVersions(fileVersion, new int[]{1,2,2,-1000})) {
                if (existingLines != null && existingLines.contains("fullscreenResolution")) {
                    break checking;
                }
                lines.add("fullscreenResolution:" + (options.field_19990 == null ? "" : options.field_19990));
            }
            // add lines added in the pre-releases of StandardSettings v1.2.1
            if (compareVersions(fileVersion, new int[]{1, 2, 1, -1000})) {
                if (existingLines != null && (existingLines.contains("entityCulling") || existingLines.contains("f1") || existingLines.contains("guiScaleOnWorldJoin") || existingLines.contains("changeOnResize"))) {
                    break checking;
                }
                lines.add("f1:");
                lines.add("guiScaleOnWorldJoin:");
                lines.add("changeOnResize:false");
            }
        }

        if (lines.size() == 0) {
            LOGGER.info("Didn't find anything to update, good luck on the runs!");
            return null;
        }
        return lines;
    }

    // returns true when versionToCheck is older than versionToCompareTo
    public static boolean compareVersions(int[] versionToCheck, int[] versionToCompareTo) {
        for (int i = 0; i < Math.max(versionToCheck.length, versionToCompareTo.length); i++) {
            int v1 = versionToCheck.length <= i ? 0 : versionToCheck[i];
            int v2 = versionToCompareTo.length <= i ? 0 : versionToCompareTo[i];
            if (v1 == v2) continue;
            return v1 < v2;
        }
        return false;
    }

    public static String getVersion() {
        return String.join(".", Arrays.stream(version).mapToObj(String::valueOf).toArray(String[]::new));
    }
}