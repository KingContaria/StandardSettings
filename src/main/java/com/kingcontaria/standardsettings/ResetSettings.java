package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.PieChartAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Scanner;

public class ResetSettings {

    private static final Logger LOGGER = StandardSettings.LOGGER;
    protected static MinecraftClient client = MinecraftClient.getInstance();
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
                        case "mouseSensitivity": client.options.sensitivity = Float.parseFloat(strings[1]); break;
                        case "fov": client.options.fov = Float.parseFloat(strings[1]) * 40.0f + 70.0f; break;
                        case "gamma": client.options.gamma = Float.parseFloat(strings[1]); break;
                        case "invertYMouse": client.options.invertYMouse = strings[1].equals("true"); break;
                        case "renderDistance": client.options.viewDistance = Integer.parseInt(strings[1]); break;
                        case "guiScale": client.options.guiScale = Integer.parseInt(strings[1]); break;
                        case "particles": client.options.particle = Integer.parseInt(strings[1]); break;
                        case "bobView": client.options.bobView = strings[1].equals("true"); break;
                        case "maxFps": client.options.maxFramerate = Integer.parseInt(strings[1]); break;
                        case "fancyGraphics": client.options.fancyGraphics = strings[1].equals("true"); break;
                        case "ao": client.options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1])); break;
                        case "renderClouds":
                            switch (strings[1]){
                                case "true": client.options.cloudMode = 2; break;
                                case "false": client.options.cloudMode = 0; break;
                                case "fast": client.options.cloudMode = 1; break;
                            } break;
                        case "chatVisibility": client.options.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(Integer.parseInt(strings[1])); break;
                        case "chatColors": client.options.chatColor = strings[1].equals("true"); break;
                        case "chatLinks": client.options.chatLink = strings[1].equals("true"); break;
                        case "chatLinksPrompt": client.options.chatLinkPrompt = strings[1].equals("true"); break;
                        case "chatOpacity": client.options.chatOpacity = Float.parseFloat(strings[1]); break;
                        case "fullscreen":
                            if(client.options.fullscreen != strings[1].equals("true")) {
                                client.toggleFullscreen();
                            }
                        case "enableVsync": client.options.vsync = strings[1].equals("true"); break;
                        case "useVbo": client.options.vbo = strings[1].equals("true"); break;
                        case "advancedItemTooltips": client.options.advancedItemTooltips = strings[1].equals("true"); break;
                        case "pauseOnLostFocus": client.options.pauseOnLostFocus = strings[1].equals("true"); break;
                        case "touchscreen": client.options.touchScreen = strings[1].equals("true"); break;
                        case "heldItemTooltips": client.options.heldItemTooltips = strings[1].equals("true"); break;
                        case "chatHeightFocused": client.options.chatHeightFocused = Float.parseFloat(strings[1]); break;
                        case "chatHeightUnfocused": client.options.chatHeightUnfocused = Float.parseFloat(strings[1]); break;
                        case "chatScale": client.options.chatScale = Float.parseFloat(strings[1]); break;
                        case "chatWidth": client.options.chatWidth = Float.parseFloat(strings[1]); break;
                        //Doesnt work rn
                        /*
                        case "forceUnicodeFont":
                            client.options.forceUnicode = strings[1].equals("true");
                            client.options.setValue();
                        */
                        case "allowBlockAlternatives": client.options.alternativeBlocks = strings[1].equals("true"); break;
                        case "reducedDebugInfo": client.options.reducedDebugInfo = strings[1].equals("true"); break;
                        case "entityShadows": client.options.entityShadows = strings[1].equals("true"); break;
                        case "perspective": client.options.perspective = Integer.parseInt(strings[1]); break;
                        case "piedirectory": ((PieChartAccessor) client).setopenProfilerSection(strings[1]); break;
                        case "hitboxes": client.getEntityRenderManager().method_10205(strings[1].equals("true")); break;
                        case "key":
                            for (KeyBinding keyBinding : client.options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setCode(Integer.parseInt(strings[1])); break;
                                }
                            } break;
                        case "soundCategory":
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string0_split[1].equals(soundCategory.getName())) {
                                    client.options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                                }
                            } break;
                        case "modelPart":
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    client.options.setPlayerModelPart(playerModelPart, strings[1].equals("true")); break;
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
        client.options.sensitivity = Check("Sensitivity",client.options.sensitivity,0,1);
        client.options.fov = Check("FOV",client.options.fov,30,110);
        client.options.gamma = Check("Brightness",client.options.gamma,0,5);
        client.options.viewDistance = Check("Render Distance",client.options.viewDistance,2,32);
        client.options.guiScale = Check("GUI Scale",client.options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        client.options.chatOpacity = Check("Chat Opacity",client.options.chatOpacity,0,1);
        client.options.chatHeightFocused = Check("(Chat) Focused Height",client.options.chatHeightFocused,0,1);
        client.options.chatHeightUnfocused = Check("(Chat) Unfocused Height",client.options.chatHeightUnfocused,0,1);
        client.options.chatScale = Check("Chat Text Size",client.options.chatScale,0,1);
        client.options.chatWidth = Check("ChatWidth",client.options.chatWidth,0,1);
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
        PrintWriter printer = null;
        try (Scanner scanner = new Scanner(new File("options.txt"))) {
            FileWriter writer = new FileWriter("standardoptions.txt");
            printer = new PrintWriter(writer);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine() + System.lineSeparator();
                printer.write(line);
            }

            printer.write("perspective:" + client.options.perspective + System.lineSeparator());
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
