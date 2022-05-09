package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.PieChartAccessor;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Iterator;

@Environment(value= EnvType.CLIENT)
public class ResetSettings {

    private static final Logger LOGGER = LogManager.getLogger();
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
                        LOGGER.warn("Skipping bad standardoption: {}", string);
                    }
                });
            }
            CompoundTag compoundTag2 = update(compoundTag);
            if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
                client.options.graphicsMode = "true".equals(compoundTag2.getString("fancyGraphics")) ? GraphicsMode.FANCY : GraphicsMode.FAST;
            }
            for (String string2 : compoundTag2.getKeys()) {
                String string22 = compoundTag2.getString(string2);
                try {
                    if ("autoJump".equals(string2)) {
                        client.options.autoJump = Boolean.parseBoolean(string22);
                    }
                    if ("autoSuggestions".equals(string2)) {
                        client.options.autoSuggestions = Boolean.parseBoolean(string22);
                    }
                    if ("chatColors".equals(string2)) {
                        client.options.chatColors = Boolean.parseBoolean(string22);
                    }
                    if ("chatLinks".equals(string2)) {
                        client.options.chatLinks = Boolean.parseBoolean(string22);
                    }
                    if ("chatLinksPrompt".equals(string2)) {
                        client.options.chatLinksPrompt = Boolean.parseBoolean(string22);
                    }
                    if ("enableVsync".equals(string2)) {
                        client.options.enableVsync = Boolean.parseBoolean(string22);
                        client.getWindow().setVsync(Boolean.parseBoolean(string22));
                    }
                    if ("entityShadows".equals(string2)) {
                        client.options.entityShadows = Boolean.parseBoolean(string22);
                    }
                    if ("forceUnicodeFont".equals(string2)) {
                        client.options.forceUnicodeFont = Boolean.parseBoolean(string22);
                        Option.FORCE_UNICODE_FONT.set(client.options, string22);
                    }
                    if ("discrete_mouse_scroll".equals(string2)) {
                        client.options.discreteMouseScroll = Boolean.parseBoolean(string22);
                    }
                    if ("invertYMouse".equals(string2)) {
                        client.options.invertYMouse = Boolean.parseBoolean(string22);
                    }
                    if ("realmsNotifications".equals(string2)) {
                        client.options.realmsNotifications = Boolean.parseBoolean(string22);
                    }
                    if ("reducedDebugInfo".equals(string2)) {
                        client.options.reducedDebugInfo = Boolean.parseBoolean(string22);
                    }
                    if ("showSubtitles".equals(string2)) {
                        client.options.showSubtitles = Boolean.parseBoolean(string22);
                    }
                    if ("touchscreen".equals(string2)) {
                        client.options.touchscreen = Boolean.parseBoolean(string22);
                    }
                    if ("fullscreen".equals(string2)) {
                        if(client.getWindow().isFullscreen() != Boolean.parseBoolean(string22)){
                            client.options.fullscreen = Boolean.parseBoolean(string22);
                            client.getWindow().toggleFullscreen();
                        }
                    }
                    if ("bobView".equals(string2)) {
                        client.options.bobView = Boolean.parseBoolean(string22);
                    }
                    if ("toggleCrouch".equals(string2)) {
                        client.options.sneakToggled = Boolean.parseBoolean(string22);
                    }
                    if ("toggleSprint".equals(string2)) {
                        client.options.sprintToggled = Boolean.parseBoolean(string22);
                    }
                    if ("mouseSensitivity".equals(string2)) {
                        client.options.mouseSensitivity = Float.parseFloat(string22);
                    }
                    if ("fov".equals(string2)) {
                        client.options.fov = Float.parseFloat(string22) * 40.0f + 70.0f;
                    }
                    if ("gamma".equals(string2)) {
                        client.options.gamma = Float.parseFloat(string22);
                    }
                    if ("renderDistance".equals(string2)) {
                        client.options.viewDistance = Integer.parseInt(string22);
                    }
                    if ("entityDistanceScaling".equals(string2)) {
                        client.options.entityDistanceScaling = Float.parseFloat(string22);
                    }
                    if ("guiScale".equals(string2)) {
                        client.options.guiScale = Integer.parseInt(string22);
                        client.onResolutionChanged();
                    }
                    if ("particles".equals(string2)) {
                        client.options.particles = ParticlesOption.byId(Integer.parseInt(string22));
                    }
                    if ("maxFps".equals(string2)) {
                        client.options.maxFps = Integer.parseInt(string22);
                        if (client.getWindow() != null) {
                            client.getWindow().setFramerateLimit(client.options.maxFps);
                        }
                    }
                    if ("graphicsMode".equals(string2)) {
                        client.options.graphicsMode = GraphicsMode.byId(Integer.parseInt(string22));
                    }
                    if ("ao".equals(string2)) {
                        switch ((int) Float.parseFloat(string22)) {
                            case 0: client.options.ao = AoOption.OFF; break;
                            case 1: client.options.ao = AoOption.MIN; break;
                            case 2: client.options.ao = AoOption.MAX;
                        }
                    }
                    if ("renderClouds".equals(string2)) {
                        if ("true".equals(string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.FANCY;
                        } else if ("false".equals(string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.OFF;
                        } else if ("fast".equals(string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.FAST;
                        }
                    }
                    if ("attackIndicator".equals(string2)) {
                        client.options.attackIndicator = AttackIndicator.byId(Integer.parseInt( string22));
                    }
                    //Deactivated
                    /*
                    if ("lang".equals(string2)) {
                        client.options.language = string22;
                        client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(string22));
                        client.reloadResources();
                    }
                    */
                    if ("chatVisibility".equals(string2)) {
                        client.options.chatVisibility = ChatVisibility.byId(Integer.parseInt(string22));
                    }
                    if ("chatOpacity".equals(string2)) {
                        client.options.chatOpacity = Float.parseFloat(string22);
                    }
                    if ("chatLineSpacing".equals(string2)) {
                        client.options.chatLineSpacing = Float.parseFloat(string22);
                    }
                    if ("textBackgroundOpacity".equals(string2)) {
                        client.options.textBackgroundOpacity = Float.parseFloat(string22);
                    }
                    if ("backgroundForChatOnly".equals(string2)) {
                        client.options.backgroundForChatOnly = "true".equals(string22);
                    }
                    if ("fullscreenResolution".equals(string2)) {
                        client.options.fullscreenResolution = string22;
                    }
                    if ("hideServerAddress".equals(string2)) {
                        client.options.hideServerAddress = "true".equals(string22);
                    }
                    if ("advancedItemTooltips".equals(string2)) {
                        client.options.advancedItemTooltips = "true".equals(string22);
                    }
                    if ("pauseOnLostFocus".equals(string2)) {
                        client.options.pauseOnLostFocus = "true".equals(string22);
                    }
                    if ("heldItemTooltips".equals(string2)) {
                        client.options.heldItemTooltips = "true".equals(string22);
                    }
                    if ("chatHeightFocused".equals(string2)) {
                        client.options.chatHeightFocused = Float.parseFloat(string22);
                    }
                    if ("chatDelay".equals(string2)) {
                        client.options.chatDelay = Float.parseFloat(string22);
                    }
                    if ("chatHeightUnfocused".equals(string2)) {
                        client.options.chatHeightUnfocused = Float.parseFloat(string22);
                    }
                    if ("chatScale".equals(string2)) {
                        client.options.chatScale = Float.parseFloat(string22);
                    }
                    if ("chatWidth".equals(string2)) {
                        client.options.chatWidth = Float.parseFloat(string22);
                    }
                    //Deactivated
                    /*
                    if ("mipmapLevels".equals(string2)) {
                        client.options.mipmapLevels = Integer.parseInt(string22);
                        client.reloadResources();
                    }
                    */
                    if ("mainHand".equals(string2)) {
                        client.options.mainArm = "left".equals(string22) ? Arm.LEFT : Arm.RIGHT;
                    }
                    if ("narrator".equals(string2)) {
                        client.options.narrator = NarratorOption.byId(Integer.parseInt(string22));
                    }
                    if ("biomeBlendRadius".equals(string2)) {
                        client.options.biomeBlendRadius = Integer.parseInt(string22);
                    }
                    if ("mouseWheelSensitivity".equals(string2)) {
                        client.options.mouseWheelSensitivity = Float.parseFloat(string22);
                    }
                    if ("rawMouseInput".equals(string2)) {
                        client.options.rawMouseInput = "true".equals(string22);
                        client.getWindow().setRawMouseMotion("true".equals( string22));
                    }
                    if ("perspective".equals(string2)) {
                        client.options.perspective = Integer.parseInt(string22);
                    }
                    if ("piedirectory".equals(string2)) {
                        string22 = string22.replace(".", "");
                        ((PieChartAccessor) client).setopenProfilerSection(string22);
                    }
                    for (KeyBinding keyBinding : client.options.keysAll) {
                        if (!string2.equals("key_" + keyBinding.getTranslationKey())) continue;
                        keyBinding.setBoundKey(InputUtil.fromTranslationKey(string22));
                    }
                    for (SoundCategory soundCategory : SoundCategory.values()) {
                        if (!string2.equals("soundCategory_" + (soundCategory).getName())) continue;
                        client.getSoundManager().updateSoundVolume(soundCategory, Float.parseFloat(string22));
                        client.options.setSoundVolume(soundCategory, Float.parseFloat(string22));
                    }
                    for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                        if (!string2.equals("modelPart_" + playerModelPart.getName())) continue;
                        client.options.setPlayerModelPart(playerModelPart, "true".equals(string22));
                    }

                    // Excluded are Language and Mipmap Levels because resources would've had to be reloaded, blocking world creation screen.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipping bad standardoption: {}:{}", string2, string22);
                }
            }
            KeyBinding.updateKeysByCode();
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load standardoptions", exception2);
        }
        client.options.write();
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
}
