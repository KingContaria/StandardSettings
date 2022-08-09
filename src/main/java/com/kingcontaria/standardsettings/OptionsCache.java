package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.Set;

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
    private double mouseSensitivity;
    private double fov;
    private double gamma;
    private int viewDistance;
    private int guiScale;
    private ParticlesOption particles;
    private int maxFps;
    private boolean fancyGraphics;
    private AoOption ao;
    private CloudRenderMode cloudRenderMode;
    private AttackIndicator attackIndicator;
    private LanguageDefinition language;
    private ChatVisibility chatVisibility;
    private double chatOpacity;
    private double textBackgroundOpacity;
    private boolean backgroundForChatOnly;
    private Optional<VideoMode> fullscreenResolution;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private double chatHeightFocused;
    private double chatHeightUnfocused;
    private double chatScale;
    private double chatWidth;
    private int mipmapLevels;
    private Arm mainArm;
    private NarratorOption narrator;
    private int biomeBlendRadius;
    private double mouseWheelSensitivity;
    private boolean rawMouseInput;
    private boolean entityCulling;
    private boolean sneaking;
    private boolean sprinting;
    private boolean chunkborders;
    private boolean hitboxes;
    private int perspective;
    private String piedirectory;
    private boolean hudHidden;
    private final String[] keysAll;
    private final float[] soundCategories;
    private Set<PlayerModelPart> playerModelParts;

    public OptionsCache(MinecraftClient client) {
        this.client = client;
        this.options = client.options;
        this.window = client.getWindow();
        keysAll = new String[options.keysAll.length];
        soundCategories = new float[SoundCategory.values().length];
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
        mouseSensitivity = options.mouseSensitivity;
        fov = options.fov;
        gamma = options.gamma;
        viewDistance = options.viewDistance;
        guiScale = options.guiScale;
        particles = options.particles;
        maxFps = options.maxFps;
        fancyGraphics = options.fancyGraphics;
        ao = options.ao;
        cloudRenderMode = options.cloudRenderMode;
        attackIndicator = options.attackIndicator;
        language = client.getLanguageManager().getLanguage();
        chatVisibility = options.chatVisibility;
        chatOpacity = options.chatOpacity;
        textBackgroundOpacity = options.textBackgroundOpacity;
        backgroundForChatOnly = options.backgroundForChatOnly;
        fullscreenResolution = window.getVideoMode();
        advancedItemTooltips = options.advancedItemTooltips;
        pauseOnLostFocus = options.pauseOnLostFocus;
        chatHeightFocused = options.chatHeightFocused;
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
            entityCulling = SodiumClientMod.options().advanced.useAdvancedEntityCulling;
        }
        sneaking = options.keySneak.isPressed();
        sprinting = options.keySprint.isPressed();
        client.debugRenderer.toggleShowChunkBorder();
        chunkborders = client.debugRenderer.toggleShowChunkBorder();
        hitboxes = client.getEntityRenderManager().shouldRenderHitboxes();
        perspective = options.perspective;
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        hudHidden = options.hudHidden;
        int i = 0;
        for (KeyBinding key : options.keysAll) {
            keysAll[i++] = key.getName();
        }
        i = 0;
        for (SoundCategory sound : SoundCategory.values()) {
            soundCategories[i++] = options.getSoundVolume(sound);
        }
        playerModelParts = options.getEnabledPlayerModelParts();

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
        client.getFontManager().setForceUnicodeFont(options.forceUnicodeFont = forceUnicodeFont, Util.getServerWorkerExecutor(), client);
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
        options.mouseSensitivity = mouseSensitivity;
        options.fov = fov;
        options.gamma = gamma;
        options.viewDistance = viewDistance;
        window.calculateScaleFactor(options.guiScale = guiScale, options.forceUnicodeFont);
        options.particles = particles;
        window.setFramerateLimit(options.maxFps = maxFps);
        options.fancyGraphics = fancyGraphics;
        options.ao = ao;
        options.cloudRenderMode = cloudRenderMode;
        options.attackIndicator = attackIndicator;
        if (!language.getCode().equals(options.language)) {
            client.getLanguageManager().setLanguage(language);
            client.getLanguageManager().apply(client.getResourceManager());
            options.language = client.getLanguageManager().getLanguage().getCode();
        }
        options.chatVisibility = chatVisibility;
        options.chatOpacity = chatOpacity;
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
        options.chatHeightUnfocused = chatHeightUnfocused;
        options.chatScale = chatScale;
        options.chatWidth = chatWidth;
        if (options.mipmapLevels != mipmapLevels) {
            client.resetMipmapLevels(options.mipmapLevels = mipmapLevels);
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mainArm = mainArm;
        options.narrator = narrator;
        options.biomeBlendRadius = biomeBlendRadius;
        options.mouseWheelSensitivity = mouseWheelSensitivity;
        options.rawMouseInput = rawMouseInput;
        if (FabricLoader.getInstance().getModContainer("sodium").isPresent()) {
            if (SodiumClientMod.options().advanced.useAdvancedEntityCulling != (SodiumClientMod.options().advanced.useAdvancedEntityCulling = entityCulling)) {
                SodiumClientMod.options().writeChanges();
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
        client.getEntityRenderManager().setRenderHitboxes(hitboxes);
        options.perspective = perspective;
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        options.hudHidden = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.keysAll) {
            keyBinding.setKeyCode(InputUtil.fromName(keysAll[i++]));
        }
        KeyBinding.updateKeysByCode();
        i = 0;
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, soundCategories[i++]);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            options.setPlayerModelPart(playerModelPart, playerModelParts.contains(playerModelPart));
        }

        StandardSettings.LOGGER.info("Restored cached options for '{}'", this.levelName);
        this.levelName = null;
    }

}