package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    private static final int[] VERSION = new int[]{1,2,3,-998};
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    private static final File standardoptionsFile = FabricLoader.getInstance().getConfigDir().resolve("standardoptions.txt").toFile();
    private static String[] standardoptionsCache;
    private static final Map<File, Long> filesLastModifiedMap = new HashMap<>();
    private static final Field[] entityCulling = initializeEntityCulling();
    public static final OptionsCache optionsCache = new OptionsCache(client);
    public static String lastWorld;
    public static boolean changeOnFocus;
    public static boolean changeOnResize;
    public static boolean inPreview;
    private static boolean skipWhenPossible;
    @Nullable
    private static Integer renderDistanceOnWorldJoin;
    @Nullable
    private static Float entityDistanceScalingOnWorldJoin;
    @Nullable
    private static Integer fovOnWorldJoin;
    @Nullable
    private static Integer guiScaleOnWorldJoin;

    public static void load() {
        LOGGER.info("Loading StandardSettings...");

        long start = System.nanoTime();

        try {
            if (!standardoptionsFile.exists()) {
                standardoptionsCache = null;
                LOGGER.error("{} is missing", standardoptionsFile.getName());
                return;
            }

            // reload and cache standardoptions if necessary
            if (standardoptionsCache == null || wereFilesModified()) {
                LOGGER.info("Reloading & caching StandardSettings...");
                standardoptionsCache = readStandardOptions().toArray(new String[0]);
            } else if (skipWhenPossible & (inPreview | changeOnFocus)) {
                changeOnFocus = false;
                LOGGER.info("Skipped loading StandardSettings because {}", inPreview ? "last world was reset in preview" : "instance wasn't focused since last settings reset");
                return;
            }

            // cache options for last world before applying standardoptions to reload later if necessary
            // allows for verifiability when rejoining a world after accidentally quitting with Atum
            optionsCache.save(lastWorld);

            resetOnWorldJoinOptions();
            loadStandardOptions();
            LOGGER.info("Finished loading StandardSettings ({})", getMs(start));

            checkSettings();
        } catch (Exception e) {
            standardoptionsCache = null;
            LOGGER.error("Failed to load StandardSettings", e);
        }
    }

    // checks if standardoptions file chain has been modified
    private static boolean wereFilesModified() {
        return filesLastModifiedMap.entrySet().stream().anyMatch(entry -> !entry.getKey().isFile() || entry.getKey().lastModified() != entry.getValue());
    }

    // creates a standardoptions file chain by checking if the first line of a file points to another file directory
    private static List<String> readStandardOptions() throws IOException {
        filesLastModifiedMap.clear();
        List<String> lines;
        File file = standardoptionsFile;
        do {
            // save the last modified time of each file to be checked later
            filesLastModifiedMap.put(file, file.lastModified());

            lines = Files.readAllLines(file.toPath());
        } while (!lines.isEmpty() && (file = new File(lines.get(0))).isFile() && !filesLastModifiedMap.containsKey(file));
        return lines;
    }

    // load standardoptions from cache, the heart of the mod if you will
    private static void loadStandardOptions() {
        for (final String line : standardoptionsCache) {
            try {
                String[] strings = line.split(":", 2);

                // skip line if value is empty
                if (strings.length < 2 || (strings[1] = strings[1].trim()).isEmpty() && !strings[0].equals("fullscreenResolution")) {
                    continue;
                }

                String[] optionKey_split = strings[0].split("_", 2);

                switch (optionKey_split[0]) {
                    case "autoJump": options.autoJump = parseBoolean(strings[1]); break;
                    case "autoSuggestions": options.autoSuggestions = parseBoolean(strings[1]); break;
                    case "chatColors": options.chatColors = parseBoolean(strings[1]); break;
                    case "chatLinks": options.chatLinks = parseBoolean(strings[1]); break;
                    case "chatLinksPrompt": options.chatLinksPrompt = parseBoolean(strings[1]); break;
                    case "enableVsync": window.setVsync(options.enableVsync = parseBoolean(strings[1])); break;
                    case "entityShadows": options.entityShadows = parseBoolean(strings[1]); break;
                    case "forceUnicodeFont": ((MinecraftClientAccessor) client).standardSettings_initFont(options.forceUnicodeFont = parseBoolean(strings[1])); break;
                    case "discrete":
                        if (optionKey_split[1].equals("mouse_scroll")) {
                            options.discreteMouseScroll = parseBoolean(strings[1]);
                        } break;
                    case "invertYMouse": options.invertYMouse = parseBoolean(strings[1]); break;
                    case "reducedDebugInfo": options.reducedDebugInfo = parseBoolean(strings[1]); break;
                    case "showSubtitles": options.showSubtitles = parseBoolean(strings[1]); break;
                    case "touchscreen": options.touchscreen = parseBoolean(strings[1]); break;
                    case "fullscreen":
                        if (window.isFullscreen() != parseBoolean(strings[1])) {
                            window.toggleFullscreen();
                            options.fullscreen = window.isFullscreen();
                        } break;
                    case "bobView": options.bobView = parseBoolean(strings[1]); break;
                    case "toggleCrouch": options.sneakToggled = parseBoolean(strings[1]); break;
                    case "toggleSprint": options.sprintToggled = parseBoolean(strings[1]); break;
                    case "mouseSensitivity": options.mouseSensitivity = parseDouble(strings[1]); break;
                    case "fov":
                        float fov = parseFloat(strings[1]);
                        options.fov = fov < 5 ? fov * 40.0f + 70.0f : fov; break;
                    case "gamma": options.gamma = parseDouble(strings[1]); break;
                    case "renderDistance": options.viewDistance = parseInt(strings[1]); break;
                    case "entityDistanceScaling": options.entityDistanceScaling = parseFloat(strings[1]); break;
                    case "guiScale": window.setScaleFactor(window.calculateScaleFactor(options.guiScale = parseInt(strings[1]), options.forceUnicodeFont)); break;
                    case "particles": options.particles = ParticlesOption.byId(parseInt(strings[1])); break;
                    case "maxFps": window.setFramerateLimit(options.maxFps = parseInt(strings[1])); break;
                    case "graphicsMode": options.graphicsMode = GraphicsMode.byId(parseInt(strings[1])); break;
                    case "ao": options.ao = AoOption.getOption(parseInt(strings[1])); break;
                    case "renderClouds": options.cloudRenderMode = strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST; break;
                    case "attackIndicator": options.attackIndicator = AttackIndicator.byId(parseInt(strings[1])); break;
                    case "lang":
                        if (!strings[1].equals(options.language)) {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(strings[1]));
                            client.getLanguageManager().apply(client.getResourceManager());
                            options.language = client.getLanguageManager().getLanguage().getCode();
                        } break;
                    case "chatVisibility": options.chatVisibility = ChatVisibility.byId(parseInt(strings[1])); break;
                    case "chatOpacity": options.chatOpacity = parseDouble(strings[1]); break;
                    case "chatLineSpacing": options.chatLineSpacing = parseDouble(strings[1]); break;
                    case "textBackgroundOpacity": options.textBackgroundOpacity = parseDouble(strings[1]); break;
                    case "backgroundForChatOnly": options.backgroundForChatOnly = parseBoolean(strings[1]); break;
                    case "fullscreenResolution":
                        if (!strings[1].equals(window.getVideoMode().isPresent() ? window.getVideoMode().get().asString() : "")) {
                            window.setVideoMode(VideoMode.fromString(strings[1]));
                            window.applyVideoMode();
                        } break;
                    case "advancedItemTooltips": options.advancedItemTooltips = parseBoolean(strings[1]); break;
                    case "pauseOnLostFocus": options.pauseOnLostFocus = parseBoolean(strings[1]); break;
                    case "chatHeightFocused": options.chatHeightFocused = parseDouble(strings[1]); break;
                    case "chatDelay": options.chatDelay = parseDouble(strings[1]); break;
                    case "chatHeightUnfocused": options.chatHeightUnfocused = parseDouble(strings[1]); break;
                    case "chatScale": options.chatScale = parseDouble(strings[1]); break;
                    case "chatWidth": options.chatWidth = parseDouble(strings[1]); break;
                    case "mipmapLevels":
                        int mipmapLevels = parseInt(strings[1]);
                        if (options.mipmapLevels != mipmapLevels) {
                            client.resetMipmapLevels(options.mipmapLevels = mipmapLevels);
                            ((BakedModelManagerAccessor) client.getBakedModelManager()).standardSettings_apply(((BakedModelManagerAccessor) client.getBakedModelManager()).standardSettings_prepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                        } break;
                    case "mainHand": options.mainArm = "left".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT; break;
                    case "narrator": options.narrator = NarratorOption.byId(parseInt(strings[1])); break;
                    case "biomeBlendRadius": options.biomeBlendRadius = parseInt(strings[1]); break;
                    case "mouseWheelSensitivity": options.mouseWheelSensitivity = parseDouble(strings[1]); break;
                    case "rawMouseInput": window.setRawMouseMotion(options.rawMouseInput = parseBoolean(strings[1])); break;
                    case "key":
                        for (KeyBinding keyBinding : options.keysAll) {
                            if (optionKey_split[1].equals(keyBinding.getTranslationKey())) {
                                keyBinding.setBoundKey(InputUtil.fromTranslationKey(strings[1])); break;
                            }
                        } break;
                    case "soundCategory":
                        for (SoundCategory soundCategory : SoundCategory.values()) {
                            if (optionKey_split[1].equals(soundCategory.getName())) {
                                options.setSoundVolume(soundCategory, parseFloat(strings[1])); break;
                            }
                        } break;
                    case "modelPart":
                        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                            if (optionKey_split[1].equals(playerModelPart.getName())) {
                                options.setPlayerModelPart(playerModelPart, parseBoolean(strings[1])); break;
                            }
                        } break;
                    case "entityCulling": setEntityCulling(parseBoolean(strings[1])); break;
                    case "sneaking": options.keySneak.setPressed(options.sneakToggled && (parseBoolean(strings[1]) != options.keySneak.isPressed())); break;
                    case "sprinting": options.keySprint.setPressed(options.sprintToggled && (parseBoolean(strings[1]) != options.keySprint.isPressed())); break;
                    case "chunkborders":
                        if (client.debugRenderer.toggleShowChunkBorder() != parseBoolean(strings[1])) {
                            client.debugRenderer.toggleShowChunkBorder();
                        } break;
                    case "hitboxes": client.getEntityRenderManager().setRenderHitboxes(parseBoolean(strings[1])); break;
                    case "perspective": options.perspective = parseInt(strings[1]) % 3; break;
                    case "piedirectory":
                        if (strings[1].split("\\.")[0].equals("root")) {
                            ((MinecraftClientAccessor) client).standardSettings_setOpenProfilerSection(strings[1].replace('.', '\u001e'));
                        } break;
                    case "f1": options.hudHidden = parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin":
                        fov = parseFloat(strings[1]);
                        fovOnWorldJoin = fov < 5 ? (int) (fov * 40 + 70) : (int) fov; break;
                    case "guiScaleOnWorldJoin": guiScaleOnWorldJoin = parseInt(strings[1]); break;
                    case "renderDistanceOnWorldJoin": renderDistanceOnWorldJoin = parseInt(strings[1]); break;
                    case "entityDistanceScalingOnWorldJoin": entityDistanceScalingOnWorldJoin = parseFloat(strings[1]); break;
                    case "changeOnResize": changeOnResize = parseBoolean(strings[1]); break;
                    case "skipWhenPossible": skipWhenPossible = parseBoolean(strings[1]); break;
                }
                // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                // also has a few extra settings that can be reset that Minecraft doesn't save to options.txt, but are important in speedrunning
            } catch (Exception e) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    // actions when world finishes loading
    public static void onWorldLoad(String worldName) {
        // skip if the world was reset in preview (and with Atum)
        if (worldName.equals(lastWorld)) {
            saveToWorldFile(worldName);

            if (client.isWindowFocused()) {
                onWorldJoin();
            } else {
                changeOnFocus = true;
            }
        }
    }

    // load OnWorldJoin options if present
    public static void onWorldJoin() {
        long start = System.nanoTime();

        // needs to be reset at the beginning to prevent loop with changeOnResize and guiScaleOnWorldJoin
        changeOnFocus = false;

        if (renderDistanceOnWorldJoin != null && options.viewDistance != renderDistanceOnWorldJoin) {
            options.viewDistance = renderDistanceOnWorldJoin;
            client.worldRenderer.scheduleTerrainUpdate();
        }
        if (entityDistanceScalingOnWorldJoin != null) {
            options.entityDistanceScaling = entityDistanceScalingOnWorldJoin;
        }
        if (fovOnWorldJoin != null) {
            options.fov = fovOnWorldJoin;
        }
        if (guiScaleOnWorldJoin != null && options.guiScale != guiScaleOnWorldJoin) {
            options.guiScale = guiScaleOnWorldJoin;
            client.onResolutionChanged();
        }

        if (fovOnWorldJoin != null || guiScaleOnWorldJoin != null || entityDistanceScalingOnWorldJoin != null || renderDistanceOnWorldJoin != null) {
            resetOnWorldJoinOptions();
            options.write();
            LOGGER.info("Changed Settings on World Join ({})", getMs(start));
        }
    }

    // resets OnWorldJoin options to their default state
    private static void resetOnWorldJoinOptions() {
        fovOnWorldJoin = null;
        entityDistanceScalingOnWorldJoin = null;
        guiScaleOnWorldJoin = null;
        renderDistanceOnWorldJoin = null;
        changeOnResize = false;
        changeOnFocus = false;
        skipWhenPossible = true;
    }

    // makes sure the values are within the boundaries of vanilla minecraft / the speedrun.com rule set
    private static void checkSettings() {
        LOGGER.info("Checking and saving Settings...");

        long start = System.nanoTime();

        options.mouseSensitivity = check("Sensitivity", options.mouseSensitivity * 2, 0.0, 2.0, true) / 2;
        options.fov = (int) (double) check("FOV", options.fov, 30.0, 110.0, false);
        options.gamma = check("Brightness", options.gamma, 0.0, 5.0, true);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32, false);
        options.entityDistanceScaling = check("Entity Distance", options.entityDistanceScaling, 0.5f, 5.0f, true);
        float entityDistanceScalingOld = options.entityDistanceScaling;
        if (entityDistanceScalingOld != (options.entityDistanceScaling = (int) (options.entityDistanceScaling * 4) / 4.0f)) {
            LOGGER.warn("Entity Distance was set to a false interval ({})", entityDistanceScalingOld);
        }
        options.guiScale = check("GUI Scale", options.guiScale, 0, Integer.MAX_VALUE, false);
        window.setScaleFactor(window.calculateScaleFactor(options.guiScale, options.forceUnicodeFont));
        options.maxFps = check("Max Framerate", options.maxFps, 1, 260, false);
        window.setFramerateLimit(options.maxFps);
        options.biomeBlendRadius = check("Biome Blend", options.biomeBlendRadius, 0, 7, false);
        options.chatOpacity = check("Chat Text Opacity", options.chatOpacity, 0.0, 1.0, true);
        options.chatLineSpacing = check("(Chat) Line Spacing", options.chatLineSpacing, 0.0, 1.0, true);
        options.textBackgroundOpacity = check("Text Background Opacity", options.textBackgroundOpacity, 0.0, 1.0, true);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0.0, 1.0, false);
        options.chatDelay = check("Chat Delay", options.chatDelay,0.0,6.0, false);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0.0, 1.0, false);
        options.chatScale = check("Chat Text Size", options.chatScale, 0.0, 1.0, true);
        options.chatWidth = check("(Chat) Width", options.chatWidth, 0.0, 1.0, false);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4, false))) {
            client.resetMipmapLevels(options.mipmapLevels);
            ((BakedModelManagerAccessor) client.getBakedModelManager()).standardSettings_apply(((BakedModelManagerAccessor) client.getBakedModelManager()).standardSettings_prepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mouseWheelSensitivity = check("Scroll Sensitivity", options.mouseWheelSensitivity, 0.01, 10.0, false);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + SoundCategoryName.get(soundCategory), options.getSoundVolume(soundCategory), 0.0f, 1.0f, true));
        }

        if (renderDistanceOnWorldJoin != null) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32, false);
        }
        if (entityDistanceScalingOnWorldJoin != null) {
            entityDistanceScalingOnWorldJoin = check("Entity Distance (On World Join)", entityDistanceScalingOnWorldJoin, 0.5f, 5.0f, true);
            entityDistanceScalingOld = entityDistanceScalingOnWorldJoin;
            entityDistanceScalingOnWorldJoin = (int) (entityDistanceScalingOnWorldJoin * 4) / 4.0f;
            if (entityDistanceScalingOld != entityDistanceScalingOnWorldJoin) {
                LOGGER.warn("Entity Distance (On World Join) was set to a false interval ({})", entityDistanceScalingOld);
            }
        }
        if (fovOnWorldJoin != null) {
            fovOnWorldJoin = check("FOV (On World Join)", fovOnWorldJoin, 30, 110, false);
        }
        if (guiScaleOnWorldJoin != null) {
            guiScaleOnWorldJoin = check("GUI Scale (On World Join)", guiScaleOnWorldJoin, 0, Integer.MAX_VALUE, false);
        }

        options.write();

        LOGGER.info("Finished checking and saving Settings ({})", getMs(start));
    }

    // check methods return the value of the setting, adjusted to be in the given bounds
    // if a setting is outside the bounds, it also gives a log output to signal the value has been corrected
    private static <T extends Number> T check(String settingName, T setting, T min, T max, boolean percent) {
        if (setting.doubleValue() < min.doubleValue()) {
            LOGGER.warn(settingName + " was too low! ({})", percent ? asPercent(setting.doubleValue()) : setting);
            return min;
        }
        if (setting.doubleValue() > max.doubleValue()) {
            LOGGER.warn(settingName + " was too high! ({})", percent ? asPercent(setting.doubleValue()) : setting);
            return max;
        }
        return setting;
    }

    private static String asPercent(double value) {
        return ((value *= 100) == (int) value ? (int) value : value) + "%";
    }

    @SuppressWarnings("unused")
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

        private final String name;
        SoundCategoryName(String name) {
            this.name = name;
        }

        private static String get(SoundCategory soundCategory) {
            try {
                return SoundCategoryName.valueOf(soundCategory.name()).name;
            } catch (IllegalArgumentException e) {
                return soundCategory.toString();
            }
        }
    }

    // returns the contents for a new standardoptions.txt file
    private static String getStandardoptionsTxt() {
        List<String> lines = new ArrayList<>();

        lines.add("autoJump:" + options.autoJump);
        lines.add("autoSuggestions:" + options.autoSuggestions);
        lines.add("chatColors:" + options.chatColors);
        lines.add("chatLinks:" + options.chatLinks);
        lines.add("chatLinksPrompt:" + options.chatLinksPrompt);
        lines.add("enableVsync:" + options.enableVsync);
        lines.add("entityShadows:" + options.entityShadows);
        lines.add("forceUnicodeFont:" + options.forceUnicodeFont);
        lines.add("discrete_mouse_scroll:" + options.discreteMouseScroll);
        lines.add("invertYMouse:" + options.invertYMouse);
        lines.add("reducedDebugInfo:" + options.reducedDebugInfo);
        lines.add("showSubtitles:" + options.showSubtitles);
        lines.add("touchscreen:" + options.touchscreen);
        lines.add("fullscreen:" + options.fullscreen);
        lines.add("bobView:" + options.bobView);
        lines.add("toggleCrouch:" + options.sneakToggled);
        lines.add("toggleSprint:" + options.sprintToggled);
        lines.add("mouseSensitivity:" + options.mouseSensitivity);
        lines.add("fov:" + (options.fov - 70.0f) / 40.0f);
        lines.add("gamma:" + options.gamma);
        lines.add("renderDistance:" + options.viewDistance);
        lines.add("entityDistanceScaling:" + options.entityDistanceScaling);
        lines.add("guiScale:" + options.guiScale);
        lines.add("particles:" + options.particles.getId());
        lines.add("maxFps:" + options.maxFps);
        lines.add("graphicsMode:" + options.graphicsMode.getId());
        lines.add("ao:" + options.ao.getValue());
        lines.add("renderClouds:" + (options.cloudRenderMode == CloudRenderMode.FAST ? "fast" : options.cloudRenderMode == CloudRenderMode.FANCY));
        lines.add("attackIndicator:" + options.attackIndicator.getId());
        lines.add("lang:" + options.language);
        lines.add("chatVisibility:" + options.chatVisibility.getId());
        lines.add("chatOpacity:" + options.chatOpacity);
        lines.add("chatLineSpacing:" + options.chatLineSpacing);
        lines.add("textBackgroundOpacity:" + options.textBackgroundOpacity);
        lines.add("backgroundForChatOnly:" + options.backgroundForChatOnly);
        lines.add("fullscreenResolution:" + (options.fullscreenResolution == null ? "" : options.fullscreenResolution));
        lines.add("advancedItemTooltips:" + options.advancedItemTooltips);
        lines.add("pauseOnLostFocus:" + options.pauseOnLostFocus);
        lines.add("chatHeightFocused:" + options.chatHeightFocused);
        lines.add("chatDelay:" + options.chatDelay);
        lines.add("chatHeightUnfocused:" + options.chatHeightUnfocused);
        lines.add("chatScale:" + options.chatScale);
        lines.add("chatWidth:" + options.chatWidth);
        lines.add("mipmapLevels:" + options.mipmapLevels);
        lines.add("mainHand:" + (options.mainArm == Arm.LEFT ? "left" : "right"));
        lines.add("narrator:" + options.narrator.getId());
        lines.add("biomeBlendRadius:" + options.biomeBlendRadius);
        lines.add("mouseWheelSensitivity:" + options.mouseWheelSensitivity);
        lines.add("rawMouseInput:" + options.rawMouseInput);

        for (KeyBinding keyBinding : options.keysAll) {
            lines.add("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.getBoundKeyTranslationKey());
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            lines.add("soundCategory_" + soundCategory.getName() + ":" + options.getSoundVolume(soundCategory));
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            lines.add("modelPart_" + playerModelPart.getName() + ":" + options.getEnabledPlayerModelParts().contains(playerModelPart));
        }

        Boolean entityCulling = getEntityCulling();
        lines.add("entityCulling:" + (entityCulling != null ? entityCulling : ""));
        lines.add("sneaking:");
        lines.add("sprinting:");
        lines.add("chunkborders:");
        lines.add("hitboxes:");
        lines.add("perspective:");
        lines.add("piedirectory:");
        lines.add("f1:");
        lines.add("fovOnWorldJoin:");
        lines.add("guiScaleOnWorldJoin:");
        lines.add("renderDistanceOnWorldJoin:");
        lines.add("entityDistanceScalingOnWorldJoin:");
        lines.add("changeOnResize:false");
        lines.add("skipWhenPossible:true");

        return String.join(System.lineSeparator(), lines);
    }

    // save the standardoptions to world file for verification purposes
    private static void saveToWorldFile(String worldName) {
        if (standardoptionsCache != null) {
            try {
                long start = System.nanoTime();
                Path path = client.getLevelStorage().getSavesDirectory().resolve(worldName).resolve(standardoptionsFile.getName());
                Files.write(path, String.join(System.lineSeparator(), standardoptionsCache).getBytes(StandardCharsets.UTF_8));
                writeVersion(Files.getFileAttributeView(path, UserDefinedFileAttributeView.class));
                LOGGER.info("Saved {} to world file '{}' ({})", standardoptionsFile.getName(), worldName, getMs(start));
            } catch (Exception e) {
                LOGGER.error("Failed to save {} to world file '{}'", standardoptionsFile.getName(), worldName, e);
            }
        }
    }

    // this is done to be compatible with different versions of sodium
    private static Field[] initializeEntityCulling() {
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            for (Field optionsClass : SodiumGameOptions.class.getFields()) {
                for (Field option : optionsClass.getType().getFields()) {
                    if (option.getType().equals(boolean.class) && option.toString().toLowerCase(Locale.ROOT).contains("entityculling")) {
                        return new Field[]{option, optionsClass};
                    }
                }
            }
            LOGGER.warn("StandardSettings couldn't find Entity Culling option in sodium options");
        }
        return null;
    }

    protected static @Nullable Boolean getEntityCulling() {
        if (entityCulling != null) {
            try {
                return (boolean) entityCulling[0].get(entityCulling[1].get(SodiumClientMod.options()));
            } catch (Exception e) {
                LOGGER.error("StandardSettings failed to get Entity Culling", e);
            }
        }
        return null;
    }

    protected static void setEntityCulling(boolean value) {
        if (entityCulling != null) {
            if (!Boolean.valueOf(value).equals(getEntityCulling())) {
                try {
                    entityCulling[0].set(entityCulling[1].get(SodiumClientMod.options()), value);
                    SodiumClientMod.options().writeChanges();
                } catch (IOException e) {
                    LOGGER.error("StandardSettings failed to save sodium options", e);
                } catch (Exception e) {
                    LOGGER.error("StandardSettings failed to set Entity Culling to " + value, e);
                }
            }
        }
    }

    public static void initialize() {
        // create standardoptions.txt
        if (!standardoptionsFile.isFile()) {
            LOGGER.info("Creating {}...", standardoptionsFile.getName());

            long start = System.nanoTime();

            try {
                // create config directory if necessary
                if (!standardoptionsFile.getParentFile().mkdirs()) {
                    LOGGER.error("Failed to create config directory");
                    return;
                }
                // create file and mark with current StandardSettings version
                Files.write(standardoptionsFile.toPath(), getStandardoptionsTxt().getBytes());
                writeVersion(Files.getFileAttributeView(standardoptionsFile.toPath(), UserDefinedFileAttributeView.class));
                LOGGER.info("Finished creating {} ({})", standardoptionsFile.getName(), getMs(start));
            } catch (Exception e) {
                LOGGER.error("Failed to create {}", standardoptionsFile.getName(), e);
            }
            return;
        }

        // check the marked StandardSettings versions along the standardoptions file chain
        Map<UserDefinedFileAttributeView, int[]> fileVersionsMap = new HashMap<>();
        List<String> lines;
        List<File> fileChain = new ArrayList<>();
        try {
            // resolve standardoptions file chain
            File file = standardoptionsFile;
            do {
                lines = Files.readAllLines(file.toPath());
                fileChain.add(file);
            } while (!lines.isEmpty() && (file = new File(lines.get(0))).isFile() && !fileChain.contains(file));

            // get the StandardSettings versions marked to the files
            for (File file2 : fileChain) {
                UserDefinedFileAttributeView view = Files.getFileAttributeView(file2.toPath(), UserDefinedFileAttributeView.class);
                fileVersionsMap.put(view, readVersion(view));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to check file versions", e);
            return;
        }

        // Finds the highest StandardSettings version of the file chain
        int[] highestVersion = new int[]{1,2,0,0};
        for (int[] fileVersion : fileVersionsMap.values()) {
            if (checkVersion(highestVersion, fileVersion)) {
                highestVersion = fileVersion;
            }
        }

        // Update standardoptions file if necessary and update the StandardSettings versions marked to the file
        try {
            List<String> linesToAdd = updateStandardOptions(highestVersion, lines);
            if (linesToAdd != null) {
                Files.write(fileChain.get(fileChain.size() - 1).toPath(), (System.lineSeparator() + String.join(System.lineSeparator(), linesToAdd)).getBytes(), StandardOpenOption.APPEND);
                LOGGER.info("Finished updating {}", standardoptionsFile.getName());
            }
            for (Map.Entry<UserDefinedFileAttributeView, int[]> entry : fileVersionsMap.entrySet()) {
                if (checkVersion(entry.getValue())) {
                    writeVersion(entry.getKey());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update {}", standardoptionsFile.getName(), e);
        }
    }

    // marks the current StandardSettings version to the file
    private static void writeVersion(UserDefinedFileAttributeView view) throws IOException {
        view.write("standardsettings", StandardCharsets.UTF_8.encode(versionToString(VERSION)));
    }

    // reads the marked StandardSettings version from the file
    private static int[] readVersion(UserDefinedFileAttributeView view) {
        try {
            String name = "standardsettings";
            ByteBuffer buf = ByteBuffer.allocate(view.size(name));
            view.read(name, buf);
            buf.flip();
            String value = StandardCharsets.UTF_8.decode(buf).toString();
            return Stream.of(value.split("\\.")).mapToInt(Integer::parseInt).toArray();
        } catch (Exception e) {
            return new int[]{1,2,0,0};
        }
    }

    // checks if fileVersion is outdated and returns a list of new lines to add to the standardoptions file
    private static @Nullable List<String> updateStandardOptions(final int[] fileVersion, List<String> existingLines) {
        if (!checkVersion(fileVersion)) {
            return null;
        }
        LOGGER.warn("{} was marked with an outdated StandardSettings version ({}), updating now...", standardoptionsFile.getName(), versionToString(fileVersion));

        // remove the values from the lines
        if (existingLines != null) {
            existingLines = removeValues(existingLines);
        } else {
            existingLines = new ArrayList<>();
        }

        List<String> lines = new ArrayList<>();

        checking:
        {
            // add lines added in 1.2.3-pre1
            if (checkVersion(fileVersion, new int[]{1,2,3,-1000})) {
                List<String> newLines = new ArrayList<>();
                newLines.add("skipWhenPossible:true");
                if (!Collections.disjoint(removeValues(newLines), existingLines)) {
                    break checking;
                }
                lines.addAll(newLines);
            }
            // add lines added in the pre-releases of StandardSettings v1.2.1
            if (checkVersion(fileVersion, new int[]{1,2,1,-1000})) {
                List<String> newLines = new ArrayList<>();
                Boolean entityCulling = getEntityCulling();
                newLines.add("entityCulling:" + (entityCulling != null ? entityCulling : ""));
                newLines.add("f1:");
                newLines.add("guiScaleOnWorldJoin:");
                newLines.add("changeOnResize:false");
                if (!Collections.disjoint(removeValues(newLines), existingLines)) {
                    break checking;
                }
                lines.addAll(newLines);
            }
        }

        if (lines.isEmpty()) {
            LOGGER.info("Didn't find anything to update, good luck on the runs!");
            return null;
        }
        return lines;
    }

    private static List<String> removeValues(List<String> list) {
        list = new ArrayList<>(list);
        list.replaceAll(line -> line.split(":", 2)[0]);
        return list;
    }

    private static boolean checkVersion(final int[] versionToCheck) {
        return checkVersion(versionToCheck, VERSION);
    }

    // returns true when versionToCheck is older than versionToCompareTo
    private static boolean checkVersion(final int[] versionToCheck, final int[] versionToCompareTo) {
        final int maxLength = Math.max(versionToCheck.length, versionToCompareTo.length);
        for (int i = 0; i < maxLength; i++) {
            int v1 = versionToCheck.length > i ? versionToCheck[i] : 0;
            int v2 = versionToCompareTo.length > i ? versionToCompareTo[i] : 0;
            if (v1 != v2) {
                return v1 < v2;
            }
        }
        return false;
    }

    private static String versionToString(int[] version) {
        return String.join(".", Arrays.stream(version).mapToObj(String::valueOf).toArray(String[]::new));
    }

    // returns milliseconds that have passed since start (in nanoseconds) in a nice String
    private static String getMs(long startNano) {
        return (System.nanoTime() - startNano) / 1000000.0 + " ms";
    }
}