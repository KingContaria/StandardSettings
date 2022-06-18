package com.kingcontaria.standardsettings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
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
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static final File optionsFile = new File("options.txt");
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin = 0;
    private static double fovOnWorldJoin = 0;

    public static void load() {

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile))){
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch(string0_split[0]){
                        case "autoJump" -> options.autoJump = Boolean.parseBoolean(strings[1]);
                        case "autoSuggestions" -> options.autoSuggestions = Boolean.parseBoolean(strings[1]);
                        case "chatColors" -> options.chatColors = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLinks = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinksPrompt = Boolean.parseBoolean(strings[1]);
                        case "enableVsync" -> client.window.setVsync(options.enableVsync = Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "forceUnicodeFont" -> Option.FORCE_UNICODE_FONT.set(options, strings[1]);
                        case "discrete_mouse_scroll" -> options.discreteMouseScroll = Boolean.parseBoolean(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "reducedDebugInfo" -> options.reducedDebugInfo = Boolean.parseBoolean(strings[1]);
                        case "showSubtitles" -> options.showSubtitles = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchscreen = Boolean.parseBoolean(strings[1]);
                        case "fullscreen" -> {
                            if (client.window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    client.window.toggleFullscreen();
                                    options.fullscreen = Boolean.parseBoolean(strings[1]);
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "mouseSensitivity" -> options.mouseSensitivity = Double.parseDouble(strings[1]);
                        case "fov" -> options.fov = Double.parseDouble(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Double.parseDouble(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particles = ParticlesOption.byId(Integer.parseInt(strings[1]));
                        case "maxFps" -> client.window.setFramerateLimit(options.maxFps = Integer.parseInt(strings[1]));
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = Integer.parseInt(strings[1]) == 0 ? AoOption.OFF : Integer.parseInt(strings[1]) == 1 ? AoOption.MIN : AoOption.MAX;
                        case "renderClouds" -> options.cloudRenderMode = strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST;
                        case "attackIndicator" -> options.attackIndicator = AttackIndicator.byId(Integer.parseInt(strings[1]));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage((options.language = strings[1])));
                            client.getLanguageManager().apply(client.getResourceManager());
                        }
                        case "chatVisibility" -> options.chatVisibility = ChatVisibility.byId(Integer.parseInt(strings[1]));
                        case "chatOpacity" -> options.chatOpacity = Double.parseDouble(strings[1]);
                        case "textBackgroundOpacity" -> options.textBackgroundOpacity = Double.parseDouble(strings[1]);
                        case "backgroundForChatOnly" -> options.backgroundForChatOnly = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Double.parseDouble(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Double.parseDouble(strings[1]);
                        case "chatScale" -> options.chatScale = Double.parseDouble(strings[1]);
                        case "chatWidth" -> options.chatWidth = Double.parseDouble(strings[1]);
                        case "mainHand" -> options.mainArm = "left".equals(strings[1]) ? Arm.LEFT : Arm.RIGHT;
                        case "narrator" -> options.narrator = NarratorOption.byId(Integer.parseInt(strings[1]));
                        case "biomeBlendRadius" -> options.biomeBlendRadius = Integer.parseInt(strings[1]);
                        case "mouseWheelSensitivity" -> options.mouseWheelSensitivity = Double.parseDouble(strings[1]);
                        case "rawMouseInput" -> Option.RAW_MOUSE_INPUT.set(options, strings[1]);
                        case "chunkborders" -> {
                            if(client.debugRenderer.shouldRender() != Boolean.parseBoolean(strings[1])){
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderManager().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Double.parseDouble(strings[1]);
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
                                    options.setSoundVolume(soundCategory, Float.parseFloat(strings[1]));
                                    client.getSoundManager().updateSoundVolume(soundCategory, options.getSoundVolume(soundCategory)); break;
                                }
                            }
                        }
                        case "modelPart" -> {
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                    }

                    // Excluded are Mipmap Levels because resources would've had to be reloaded, blocking world creation screen.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    if(!string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")){
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            }
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings");
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin(){
        if (renderDistanceOnWorldJoin != 0) {
            Option.RENDER_DISTANCE.set(options, renderDistanceOnWorldJoin);
        }
        if (fovOnWorldJoin != 0) {
            Option.FOV.set(options, fovOnWorldJoin);
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0) {
            options.write();
            LOGGER.info("Changed Settings on World Join");
        }
    }

    public static void checkSettings(){
        options.mouseSensitivity = Check("Sensitivity",options.mouseSensitivity,0,1);
        options.fov = Math.round(Check("FOV",options.fov,30,110));
        options.gamma = Check("Brightness",options.gamma,0,5);
        options.viewDistance = Check("Render Distance",options.viewDistance,2,32);
        options.guiScale = Check("GUI Scale",options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.biomeBlendRadius = Check("Biome Blend Radius",options.biomeBlendRadius,0,7);
        options.chatOpacity = Check("Chat Opacity",options.chatOpacity,0,1);
        options.textBackgroundOpacity = Check("Text Background Opacity",options.textBackgroundOpacity,0,1);
        options.chatHeightFocused = Check("(Chat) Focused Height",options.chatHeightFocused,0,1);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height",options.chatHeightUnfocused,0,1);
        options.chatScale = Check("Chat Text Size",options.chatScale,0,1);
        options.chatWidth = Check("ChatWidth",options.chatWidth,0,1);
        options.mouseWheelSensitivity = Check("Scroll Sensitivity",options.mouseWheelSensitivity,0.01,10);
        for(SoundCategory soundCategory : SoundCategory.values()){
            float i = Check(soundCategory.getName(),options.getSoundVolume(soundCategory));
            client.getSoundManager().updateSoundVolume(soundCategory, i);
            options.setSoundVolume(soundCategory, i);
        }
        if(options.mipmapLevels < 0){
            LOGGER.warn("Mipmap Levels was too low! (" + options.mipmapLevels + ")");
            LOGGER.error("Mipmap Levels can not be corrected!");
        }else {
            if (options.mipmapLevels > 4) {
                LOGGER.warn("Mipmap Levels was too high! (" + options.mipmapLevels + ")");
                LOGGER.error("Mipmap Levels can not be corrected!");
            }
        }

        if(renderDistanceOnWorldJoin != 0){
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)",renderDistanceOnWorldJoin,2,32);
        }
        if(fovOnWorldJoin != 0){
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)",fovOnWorldJoin,30,110));
        }

        client.window.setScaleFactor(client.window.calculateScaleFactor(options.guiScale, client.forcesUnicodeFont()));
        LOGGER.info("Finished checking Settings");
    }

    private static double Check(String settingName, double setting, double min, double max){
        if(setting<min){
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if(setting>max){
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    private static float Check(String settingName, float setting){
        if(setting<0){
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return 0;
        }
        if(setting>1){
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return 1;
        }
        return setting;
    }

    private static int Check(String settingName, int setting, int min, int max){
        if(setting<min){
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if(setting>max){
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    public static void save() {
        LOGGER.info("Saving StandardSettings...");

        if(!optionsFile.exists()) options.write();
        if(!standardoptionsFile.getParentFile().exists()) standardoptionsFile.getParentFile().mkdir();

        String rd = "renderDistanceOnWorldJoin:";
        String fov = "fovOnWorldJoin:";

        try (Scanner standardoptionsTxt = new Scanner(standardoptionsFile)) {
            while (standardoptionsTxt.hasNextLine()) {
                String line = standardoptionsTxt.nextLine();
                switch (line.split(":")[0]) {
                    case "renderDistanceOnWorldJoin" -> rd = line;
                    case "fovOnWorldJoin" -> fov = line;
                }
            }
            Files.copy(optionsFile.toPath(), standardoptionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.write(standardoptionsFile.toPath(), ("perspective:" + options.perspective + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            client.debugRenderer.toggleShowChunkBorder();
            Files.write(standardoptionsFile.toPath(), ("chunkborders:" + client.debugRenderer.toggleShowChunkBorder() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(standardoptionsFile.toPath(), ("hitboxes:" + client.getEntityRenderManager().shouldRenderHitboxes() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(standardoptionsFile.toPath(), (rd + System.lineSeparator() + fov).getBytes(), StandardOpenOption.APPEND);
            LOGGER.info("Finished saving StandardSettings");
        } catch (IOException e) {
            LOGGER.error("Failed to save StandardSettings");
            throw new RuntimeException(e);
        }
    }
}