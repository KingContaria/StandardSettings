package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
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
    protected static MinecraftClient client = MinecraftClient.getInstance();
    private static final File standardoptionsFile = new File("standardoptions.txt");

    public static void LoadStandardSettings() {

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            CompoundTag compoundTag = new CompoundTag();
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
            CompoundTag compoundTag2 = update(compoundTag);
            for (String string2 : compoundTag2.getKeys()) {
                String string22 = compoundTag2.getString(string2);
                String[] string2_split = string2.split("_");
                try {
                    switch(string2_split[0]){
                        case "autoJump": client.options.autoJump = Boolean.parseBoolean(string22); break;
                        case "autoSuggestions": client.options.autoSuggestions = Boolean.parseBoolean(string22); break;
                        case "chatColors": client.options.chatColors = Boolean.parseBoolean(string22); break;
                        case "chatLinks": client.options.chatLinks = Boolean.parseBoolean(string22); break;
                        case "chatLinksPrompt": client.options.chatLinksPrompt = Boolean.parseBoolean(string22); break;
                        case "enableVsync":
                            client.options.enableVsync = Boolean.parseBoolean(string22);
                            client.window.setVsync(Boolean.parseBoolean(string22)); break;
                        case "entityShadows": client.options.entityShadows = Boolean.parseBoolean(string22); break;
                        case "forceUnicodeFont":
                            client.options.forceUnicodeFont = Boolean.parseBoolean(string22);
                            Option.FORCE_UNICODE_FONT.set(client.options, string22); break;
                        case "discrete_mouse_scroll": client.options.discreteMouseScroll = Boolean.parseBoolean(string22); break;
                        case "invertYMouse": client.options.invertYMouse = Boolean.parseBoolean(string22); break;
                        case "realmsNotifications": client.options.realmsNotifications = Boolean.parseBoolean(string22); break;
                        case "reducedDebugInfo": client.options.reducedDebugInfo = Boolean.parseBoolean(string22); break;
                        case "showSubtitles": client.options.showSubtitles = Boolean.parseBoolean(string22); break;
                        case "touchscreen": client.options.touchscreen = Boolean.parseBoolean(string22); break;
                        case "fullscreen":
                            if(client.window.isFullscreen() != Boolean.parseBoolean(string22)){
                                client.options.fullscreen = Boolean.parseBoolean(string22);
                                client.window.toggleFullscreen();
                            } break;
                        case "bobView": client.options.bobView = Boolean.parseBoolean(string22); break;
                        case "mouseSensitivity": client.options.mouseSensitivity = Float.parseFloat(string22); break;
                        case "fov": client.options.fov = Float.parseFloat(string22) * 40.0f + 70.0f; break;
                        case "gamma": client.options.gamma = Float.parseFloat(string22); break;
                        case "renderDistance": client.options.viewDistance = Integer.parseInt(string22); break;
                        case "guiScale":
                            client.options.guiScale = Integer.parseInt(string22);
                            int i = client.window.calculateScaleFactor(client.options.guiScale, client.forcesUnicodeFont());
                            client.window.setScaleFactor(i); break;
                        case "particles": client.options.particles = ParticlesOption.byId(Integer.parseInt(string22)); break;
                        case "maxFps":
                            client.options.maxFps = Integer.parseInt(string22);
                            if (client.window != null) {
                                client.window.setFramerateLimit(client.options.maxFps);
                            } break;
                        case "fancyGraphics": client.options.fancyGraphics = "true".equals(string22); break;
                        case "ao":
                            switch ((int) Float.parseFloat(string22)) {
                                case 0: client.options.ao = AoOption.OFF; break;
                                case 1: client.options.ao = AoOption.MIN; break;
                                case 2: client.options.ao = AoOption.MAX;
                            } break;
                        case "renderClouds":
                            if ("true".equals(string22)) {
                                client.options.cloudRenderMode = CloudRenderMode.FANCY;
                            } else if ("false".equals(string22)) {
                                client.options.cloudRenderMode = CloudRenderMode.OFF;
                            } else if ("fast".equals(string22)) {
                                client.options.cloudRenderMode = CloudRenderMode.FAST;
                            } break;
                        case "attackIndicator": client.options.attackIndicator = AttackIndicator.byId(Integer.parseInt(string22)); break;
                        case "chatVisibility": client.options.chatVisibility = ChatVisibility.byId(Integer.parseInt(string22)); break;
                        case "chatOpacity": client.options.chatOpacity = Float.parseFloat(string22); break;
                        case "textBackgroundOpacity": client.options.textBackgroundOpacity = Float.parseFloat(string22); break;
                        case "backgroundForChatOnly": client.options.backgroundForChatOnly = "true".equals(string22); break;
                        case "fullscreenResolution": client.options.fullscreenResolution = string22; break;
                        case "hideServerAddress": client.options.hideServerAddress = "true".equals(string22); break;
                        case "advancedItemTooltips": client.options.advancedItemTooltips = "true".equals(string22); break;
                        case "pauseOnLostFocus": client.options.pauseOnLostFocus = "true".equals(string22); break;
                        case "heldItemTooltips": client.options.heldItemTooltips = "true".equals(string22); break;
                        case "chatHeightFocused": client.options.chatHeightFocused = Float.parseFloat(string22); break;
                        case "chatHeightUnfocused": client.options.chatHeightUnfocused = Float.parseFloat(string22); break;
                        case "chatScale": client.options.chatScale = Float.parseFloat(string22); break;
                        case "chatWidth": client.options.chatWidth = Float.parseFloat(string22); break;
                        case "mainHand": client.options.mainArm = "left".equals(string22) ? Arm.LEFT : Arm.RIGHT; break;
                        case "narrator": client.options.narrator = NarratorOption.byId(Integer.parseInt(string22)); break;
                        case "biomeBlendRadius": client.options.biomeBlendRadius = Integer.parseInt(string22); break;
                        case "mouseWheelSensitivity": client.options.mouseWheelSensitivity = Float.parseFloat(string22); break;
                        case "rawMouseInput":
                            client.options.field_20308 = "true".equals(string22);
                            Option.RAW_MOUSE_INPUT.set(client.options, string22); break;
                        case "chunkborders":
                            if(client.debugRenderer.toggleShowChunkBorder() != "true".equals(string22)){
                                client.debugRenderer.toggleShowChunkBorder();
                            } break;
                        case "hitboxes": client.getEntityRenderManager().setRenderHitboxes("true".equals(string22)); break;
                        case "key":
                            for (KeyBinding keyBinding : client.options.keysAll) {
                                if (string2_split[1].equals(keyBinding.getId())) {
                                    keyBinding.setKeyCode(InputUtil.fromName(string22)); break;
                                }
                            } break;
                        case "soundCategory":
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string2_split[1].equals(soundCategory.getName())) {
                                    client.getSoundManager().updateSoundVolume(soundCategory, Float.parseFloat(string22));
                                    client.options.setSoundVolume(soundCategory, Float.parseFloat(string22)); break;
                                }
                            } break;
                        case "modelPart":
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (string2.equals("modelPart_" + playerModelPart.getName())) {
                                    client.options.setPlayerModelPart(playerModelPart, "true".equals(string22)); break;
                                }
                            }
                    }

                    // Excluded are Language and Mipmap Levels because resources would've had to be reloaded, blocking world creation screen.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    LOGGER.warn("Skipping bad StandardSetting: {}:{}",  string2,  string22);
                }
            }
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings");
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    private static CompoundTag update(CompoundTag tag) {
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
        client.options.mouseSensitivity = Check("Sensitivity",client.options.mouseSensitivity,0,1);
        client.options.fov = Check("FOV",client.options.fov,30,110);
        client.options.gamma = Check("Brightness",client.options.gamma,0,5);
        client.options.viewDistance = Check("Render Distance",client.options.viewDistance,2,32);
        client.options.guiScale = Check("GUI Scale",client.options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        client.options.biomeBlendRadius = Check("Biome Blend Radius",client.options.biomeBlendRadius,0,7);
        client.options.chatOpacity = Check("Chat Opacity",client.options.chatOpacity,0,1);
        client.options.textBackgroundOpacity = Check("Text Background Opacity",client.options.textBackgroundOpacity,0,1);
        client.options.chatHeightFocused = Check("(Chat) Focused Height",client.options.chatHeightFocused,0,1);
        client.options.chatHeightUnfocused = Check("(Chat) Unfocused Height",client.options.chatHeightUnfocused,0,1);
        client.options.chatScale = Check("Chat Text Size",client.options.chatScale,0,1);
        client.options.chatWidth = Check("ChatWidth",client.options.chatWidth,0,1);
        client.options.mouseWheelSensitivity = Check("Scroll Sensitivity",client.options.mouseWheelSensitivity,0.01,10);
        for(SoundCategory soundCategory : SoundCategory.values()){
            float i = Check(soundCategory.getName(),client.options.getSoundVolume(soundCategory),0,1);
            client.getSoundManager().updateSoundVolume(soundCategory, i);
            client.options.setSoundVolume(soundCategory, i);
        }
        if(client.options.mipmapLevels<0){
            LOGGER.warn("Mipmap Levels was too low! (" + client.options.mipmapLevels + ")");
            LOGGER.error("Mipmap Levels can not be corrected!");
        }else {
            if (client.options.mipmapLevels > 4) {
                LOGGER.warn("Mipmap Levels was too high! (" + client.options.mipmapLevels + ")");
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

        PrintWriter printer = null;
        try (Scanner scanner = new Scanner(new File("options.txt"))) {
            FileWriter writer = new FileWriter("standardoptions.txt");
            printer = new PrintWriter(writer);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine() + System.lineSeparator();
                printer.write(line);
            }

            printer.write("perspective:" + client.options.perspective + System.lineSeparator());
            client.debugRenderer.toggleShowChunkBorder();
            printer.write("chunkborders:" + client.debugRenderer.toggleShowChunkBorder() + System.lineSeparator());
            printer.write("hitboxes:" + client.getEntityRenderManager().shouldRenderHitboxes());

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
