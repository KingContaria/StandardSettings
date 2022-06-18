package com.kingcontaria.standardsettings.mixins;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)

public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    private static final Identifier SET_STANDARDSETTINGS_BUTTON_TEXTURE = new Identifier("textures/item/writable_book.png");
    private ButtonWidget SetStandardSettingsButton;

    @Inject(method = "initWidgetsNormal", at = @At("HEAD"))
    private void addCustomButton(int y, int spacingY, CallbackInfo ci){
        SetStandardSettingsButton = new ButtonWidget(this.width / 2 + 104, y, 20, 20, "", buttonWidget -> {
            if(Screen.hasShiftDown() && StandardSettings.standardoptionsFile.exists()){
                StandardSettings.LOGGER.info("Opening standardoptions.txt...");
                Util.getOperatingSystem().open(StandardSettings.standardoptionsFile);
            }else {
                StandardSettings.save(StandardSettings.standardoptionsFile);
            }
        });
        this.addButton(SetStandardSettingsButton);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void writableBookOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SET_STANDARDSETTINGS_BUTTON_TEXTURE);
        blit(SetStandardSettingsButton.x+2, SetStandardSettingsButton.y+2, 0.0f, 0.0f, 16, 16, 16, 16);
    }
}