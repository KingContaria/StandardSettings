package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.PieChartAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

@Environment(value= EnvType.CLIENT)
public class ResetSettings {

    private static final Logger LOGGER = StandardSettings.LOGGER;
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static final File standardoptionsFile = new File("standardoptions.txt");

    public static void LoadStandardSettings() {
        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            NbtCompound compoundTag = new NbtCompound();
            try (BufferedReader bufferedReader = Files.newReader(standardoptionsFile, Charsets.UTF_8)) {
                bufferedReader.lines().forEach(string -> {
                    try {
                        Iterator iterator = COLON_SPLITTER.split(string).iterator();
                        compoundTag.putString((String) iterator.next(), (String) iterator.next());
                    } catch (Exception exception) {
                        LOGGER.warn("Skipping bad StandardSetting: {}", string);
                    }
                });
            }
            NbtCompound compoundTag2 = update(compoundTag);
            if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
                options.graphicsMode = "true".equals(compoundTag2.getString("fancyGraphics")) ? GraphicsMode.FANCY : GraphicsMode.FAST;
            }
            for (String string2 : compoundTag2.getKeys()) {
                String string22 = compoundTag2.getString(string2);
                String[] string2_split = string2.split("_");
                try {
                    switch(string2_split[0]){
                        case "autoJump": options.autoJump = Boolean.parseBoolean(string22); break;
                        case "autoSuggestions": options.autoSuggestions = Boolean.parseBoolean(string22); break;
                        case "chatColors": options.chatColors = Boolean.parseBoolean(string22); break;
                        case "chatLinks": options.chatLinks = Boolean.parseBoolean(string22); break;
                        case "chatLinksPrompt": options.chatLinksPrompt = Boolean.parseBoolean(string22); break;
                        case "enableVsync":
                            options.enableVsync = Boolean.parseBoolean(string22);
                            client.getWindow().setVsync(Boolean.parseBoolean(string22)); break;
                        case "entityShadows": options.entityShadows = Boolean.parseBoolean(string22); break;
                        case "forceUnicodeFont":
                            options.forceUnicodeFont = Boolean.parseBoolean(string22);
                            ((PieChartAccessor)client).callInitFont(Boolean.parseBoolean(string22)); break;
                        case "discrete_mouse_scroll": options.discreteMouseScroll = Boolean.parseBoolean(string22); break;
                        case "invertYMouse": options.invertYMouse = Boolean.parseBoolean(string22); break;
                        case "realmsNotifications": options.realmsNotifications = Boolean.parseBoolean(string22); break;
                        case "reducedDebugInfo": options.reducedDebugInfo = Boolean.parseBoolean(string22); break;
                        case "showSubtitles": options.showSubtitles = Boolean.parseBoolean(string22); break;
                        case "touchscreen": options.touchscreen = Boolean.parseBoolean(string22); break;
                        case "fullscreen":
                            if(client.getWindow().isFullscreen() != Boolean.parseBoolean(string22)){
                                if(client.isWindowFocused()){
                                    client.getWindow().toggleFullscreen();
                                    options.fullscreen = Boolean.parseBoolean(string22);
                                }else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            } break;
                        case "bobView": options.bobView = Boolean.parseBoolean(string22); break;
                        case "toggleCrouch": options.sneakToggled = Boolean.parseBoolean(string22); break;
                        case "toggleSprint": options.sprintToggled = Boolean.parseBoolean(string22); break;
                        case "darkMojangStudiosBackground": options.monochromeLogo = Boolean.parseBoolean(string22); break;
                        case "hideLightningFlashes": options.hideLightningFlashes = Boolean.parseBoolean(string22); break;
                        case "mouseSensitivity": options.mouseSensitivity = Float.parseFloat(string22); break;
                        case "fov": options.fov = Float.parseFloat(string22) * 40.0f + 70.0f; break;
                        case "screenEffectScale": options.distortionEffectScale = Float.parseFloat(string22); break;
                        case "fovEffectScale": options.fovEffectScale = Float.parseFloat(string22); break;
                        case "gamma": options.gamma = Float.parseFloat(string22); break;
                        case "renderDistance": options.viewDistance = Integer.parseInt(string22); break;
                        case "entityDistanceScaling": options.entityDistanceScaling = Float.parseFloat(string22); break;
                        case "guiScale":
                            options.guiScale = Integer.parseInt(string22);
                            int i = client.getWindow().calculateScaleFactor(options.guiScale, client.forcesUnicodeFont());
                            client.getWindow().setScaleFactor(i); break;
                        case "particles": options.particles = ParticlesMode.byId(Integer.parseInt(string22)); break;
                        case "maxFps":
                            options.maxFps = Integer.parseInt(string22);
                            if (client.getWindow() != null) {
                                client.getWindow().setFramerateLimit(options.maxFps);
                            } break;
                        case "graphicsMode": options.graphicsMode = GraphicsMode.byId(Integer.parseInt(string22)); break;
                        case "ao": options.ao = AoMode.byId((int) Float.parseFloat(string22)); break;
                        case "renderClouds":
                            if ("true".equals(string22)) {
                                options.cloudRenderMode = CloudRenderMode.FANCY;
                            } else if ("false".equals(string22)) {
                                options.cloudRenderMode = CloudRenderMode.OFF;
                            } else if ("fast".equals(string22)) {
                                options.cloudRenderMode = CloudRenderMode.FAST;
                            } break;
                        case "attackIndicator": options.attackIndicator = AttackIndicator.byId(Integer.parseInt(string22)); break;
                        case "chatVisibility": options.chatVisibility = ChatVisibility.byId(Integer.parseInt(string22)); break;
                        case "chatOpacity": options.chatOpacity = Float.parseFloat(string22); break;
                        case "chatLineSpacing": options.chatLineSpacing = Float.parseFloat(string22); break;
                        case "textBackgroundOpacity": options.textBackgroundOpacity = Float.parseFloat(string22); break;
                        case "backgroundForChatOnly": options.backgroundForChatOnly = "true".equals(string22); break;
                        case "fullscreenResolution": options.fullscreenResolution = string22; break;
                        case "hideServerAddress": options.hideServerAddress = "true".equals(string22); break;
                        case "advancedItemTooltips": options.advancedItemTooltips = "true".equals(string22); break;
                        case "pauseOnLostFocus": options.pauseOnLostFocus = "true".equals(string22); break;
                        case "heldItemTooltips": options.heldItemTooltips = "true".equals(string22); break;
                        case "chatHeightFocused": options.chatHeightFocused = Float.parseFloat(string22); break;
                        case "chatDelay": options.chatDelay = Float.parseFloat(string22); break;
                        case "chatHeightUnfocused": options.chatHeightUnfocused = Float.parseFloat(string22); break;
                        case "chatScale": options.chatScale = Float.parseFloat(string22); break;
                        case "chatWidth": options.chatWidth = Float.parseFloat(string22); break;
                        case "mainHand": options.mainArm = "left".equals(string22) ? Arm.LEFT : Arm.RIGHT; break;
                        case "narrator": options.narrator = NarratorMode.byId(Integer.parseInt(string22)); break;
                        case "biomeBlendRadius": options.biomeBlendRadius = Integer.parseInt(string22); break;
                        case "mouseWheelSensitivity": options.mouseWheelSensitivity = Float.parseFloat(string22); break;
                        case "rawMouseInput":
                            options.rawMouseInput = "true".equals(string22);
                            client.getWindow().setRawMouseMotion("true".equals(string22)); break;
                        case "perspective":
                            switch (Integer.parseInt(string22) % 3) {
                                case 1 -> options.setPerspective(Perspective.THIRD_PERSON_BACK);
                                case 2 -> options.setPerspective(Perspective.THIRD_PERSON_FRONT);
                                default -> options.setPerspective(Perspective.FIRST_PERSON);
                            } break;
                        case "piedirectory":
                            string22 = string22.replace(".", "");
                            ((PieChartAccessor) client).setopenProfilerSection(string22); break;
                        case "chunkborders":
                            if(client.debugRenderer.toggleShowChunkBorder() != "true".equals(string22)){
                                client.debugRenderer.toggleShowChunkBorder();
                            } break;
                        case "hitboxes": client.getEntityRenderDispatcher().setRenderHitboxes("true".equals(string22)); break;
                        case "key":
                            for (KeyBinding keyBinding : options.allKeys) {
                                if (string2_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setBoundKey(InputUtil.fromTranslationKey(string22)); break;
                                }
                            } break;
                        case "soundCategory":
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string2_split[1].equals(soundCategory.getName())) {
                                    client.getSoundManager().updateSoundVolume(soundCategory, Float.parseFloat(string22));
                                    options.setSoundVolume(soundCategory, Float.parseFloat(string22)); break;
                                }
                            } break;
                        case "modelPart":
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (string2.equals("modelPart_" + playerModelPart.getName())) {
                                    options.togglePlayerModelPart(playerModelPart, "true".equals(string22)); break;
                                }
                            }
                    }

                    // Excluded are Language and Mipmap Levels because resources would've had to be reloaded, blocking world creation screen.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipping bad StandardSetting: {}:{}", string2, string22);
                }
            }
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings");
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    private static NbtCompound update(NbtCompound tag) {
        int i = 0;
        try {
            i = Integer.parseInt(tag.getString("version"));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return NbtHelper.update(client.getDataFixer(), DataFixTypes.OPTIONS, tag, i);
    }

    public static void CheckSettings(){
        options.mouseSensitivity = Check("Sensitivity",options.mouseSensitivity,0,1);
        options.fov = Check("FOV",options.fov,30,110);
        options.distortionEffectScale = Check("Distortion Effects",options.distortionEffectScale,0,1);
        options.fovEffectScale = Check("FOV Effects", options.fovEffectScale,0,1);
        options.gamma = Check("Brightness",options.gamma,0,5);
        options.viewDistance = Check("Render Distance",options.viewDistance,2,32);
        options.simulationDistance = Check("Simulation Distance",options.simulationDistance,5,32);
        options.entityDistanceScaling = Check("Entity Distance",options.entityDistanceScaling,0.5f,5);
        options.guiScale = Check("GUI Scale",options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.biomeBlendRadius = Check("Biome Blend Radius",options.biomeBlendRadius,0,7);
        options.chatOpacity = Check("Chat Opacity",options.chatOpacity,0,1);
        options.chatLineSpacing = Check("Line Spacing",options.chatLineSpacing,0,1);
        options.textBackgroundOpacity = Check("Text Background Opacity",options.textBackgroundOpacity,0,1);
        options.chatHeightFocused = Check("(Chat) Focused Height",options.chatHeightFocused,0,1);
        options.chatDelay = Check("Chat Delay",options.chatDelay,0,6);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height",options.chatHeightUnfocused,0,1);
        options.chatScale = Check("Chat Text Size",options.chatScale,0,1);
        options.chatWidth = Check("ChatWidth",options.chatWidth,0,1);
        options.mouseWheelSensitivity = Check("Scroll Sensitivity",options.mouseWheelSensitivity,0.01,10);
        for(SoundCategory soundCategory : SoundCategory.values()){
            float i = Check(soundCategory.getName(),options.getSoundVolume(soundCategory),0,1);
            client.getSoundManager().updateSoundVolume(soundCategory, i);
            options.setSoundVolume(soundCategory, i);
        }
        if(options.mipmapLevels<0){
            LOGGER.warn("Mipmap Levels was too low! (" + options.mipmapLevels + ")");
            LOGGER.error("Mipmap Levels can not be corrected!");
        }else {
            if (options.mipmapLevels > 4) {
                LOGGER.warn("Mipmap Levels was too high! (" + options.mipmapLevels + ")");
                LOGGER.error("Mipmap Levels can not be corrected!");
            }
        }
        LOGGER.info("Finished checking Settings");
    }

    public static double Check(String settingName, double setting, double min, double max){
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

    public static float Check(String settingName, float setting, float min, float max){
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

    public static int Check(String settingName, int setting, int min, int max){
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

    public static void SetStandardSettings() {
        LOGGER.info("Saving StandardSettings...");

        if(!new File("options.txt").exists()){
            options.write();
        }

        PrintWriter printer = null;
        try (Scanner scanner = new Scanner(new File("options.txt"))) {
            FileWriter writer = new FileWriter("standardoptions.txt");
            printer = new PrintWriter(writer);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine() + System.lineSeparator();
                printer.write(line);
            }

            printer.write("perspective:" + options.getPerspective() + System.lineSeparator());
            printer.write("piedirectory:" + ((PieChartAccessor) client).getopenProfilerSection().replace("", ".") + System.lineSeparator());
            client.debugRenderer.toggleShowChunkBorder();
            printer.write("chunkborders:" + client.debugRenderer.toggleShowChunkBorder() + System.lineSeparator());
            printer.write("hitboxes:" + client.getEntityRenderDispatcher().shouldRenderHitboxes());

        } catch (IOException e) {
            LOGGER.error("Failed to save StandardSettings");
            throw new RuntimeException(e);
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
            LOGGER.info("Finished saving StandardSettings");
        }
    }
}