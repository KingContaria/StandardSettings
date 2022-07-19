package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static File lastUsedFile;
    public static long fileLastModified;
    public static final File optionsFile = options.getOptionsFile();
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static int simulationDistanceOnWorldJoin;
    private static float entityDistanceScalingOnWorldJoin;
    private static double fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        fovOnWorldJoin = entityDistanceScalingOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;

        System.out.println(options.soundDevice);

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));

            String string = bufferedReader.readLine();

            if (new File(string).exists()) {
                LOGGER.info("Using global standardoptions file");
                bufferedReader.close();
                bufferedReader = new BufferedReader(new FileReader(lastUsedFile = new File(string)));
                string = bufferedReader.readLine();
            } else {
                lastUsedFile = standardoptionsFile;
            }
            fileLastModified = lastUsedFile.lastModified();

            do {
                String[] strings = string.split(":", 2);
                String[] string0_split = strings[0].split("_", 2);
                try {
                    switch (string0_split[0]) {
                        case "autoJump" -> options.autoJump = Boolean.parseBoolean(strings[1]);
                        case "autoSuggestions" -> options.autoSuggestions = Boolean.parseBoolean(strings[1]);
                        case "chatColors" -> options.chatColors = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLinks = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinksPrompt = Boolean.parseBoolean(strings[1]);
                        case "enableVsync" -> window.setVsync(options.enableVsync = Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "forceUnicodeFont" -> ((MinecraftClientAccessor) client).callInitFont(options.forceUnicodeFont = Boolean.parseBoolean(strings[1]));
                        case "discrete_mouse_scroll" -> options.discreteMouseScroll = Boolean.parseBoolean(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "reducedDebugInfo" -> options.reducedDebugInfo = Boolean.parseBoolean(strings[1]);
                        case "showSubtitles" -> options.showSubtitles = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchscreen = Boolean.parseBoolean(strings[1]);
                        case "fullscreen" -> {
                            if (window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    window.toggleFullscreen();
                                    options.fullscreen = window.isFullscreen();
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "toggleCrouch" -> options.sneakToggled = Boolean.parseBoolean(strings[1]);
                        case "toggleSprint" -> options.sprintToggled = Boolean.parseBoolean(strings[1]);
                        case "darkMojangStudiosBackground" -> options.monochromeLogo = Boolean.parseBoolean(strings[1]);
                        case "hideLightningFlashes" -> options.hideLightningFlashes = Boolean.parseBoolean(strings[1]);
                        case "mouseSensitivity" -> options.mouseSensitivity = Double.parseDouble(strings[1]);
                        case "fov" -> options.fov = Double.parseDouble(strings[1]) * 40.0f + 70.0f;
                        case "screenEffectScale" -> options.distortionEffectScale = Float.parseFloat(strings[1]);
                        case "fovEffectScale" -> options.fovEffectScale = Float.parseFloat(strings[1]);
                        case "gamma" -> options.gamma = Double.parseDouble(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "entityDistanceScaling" -> options.entityDistanceScaling = Float.parseFloat(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particles = ParticlesMode.byId(Integer.parseInt(strings[1]));
                        case "maxFps" -> window.setFramerateLimit(options.maxFps = Integer.parseInt(strings[1]));
                        case "graphicsMode" -> options.graphicsMode = GraphicsMode.byId(Integer.parseInt(strings[1]));
                        case "ao" -> options.ao = AoMode.byId(Integer.parseInt(strings[1]));
                        case "renderClouds" -> options.cloudRenderMode = strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST;
                        case "attackIndicator" -> options.attackIndicator = AttackIndicator.byId(Integer.parseInt(strings[1]));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage((options.language = strings[1])));
                            client.getLanguageManager().reload(client.getResourceManager());
                        }
                        case "chatVisibility" -> options.chatVisibility = ChatVisibility.byId(Integer.parseInt(strings[1]));
                        case "chatOpacity" -> options.chatOpacity = Double.parseDouble(strings[1]);
                        case "chatLineSpacing" -> options.chatLineSpacing = Double.parseDouble(strings[1]);
                        case "textBackgroundOpacity" -> options.textBackgroundOpacity = Double.parseDouble(strings[1]);
                        case "backgroundForChatOnly" -> options.backgroundForChatOnly = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Double.parseDouble(strings[1]);
                        case "chatDelay" -> options.chatDelay = Double.parseDouble(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Double.parseDouble(strings[1]);
                        case "chatScale" -> options.chatScale = Double.parseDouble(strings[1]);
                        case "chatWidth" -> options.chatWidth = Double.parseDouble(strings[1]);
                        case "mipmapLevels" -> {
                            if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                                client.setMipmapLevels(options.mipmapLevels = Integer.parseInt(strings[1]));
                                ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                            }
                        }
                        case "mainHand" -> options.mainArm = "left".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT;
                        case "narrator" -> options.narrator = NarratorMode.byId(Integer.parseInt(strings[1]));
                        case "biomeBlendRadius" -> options.biomeBlendRadius = Integer.parseInt(strings[1]);
                        case "mouseWheelSensitivity" -> options.mouseWheelSensitivity = Double.parseDouble(strings[1]);
                        case "rawMouseInput" -> window.setRawMouseMotion(options.rawMouseInput = Boolean.parseBoolean(strings[1]));
                        case "showAutosaveIndicator" -> options.showAutosaveIndicator = Boolean.parseBoolean(strings[1]);
                        case "sneaking" -> options.sneakKey.setPressed(options.sneakToggled && Boolean.parseBoolean(strings[1]));
                        case "sprinting" -> options.sprintKey.setPressed(options.sprintToggled && Boolean.parseBoolean(strings[1]));
                        case "chunkborders" -> {
                            if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.setPerspective(strings[1].equals("THIRD_PERSON_BACK") ? Perspective.THIRD_PERSON_BACK : strings[1].equals("THIRD_PERSON_FRONT") ? Perspective.THIRD_PERSON_FRONT : Perspective.FIRST_PERSON);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace(".",""));
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Double.parseDouble(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "simulationDistanceOnWorldJoin" -> simulationDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "entityDistanceScalingOnWorldJoin" -> entityDistanceScalingOnWorldJoin = Float.parseFloat(strings[1]);
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
                    }
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    if (!string.equals("sneaking:") && !string.equals("sprinting:") && !string.equals("chunkborders:") && !string.equals("hitboxes:") && !string.equals("perspective:") && !string.equals("piedirectory:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("simulationDistanceOnWorldJoin:") && !string.equals("entityDistanceScalingOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            } while ((string = bufferedReader.readLine()) != null);
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin(){
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.viewDistance = renderDistanceOnWorldJoin;
        }
        if (simulationDistanceOnWorldJoin != 0) {
            options.simulationDistance = simulationDistanceOnWorldJoin;
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            options.entityDistanceScaling = entityDistanceScalingOnWorldJoin;
        }
        if (fovOnWorldJoin != 0) {
            options.fov = fovOnWorldJoin;
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0 || simulationDistanceOnWorldJoin != 0 || entityDistanceScalingOnWorldJoin != 0) {
            fovOnWorldJoin = entityDistanceScalingOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.mouseSensitivity = check("Sensitivity", options.mouseSensitivity, 0, 1);
        options.fov = Math.round(check("FOV", options.fov, 30, 110));
        options.distortionEffectScale = check("Distortion Effects", options.distortionEffectScale, 0, 1);
        options.fovEffectScale = check("FOV Effects", options.fovEffectScale, 0, 1);
        options.gamma = check("Brightness", options.gamma, 0, 5);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.simulationDistance = check("Simulation Distance", options.simulationDistance, 5, 32);
        options.entityDistanceScaling = (float) Math.round(check("Entity Distance", options.entityDistanceScaling, 0.5f, 5) * 4) / 4;
        options.guiScale = check("GUI Scale", options.guiScale, 0, 4);
        options.maxFps = check("Max FPS", options.maxFps, 1, 260);
        options.biomeBlendRadius = check("Biome Blend Radius", options.biomeBlendRadius, 0, 7);
        options.chatOpacity = check("Chat Opacity", options.chatOpacity, 0, 1);
        options.chatLineSpacing = check("Line Spacing", options.chatLineSpacing, 0, 1);
        options.textBackgroundOpacity = check("Text Background Opacity", options.textBackgroundOpacity, 0, 1);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0, 1);
        options.chatDelay = check("Chat Delay", options.chatDelay, 0, 6);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1);
        options.chatScale = check("Chat Text Size", options.chatScale, 0, 1);
        options.chatWidth = check("Chat Width", options.chatWidth, 0, 1);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.setMipmapLevels(options.mipmapLevels);
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mouseWheelSensitivity = check("Scroll Sensitivity", options.mouseWheelSensitivity, 0.01, 10);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check(soundCategory.getName(), options.getSoundVolume(soundCategory), 0, 1));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)",renderDistanceOnWorldJoin,2,32);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            simulationDistanceOnWorldJoin = check("Simulation Distance", simulationDistanceOnWorldJoin, 5, 32);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = (float) Math.round(check("Entity Distance (On World Join)", entityDistanceScalingOnWorldJoin, 0.5f, 5) * 4) / 4;
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.guiScale, client.forcesUnicodeFont()));
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double check(String settingName, double setting, double min, double max) {
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

    private static float check(String settingName, float setting, float min, float max) {
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
}