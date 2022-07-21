package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Optional;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static File lastUsedFile;
    public static long fileLastModified;
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static double fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));

            String string = bufferedReader.readLine();

            if (string != null && new File(string).exists()) {
                LOGGER.info("Using global standardoptions file");
                bufferedReader.close();
                bufferedReader = new BufferedReader(new FileReader(lastUsedFile = new File(string)));
                string = bufferedReader.readLine();
            } else {
                lastUsedFile = standardoptionsFile;
            }
            fileLastModified = lastUsedFile.lastModified();

            do {
                try {
                    String[] strings = string.split(":", 2);
                    String[] string0_split = strings[0].split("_", 2);
                    switch (string0_split[0]) {
                        case "autoJump" -> options.autoJump = Boolean.parseBoolean(strings[1]);
                        case "autoSuggestions" -> options.autoSuggestions = Boolean.parseBoolean(strings[1]);
                        case "chatColors" -> options.chatColors = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLinks = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinksPrompt = Boolean.parseBoolean(strings[1]);
                        case "enableVsync" -> window.setVsync(options.enableVsync = Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "forceUnicodeFont" -> client.getFontManager().setForceUnicodeFont(options.forceUnicodeFont = Boolean.parseBoolean(strings[1]), Util.getServerWorkerExecutor(), client);
                        case "discrete" -> options.discreteMouseScroll = Boolean.parseBoolean(strings[1]);
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
                        case "mouseSensitivity" -> options.mouseSensitivity = Double.parseDouble(strings[1]);
                        case "fov" -> options.fov = Double.parseDouble(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Double.parseDouble(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particles = ParticlesOption.byId(Integer.parseInt(strings[1]));
                        case "maxFps" -> window.setFramerateLimit(options.maxFps = Integer.parseInt(strings[1]));
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = AoOption.getOption(Integer.parseInt(strings[1]));
                        case "renderClouds" -> options.cloudRenderMode = strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST;
                        case "attackIndicator" -> options.attackIndicator = AttackIndicator.byId(Integer.parseInt(strings[1]));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(strings[1]));
                            client.getLanguageManager().apply(client.getResourceManager());
                            options.language = client.getLanguageManager().getLanguage().getCode();
                        }
                        case "chatVisibility" -> options.chatVisibility = ChatVisibility.byId(Integer.parseInt(strings[1]));
                        case "chatOpacity" -> options.chatOpacity = Double.parseDouble(strings[1]);
                        case "textBackgroundOpacity" -> options.textBackgroundOpacity = Double.parseDouble(strings[1]);
                        case "backgroundForChatOnly" -> options.backgroundForChatOnly = Boolean.parseBoolean(strings[1]);
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
                        case "chatHeightFocused" -> options.chatHeightFocused = Double.parseDouble(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Double.parseDouble(strings[1]);
                        case "chatScale" -> options.chatScale = Double.parseDouble(strings[1]);
                        case "chatWidth" -> options.chatWidth = Double.parseDouble(strings[1]);
                        case "mipmapLevels" -> {
                            if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                                client.resetMipmapLevels(options.mipmapLevels = Integer.parseInt(strings[1]));
                                ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                            }
                        }
                        case "mainHand" -> options.mainArm = "left".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT;
                        case "narrator" -> options.narrator = NarratorOption.byId(Integer.parseInt(strings[1]));
                        case "biomeBlendRadius" -> options.biomeBlendRadius = Integer.parseInt(strings[1]);
                        case "mouseWheelSensitivity" -> options.mouseWheelSensitivity = Double.parseDouble(strings[1]);
                        case "rawMouseInput" -> window.setRawMouseMotion(options.rawMouseInput = Boolean.parseBoolean(strings[1]));
                        case "sneaking" -> options.keySneak.setPressed(options.sneakToggled && Boolean.parseBoolean(strings[1]));
                        case "sprinting" -> options.keySprint.setPressed(options.sprintToggled && Boolean.parseBoolean(strings[1]));
                        case "chunkborders" -> {
                            if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderManager().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.perspective = Integer.parseInt(strings[1]) % 3;
                        case "piedirectory" -> {
                            if (!strings[1].split("\\.")[0].equals("root")) break;
                            ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace('.','\u001e'));
                        }
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Double.parseDouble(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "key" -> {
                            for (KeyBinding keyBinding : options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getId())) {
                                    keyBinding.setKeyCode(InputUtil.fromName(strings[1])); break;
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
                                    options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                        // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                    }
                } catch (Exception exception) {
                    if (string != null && !string.equals("sneaking:") && !string.equals("sprinting:") && !string.equals("chunkborders:") && !string.equals("hitboxes:") && !string.equals("perspective:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            } while ((string = bufferedReader.readLine()) != null);
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.viewDistance = renderDistanceOnWorldJoin;
        }
        if (fovOnWorldJoin != 0) {
            options.fov = fovOnWorldJoin;
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0) {
            fovOnWorldJoin = renderDistanceOnWorldJoin = 0;
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.mouseSensitivity = check("Sensitivity", options.mouseSensitivity, 0, 1);
        options.fov = Math.round(check("FOV", options.fov, 30, 110));
        options.gamma = check("Brightness", options.gamma, 0, 5);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = check("GUI Scale", options.guiScale, 0, Integer.MAX_VALUE);
        options.maxFps = check("Max FPS", options.maxFps, 1, 260);
        options.biomeBlendRadius = check("Biome Blend Radius", options.biomeBlendRadius, 0, 7);
        options.chatOpacity = check("Chat Opacity", options.chatOpacity, 0, 1);
        options.textBackgroundOpacity = check("Text Background Opacity", options.textBackgroundOpacity, 0, 1);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0, 1);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1);
        options.chatScale = check("Chat Text Size", options.chatScale, 0, 1);
        options.chatWidth = check("Chat Width", options.chatWidth, 0, 1);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.resetMipmapLevels(options.mipmapLevels);
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mouseWheelSensitivity = check("Scroll Sensitivity", options.mouseWheelSensitivity, 0.01, 10);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + soundCategory.name(), options.getSoundVolume(soundCategory)));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.guiScale, options.forceUnicodeFont));
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double check(String settingName, double setting, double min, double max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting>max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }

    private static float check(String settingName, float setting) {
        if (setting < 0) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return 0;
        }
        if (setting > 1) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return 1;
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

    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder("autoJump:" + options.autoJump + l +
                "autoSuggestions:" + options.autoSuggestions + l +
                "chatColors:" + options.chatColors + l +
                "chatLinks:" + options.chatLinks + l +
                "chatLinksPrompt:" + options.chatLinksPrompt + l +
                "enableVsync:" + options.enableVsync + l +
                "entityShadows:" + options.entityShadows + l +
                "forceUnicodeFont:" + options.forceUnicodeFont + l +
                "discrete_mouse_scroll:" + options.discreteMouseScroll + l +
                "invertYMouse:" + options.invertYMouse + l +
                "reducedDebugInfo:" + options.reducedDebugInfo + l +
                "showSubtitles:" + options.showSubtitles + l +
                "touchscreen:" + options.touchscreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "toggleCrouch:" + options.sneakToggled + l +
                "toggleSprint:" + options.sprintToggled + l +
                "mouseSensitivity:" + options.mouseSensitivity + l +
                "fov:" + (options.fov - 70.0f) / 40.0f + l +
                "gamma:" + options.gamma + l +
                "renderDistance:" + options.viewDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.particles.getId() + l +
                "maxFps:" + options.maxFps + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.ao.getValue() + l +
                "renderClouds:" + (options.cloudRenderMode == CloudRenderMode.FAST ? "fast" : options.cloudRenderMode == CloudRenderMode.FANCY) + l +
                "attackIndicator:" + options.attackIndicator.getId() + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.chatVisibility.getId() + l +
                "chatOpacity:" + options.chatOpacity + l +
                "textBackgroundOpacity:" + options.textBackgroundOpacity + l +
                "backgroundForChatOnly:" + options.backgroundForChatOnly + l +
                "fullscreenResolution:" + (options.fullscreenResolution == null ? "" : options.fullscreenResolution) + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "chatHeightFocused:" + options.chatHeightFocused + l +
                "chatHeightUnfocused:" + options.chatHeightUnfocused + l +
                "chatScale:" + options.chatScale + l +
                "chatWidth:" + options.chatWidth + l +
                "mipmapLevels:" + options.mipmapLevels + l +
                "mainHand:" + (options.mainArm == Arm.LEFT ? "left" : "right") + l +
                "narrator:" + options.narrator.getId() + l +
                "biomeBlendRadius:" + options.biomeBlendRadius + l +
                "mouseWheelSensitivity:" + options.mouseWheelSensitivity + l +
                "rawMouseInput:" + options.rawMouseInput + l);
        for (KeyBinding keyBinding : options.keysAll) {
            string.append("key_").append(keyBinding.getId()).append(":").append(keyBinding.getName()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.getEnabledPlayerModelParts().contains(playerModelPart)).append(l);
        }
        string.append("sneaking:").append(l).append("sprinting:").append(l).append("chunkborders:").append(l).append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("fovOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:");

        return string.toString();
    }
}