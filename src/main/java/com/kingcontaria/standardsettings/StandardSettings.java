package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final int[] version = new int[]{1,2,1,-995};
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static int renderDistanceOnWorldJoin;
    private static int simulationDistanceOnWorldJoin;
    private static double entityDistanceScalingOnWorldJoin;
    private static int fovOnWorldJoin;
    private static int guiScaleOnWorldJoin;
    public static OptionsCache optionsCache = new OptionsCache(client);
    public static String lastQuitWorld;
    public static String[] standardoptionsCache;
    private static Map<File, Long> filesLastModifiedMap;

    public static void load() {
        long start = System.nanoTime();

        entityDistanceScalingOnWorldJoin = fovOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;
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
                    case "key" -> {
                        for (KeyBinding keyBinding : options.allKeys) {
                            if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                keyBinding.setBoundKey(InputUtil.fromTranslationKey(strings[1])); break;
                            }
                        }
                    }
                    case "soundCategory" -> {
                        for (SoundCategory soundCategory : SoundCategory.values()) {
                            if (string0_split[1].equals(soundCategory.getName())) {
                                options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                            }
                        }
                    }
                    case "modelPart" -> {
                        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                            if (string0_split[1].equals(playerModelPart.getName())) {
                                options.togglePlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                            }
                        }
                    }
                    case "autoJump" -> options.getAutoJump().setValue(Boolean.parseBoolean(strings[1]));
                    case "autoSuggestions" -> options.getAutoSuggestions().setValue(Boolean.parseBoolean(strings[1]));
                    case "chatColors" -> options.getChatColors().setValue(Boolean.parseBoolean(strings[1]));
                    case "chatLinks" -> options.getChatLinks().setValue(Boolean.parseBoolean(strings[1]));
                    case "chatLinksPrompt" -> options.getChatLinksPrompt().setValue(Boolean.parseBoolean(strings[1]));
                    case "enableVsync" -> options.getEnableVsync().setValue(Boolean.parseBoolean(strings[1]));
                    case "entityShadows" -> options.getEntityShadows().setValue(Boolean.parseBoolean(strings[1]));
                    case "forceUnicodeFont" -> options.getForceUnicodeFont().setValue(Boolean.parseBoolean(strings[1]));
                    case "discrete" -> options.getDiscreteMouseScroll().setValue(Boolean.parseBoolean(strings[1]));
                    case "invertYMouse" -> options.getInvertYMouse().setValue(Boolean.parseBoolean(strings[1]));
                    case "reducedDebugInfo" -> options.getReducedDebugInfo().setValue(Boolean.parseBoolean(strings[1]));
                    case "showSubtitles" -> options.getShowSubtitles().setValue(Boolean.parseBoolean(strings[1]));
                    case "directionalAudio" -> options.getDirectionalAudio().setValue(Boolean.parseBoolean(strings[1]));
                    case "touchscreen" -> options.getTouchscreen().setValue(Boolean.parseBoolean(strings[1]));
                    case "fullscreen" -> {
                        if (window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                            if (client.isWindowFocused()) {
                                window.toggleFullscreen();
                                options.getFullscreen().setValue(window.isFullscreen());
                            } else {
                                LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        }
                    }
                    case "bobView" -> options.getBobView().setValue(Boolean.parseBoolean(strings[1]));
                    case "toggleCrouch" -> options.getSneakToggled().setValue(Boolean.parseBoolean(strings[1]));
                    case "toggleSprint" -> options.getSprintToggled().setValue(Boolean.parseBoolean(strings[1]));
                    case "darkMojangStudiosBackground" -> options.getMonochromeLogo().setValue(Boolean.parseBoolean(strings[1]));
                    case "hideLightningFlashes" -> options.getHideLightningFlashes().setValue(Boolean.parseBoolean(strings[1]));
                    case "mouseSensitivity" -> options.getMouseSensitivity().setValue(Double.parseDouble(strings[1]));
                    case "fov" -> options.getFov().setValue((int) (Double.parseDouble(strings[1]) * 40.0f + 70.0f));
                    case "screenEffectScale" -> options.getDistortionEffectScale().setValue(Double.parseDouble(strings[1]));
                    case "fovEffectScale" -> options.getFovEffectScale().setValue(Double.parseDouble(strings[1]));
                    case "darknessEffectScale" -> options.getDarknessEffectScale().setValue(Double.parseDouble(strings[1]));
                    case "gamma" -> options.getGamma().setValue(Double.parseDouble(strings[1]));
                    case "renderDistance" -> options.getViewDistance().setValue(Integer.parseInt(strings[1]));
                    case "entityDistanceScaling" -> options.getEntityDistanceScaling().setValue(Double.parseDouble(strings[1]));
                    case "guiScale" -> options.getGuiScale().setValue(Integer.parseInt(strings[1]));
                    case "particles" -> options.getParticles().setValue(ParticlesMode.byId(Integer.parseInt(strings[1])));
                    case "maxFps" -> options.getMaxFps().setValue(Integer.parseInt(strings[1]));
                    case "graphicsMode" -> options.getGraphicsMode().setValue(GraphicsMode.byId(Integer.parseInt(strings[1])));
                    case "ao" -> options.getAo().setValue(AoMode.byId(Integer.parseInt(strings[1])));
                    case "renderClouds" -> options.getCloudRenderMod().setValue(strings[1].equals("\"true\"") ? CloudRenderMode.FANCY : strings[1].equals("\"false\"") ? CloudRenderMode.OFF : CloudRenderMode.FAST);
                    case "attackIndicator" -> options.getAttackIndicator().setValue(AttackIndicator.byId(Integer.parseInt(strings[1])));
                    case "lang" -> {
                        client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(strings[1]));
                        client.getLanguageManager().reload(client.getResourceManager());
                        options.language = client.getLanguageManager().getLanguage().getCode();
                    }
                    case "chatVisibility" -> options.getChatVisibility().setValue(ChatVisibility.byId(Integer.parseInt(strings[1])));
                    case "chatOpacity" -> options.getChtOpacity().setValue(Double.parseDouble(strings[1]));
                    case "chatLineSpacing" -> options.getChatLineSpacing().setValue(Double.parseDouble(strings[1]));
                    case "textBackgroundOpacity" -> options.getTextBackgroundOpacity().setValue(Double.parseDouble(strings[1]));
                    case "backgroundForChatOnly" -> options.getBackgroundForChatOnly().setValue(Boolean.parseBoolean(strings[1]));
                    case "fullscreenResolution" -> {
                        if (!strings[1].equals(options.fullscreenResolution)) {
                            if (strings[1].equals("")) {
                                window.setVideoMode(Optional.empty());
                                window.applyVideoMode(); break;
                            }
                            for (int i = 0; i < window.getMonitor().getVideoModeCount(); i++) {
                                if (window.getMonitor().getVideoMode(i).asString().equals(strings[1])) {
                                    window.setVideoMode(Optional.ofNullable(window.getMonitor().getVideoMode(i)));
                                    window.applyVideoMode(); break;
                                }
                            }
                            LOGGER.warn("Could not resolve Fullscreen Resolution: " + strings[1]);
                        }
                    }
                    case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                    case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                    case "chatHeightFocused" -> options.getChatHeightFocused().setValue(Double.parseDouble(strings[1]));
                    case "chatDelay" -> options.getChatDelay().setValue(Double.parseDouble(strings[1]));
                    case "chatHeightUnfocused" -> options.getChatHeightUnfocused().setValue(Double.parseDouble(strings[1]));
                    case "chatScale" -> options.getChatScale().setValue(Double.parseDouble(strings[1]));
                    case "chatWidth" -> options.getChatWidth().setValue(Double.parseDouble(strings[1]));
                    case "mipmapLevels" -> {
                        if (options.getMipmapLevels().getValue() != Integer.parseInt(strings[1])) {
                            options.getMipmapLevels().setValue(Integer.parseInt(strings[1]));
                            client.setMipmapLevels(options.getMipmapLevels().getValue());
                            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                        }
                    }
                    case "mainHand" -> options.getMainArm().setValue("\"left\"".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT);
                    case "narrator" -> options.getNarrator().setValue(NarratorMode.byId(Integer.parseInt(strings[1])));
                    case "biomeBlendRadius" -> options.getBiomeBlendRadius().setValue(Integer.parseInt(strings[1]));
                    case "mouseWheelSensitivity" -> options.getMouseWheelSensitivity().setValue(Double.parseDouble(strings[1]));
                    case "rawMouseInput" -> options.getRawMouseInput().setValue(Boolean.parseBoolean(strings[1]));
                    case "showAutosaveIndicator" -> options.getShowAutosaveIndicator().setValue(Boolean.parseBoolean(strings[1]));
                    case "chatPreview" -> options.getChatPreview().setValue(Boolean.parseBoolean(strings[1]));
                    case "onlyShowSecureChat" -> options.getOnlyShowSecureChat().setValue(Boolean.parseBoolean(strings[1]));
                    case "entityCulling" -> {
                        if (FabricLoader.getInstance().getModContainer("sodium").isPresent()) {
                            if (SodiumClientMod.options().performance.useEntityCulling != (SodiumClientMod.options().performance.useEntityCulling = Boolean.parseBoolean(strings[1]))) {
                                SodiumClientMod.options().writeChanges();
                            }
                        }
                    }
                    case "sneaking" -> {
                        if (options.getSneakToggled().getValue() && (Boolean.parseBoolean(strings[1]) != options.sneakKey.isPressed())) {
                            options.sneakKey.setPressed(true);
                        }
                    }
                    case "sprinting" -> {
                        if (options.getSprintToggled().getValue() && (Boolean.parseBoolean(strings[1]) != options.sprintKey.isPressed())) {
                            options.sprintKey.setPressed(true);
                        }
                    }
                    case "chunkborders" -> {
                        if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                            client.debugRenderer.toggleShowChunkBorder();
                        }
                    }
                    case "hitboxes" -> client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                    case "perspective" -> options.setPerspective(Perspective.values()[Integer.parseInt(strings[1]) % 3]);
                    case "piedirectory" -> {
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace('.','\u001e'));
                    }
                    case "f1" -> options.hudHidden = Boolean.parseBoolean(strings[1]);
                    case "fovOnWorldJoin" -> fovOnWorldJoin = Integer.parseInt(strings[1]);
                    case "guiScaleOnWorldJoin" -> guiScaleOnWorldJoin = Integer.parseInt(strings[1]);
                    case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                    case "simulationDistanceOnWorldJoin" -> simulationDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                    case "entityDistanceScalingOnWorldJoin" -> entityDistanceScalingOnWorldJoin = Double.parseDouble(strings[1]);
                    case "changeOnResize" -> changeOnResize = Boolean.parseBoolean(strings[1]);
                }
                // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
            } catch (Exception exception) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.getViewDistance().setValue(renderDistanceOnWorldJoin);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            options.getSimulationDistance().setValue(simulationDistanceOnWorldJoin);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            options.getEntityDistanceScaling().setValue(entityDistanceScalingOnWorldJoin);
        }
        if (fovOnWorldJoin != 0) {
            options.getFov().setValue(fovOnWorldJoin);
        }
        if (guiScaleOnWorldJoin != -1) {
            options.getGuiScale().setValue(guiScaleOnWorldJoin);
            client.onResolutionChanged();
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0 || simulationDistanceOnWorldJoin != 0 || entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = fovOnWorldJoin = 0;
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.getMouseSensitivity().setValue(check("Sensitivity", options.getMouseSensitivity().getValue() * 2, 0, 2, true) / 2);
        options.getFov().setValue(check("FOV", options.getFov().getValue(), 30, 110));
        options.getDistortionEffectScale().setValue(check("Distortion Effects", options.getDistortionEffectScale().getValue(), 0, 1, true));
        options.getFovEffectScale().setValue(check("FOV Effects", options.getFovEffectScale().getValue(),0,1, true));
        options.getGamma().setValue(check("Brightness", options.getGamma().getValue(), 0, 1, true));
        options.getViewDistance().setValue(check("Render Distance", options.getViewDistance().getValue(), 2, 32));
        options.getSimulationDistance().setValue(check("Simulation Distance", options.getSimulationDistance().getValue(), 5, 32));
        options.getEntityDistanceScaling().setValue(check("Entity Distance", options.getEntityDistanceScaling().getValue(), 0.5f, 5, true));
        double entityDistanceScalingTemp = options.getEntityDistanceScaling().getValue();
        options.getEntityDistanceScaling().setValue(Math.round(options.getEntityDistanceScaling().getValue() * 4) / 4.0D);
        if (entityDistanceScalingTemp != options.getEntityDistanceScaling().getValue()) {
            LOGGER.warn("Entity Distance was set to a false interval ({})", entityDistanceScalingTemp);
        }
        options.getGuiScale().setValue(check("GUI Scale", options.getGuiScale().getValue(), 0, Integer.MAX_VALUE));
        options.getMaxFps().setValue(check("Max Framerate", options.getMaxFps().getValue(), 1, 260));
        options.getBiomeBlendRadius().setValue(check("Biome Blend", options.getBiomeBlendRadius().getValue(), 0, 7));
        options.getChtOpacity().setValue(check("Chat Text Opacity", options.getChtOpacity().getValue(), 0, 1, true));
        options.getChatLineSpacing().setValue(check("Line Spacing", options.getChatLineSpacing().getValue(), 0, 1, true));
        options.getTextBackgroundOpacity().setValue(check("Text Background Opacity", options.getTextBackgroundOpacity().getValue(), 0, 1, true));
        options.getChatHeightFocused().setValue(check("(Chat) Focused Height", options.getChatHeightFocused().getValue(), 0, 1, false));
        options.getChatDelay().setValue(check("Chat Delay", options.getChatDelay().getValue(), 0, 6, false));
        options.getChatHeightUnfocused().setValue(check("(Chat) Unfocused Height", options.getChatHeightUnfocused().getValue(), 0, 1, false));
        options.getChatScale().setValue(check("Chat Text Size", options.getChatScale().getValue(), 0, 1, true));
        options.getChatWidth().setValue(check("(Chat) Width", options.getChatWidth().getValue(), 0, 1, false));
        if (options.getMipmapLevels().getValue() < 0 || options.getMipmapLevels().getValue() > 4) {
            options.getMipmapLevels().setValue(check("Mipmap Levels", options.getMipmapLevels().getValue(), 0, 4));
            client.setMipmapLevels(options.getMipmapLevels().getValue());
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.getMouseWheelSensitivity().setValue(check("Scroll Sensitivity", options.getMouseWheelSensitivity().getValue(), 0.01, 10, false));
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + SoundCategoryName.valueOf(soundCategory.name()).assignedName, options.getSoundVolume(soundCategory), 0, 1, true));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            simulationDistanceOnWorldJoin = check("Simulation Distance (On World Join)", simulationDistanceOnWorldJoin, 5, 32);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = check("Entity Distance (On World Join)", entityDistanceScalingOnWorldJoin, 0.5f, 5, true);
            entityDistanceScalingTemp = entityDistanceScalingOnWorldJoin;
            if (entityDistanceScalingTemp != (entityDistanceScalingOnWorldJoin = Math.round(entityDistanceScalingOnWorldJoin * 4) / 4.0D)) {
                LOGGER.warn("Entity Distance (On World Join) was set to a false interval ({})", entityDistanceScalingTemp);
            }
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

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
        StringBuilder string = new StringBuilder("autoJump:" + options.getAutoJump().getValue() + l +
                "autoSuggestions:" + options.getAutoSuggestions().getValue() + l +
                "chatColors:" + options.getChatColors().getValue() + l +
                "chatLinks:" + options.getChatLinks().getValue() + l +
                "chatLinksPrompt:" + options.getChatLinksPrompt().getValue() + l +
                "enableVsync:" + options.getEnableVsync().getValue() + l +
                "entityShadows:" + options.getEntityShadows().getValue() + l +
                "forceUnicodeFont:" + options.getForceUnicodeFont().getValue() + l +
                "discrete_mouse_scroll:" + options.getDiscreteMouseScroll().getValue() + l +
                "invertYMouse:" + options.getInvertYMouse().getValue() + l +
                "reducedDebugInfo:" + options.getReducedDebugInfo().getValue() + l +
                "showSubtitles:" + options.getShowSubtitles().getValue() + l +
                "directionalAudio:" + options.getDirectionalAudio().getValue() + l +
                "touchscreen:" + options.getTouchscreen().getValue() + l +
                "fullscreen:" + options.getFullscreen().getValue() + l +
                "bobView:" + options.getBobView().getValue() + l +
                "toggleCrouch:" + options.getSneakToggled().getValue() + l +
                "toggleSprint:" + options.getSprintToggled().getValue() + l +
                "darkMojangStudiosBackground:" + options.getMonochromeLogo().getValue() + l +
                "hideLightningFlashes:" + options.getHideLightningFlashes().getValue() + l +
                "mouseSensitivity:" + options.getMouseSensitivity().getValue() + l +
                "fov:" + (options.getFov().getValue() - 70.0f) / 40.0f + l +
                "screenEffectScale:" + options.getDistortionEffectScale().getValue() + l +
                "fovEffectScale:" + options.getFovEffectScale().getValue() + l +
                "darknessEffectScale:" + options.getDarknessEffectScale().getValue() + l +
                "gamma:" + options.getGamma().getValue() + l +
                "renderDistance:" + options.getViewDistance().getValue() + l +
                "entityDistanceScaling:" + options.getEntityDistanceScaling().getValue() + l +
                "guiScale:" + options.getGuiScale().getValue() + l +
                "particles:" + options.getParticles().getValue().getId() + l +
                "maxFps:" + options.getMaxFps().getValue() + l +
                "graphicsMode:" + options.getGraphicsMode().getValue().getId() + l +
                "ao:" + options.getAo().getValue().getId() + l +
                "renderClouds:\"" + (options.getCloudRenderMod().getValue() == CloudRenderMode.FAST ? "fast" : options.getCloudRenderMod().getValue() == CloudRenderMode.FANCY) + "\"" + l +
                "attackIndicator:" + options.getAttackIndicator().getValue().getId() + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.getChatVisibility().getValue().getId() + l +
                "chatOpacity:" + options.getChtOpacity().getValue() + l +
                "chatLineSpacing:" + options.getChatLineSpacing().getValue() + l +
                "textBackgroundOpacity:" + options.getTextBackgroundOpacity().getValue() + l +
                "backgroundForChatOnly:" + options.getBackgroundForChatOnly().getValue() + l +
                "fullscreenResolution:" + (options.fullscreenResolution == null ? "" : options.fullscreenResolution) + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "chatHeightFocused:" + options.getChatHeightFocused().getValue() + l +
                "chatDelay:" + options.getChatDelay().getValue() + l +
                "chatHeightUnfocused:" + options.getChatHeightUnfocused().getValue() + l +
                "chatScale:" + options.getChatScale().getValue() + l +
                "chatWidth:" + options.getChatWidth().getValue() + l +
                "mipmapLevels:" + options.getMipmapLevels().getValue() + l +
                "mainHand:" + (options.getMainArm().getValue() == Arm.LEFT ? "\"left\"" : "\"right\"") + l +
                "narrator:" + options.getNarrator().getValue().getId() + l +
                "biomeBlendRadius:" + options.getBiomeBlendRadius().getValue() + l +
                "mouseWheelSensitivity:" + options.getMouseWheelSensitivity().getValue() + l +
                "rawMouseInput:" + options.getRawMouseInput().getValue() + l +
                "showAutosaveIndicator:" + options.getShowAutosaveIndicator().getValue() + l +
                "chatPreview:" + options.getChatPreview().getValue() + l +
                "onlyShowSecureChat:" + options.getOnlyShowSecureChat().getValue() + l);
        for (KeyBinding keyBinding : options.allKeys) {
            string.append("key_").append(keyBinding.getTranslationKey()).append(":").append(keyBinding.getBoundKeyTranslationKey()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.isPlayerModelPartEnabled(playerModelPart)).append(l);
        }
        string.append("entityCulling:").append(FabricLoader.getInstance().getModContainer("sodium").isPresent() ? SodiumClientMod.options().performance.useEntityCulling : "").append(l).append("sneaking:").append(l).append("sprinting:").append(l).append("chunkborders:").append(l).append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("simulationDistanceOnWorldJoin:").append(l).append("entityDistanceScalingOnWorldJoin:").append(l).append("changeOnResize:false");

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
            lines.add("entityCulling:" + (FabricLoader.getInstance().getModContainer("sodium").isPresent() ? SodiumClientMod.options().performance.useEntityCulling : ""));
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