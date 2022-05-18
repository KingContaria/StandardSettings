package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.PieChartAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ResetSettings {

    private static final Logger LOGGER = StandardSettings.LOGGER;
    protected static MinecraftClient client = MinecraftClient.getInstance();
    private static final File standardoptionsFile = new File("standardoptions.txt");
    public static int rdonworldjoin;
    public static boolean changerdonjoin = false;

    public static void LoadStandardSettings() {
        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            changerdonjoin = false;
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
                        case "renderDistanceOnWorldJoin":
                            rdonworldjoin = Integer.parseInt(strings[1]);
                            changerdonjoin = true; break;
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
                        case "piedirectory":
                            ((PieChartAccessor) client).setopenProfilerSection(strings[1]); break;
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
}
