package com.kingcontaria.standardsettings;

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
import java.util.Scanner;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("standardoptions.txt");
    private static final File optionsFile = new File("options.txt");
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin = 0;
    private static int simulationDistanceOnWorldJoin = 0;
    private static double entityDistanceScalingOnWorldJoin = 0;
    private static int fovOnWorldJoin = 0;

    public static void load() {

        entityDistanceScalingOnWorldJoin = fovOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;

        try {

            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch(string0_split[0]){
                        case "autoJump" -> options.getAutoJump().setValue(Boolean.parseBoolean(strings[1]));
                        case "autoSuggestions" -> options.getAutoSuggestions().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatColors" -> options.getChatColors().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinks" -> options.getChatLinks().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinksPrompt" -> options.getChatLinksPrompt().setValue(Boolean.parseBoolean(strings[1]));
                        case "enableVsync" -> {
                            options.getEnableVsync().setValue(Boolean.parseBoolean(strings[1]));
                            window.setVsync(options.getEnableVsync().getValue());
                        }
                        case "entityShadows" -> options.getEntityShadows().setValue(Boolean.parseBoolean(strings[1]));
                        case "forceUnicodeFont" -> {
                            options.getForceUnicodeFont().setValue(Boolean.parseBoolean(strings[1]));
                            ((MinecraftClientAccessor) client).callInitFont(options.getForceUnicodeFont().getValue());
                        }
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
                        case "fov" -> options.getFov().setValue((int) (Float.parseFloat(strings[1]) * 40.0f + 70.0f));
                        case "screenEffectScale" -> options.getDistortionEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "fovEffectScale" -> options.getFovEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "darknessEffectScale" -> options.getDarknessEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "gamma" -> options.getGamma().setValue(Double.parseDouble(strings[1]));
                        case "renderDistance" -> options.getViewDistance().setValue(Integer.parseInt(strings[1]));
                        case "entityDistanceScaling" -> options.getEntityDistanceScaling().setValue(Double.parseDouble(strings[1]));
                        case "guiScale" -> options.getGuiScale().setValue(Integer.parseInt(strings[1]));
                        case "particles" -> options.getParticles().setValue(ParticlesMode.byId(Integer.parseInt(strings[1])));
                        case "maxFps" -> {
                            options.getMaxFps().setValue(Integer.parseInt(strings[1]));
                            window.setFramerateLimit(options.getMaxFps().getValue());
                        }
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
                        case "mainHand" -> options.getMainArm().setValue("\"left\"".equals(strings[1]) ? Arm.LEFT : Arm.RIGHT);
                        case "narrator" -> options.getNarrator().setValue(NarratorMode.byId(Integer.parseInt(strings[1])));
                        case "biomeBlendRadius" -> options.getBiomeBlendRadius().setValue(Integer.parseInt(strings[1]));
                        case "mouseWheelSensitivity" -> options.getMouseWheelSensitivity().setValue(Double.parseDouble(strings[1]));
                        case "rawMouseInput" -> {
                            options.getRawMouseInput().setValue(Boolean.parseBoolean(strings[1]));
                            window.setRawMouseMotion(options.getRawMouseInput().getValue());
                        }
                        case "chatPreview" -> options.getChatPreview().setValue(Boolean.parseBoolean(strings[1]));
                        case "onlyShowSecureChat" -> options.getOnlyShowSecureChat().setValue(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.setPerspective(strings[1].equals("THIRD_PERSON_BACK") ? Perspective.THIRD_PERSON_BACK : strings[1].equals("THIRD_PERSON_FRONT") ? Perspective.THIRD_PERSON_FRONT : Perspective.FIRST_PERSON);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace(".",""));
                        case "chunkborders" -> {
                            if(client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])){
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "renderDistanceOnWorldJoin" -> {
                            try {
                                renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
                        case "simulationDistanceOnWorldJoin" -> {
                            try {
                                simulationDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
                        case "entityDistanceScalingOnWorldJoin" -> {
                            try {
                                entityDistanceScalingOnWorldJoin = Double.parseDouble(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
                        case "fovOnWorldJoin" -> {
                            try {
                                fovOnWorldJoin = Integer.parseInt(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
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

                    // Excluded are Mipmap Levels because resources would've had to be reloaded, blocking world creation screen.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    LOGGER.warn("Skipping bad StandardSetting: " + string);
                }
            }
            bufferedReader.close();
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings");
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin(){
        if (renderDistanceOnWorldJoin != 0) {
            options.getViewDistance().setValue(renderDistanceOnWorldJoin);
        }
        if(simulationDistanceOnWorldJoin != 0){
            options.getSimulationDistance().setValue(simulationDistanceOnWorldJoin);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            options.getEntityDistanceScaling().setValue(entityDistanceScalingOnWorldJoin);
        }
        if (fovOnWorldJoin != 0) {
            options.getFov().setValue(fovOnWorldJoin);
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0 || simulationDistanceOnWorldJoin != 0 || entityDistanceScalingOnWorldJoin != 0){
            options.write();
            LOGGER.info("Changed Settings on World Join");
        }
    }

    public static void checkSettings(){
        options.getMouseSensitivity().setValue(Check("Sensitivity",options.getMouseSensitivity().getValue(),0,1));
        options.getFov().setValue(Math.round(Check("FOV",options.getFov().getValue(),30,110)));
        options.getDistortionEffectScale().setValue(Check("Distortion Effects",options.getDistortionEffectScale().getValue(),0,1));
        options.getFovEffectScale().setValue(Check("FOV Effects", options.getFovEffectScale().getValue(),0,1));
        options.getGamma().setValue(Check("Brightness",options.getGamma().getValue(),0,1));
        options.getViewDistance().setValue(Check("Render Distance",options.getViewDistance().getValue(),2,32));
        options.getSimulationDistance().setValue(Check("Simulation Distance",options.getSimulationDistance().getValue(),5,32));
        options.getEntityDistanceScaling().setValue((double) Math.round(Check("Entity Distance",options.getEntityDistanceScaling().getValue(),0.5f,5)*4)/4);
        options.getGuiScale().setValue(Check("GUI Scale",options.getGuiScale().getValue(),0,4));
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.getBiomeBlendRadius().setValue(Check("Biome Blend Radius",options.getBiomeBlendRadius().getValue(),0,7));
        options.getChtOpacity().setValue(Check("Chat Opacity",options.getChtOpacity().getValue(),0,1));
        options.getChatLineSpacing().setValue(Check("Line Spacing",options.getChatLineSpacing().getValue(),0,1));
        options.getTextBackgroundOpacity().setValue(Check("Text Background Opacity",options.getTextBackgroundOpacity().getValue(),0,1));
        options.getChatHeightFocused().setValue(Check("(Chat) Focused Height",options.getChatHeightFocused().getValue(),0,1));
        options.getChatDelay().setValue(Check("Chat Delay",options.getChatDelay().getValue(),0,6));
        options.getChatHeightUnfocused().setValue(Check("(Chat) Unfocused Height",options.getChatHeightUnfocused().getValue(),0,1));
        options.getChatScale().setValue(Check("Chat Text Size",options.getChatScale().getValue(),0,1));
        options.getChatWidth().setValue(Check("ChatWidth",options.getChatWidth().getValue(),0,1));
        options.getMouseWheelSensitivity().setValue(Check("Scroll Sensitivity",options.getMouseWheelSensitivity().getValue(),0.01,10));
        for(SoundCategory soundCategory : SoundCategory.values()){
            float i = Check(soundCategory.getName(),options.getSoundVolume(soundCategory));
            client.getSoundManager().updateSoundVolume(soundCategory, i);
            options.setSoundVolume(soundCategory, i);
        }
        if(options.getMipmapLevels().getValue()<0){
            LOGGER.warn("Mipmap Levels was too low! (" + options.getMipmapLevels().getValue() + ")");
            LOGGER.error("Mipmap Levels can not be corrected!");
        }else {
            if (options.getMipmapLevels().getValue() > 4) {
                LOGGER.warn("Mipmap Levels was too high! (" + options.getMipmapLevels().getValue() + ")");
                LOGGER.error("Mipmap Levels can not be corrected!");
            }
        }

        if(renderDistanceOnWorldJoin != 0){
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)",renderDistanceOnWorldJoin,2,32);
        }
        if(simulationDistanceOnWorldJoin != 0){
            simulationDistanceOnWorldJoin = Check("Simulation Distance",simulationDistanceOnWorldJoin,5,32);
        }
        if(entityDistanceScalingOnWorldJoin != 0){
            entityDistanceScalingOnWorldJoin = (double) Math.round(Check("Entity Distance (On World Join)",entityDistanceScalingOnWorldJoin,0.5f,5)*4)/4;
        }
        if(fovOnWorldJoin != 0){
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)",fovOnWorldJoin,30,110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.getGuiScale().getValue(), client.forcesUnicodeFont()));
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

        if(!optionsFile.exists()){
            options.write();
        }

        String rd = "renderDistanceOnWorldJoin:";
        String ed = "entityDistanceScalingOnWorldJoin:";
        String sd = "simulationDistanceOnWorldJoin:";
        String fov = "fovOnWorldJoin:";

        try {
            Scanner standardoptionsTxt = new Scanner(standardoptionsFile);
            while (standardoptionsTxt.hasNextLine()) {
                String line = standardoptionsTxt.nextLine();
                switch (line.split(":")[0]) {
                    case "renderDistanceOnWorldJoin" -> rd = line;
                    case "entityDistanceScalingOnWorldJoin" -> ed = line;
                    case "simulationDistanceOnWorldJoin" -> sd = line;
                    case "fovOnWorldJoin" -> fov = line;
                }
            }
        } catch (FileNotFoundException e) {
            // empty catch block
        }

        PrintWriter printer = null;
        try (Scanner optionsTxt = new Scanner(optionsFile)) {
            printer = new PrintWriter(standardoptionsFile);

            while (optionsTxt.hasNextLine()) {
                String line = optionsTxt.nextLine() + System.lineSeparator();
                printer.write(line);
            }

            printer.write("perspective:" + client.options.getPerspective() + System.lineSeparator());
            printer.write("piedirectory:" + ((MinecraftClientAccessor)client).getOpenProfilerSection().replace("",".") + System.lineSeparator());
            client.debugRenderer.toggleShowChunkBorder();
            printer.write("chunkborders:" + client.debugRenderer.toggleShowChunkBorder() + System.lineSeparator());
            printer.write("hitboxes:" + client.getEntityRenderDispatcher().shouldRenderHitboxes() + System.lineSeparator());
            printer.write(rd + System.lineSeparator() + sd + System.lineSeparator() + ed + System.lineSeparator() + fov);

            LOGGER.info("Finished saving StandardSettings");

        } catch (IOException e) {
            LOGGER.error("Failed to save StandardSettings", e);
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
    }
}