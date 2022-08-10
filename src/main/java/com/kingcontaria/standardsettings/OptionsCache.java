package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.Display;

import java.util.Set;

public class OptionsCache {

    private final MinecraftClient client;
    private final GameOptions options;
    private String levelName;
    private boolean chatColor;
    private boolean chatLink;
    private boolean chatLinkPrompt;
    private boolean vsync;
    private boolean vbo;
    private boolean entityShadows;
    private boolean forceUnicode;
    private boolean alternativeBlocks;
    private boolean invertYMouse;
    private boolean reducedDebugInfo;
    private boolean touchScreen;
    private boolean fullscreen;
    private boolean bobView;
    private boolean anaglyph3d;
    private float sensitivity;
    private float fov;
    private float gamma;
    private int viewDistance;
    private int guiScale;
    private int particle;
    private int maxFramerate;
    private boolean fancyGraphics;
    private int ao;
    private int cloudMode;
    private LanguageDefinition language;
    private PlayerEntity.ChatVisibilityType chatVisibilityType;
    private float chatOpacity;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private float chatHeightFocused;
    private float chatHeightUnfocused;
    private float chatScale;
    private float chatWidth;
    private int mipmapLevels;
    private boolean hitboxes;
    private int perspective;
    private String piedirectory;
    private boolean hudHidden;
    private final int[] keysAll;
    private final float[] soundCategories;
    private Set<PlayerModelPart> playerModelParts;

    public OptionsCache(MinecraftClient client) {
        this.client = client;
        this.options = client.options;
        keysAll = new int[options.keysAll.length];
        soundCategories = new float[SoundCategory.values().length];
    }

    public void save(String levelName) {
        chatColor = options.chatColor;
        chatLink = options.chatLink;
        chatLinkPrompt = options.chatLinkPrompt;
        vsync = options.vsync;
        vbo = options.vbo;
        entityShadows = options.entityShadows;
        forceUnicode = options.forceUnicode;
        alternativeBlocks = options.alternativeBlocks;
        invertYMouse = options.invertYMouse;
        reducedDebugInfo = options.reducedDebugInfo;
        touchScreen = options.touchScreen;
        fullscreen = options.fullscreen;
        bobView = options.bobView;
        anaglyph3d = options.anaglyph3d;
        sensitivity = options.sensitivity;
        fov = options.fov;
        gamma = options.gamma;
        viewDistance = options.viewDistance;
        guiScale = options.guiScale;
        particle = options.particle;
        maxFramerate = options.maxFramerate;
        fancyGraphics = options.fancyGraphics;
        ao = options.ao;
        cloudMode = options.cloudMode;
        language = client.getLanguageManager().getLanguage();
        chatVisibilityType = options.chatVisibilityType;
        chatOpacity = options.chatOpacity;
        advancedItemTooltips = options.advancedItemTooltips;
        pauseOnLostFocus = options.pauseOnLostFocus;
        chatHeightFocused = options.chatHeightFocused;
        chatHeightUnfocused = options.chatHeightUnfocused;
        chatScale = options.chatScale;
        chatWidth = options.chatWidth;
        mipmapLevels = options.mipmapLevels;
        hitboxes = client.getEntityRenderManager().method_10203();
        perspective = options.perspective;
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        hudHidden = options.hudHidden;
        int i = 0;
        for (KeyBinding key : options.keysAll) {
            keysAll[i++] = key.getCode();
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
        options.chatColor = chatColor;
        options.chatLink = chatLink;
        options.chatLinkPrompt = chatLinkPrompt;
        Display.setVSyncEnabled(options.vsync = vsync);
        options.vbo = vbo;
        options.entityShadows = entityShadows;
        client.textRenderer.method_960(client.getLanguageManager().method_5938() || (options.forceUnicode = forceUnicode));
        options.alternativeBlocks = alternativeBlocks;
        options.invertYMouse = invertYMouse;
        options.reducedDebugInfo = reducedDebugInfo;
        options.touchScreen = touchScreen;
        if (options.fullscreen != (options.fullscreen = fullscreen)) {
            if (client.isWindowFocused()) {
                client.toggleFullscreen();
            } else {
                StandardSettings.LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
            }
        }
        options.bobView = bobView;
        if (options.anaglyph3d != (options.anaglyph3d = anaglyph3d)) {
            client.getTextureManager().reload(client.getResourceManager());
        }
        options.sensitivity = sensitivity;
        options.fov = fov;
        options.gamma = gamma;
        options.viewDistance = viewDistance;
        options.guiScale = guiScale;
        options.particle = particle;
        options.maxFramerate = maxFramerate;
        options.fancyGraphics = fancyGraphics;
        options.ao = ao;
        options.cloudMode = cloudMode;
        if (!language.method_5935().equals(options.language)) {
            client.getLanguageManager().method_5939(language);
            client.getLanguageManager().reload(client.getResourceManager());
            options.language = client.getLanguageManager().getLanguage().method_5935();
        }
        options.chatVisibilityType = chatVisibilityType;
        options.chatOpacity = chatOpacity;
        options.advancedItemTooltips = advancedItemTooltips;
        options.pauseOnLostFocus = pauseOnLostFocus;
        options.chatHeightFocused = chatHeightFocused;
        options.chatHeightUnfocused = chatHeightUnfocused;
        options.chatScale = chatScale;
        options.chatWidth = chatWidth;
        if (options.mipmapLevels != mipmapLevels) {
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = mipmapLevels);
            client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            client.getSpriteAtlasTexture().setFilter(false, options.mipmapLevels > 0);
            client.getTextureManager().reload(client.getResourceManager());
        }
        client.getEntityRenderManager().method_10205(hitboxes);
        options.perspective = perspective;
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        options.hudHidden = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.keysAll) {
            keyBinding.setCode(keysAll[i++]);
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