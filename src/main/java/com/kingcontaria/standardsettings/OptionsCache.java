package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;

import java.io.IOException;
import java.util.Optional;

public class OptionsCache {

    private final MinecraftClient client;
    private final GameOptions options;
    private final Window window;
    private String levelName;
    private boolean autoJump;
    private boolean autoSuggestions;
    private boolean chatColors;
    private boolean chatLinks;
    private boolean chatLinksPrompt;
    private boolean enableVsync;
    private boolean entityShadows;
    private boolean forceUnicodeFont;
    private boolean discreteMouseScroll;
    private boolean invertYMouse;
    private boolean reducedDebugInfo;
    private boolean showSubtitles;
    private boolean touchscreen;
    private boolean fullscreen;
    private boolean bobView;
    private boolean sneakToggled;
    private boolean sprintToggled;
    private float distortionEffectScale;
    private float fovEffectScale;
    private double mouseSensitivity;
    private double fov;
    private boolean monochromeLogo;
    private double gamma;
    private int viewDistance;
    private float entityDistanceScaling;
    private int guiScale;
    private ParticlesMode particles;
    private int maxFps;
    private GraphicsMode graphicsMode;
    private AoMode ao;
    private CloudRenderMode cloudRenderMode;
    private AttackIndicator attackIndicator;
    private LanguageDefinition language;
    private ChatVisibility chatVisibility;
    private double chatOpacity;
    private double chatLineSpacing;
    private double textBackgroundOpacity;
    private boolean backgroundForChatOnly;
    private Optional<VideoMode> fullscreenResolution;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private double chatHeightFocused;
    private double chatDelay;
    private double chatHeightUnfocused;
    private double chatScale;
    private double chatWidth;
    private int mipmapLevels;
    private Arm mainArm;
    private NarratorMode narrator;
    private int biomeBlendRadius;
    private double mouseWheelSensitivity;
    private boolean rawMouseInput;
    private boolean entityCulling;
    private boolean sneaking;
    private boolean sprinting;
    private boolean chunkborders;
    private boolean hitboxes;
    private Perspective perspective;
    private String piedirectory;
    private boolean hudHidden;
    private String[] keysAll;
    private float[] soundCategories;
    private boolean[] playerModelParts;

    public OptionsCache(MinecraftClient client) {
        this.client = client;
        this.options = client.options;
        this.window = client.getWindow();
    }

    public void save(String levelName) {
        autoJump = options.autoJump;
        autoSuggestions = options.autoSuggestions;
        chatColors = options.chatColors;
        chatLinks = options.chatLinks;
        chatLinksPrompt = options.chatLinksPrompt;
        enableVsync = options.enableVsync;
        entityShadows = options.entityShadows;
        forceUnicodeFont = options.forceUnicodeFont;
        discreteMouseScroll = options.discreteMouseScroll;
        invertYMouse = options.invertYMouse;
        reducedDebugInfo = options.reducedDebugInfo;
        showSubtitles = options.showSubtitles;
        touchscreen = options.touchscreen;
        fullscreen = options.fullscreen;
        bobView = options.bobView;
        sneakToggled = options.sneakToggled;
        sprintToggled = options.sprintToggled;
        monochromeLogo = options.monochromeLogo;
        mouseSensitivity = options.mouseSensitivity;
        fov = options.fov;
        distortionEffectScale = options.distortionEffectScale;
        fovEffectScale = options.fovEffectScale;
        gamma = options.gamma;
        viewDistance = options.viewDistance;
        entityDistanceScaling = options.entityDistanceScaling;
        guiScale = options.guiScale;
        particles = options.particles;
        maxFps = options.maxFps;
        graphicsMode = options.graphicsMode;
        ao = options.ao;
        cloudRenderMode = options.cloudRenderMode;
        attackIndicator = options.attackIndicator;
        language = client.getLanguageManager().getLanguage();
        chatVisibility = options.chatVisibility;
        chatOpacity = options.chatOpacity;
        chatLineSpacing = options.chatLineSpacing;
        textBackgroundOpacity = options.textBackgroundOpacity;
        backgroundForChatOnly = options.backgroundForChatOnly;
        fullscreenResolution = window.getVideoMode();
        advancedItemTooltips = options.advancedItemTooltips;
        pauseOnLostFocus = options.pauseOnLostFocus;
        chatHeightFocused = options.chatHeightFocused;
        chatDelay = options.chatDelay;
        chatHeightUnfocused = options.chatHeightUnfocused;
        chatScale = options.chatScale;
        chatWidth = options.chatWidth;
        mipmapLevels = options.mipmapLevels;
        mainArm = options.mainArm;
        narrator = options.narrator;
        biomeBlendRadius = options.biomeBlendRadius;
        mouseWheelSensitivity = options.mouseWheelSensitivity;
        rawMouseInput = options.rawMouseInput;
        if (FabricLoader.getInstance().getModContainer("sodium").isPresent()) {
            entityCulling = SodiumClientMod.options().performance.useEntityCulling;
        }
        sneaking = options.keySneak.isPressed();
        sprinting = options.keySprint.isPressed();
        client.debugRenderer.toggleShowChunkBorder();
        chunkborders = client.debugRenderer.toggleShowChunkBorder();
        hitboxes = client.getEntityRenderDispatcher().shouldRenderHitboxes();
        perspective = options.getPerspective();
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        hudHidden = options.hudHidden;
        keysAll = new String[options.keysAll.length];
        int i = 0;
        for (KeyBinding key : options.keysAll) {
            keysAll[i++] = key.getBoundKeyTranslationKey();
        }
        soundCategories = new float[SoundCategory.values().length];
        i = 0;
        for (SoundCategory sound : SoundCategory.values()) {
            soundCategories[i++] = options.getSoundVolume(sound);
        }
        playerModelParts = new boolean[PlayerModelPart.values().length];
        i = 0;
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            playerModelParts[i++] = options.isPlayerModelPartEnabled(playerModelPart);
        }

