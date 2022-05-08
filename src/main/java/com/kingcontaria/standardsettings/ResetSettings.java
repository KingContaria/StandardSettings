package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

@Environment(value= EnvType.CLIENT)
public class ResetSettings {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    protected MinecraftClient client;
    private final Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
    private File standardoptionsFile;

    public ResetSettings() {
        this.client = MinecraftClient.getInstance();
        this.standardoptionsFile = new File(standardoptionsFile, "standardoptions.txt");
        LoadStandardSettings();
        client.options.write();
    }

    public void LoadStandardSettings() {
        try {
            if (!this.standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            this.soundVolumeLevels.clear();
            CompoundTag compoundTag = new CompoundTag();
            try (BufferedReader bufferedReader = Files.newReader((File)this.standardoptionsFile, (Charset)Charsets.UTF_8);) {
                bufferedReader.lines().forEach(string -> {
                    try {
                        Iterator iterator = COLON_SPLITTER.split((CharSequence) string).iterator();
                        compoundTag.putString((String) iterator.next(), (String) iterator.next());
                    } catch (Exception exception) {
                        LOGGER.warn("Skipping bad standardoption: {}", string);
                    }
                });
            }
            CompoundTag compoundTag2 = this.update(compoundTag);
            if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
                client.options.graphicsMode = "true".equals((Object)compoundTag2.getString("fancyGraphics")) ? GraphicsMode.FANCY : GraphicsMode.FAST;
            }
            for (String string2 : compoundTag2.getKeys()) {
                String string22 = compoundTag2.getString(string2);
                try {
                    if ("autoJump".equals((Object)string2)) {
                        client.options.autoJump = Boolean.parseBoolean(string22);
                    }
                    if ("autoSuggestions".equals((Object)string2)) {
                        client.options.autoSuggestions = Boolean.parseBoolean(string22);
                    }
                    if ("chatColors".equals((Object)string2)) {
                        client.options.chatColors = Boolean.parseBoolean(string22);
                    }
                    if ("chatLinks".equals((Object)string2)) {
                        client.options.chatLinks = Boolean.parseBoolean(string22);
                    }
                    if ("chatLinksPrompt".equals((Object)string2)) {
                        client.options.chatLinksPrompt = Boolean.parseBoolean(string22);
                    }
                    if ("enableVsync".equals((Object)string2)) {
                        client.options.enableVsync = Boolean.parseBoolean(string22);
                        client.getWindow().setVsync(Boolean.parseBoolean(string22));
                    }
                    if ("entityShadows".equals((Object)string2)) {
                        client.options.entityShadows = Boolean.parseBoolean(string22);
                    }
                    if ("forceUnicodeFont".equals((Object)string2)) {
                        client.options.forceUnicodeFont = Boolean.parseBoolean(string22);
                        Option.FORCE_UNICODE_FONT.set(client.options, string22);
                    }
                    if ("discrete_mouse_scroll".equals((Object)string2)) {
                        client.options.discreteMouseScroll = Boolean.parseBoolean(string22);
                    }
                    if ("invertYMouse".equals((Object)string2)) {
                        client.options.invertYMouse = Boolean.parseBoolean(string22);
                    }
                    if ("realmsNotifications".equals((Object)string2)) {
                        client.options.realmsNotifications = Boolean.parseBoolean(string22);
                    }
                    if ("reducedDebugInfo".equals((Object)string2)) {
                        client.options.reducedDebugInfo = Boolean.parseBoolean(string22);
                    }
                    if ("showSubtitles".equals((Object)string2)) {
                        client.options.showSubtitles = Boolean.parseBoolean(string22);
                    }
                    if ("touchscreen".equals((Object)string2)) {
                        client.options.touchscreen = Boolean.parseBoolean(string22);
                    }
                    if ("fullscreen".equals((Object)string2)) {
                        if(client.getWindow().isFullscreen() != Boolean.parseBoolean(string22)){
                            client.options.fullscreen = Boolean.parseBoolean(string22);
                            client.getWindow().toggleFullscreen();
                        }
                    }
                    if ("bobView".equals((Object)string2)) {
                        client.options.bobView = Boolean.parseBoolean(string22);
                    }
                    if ("toggleCrouch".equals((Object)string2)) {
                        client.options.sneakToggled = Boolean.parseBoolean(string22);
                    }
                    if ("toggleSprint".equals((Object)string2)) {
                        client.options.sprintToggled = Boolean.parseBoolean(string22);
                    }
                    if ("mouseSensitivity".equals((Object)string2)) {
                        client.options.mouseSensitivity = Float.parseFloat(string22);
                    }
                    if ("fov".equals((Object)string2)) {
                        client.options.fov = Float.parseFloat(string22) * 40.0f + 70.0f;
                    }
                    if ("gamma".equals((Object)string2)) {
                        client.options.gamma = Float.parseFloat(string22);
                    }
                    if ("renderDistance".equals((Object)string2)) {
                        client.options.viewDistance = Integer.parseInt((String)string22);
                    }
                    if ("entityDistanceScaling".equals((Object)string2)) {
                        client.options.entityDistanceScaling = Float.parseFloat((String)string22);
                    }
                    if ("guiScale".equals((Object)string2)) {
                        client.options.guiScale = Integer.parseInt((String)string22);
                        client.onResolutionChanged();
                    }
                    if ("particles".equals((Object)string2)) {
                        client.options.particles = ParticlesOption.byId(Integer.parseInt((String)string22));
                    }
                    if ("maxFps".equals((Object)string2)) {
                        client.options.maxFps = Integer.parseInt((String)string22);
                        if (this.client.getWindow() != null) {
                            this.client.getWindow().setFramerateLimit(client.options.maxFps);
                        }
                    }
                    if ("graphicsMode".equals((Object)string2)) {
                        client.options.graphicsMode = GraphicsMode.byId(Integer.parseInt((String)string22));
                    }
                    if ("ao".equals((Object)string2)) {
                        client.options.ao = "true".equals((Object)string22) ? AoOption.MAX : ("false".equals((Object)string22) ? AoOption.OFF : AoOption.getOption(Integer.parseInt((String)string22)));
                    }
                    if ("renderClouds".equals((Object)string2)) {
                        if ("true".equals((Object)string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.FANCY;
                        } else if ("false".equals((Object)string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.OFF;
                        } else if ("fast".equals((Object)string22)) {
                            client.options.cloudRenderMode = CloudRenderMode.FAST;
                        }
                    }
                    if ("attackIndicator".equals((Object)string2)) {
                        client.options.attackIndicator = AttackIndicator.byId(Integer.parseInt((String) string22));
                    }
                    //Deactivated
                    /*
                    if ("lang".equals((Object)string2)) {
                        client.options.language = string22;
                        client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(string22));
                        client.reloadResources();
                    }
                    */
                    if ("chatVisibility".equals((Object)string2)) {
                        client.options.chatVisibility = ChatVisibility.byId(Integer.parseInt((String)string22));
                    }
                    if ("chatOpacity".equals((Object)string2)) {
                        client.options.chatOpacity = Float.parseFloat(string22);
                    }
                    if ("chatLineSpacing".equals((Object)string2)) {
                        client.options.chatLineSpacing = Float.parseFloat(string22);
                    }
                    if ("textBackgroundOpacity".equals((Object)string2)) {
                        client.options.textBackgroundOpacity = Float.parseFloat(string22);
                    }
                    if ("backgroundForChatOnly".equals((Object)string2)) {
                        client.options.backgroundForChatOnly = "true".equals((Object)string22);
                    }
                    if ("fullscreenResolution".equals((Object)string2)) {
                        client.options.fullscreenResolution = string22;
                    }
                    if ("hideServerAddress".equals((Object)string2)) {
                        client.options.hideServerAddress = "true".equals((Object)string22);
                    }
                    if ("advancedItemTooltips".equals((Object)string2)) {
                        client.options.advancedItemTooltips = "true".equals((Object)string22);
                    }
                    if ("pauseOnLostFocus".equals((Object)string2)) {
                        client.options.pauseOnLostFocus = "true".equals((Object)string22);
                    }
                    if ("heldItemTooltips".equals((Object)string2)) {
                        client.options.heldItemTooltips = "true".equals((Object)string22);
                    }
                    if ("chatHeightFocused".equals((Object)string2)) {
                        client.options.chatHeightFocused = Float.parseFloat(string22);
                    }
                    if ("chatDelay".equals((Object)string2)) {
                        client.options.chatDelay = Float.parseFloat(string22);
                    }
                    if ("chatHeightUnfocused".equals((Object)string2)) {
                        client.options.chatHeightUnfocused = Float.parseFloat(string22);
                    }
                    if ("chatScale".equals((Object)string2)) {
                        client.options.chatScale = Float.parseFloat(string22);
                    }
                    if ("chatWidth".equals((Object)string2)) {
                        client.options.chatWidth = Float.parseFloat(string22);
                    }
                    //Deactivated
                    /*
                    if ("mipmapLevels".equals((Object)string2)) {
                        client.options.mipmapLevels = Integer.parseInt((String)string22);
                        client.reloadResources();
                    }
                    */
                    if ("mainHand".equals((Object)string2)) {
                        Arm arm = client.options.mainArm = "left".equals((Object)string22) ? Arm.LEFT : Arm.RIGHT;
                    }
                    if ("narrator".equals((Object)string2)) {
                        client.options.narrator = NarratorOption.byId(Integer.parseInt((String)string22));
                    }
                    if ("biomeBlendRadius".equals((Object)string2)) {
                        client.options.biomeBlendRadius = Integer.parseInt((String)string22);
                    }
                    if ("mouseWheelSensitivity".equals((Object)string2)) {
                        client.options.mouseWheelSensitivity = Float.parseFloat(string22);
                    }
                    if ("rawMouseInput".equals((Object)string2)) {
                        client.options.rawMouseInput = "true".equals((Object)string22);
                        client.getWindow().setRawMouseMotion("true".equals((Object) string22));
                    }
                    if ("perspective".equals((Object)string2)) {
                        client.options.perspective = Integer.parseInt((String)string22);
                    }
                    if ("piedirectory".equals((Object)string2)) {
                        string22 = string22.replace(".", "");
                        ((PieChartAccessor) client).setopenProfilerSection((String)string22);
                    }
                    for (KeyBinding keyBinding : client.options.keysAll) {
                        if (!string2.equals((Object)("key_" + keyBinding.getTranslationKey()))) continue;
                        keyBinding.setBoundKey(InputUtil.fromTranslationKey(string22));
                    }
                    for (SoundCategory soundCategory : SoundCategory.values()) {
                        if (!string2.equals((Object)("soundCategory_" + ((SoundCategory)((Object)soundCategory)).getName()))) continue;
                        this.client.getSoundManager().updateSoundVolume(soundCategory, Float.parseFloat(string22));
                        client.options.setSoundVolume(soundCategory, Float.parseFloat(string22));
                    }
                    for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                        if (!string2.equals((Object)("modelPart_" + ((PlayerModelPart)((Object)playerModelPart)).getName()))) continue;
                        client.options.setPlayerModelPart((PlayerModelPart)((Object)playerModelPart), "true".equals((Object)string22));
                    }

                    // Excluded are Language and Mipmap Levels because resources would've had to be reloaded.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft are not included.
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipping bad standardoption: {}:{}", (Object)string2, (Object)string22);
                }
            }
            KeyBinding.updateKeysByCode();
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load standardoptions", (Throwable)exception2);
        }
    }

    private CompoundTag update(CompoundTag tag) {
        int i = 0;
        try {
            i = Integer.parseInt((String)tag.getString("version"));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return NbtHelper.update(client.getDataFixer(), DataFixTypes.OPTIONS, tag, i);
    }
}
