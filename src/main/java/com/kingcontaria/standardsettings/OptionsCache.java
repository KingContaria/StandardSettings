package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Language;
import org.lwjgl.opengl.Display;

public class OptionsCache {

    private final Minecraft client;
    private final GameOptions options;
    private String levelName;
    private float musicVolume;
    private float soundVolume;
    private boolean chatColor;
    private boolean chatLink;
    private boolean chatLinkPrompt;
    private boolean vsync;
    private boolean renderClouds;
    private boolean invertYMouse;
    private boolean touchScreen;
    private boolean fullscreen;
    private boolean bobView;
    private boolean anaglyph3d;
    private float sensitivity;
    private float fov;
    private float gamma;
    private int renderDistance;
    private int guiScale;
    private int particle;
    private int maxFramerate;
    private boolean fancyGraphics;
    private int ao;
    private String language;
    private int chatVisibility;
    private float chatOpacity;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private float chatHeightFocused;
    private float chatHeightUnfocused;
    private float chatScale;
    private float chatWidth;
    private boolean hitboxes;
    private int perspective;
    private String piedirectory;
    private boolean hudHidden;
    private final int[] keysAll;

    public OptionsCache(Minecraft client) {
        this.client = client;
        this.options = client.options;
        keysAll = new int[options.keysAll.length];
    }

    public void save(String levelName) {
        musicVolume = options.musicVolume;
        soundVolume = options.soundVolume;
        chatColor = options.chatColor;
        chatLink = options.chatLink;
        chatLinkPrompt = options.chatLinkPrompt;
        vsync = options.vsync;
        renderClouds = options.renderClouds;
        invertYMouse = options.invertYMouse;
        touchScreen = options.touchScreen;
        fullscreen = options.fullscreen;
        bobView = options.bobView;
        anaglyph3d = options.anaglyph3d;
        sensitivity = options.sensitivity;
        fov = options.fov;
        gamma = options.gamma;
        renderDistance = options.renderDistance;
        guiScale = options.guiScale;
        particle = options.particle;
        maxFramerate = options.maxFramerate;
        fancyGraphics = options.fancyGraphics;
        ao = options.ao;
        language = client.options.language;
        chatVisibility = options.chatVisibility;
        chatOpacity = options.chatOpacity;
        advancedItemTooltips = options.advancedItemTooltips;
        pauseOnLostFocus = options.pauseOnLostFocus;
        chatHeightFocused = options.chatHeightFocused;
        chatHeightUnfocused = options.chatHeightUnfocused;
        chatScale = options.chatScale;
        chatWidth = options.chatWidth;
        hitboxes = EntityRenderDispatcher.field_5192;
        perspective = options.perspective;
        piedirectory = ((MinecraftAccessor)client).getOpenProfilerSection();
        hudHidden = options.hudHidden;
        int i = 0;
        for (KeyBinding key : options.keysAll) {
            keysAll[i++] = key.code;
        }

        StandardSettings.LOGGER.info("Cached options for '" + (this.levelName = levelName) + "'" + (this.levelName != null ? " & abandoned old cache" : ""));
    }

    public void load(String levelName) {
        if (!levelName.equals(this.levelName)) {
            return;
        }
        options.musicVolume = musicVolume;
        options.soundVolume = soundVolume;
        options.chatColor = chatColor;
        options.chatLink = chatLink;
        options.chatLinkPrompt = chatLinkPrompt;
        Display.setVSyncEnabled(options.vsync = vsync);
        options.renderClouds = renderClouds;
        client.textRenderer.method_960(Language.getInstance().method_638());
        options.invertYMouse = invertYMouse;
        options.touchScreen = touchScreen;
        if (options.fullscreen != fullscreen) {
            if (Display.isActive()) {
                client.toggleFullscreen();
            } else {
                StandardSettings.LOGGER.severe("Could not reset fullscreen mode because window wasn't focused!");
            }
        }
        options.bobView = bobView;
        if (options.anaglyph3d != (options.anaglyph3d = anaglyph3d)) {
            client.field_3813.updateAnaglyph3D();
        }
        options.sensitivity = sensitivity;
        options.fov = fov;
        options.gamma = gamma;
        options.renderDistance = renderDistance;
        options.guiScale = guiScale;
        options.particle = particle;
        options.maxFramerate = maxFramerate;
        options.fancyGraphics = fancyGraphics;
        options.ao = ao;
        if (!language.equals(options.language)) {
            Language.getInstance().method_631(language, false);
            options.language = language;
            client.textRenderer.method_960(Language.getInstance().method_638());
            client.textRenderer.setRightToLeft(Language.hasSpecialCharacters(options.language));
        }
        options.chatVisibility = chatVisibility;
        options.chatOpacity = chatOpacity;
        options.advancedItemTooltips = advancedItemTooltips;
        options.pauseOnLostFocus = pauseOnLostFocus;
        options.chatHeightFocused = chatHeightFocused;
        options.chatHeightUnfocused = chatHeightUnfocused;
        options.chatScale = chatScale;
        options.chatWidth = chatWidth;
        EntityRenderDispatcher.field_5192 = hitboxes;
        options.perspective = perspective;
        ((MinecraftAccessor)client).setOpenProfilerSection(piedirectory);
        options.hudHidden = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.keysAll) {
            keyBinding.code = keysAll[i++];
        }
        KeyBinding.updateKeysByCode();

        StandardSettings.LOGGER.info("Restored cached options for '" + this.levelName + "'");
        this.levelName = null;
    }

}