        StandardSettings.LOGGER.info("Cached options for '{}'" + (this.levelName != null ? " & abandoned old cache" : ""), this.levelName = levelName);
    }

    public void load(String levelName) {
        if (!levelName.equals(this.levelName)) {
            return;
        }
        options.autoJump = autoJump;
        options.autoSuggestions = autoSuggestions;
        options.chatColors = chatColors;
        options.chatLinks = chatLinks;
        options.chatLinksPrompt = chatLinksPrompt;
        options.enableVsync = enableVsync;
        options.entityShadows = entityShadows;
        ((MinecraftClientAccessor)client).callInitFont(options.forceUnicodeFont = forceUnicodeFont);
        options.discreteMouseScroll = discreteMouseScroll;
        options.invertYMouse = invertYMouse;
        options.reducedDebugInfo = reducedDebugInfo;
        options.showSubtitles = showSubtitles;
        options.touchscreen = touchscreen;
        if (window.isFullscreen() != fullscreen) {
            if (client.isWindowFocused()) {
                window.toggleFullscreen();
            } else {
                StandardSettings.LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
            }
            options.fullscreen = window.isFullscreen();
        }
        options.bobView = bobView;
        options.sneakToggled = sneakToggled;
        options.sprintToggled = sprintToggled;
        options.monochromeLogo = monochromeLogo;
        options.mouseSensitivity = mouseSensitivity;
        options.fov = fov;
        options.distortionEffectScale = distortionEffectScale;
        options.fovEffectScale = fovEffectScale;
        options.gamma = gamma;
        options.viewDistance = viewDistance;
        options.entityDistanceScaling = entityDistanceScaling;
        window.calculateScaleFactor(options.guiScale = guiScale, options.forceUnicodeFont);
        options.particles = particles;
        window.setFramerateLimit(options.maxFps = maxFps);
        options.graphicsMode = graphicsMode;
        options.ao = ao;
        options.cloudRenderMode = cloudRenderMode;
        options.attackIndicator = attackIndicator;
        if (!language.getCode().equals(options.language)) {
            client.getLanguageManager().setLanguage(language);
            client.getLanguageManager().reload(client.getResourceManager());
            options.language = client.getLanguageManager().getLanguage().getCode();
        }
        options.chatVisibility = chatVisibility;
        options.chatOpacity = chatOpacity;
        options.chatLineSpacing = chatLineSpacing;
        options.textBackgroundOpacity = textBackgroundOpacity;
        options.backgroundForChatOnly = backgroundForChatOnly;
        if (fullscreenResolution != window.getVideoMode()) {
            window.setVideoMode(fullscreenResolution);
            window.applyVideoMode();
            options.fullscreenResolution = window.getVideoMode().toString();
        }
        options.advancedItemTooltips = advancedItemTooltips;
        options.pauseOnLostFocus = pauseOnLostFocus;
        options.chatHeightFocused = chatHeightFocused;
        options.chatDelay = chatDelay;
        options.chatHeightUnfocused = chatHeightUnfocused;
        options.chatScale = chatScale;
        options.chatWidth = chatWidth;
        if (options.mipmapLevels != mipmapLevels) {
            client.setMipmapLevels(options.mipmapLevels = mipmapLevels);
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mainArm = mainArm;
        options.narrator = narrator;
        options.biomeBlendRadius = biomeBlendRadius;
        options.mouseWheelSensitivity = mouseWheelSensitivity;
        options.rawMouseInput = rawMouseInput;
        if (FabricLoader.getInstance().getModContainer("sodium").isPresent()) {
            if (SodiumClientMod.options().performance.useEntityCulling != (SodiumClientMod.options().performance.useEntityCulling = entityCulling)) {
                try {
                    SodiumClientMod.options().writeChanges();
                } catch (IOException e) {
                    // empty catch block
                }
            }
        }
        if (options.sneakToggled && (sneaking != options.keySneak.isPressed())) {
            options.keySneak.setPressed(true);
        }
        if (options.sprintToggled && (sprinting != options.keySprint.isPressed())) {
            options.keySprint.setPressed(true);
        }
        if (client.debugRenderer.toggleShowChunkBorder() != chunkborders) {
            client.debugRenderer.toggleShowChunkBorder();
        }
        client.getEntityRenderDispatcher().setRenderHitboxes(hitboxes);
        options.setPerspective(perspective);
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        options.hudHidden = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.keysAll) {
            keyBinding.setBoundKey(InputUtil.fromTranslationKey(keysAll[i++]));
        }
        KeyBinding.updateKeysByCode();
        i = 0;
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, soundCategories[i++]);
        }
        i = 0;
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            options.togglePlayerModelPart(playerModelPart, playerModelParts[i++]);
        }

        StandardSettings.LOGGER.info("Restored cached options for '{}'", this.levelName);
        this.levelName = null;
    }

}