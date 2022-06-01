package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.PieChartAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatVisibility;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Scanner;

public class ResetSettings {

    private static final Logger LOGGER = StandardSettings.LOGGER;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static final File standardoptionsFile = new File("standardoptions.txt");

    public static void LoadStandardSettings() {
        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                try {
                    String[] strings = string.split(":");
                    String[] string0_split = strings[0].split("_");

                    switch (string0_split[0]){
                        case "mouseSensitivity": options.sensitivity = Float.parseFloat(strings[1]); break;
                        case "fov": options.fov = Float.parseFloat(strings[1]) * 40.0f + 70.0f; break;
                        case "gamma": options.gamma = Float.parseFloat(strings[1]); break;
                        case "invertYMouse": options.invertYMouse = strings[1].equals("true"); break;
                        case "renderDistance": options.viewDistance = Integer.parseInt(strings[1]); break;
                        case "guiScale": options.guiScale = Integer.parseInt(strings[1]); break;
                        case "particles": options.particle = Integer.parseInt(strings[1]); break;
                        case "bobView": options.bobView = strings[1].equals("true"); break;
                        case "maxFps": options.maxFramerate = Integer.parseInt(strings[1]); break;
                        case "fancyGraphics": options.fancyGraphics = strings[1].equals("true"); break;
                        case "ao": options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1])); break;
                        case "chatVisibility": options.field_7671 = ChatVisibility.get(Integer.parseInt(strings[1])); break;
                        case "chatColors": options.chatColor = strings[1].equals("true"); break;
                        case "chatLinks": options.chatLink = strings[1].equals("true"); break;
                        case "chatLinksPrompt": options.chatLinkPrompt = strings[1].equals("true"); break;
                        case "chatOpacity": options.chatOpacity = Float.parseFloat(strings[1]); break;
                        case "fullscreen":
                            if(options.fullscreen != strings[1].equals("true")){
                                if(client.isWindowFocused()){
                                    client.toggleFullscreen();
                                    options.fullscreen = strings[1].equals("true");
                                }else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            } break;
                        case "enableVsync": options.vsync = strings[1].equals("true"); break;
                        case "useVbo": options.vbo = strings[1].equals("true"); break;
                        case "advancedItemTooltips": options.advancedItemTooltips = strings[1].equals("true"); break;
                        case "pauseOnLostFocus": options.pauseOnLostFocus = strings[1].equals("true"); break;
                        case "touchscreen": options.touchScreen = strings[1].equals("true"); break;
                        case "heldItemTooltips": options.heldItemTooltips = strings[1].equals("true"); break;
                        case "chatHeightFocused": options.chatHeightFocused = Float.parseFloat(strings[1]); break;
                        case "chatHeightUnfocused": options.chatHeightUnfocused = Float.parseFloat(strings[1]); break;
                        case "chatScale": options.chatScale = Float.parseFloat(strings[1]); break;
                        case "chatWidth": options.chatWidth = Float.parseFloat(strings[1]); break;
                        //Doesnt work rn
                        /*
                        case "forceUnicodeFont":
                            options.forceUnicode = strings[1].equals("true");
                            options.setValue();
                        */
                        case "allowBlockAlternatives": options.alternativeBlocks = strings[1].equals("true"); break;
                        case "reducedDebugInfo": options.reducedDebugInfo = strings[1].equals("true"); break;
                        case "renderClouds": options.entityShadows = strings[1].equals("true"); break;
                        case "perspective": options.perspective = Integer.parseInt(strings[1]); break;
                        case "piedirectory":
                            ((PieChartAccessor) client).setopenProfilerSection(strings[1]); break;
                        case "hitboxes": client.getEntityRenderManager().method_10205(strings[1].equals("true")); break;
                        case "key":
                            for (KeyBinding keyBinding : options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setCode(Integer.parseInt(strings[1])); break;
                                }
                            } break;
                        case "soundCategory":
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string0_split[1].equals(soundCategory.getName())) {
                                    options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                                }
                            } break;
                        case "modelPart":
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    options.setPlayerModelPart(playerModelPart, strings[1].equals("true")); break;
                                }
                            } break;
                    }

                    // Excluded are Language, 3D Anaglyph and Mipmap Levels.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                } catch (Exception exception) {
                    LOGGER.warn("Skipping bad StandardSetting: " + string);
                }
            }
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            LOGGER.info("Finished loading StandardSettings");
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void CheckSettings(){
        options.sensitivity = Check("Sensitivity",options.sensitivity,0,1);
        options.fov = Check("FOV",options.fov,30,110);
        options.gamma = Check("Brightness",options.gamma,0,5);
        options.viewDistance = Check("Render Distance",options.viewDistance,2,32);
        options.guiScale = Check("GUI Scale",options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.chatOpacity = Check("Chat Opacity",options.chatOpacity,0,1);
        options.chatHeightFocused = Check("(Chat) Focused Height",options.chatHeightFocused,0,1);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height",options.chatHeightUnfocused,0,1);
        options.chatScale = Check("Chat Text Size",options.chatScale,0,1);
        options.chatWidth = Check("ChatWidth",options.chatWidth,0,1);
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
            options.save();
        }

        PrintWriter printer = null;
        try (Scanner scanner = new Scanner(new File("options.txt"))) {
            FileWriter writer = new FileWriter("standardoptions.txt");
            printer = new PrintWriter(writer);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine() + System.lineSeparator();
                printer.write(line);
            }

            printer.write("perspective:" + options.perspective + System.lineSeparator());
            printer.write("piedirectory:" + ((PieChartAccessor) client).getopenProfilerSection() + System.lineSeparator());
            printer.write("hitboxes:" + client.getEntityRenderManager().method_10203());

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