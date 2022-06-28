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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static final File optionsFile = options.getOptionsFile();
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static int simulationDistanceOnWorldJoin;
    private static double entityDistanceScalingOnWorldJoin;
    private static int fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        entityDistanceScalingOnWorldJoin = fovOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile))) {

            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch (string0_split[0]) {
                        case "autoJump" -> options.getAutoJump().setValue(Boolean.parseBoolean(strings[1]));
                        case "autoSuggestions" -> options.getAutoSuggestions().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatColors" -> options.getChatColors().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinks" -> options.getChatLinks().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinksPrompt" -> options.getChatLinksPrompt().setValue(Boolean.parseBoolean(strings[1]));
                        case "enableVsync" -> options.getEnableVsync().setValue(Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.getEntityShadows().setValue(Boolean.parseBoolean(strings[1]));
                        case "forceUnicodeFont" -> options.getForceUnicodeFont().setValue(Boolean.parseBoolean(strings[1]));
                        case "discrete_mouse_scroll" -> options.getDiscreteMouseScroll().setValue(Boolean.parseBoolean(strings[1]));
                        case "invertYMouse" -> options.getInvertYMouse().setValue(Boolean.parseBoolean(strings[1]));
                        case "reducedDebugInfo" -> options.getReducedDebugInfo().setValue(Boolean.parseBoolean(strings[1]));
                        case "showSubtitles" -> options.getShowSubtitles().setValue(Boolean.parseBoolean(strings[1]));
                        case "directionalAudio" -> options.getDirectionalAudio().setValue(Boolean.parseBoolean(strings[1]));
                        case "touchscreen" -> options.getTouchscreen().setValue(Boolean.parseBoolean(strings[1]));
                        case "fullscreen" -> {
                            if (window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    window.toggleFullscreen();
                                    options.getFullscreen().setValue(Boolean.parseBoolean(strings[1]));
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
                        case "renderClouds" -> options.getCloudRenderMod().setValue(strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST);
                        case "attackIndicator" -> options.getAttackIndicator().setValue(AttackIndicator.byId(Integer.parseInt(strings[1])));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage((options.language = strings[1])));
                            client.getLanguageManager().reload(client.getResourceManager());
                        }
                        case "chatVisibility" -> options.getChatVisibility().setValue(ChatVisibility.byId(Integer.parseInt(strings[1])));
                        case "chatOpacity" -> options.getChtOpacity().setValue(Double.parseDouble(strings[1]));
                        case "chatLineSpacing" -> options.getChatLineSpacing().setValue(Double.parseDouble(strings[1]));
                        case "textBackgroundOpacity" -> options.getTextBackgroundOpacity().setValue(Double.parseDouble(strings[1]));
                        case "backgroundForChatOnly" -> options.getBackgroundForChatOnly().setValue(Boolean.parseBoolean(strings[1]));
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
                        case "mainHand" -> options.getMainArm().setValue("\"left\"".equals(strings[1]) ? Arm.LEFT : Arm.RIGHT);
                        case "narrator" -> options.getNarrator().setValue(NarratorMode.byId(Integer.parseInt(strings[1])));
                        case "biomeBlendRadius" -> options.getBiomeBlendRadius().setValue(Integer.parseInt(strings[1]));
                        case "mouseWheelSensitivity" -> options.getMouseWheelSensitivity().setValue(Double.parseDouble(strings[1]));
                        case "rawMouseInput" -> options.getRawMouseInput().setValue(Boolean.parseBoolean(strings[1]));
                        case "showAutosaveIndicator" -> options.getShowAutosaveIndicator().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatPreview" -> options.getChatPreview().setValue(Boolean.parseBoolean(strings[1]));
                        case "onlyShowSecureChat" -> options.getOnlyShowSecureChat().setValue(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.setPerspective(strings[1].equals("THIRD_PERSON_BACK") ? Perspective.THIRD_PERSON_BACK : strings[1].equals("THIRD_PERSON_FRONT") ? Perspective.THIRD_PERSON_FRONT : Perspective.FIRST_PERSON);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace(".",""));
                        case "chunkborders" -> {
                            if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "simulationDistanceOnWorldJoin" -> simulationDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "entityDistanceScalingOnWorldJoin" -> entityDistanceScalingOnWorldJoin = Double.parseDouble(strings[1]);
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Integer.parseInt(strings[1]);
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
                                    options.setSoundVolume(soundCategory, Float.parseFloat(strings[1]));
                                    client.getSoundManager().updateSoundVolume(soundCategory, options.getSoundVolume(soundCategory)); break;
                                }
                            }
                        }
                        case "modelPart" -> {
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    options.togglePlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                    }
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    if (!string.equals("renderDistanceOnWorldJoin:") && !string.equals("simulationDistanceOnWorldJoin:") && !string.equals("entityDistanceScalingOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            }
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start)/1000000.0f);
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
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
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0 || simulationDistanceOnWorldJoin != 0 || entityDistanceScalingOnWorldJoin != 0) {
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.getMouseSensitivity().setValue(Check("Sensitivity", options.getMouseSensitivity().getValue(), 0, 1));
        options.getFov().setValue(Math.round(Check("FOV", options.getFov().getValue(), 30, 110)));
        options.getDistortionEffectScale().setValue(Check("Distortion Effects", options.getDistortionEffectScale().getValue(), 0, 1));
        options.getFovEffectScale().setValue(Check("FOV Effects", options.getFovEffectScale().getValue(),0,1));
        options.getGamma().setValue(Check("Brightness", options.getGamma().getValue(), 0, 1));
        options.getViewDistance().setValue(Check("Render Distance", options.getViewDistance().getValue(), 2, 32));
        options.getSimulationDistance().setValue(Check("Simulation Distance", options.getSimulationDistance().getValue(), 5, 32));
        options.getEntityDistanceScaling().setValue((double) Math.round(Check("Entity Distance", options.getEntityDistanceScaling().getValue(), 0.5f, 5) * 4) / 4);
        options.getGuiScale().setValue(Check("GUI Scale", options.getGuiScale().getValue(), 0, 4));
        // Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.getBiomeBlendRadius().setValue(Check("Biome Blend Radius", options.getBiomeBlendRadius().getValue(), 0, 7));
        options.getChtOpacity().setValue(Check("Chat Opacity", options.getChtOpacity().getValue(), 0, 1));
        options.getChatLineSpacing().setValue(Check("Line Spacing", options.getChatLineSpacing().getValue(), 0, 1));
        options.getTextBackgroundOpacity().setValue(Check("Text Background Opacity", options.getTextBackgroundOpacity().getValue(), 0, 1));
        options.getChatHeightFocused().setValue(Check("(Chat) Focused Height", options.getChatHeightFocused().getValue(), 0, 1));
        options.getChatDelay().setValue(Check("Chat Delay", options.getChatDelay().getValue(), 0, 6));
        options.getChatHeightUnfocused().setValue(Check("(Chat) Unfocused Height", options.getChatHeightUnfocused().getValue(), 0, 1));
        options.getChatScale().setValue(Check("Chat Text Size", options.getChatScale().getValue(), 0, 1));
        options.getChatWidth().setValue(Check("Chat Width", options.getChatWidth().getValue(), 0, 1));
        options.getMouseWheelSensitivity().setValue(Check("Scroll Sensitivity", options.getMouseWheelSensitivity().getValue(), 0.01, 10));
        for (SoundCategory soundCategory : SoundCategory.values()) {
            client.getSoundManager().updateSoundVolume(soundCategory, Check(soundCategory.getName(), options.getSoundVolume(soundCategory)));
            options.setSoundVolume(soundCategory, options.getSoundVolume(soundCategory));
        }
        if (options.getMipmapLevels().getValue() < 0 || options.getMipmapLevels().getValue() > 4) {
            options.getMipmapLevels().setValue(Check("Mipmap Levels", options.getMipmapLevels().getValue(), 0, 4));
            client.setMipmapLevels(options.getMipmapLevels().getValue());
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            simulationDistanceOnWorldJoin = Check("Simulation Distance", simulationDistanceOnWorldJoin, 5, 32);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = (double) Math.round(Check("Entity Distance (On World Join)", entityDistanceScalingOnWorldJoin, 0.5f, 5) * 4) / 4;
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.getGuiScale().getValue(), client.forcesUnicodeFont()));
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double Check(String settingName, double setting, double min, double max) {
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

    private static float Check(String settingName, float setting) {
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

    private static int Check(String settingName, int setting, int min, int max) {
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

    public static void save() {
        LOGGER.info("Saving StandardSettings...");

        long start = System.nanoTime();

        if (!optionsFile.exists()) options.write();
        if (!standardoptionsFile.getParentFile().exists()) standardoptionsFile.getParentFile().mkdir();

        String rd = "renderDistanceOnWorldJoin:";
        String sd = "simulationDistanceOnWorldJoin:";
        String ed = "entityDistanceScalingOnWorldJoin:";
        String fov = "fovOnWorldJoin:";

        if (standardoptionsFile.exists()) {
            try (Scanner standardoptionsTxt = new Scanner(standardoptionsFile)) {
                while (standardoptionsTxt.hasNextLine()) {
                    String line = standardoptionsTxt.nextLine();
                    switch (line.split(":")[0]) {
                        case "renderDistanceOnWorldJoin" -> rd = line;
                        case "simulationDistanceOnWorldJoin:" -> sd = line;
                        case "entityDistanceScalingOnWorldJoin:" -> ed = line;
                        case "fovOnWorldJoin" -> fov = line;
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.copy(optionsFile.toPath(), standardoptionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.write(standardoptionsFile.toPath(), ("perspective:" + options.getPerspective() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(standardoptionsFile.toPath(), ("piedirectory:" + ((MinecraftClientAccessor)client).getOpenProfilerSection().replace("",".") + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            client.debugRenderer.toggleShowChunkBorder();
            Files.write(standardoptionsFile.toPath(), ("chunkborders:" + client.debugRenderer.toggleShowChunkBorder() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(standardoptionsFile.toPath(), ("hitboxes:" + client.getEntityRenderDispatcher().shouldRenderHitboxes() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(standardoptionsFile.toPath(), (rd + System.lineSeparator() + sd + System.lineSeparator() + ed + System.lineSeparator() + fov).getBytes(), StandardOpenOption.APPEND);
            LOGGER.info("Finished saving StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (IOException e) {
            LOGGER.error("Failed to save StandardSettings", e);
        }
    }
}