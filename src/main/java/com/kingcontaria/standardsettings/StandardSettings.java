package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.accessors.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_1659;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.Window;
import net.minecraft.util.logging.LogManager;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StandardSettings {

    public static final int[] version = new int[]{1,2,2,0};
    public static final LogManager LOGGER = MinecraftClient.getInstance().getLogManager();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    public static final File standardoptionsFile = new File(FabricLoader.getInstance().getConfigDir().resolve("standardoptions.txt").toUri());
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static Optional<Integer> renderDistanceOnWorldJoin = Optional.empty();
    private static Optional<Float> fovOnWorldJoin = Optional.empty();
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
                LOGGER.severe("standardoptions.txt is missing");
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
                    LOGGER.severe("standardoptions.txt is empty");
                    return;
                }
                standardoptionsCache = lines.toArray(new String[0]);
            }
            load(standardoptionsCache);
            LOGGER.info("Finished loading StandardSettings (" + (System.nanoTime() - start) / 1000000.0f + " ms)");
        } catch (Exception e) {
            standardoptionsCache = null;
            LOGGER.severe("Failed to load StandardSettings", e);
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
        boolean reload = false;

        for (String line : lines) {
            try {
                String[] strings = line.split(":", 2);

                // skip line if value is empty
                if (strings.length < 2 || (strings[1] = strings[1].trim()).equals("")) {
                    continue;
                }
                String[] string0_split = strings[0].split("_", 2);

                switch (string0_split[0]) {
                    case "music": options.musicVolume = Float.parseFloat(strings[1]); break;
                    case "sound": options.soundVolume = Float.parseFloat(strings[1]); break;
                    case "invertYMouse": options.invertYMouse = Boolean.parseBoolean(strings[1]); break;
                    case "mouseSensitivity": options.sensitivity = Float.parseFloat(strings[1]); break;
                    case "fov": options.fov = Float.parseFloat(strings[1]); break;
                    case "gamma": options.gamma = Float.parseFloat(strings[1]); break;
                    case "viewDistance": options.renderDistance = Integer.parseInt(strings[1]); break;
                    case "guiScale": options.guiScale = Integer.parseInt(strings[1]); break;
                    case "particles": options.particle = Integer.parseInt(strings[1]); break;
                    case "bobView": options.bobView = Boolean.parseBoolean(strings[1]); break;
                    case "anaglyph3d":
                        if (options.anaglyph3d != (options.anaglyph3d = Boolean.parseBoolean(strings[1]))) {
                            client.getTextureManager().reload(client.getResourceManager());
                        } break;
                    case "advancedOpengl": options.advancedOpengl = Boolean.parseBoolean(strings[1]); break;
                    case "fpsLimit": options.maxFramerate = Integer.parseInt(strings[1]); break;
                    case "difficulty": options.difficultyLevel = Integer.parseInt(strings[1]); break;
                    case "fancyGraphics": options.fancyGraphics = Boolean.parseBoolean(strings[1]); break;
                    case "ao": options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1])); break;
                    case "clouds": options.renderClouds = Boolean.parseBoolean(strings[1]); break;
                    case "skin":
                        if (!options.currentTexturePackName.equals(strings[1])) {
                            Optional<class_1659> selectedPack = client.getResourcePackLoader().method_5904().stream().filter(obj -> strings[1].equals(((class_1659) obj).method_5914())).findFirst();
                            if (selectedPack.isPresent() || "Default".equals(strings[1])) {
                                if (selectedPack.isPresent()) {
                                    client.getResourcePackLoader().method_5903(selectedPack.get());
                                } else {
                                    client.getResourcePackLoader().method_5903();
                                }
                                client.stitchTextures();
                                options.currentTexturePackName = client.getResourcePackLoader().method_5906();
                            } else {
                                StandardSettings.LOGGER.warn("resource pack " + strings[1] + " was not found");
                            }
                        } break;
                    case "lang":
                        if (!options.language.equals(strings[1]) && ((LanguageManagerAccessor)client.getLanguageManager()).getField_6653().containsKey(strings[1])) {
                            client.getLanguageManager().method_5939(((LanguageManagerAccessor)client.getLanguageManager()).getField_6653().get(options.language = strings[1]));
                            client.getLanguageManager().reload(client.getResourceManager());
                            client.textRenderer.method_960(client.getLanguageManager().method_5938());
                            client.textRenderer.setRightToLeft(client.getLanguageManager().method_5941());
                        } break;
                    case "chatVisibility": options.chatVisibility = Integer.parseInt(strings[1]); break;
                    case "chatColors": options.chatColor = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinks": options.chatLink = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinksPrompt": options.chatLinkPrompt = Boolean.parseBoolean(strings[1]); break;
                    case "chatOpacity": options.chatOpacity = Float.parseFloat(strings[1]); break;
                    case "fullscreen":
                        if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                            if (Display.isActive()) {
                                client.toggleFullscreen();
                                options.fullscreen = Boolean.parseBoolean(strings[1]);
                            } else {
                                LOGGER.severe("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        } break;
                    case "enableVsync": Display.setVSyncEnabled(options.vsync = Boolean.parseBoolean(strings[1])); break;
                    case "advancedItemTooltips": options.advancedItemTooltips = Boolean.parseBoolean(strings[1]); break;
                    case "pauseOnLostFocus": options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]); break;
                    case "showCape": options.field_5053 = Boolean.parseBoolean(strings[1]); break;
                    case "touchscreen": options.touchScreen = Boolean.parseBoolean(strings[1]); break;
                    case "chatHeightFocused": options.chatHeightFocused = Float.parseFloat(strings[1]); break;
                    case "chatHeightUnfocused": options.chatHeightUnfocused = Float.parseFloat(strings[1]); break;
                    case "chatScale": options.chatScale = Float.parseFloat(strings[1]); break;
                    case "chatWidth": options.chatWidth = Float.parseFloat(strings[1]); break;
                    case "key":
                        for (KeyBinding keyBinding : options.keysAll) {
                            if (string0_split[1].equals(keyBinding.translationKey)) {
                                keyBinding.code = Integer.parseInt(strings[1]); break;
                            }
                        } break;
                    case "hitboxes": EntityRenderDispatcher.field_5192 = Boolean.parseBoolean(strings[1]); break;
                    case "perspective": options.perspective = Integer.parseInt(strings[1]) % 3; break;
                    case "piedirectory":
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]); break;
                    case "f1": options.hudHidden = Boolean.parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin": fovOnWorldJoin = Optional.of(Float.parseFloat(strings[1])); break;
                    case "guiScaleOnWorldJoin": guiScaleOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "renderDistanceOnWorldJoin": renderDistanceOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "changeOnResize": changeOnResize = Boolean.parseBoolean(strings[1]); break;
                }
                // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                // also has a few extra settings that can be reset that Minecraft doesn't save to options.txt, but are important in speedrunning
            } catch (Exception e) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    // load OnWorldJoin options if present
    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        renderDistanceOnWorldJoin.ifPresent(renderDistance -> options.renderDistance = renderDistance);
        fovOnWorldJoin.ifPresent(fov -> options.fov = fov);
        guiScaleOnWorldJoin.ifPresent(guiScale -> {
            options.guiScale = guiScale;
            if (client.currentScreen != null) {
                Window window = new Window(client.options, client.width, client.height);
                client.currentScreen.init(client, window.getWidth(), window.getHeight());
            }
        });

        if (fovOnWorldJoin.isPresent() || guiScaleOnWorldJoin.isPresent() || renderDistanceOnWorldJoin.isPresent()) {
            emptyOnWorldJoinOptions();
            options.save();
            LOGGER.info("Changed Settings on World Join (" + (System.nanoTime() - start) / 1000000.0f + " ms)");
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

        options.sensitivity = check("Sensitivity", options.sensitivity * 2, 0, 2, true) / 2;
        options.fov = check("FOV", options.fov, 0, 1, true);
        options.gamma = check("Brightness", options.gamma, 0, 5, true);
        options.renderDistance = check("Render Distance", options.renderDistance, 0, 3);
        options.guiScale = check("GUI Scale", options.guiScale, 0, 3);
        options.maxFramerate = check("Max Framerate", options.maxFramerate, 0, 2);
        options.chatOpacity = check("(Chat) Opacity", options.chatOpacity, 0, 1, true);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0, 1, false);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1, false);
        options.chatScale = check("(Chat) Scale", options.chatScale, 0, 1, true);
        options.chatWidth = check("(Chat) Width", options.chatWidth, 0, 1, false);
        options.musicVolume = check("Music", options.musicVolume, 0, 1, true);
        options.soundVolume = check("Sound", options.soundVolume, 0, 1, true);

        if (renderDistanceOnWorldJoin.isPresent()) {
            renderDistanceOnWorldJoin = Optional.of(check("Render Distance (On World Join)", renderDistanceOnWorldJoin.get(), 2, 16));
        }
        if (fovOnWorldJoin.isPresent()) {
            fovOnWorldJoin = Optional.of(check("FOV (On World Join)", fovOnWorldJoin.get(), 0, 1, false));
        }
        if (guiScaleOnWorldJoin.isPresent()) {
            guiScaleOnWorldJoin = Optional.of(check("GUI Scale (On World Join)", guiScaleOnWorldJoin.get(), 0, 3));
        }

        options.save();

        LOGGER.info("Finished checking and saving Settings (" + (System.nanoTime() - start) / 1000000.0f + " ms)");
    }

    // check methods return the value of the setting, adjusted to be in the given bounds
    // if a setting is outside the bounds, it also gives a log output to signal the value has been corrected
    private static float check(String settingName, float setting, float min, float max, boolean percent) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! (" + (percent ? asPercent(setting) : setting) + ")");
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! (" + (percent ? asPercent(setting) : setting) + ")");
            return max;
        }
        return setting;
    }

    private static int check(String settingName, int setting, int min, int max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    private static String asPercent(double value) {
        return value * 100 == (int) (value * 100) ? (int) (value * 100) + "%" : value * 100 + "%";
    }

    // returns the contents for a new standardoptions.txt file
    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder(
                "music:" + options.musicVolume + l +
                "sound:" + options.soundVolume + l +
                "chatColors:" + options.chatColor + l +
                "chatLinks:" + options.chatLink + l +
                "chatLinksPrompt:" + options.chatLinkPrompt + l +
                "enableVsync:" + options.vsync + l +
                "invertYMouse:" + options.invertYMouse + l +
                "touchscreen:" + options.touchScreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "anaglyph3d:" + options.anaglyph3d + l +
                "mouseSensitivity:" + options.sensitivity + l +
                "fov:" + options.fov + l +
                "gamma:" + options.gamma + l +
                "viewDistance:" + options.renderDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.particle + l +
                "maxFps:" + options.maxFramerate + l +
                "difficulty:" + options.difficultyLevel + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.ao + l +
                "clouds:" + options.renderClouds + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.chatVisibility + l +
                "chatOpacity:" + options.chatOpacity + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "showCape:" + options.field_5053 + l +
                "chatHeightFocused:" + options.chatHeightFocused + l +
                "chatHeightUnfocused:" + options.chatHeightUnfocused + l +
                "chatScale:" + options.chatScale + l +
                "chatWidth:" + options.chatWidth + l);
        for (KeyBinding keyBinding : options.keysAll) {
            string.append("key_").append(keyBinding.translationKey).append(":").append(keyBinding.code).append(l);
        }
        string.append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("changeOnResize:false");

        return string.toString();
    }

    public static List<String> checkVersion(int[] fileVersion, List<String> existingLines) {
        if (compareVersions(fileVersion, version)) {
            LOGGER.warn("standardoptions.txt was marked with an outdated StandardSettings version (" + String.join(".", Arrays.stream(fileVersion).mapToObj(String::valueOf).toArray(String[]::new) + "), updating now..."));
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