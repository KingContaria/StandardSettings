package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.minecraft.class_4107;
import net.minecraft.class_4117;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Set;

public class OptionsCache {

    private final MinecraftClient client;
    private final GameOptions options;
    private final class_4117 window;
    private String levelName;
    private boolean autoJump;
    private boolean autoSuggestions;
    private boolean chatColors;
    private boolean chatLinks;
    private boolean chatLinksPrompt;
    private boolean enableVsync;
    private boolean vbo;
    private boolean entityShadows;
    private boolean forceUnicodeFont;
    private boolean invertYMouse;
    private boolean reducedDebugInfo;
    private boolean showSubtitles;
    private boolean touchscreen;
    private boolean fullscreen;
    private boolean bobView;
    private double mouseSensitivity;
    private double fov;
    private double gamma;
    private int viewDistance;
    private int guiScale;
    private int particles;
    private int maxFps;
    private boolean fancyGraphics;
    private int ao;
    private int cloudMode;
    private int attackIndicator;
    private LanguageDefinition language;
    private PlayerEntity.ChatVisibilityType chatVisibilityType;
    private double chatOpacity;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private double chatHeightFocused;
    private double chatHeightUnfocused;
    private double chatScale;
    private double chatWidth;
    private int mipmapLevels;
    private HandOption mainArm;
    private int narrator;
    private int biomeBlendRadius;
    private double mouseWheelSensitivity;
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
        this.window = client.field_19944;
        keysAll = new String[options.keysAll.length];
        soundCategories = new float[SoundCategory.values().length];
    }

    public void save(String levelName) {
        autoJump = options.field_14902;
        autoSuggestions = options.field_19978;
        chatColors = options.chatColor;
        chatLinks = options.chatLink;
        chatLinksPrompt = options.chatLinkPrompt;
        enableVsync = options.field_19991;
        vbo = options.vbo;
        entityShadows = options.entityShadows;
        forceUnicodeFont = options.forceUnicode;
        invertYMouse = options.invertYMouse;
        reducedDebugInfo = options.reducedDebugInfo;
        showSubtitles = options.field_13292;
        touchscreen = options.touchScreen;
        fullscreen = options.fullscreen;
        bobView = options.bobView;
        mouseSensitivity = options.field_19988;
        fov = options.field_19984;
        gamma = options.field_19985;
        viewDistance = options.viewDistance;
        guiScale = options.guiScale;
        particles = options.particle;
        maxFps = options.maxFramerate;
        fancyGraphics = options.fancyGraphics;
        ao = options.ao;
        cloudMode = options.cloudMode;
        attackIndicator = options.field_13290;
        language = client.getLanguageManager().getLanguage();
        chatVisibilityType = options.chatVisibilityType;
        chatOpacity = options.field_19989;
        advancedItemTooltips = options.field_19992;
        pauseOnLostFocus = options.field_19973;
        chatHeightFocused = options.field_19977;
        chatHeightUnfocused = options.field_19976;
        chatScale = options.field_19974;
        chatWidth = options.field_19975;
        mipmapLevels = options.mipmapLevels;
        mainArm = options.field_13289;
        narrator = options.field_15879;
        biomeBlendRadius = options.field_19979;
        mouseWheelSensitivity = options.field_19980;
        client.field_13282.method_13451();
        chunkborders = client.field_13282.method_13451();
        hitboxes = client.getEntityRenderManager().method_10203();
        perspective = options.perspective;
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        hudHidden = options.field_19987;
        int i = 0;
        for (KeyBinding key : options.keysAll) {
            keysAll[i++] = key.getTranslationKey();
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
        options.field_14902 = autoJump;
        options.field_19978 = autoSuggestions;
        options.chatColor = chatColors;
        options.chatLink = chatLinks;
        options.chatLinkPrompt = chatLinksPrompt;
        options.field_19991 = enableVsync;
        client.field_19944.method_18306();
        options.entityShadows = entityShadows;
        client.method_9391().method_18454(options.forceUnicode = forceUnicodeFont);
        options.invertYMouse = invertYMouse;
        options.reducedDebugInfo = reducedDebugInfo;
        options.field_13292 = showSubtitles;
        options.touchScreen = touchscreen;
        if (options.fullscreen != fullscreen) {
            if (client.isWindowFocused()) {
                window.method_18313();
            } else {
                StandardSettings.LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
            }
        }
        options.bobView = bobView;
        options.field_19988 = mouseSensitivity;
        options.field_19984 = fov;
        options.field_19985 = gamma;
        options.viewDistance = viewDistance;
        options.guiScale = guiScale;
        window.method_18314();
        options.particle = particles;
        options.maxFramerate = maxFps;
        options.fancyGraphics = fancyGraphics;
        options.ao = ao;
        options.cloudMode = cloudMode;
        options.field_13290 = attackIndicator;
        if (!options.language.equals(options.language = language.method_5935())) {
            client.getLanguageManager().method_5939(language);
            client.getLanguageManager().reload(client.getResourceManager());
        }
        options.chatVisibilityType = chatVisibilityType;
        options.field_19989 = chatOpacity;
        options.field_19992 = advancedItemTooltips;
        options.field_19973 = pauseOnLostFocus;
        options.field_19977 = chatHeightFocused;
        options.field_19976 = chatHeightUnfocused;
        options.field_19974 = chatScale;
        options.field_19975 = chatWidth;
        if (options.mipmapLevels != mipmapLevels) {
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = mipmapLevels);
            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
            client.method_18222().reload(client.getResourceManager());
        }
        options.field_13289 = mainArm;
        options.field_15879 = narrator;
        options.field_19979 = biomeBlendRadius;
        options.field_19980 = mouseWheelSensitivity;
        if (client.field_13282.method_13451() != chunkborders) {
            client.field_13282.method_13451();
        }
        client.getEntityRenderManager().method_10205(hitboxes);
        options.perspective = perspective;
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        options.field_19987 = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.keysAll) {
            keyBinding.method_18170(class_4107.method_18156(keysAll[i++]));
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