package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatVisibility;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static File lastUsedFile;
    public static long fileLastModified;
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static float fovOnWorldJoin;

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
                    strings[1] = strings[1].trim();
                    String[] string0_split = strings[0].split("_", 2);
                    switch (string0_split[0]) {
                        case "mouseSensitivity" -> options.sensitivity = Float.parseFloat(strings[1]);
                        case "fov" -> options.fov = Float.parseFloat(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Float.parseFloat(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particle = Integer.parseInt(strings[1]);
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "anaglyph3d" -> {
                            if (options.anaglyph3d != Boolean.parseBoolean(strings[1])) {
                                options.anaglyph3d = Boolean.parseBoolean(strings[1]);
                                client.getTextureManager().reload(client.getResourceManager());
                            }
                        }
                        case "maxFps" -> options.maxFramerate = Integer.parseInt(strings[1]);
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1]));
                        case "lang" -> {
                            if (!options.language.equals(strings[1]) && ((LanguageManagerAccessor)client.getLanguageManager()).getField_6653().containsKey(strings[1])) {
                                client.getLanguageManager().method_5939((LanguageDefinition) ((LanguageManagerAccessor)client.getLanguageManager()).getField_6653().get(options.language = strings[1]));
                                client.getLanguageManager().reload(client.getResourceManager());
                            }
                        }
                        case "chatVisibility" -> options.field_7671 = ChatVisibility.get(Integer.parseInt(strings[1]));
                        case "chatColors" -> options.chatColor = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLink = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinkPrompt = Boolean.parseBoolean(strings[1]);
                        case "chatOpacity" -> options.chatOpacity = Float.parseFloat(strings[1]);
                        case "fullscreen" -> {
                            if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    client.toggleFullscreen();
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "enableVsync" -> options.vsync = Boolean.parseBoolean(strings[1]);
                        case "useVbo" -> options.vbo = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchScreen = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Float.parseFloat(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Float.parseFloat(strings[1]);
                        case "chatScale" -> options.chatScale = Float.parseFloat(strings[1]);
                        case "chatWidth" -> options.chatWidth = Float.parseFloat(strings[1]);
                        case "mipmapLevels" -> {
                            if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                                client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = Integer.parseInt(strings[1]));
                                client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                                client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
                                ((MinecraftClientAccessor)client).getModelManager().reload(client.getResourceManager());
                            }
                        }
                        case "forceUnicodeFont" -> client.textRenderer.method_960(client.getLanguageManager().method_5938() || (options.forceUnicode = Boolean.parseBoolean(strings[1])));
                        case "allowBlockAlternatives" -> options.alternativeBlocks = Boolean.parseBoolean(strings[1]);
                        case "reducedDebugInfo" -> options.reducedDebugInfo = Boolean.parseBoolean(strings[1]);
                        case "renderClouds" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "hitboxes" -> client.getEntityRenderManager().method_10205(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.perspective = Integer.parseInt(strings[1]) % 3;
                        case "piedirectory" -> {
                            if (!strings[1].split("\\.")[0].equals("root")) break;
                            ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]);
                        }
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Float.parseFloat(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "key" -> {
                            for (KeyBinding keyBinding : options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setCode(Integer.parseInt(strings[1])); break;
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
                    }
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                } catch (Exception exception) {
                    if (string != null && !string.equals("hitboxes:") && !string.equals("perspective:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
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
            options.save();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.sensitivity = check("Sensitivity", options.sensitivity, 0, 2);
        options.fov = Math.round(check("FOV", options.fov, 30, 110));
        options.gamma = check("Brightness", options.gamma, 0, 5);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = check("GUI Scale", options.guiScale, 0, 3);
        options.maxFramerate = check("Max FPS", options.maxFramerate, 1, 260);
        options.chatOpacity = check("Chat Opacity", options.chatOpacity, 0, 1);
        options.chatHeightFocused = check("(Chat) Focused Height", options.chatHeightFocused, 0, 1);
        options.chatHeightUnfocused = check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1);
        options.chatScale = check("Chat Text Size", options.chatScale, 0, 1);
        options.chatWidth = check("Chat Width", options.chatWidth, 0, 1);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels);
            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
            ((MinecraftClientAccessor)client).getModelManager().reload(client.getResourceManager());
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + soundCategory.name(), options.getSoundVolume(soundCategory), 0, 1));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
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

    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder("chatColors:" + options.chatColor + l +
                "chatLinks:" + options.chatLink + l +
                "chatLinksPrompt:" + options.chatLinkPrompt + l +
                "enableVsync:" + options.vsync + l +
                "useVbo:" + options.vbo + l +
                "forceUnicodeFont:" + options.forceUnicode + l +
                "invertYMouse:" + options.invertYMouse + l +
                "allowBlockAlternatives:" + options.alternativeBlocks + l +
                "reducedDebugInfo:" + options.reducedDebugInfo + l +
                "touchscreen:" + options.touchScreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "anaglyph3d:" + options.anaglyph3d + l +
                "mouseSensitivity:" + options.sensitivity + l +
                "fov:" + (options.fov - 70.0f) / 40.0f + l +
                "gamma:" + options.gamma + l +
                "renderDistance:" + options.viewDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.particle + l +
                "maxFps:" + options.maxFramerate + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.ao + l +
                "renderClouds:" + options.entityShadows + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.field_7671.getId() + l +
                "chatOpacity:" + options.chatOpacity + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "chatHeightFocused:" + options.chatHeightFocused + l +
                "chatHeightUnfocused:" + options.chatHeightUnfocused + l +
                "chatScale:" + options.chatScale + l +
                "chatWidth:" + options.chatWidth + l +
                "mipmapLevels:" + options.mipmapLevels + l);
        for (KeyBinding keyBinding : options.keysAll) {
            string.append("key_").append(keyBinding.getTranslationKey()).append(":").append(keyBinding.getCode()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.getEnabledPlayerModelParts().contains(playerModelPart)).append(l);
        }
        string.append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("fovOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:");

        return string.toString();
    }
}