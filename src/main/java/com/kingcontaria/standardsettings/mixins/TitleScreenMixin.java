package com.kingcontaria.standardsettings.mixins;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.OperatingSystem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TitleScreen.class)

public class TitleScreenMixin extends Screen {
    private static final Identifier SET_STANDARDSETTINGS_BUTTON_TEXTURE = new Identifier("textures/items/book_writable.png");
    private ButtonWidget SetStandardSettingsButton = null;

    @Inject(method = "initWidgetsNormal", at = @At("HEAD"))
    private void addCustomButton(int y, int spacingY, CallbackInfo ci){
        SetStandardSettingsButton = new ButtonWidget(420,this.width / 2 + 104, y, 20, 20, "");
        this.buttons.add(SetStandardSettingsButton);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void writableBookOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTexture(SET_STANDARDSETTINGS_BUTTON_TEXTURE);
        drawTexture(SetStandardSettingsButton.x+2, SetStandardSettingsButton.y+2, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    private void standardsettingsButtonPressed(ButtonWidget button, CallbackInfo ci){
        if(button.id == 420){
            if(Screen.hasShiftDown() && StandardSettings.standardoptionsFile.exists()){
                String string = StandardSettings.standardoptionsFile.getAbsolutePath();
                if (Util.method_6318() == OperatingSystem.OSX) {
                    try {
                        StandardSettings.LOGGER.info("Opening standardoptions.txt...");
                        Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string});
                    }
                    catch (IOException iOException) {
                        StandardSettings.LOGGER.error("Couldn't open file", iOException);
                    }
                }else if (Util.method_6318() == OperatingSystem.WINDOWS) {
                    String string2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", string);
                    try {
                        StandardSettings.LOGGER.info("Opening standardoptions.txt...");
                        Runtime.getRuntime().exec(string2);
                    }
                    catch (IOException iOException) {
                        StandardSettings.LOGGER.error("Couldn't open file", iOException);
                    }
                }else {
                    StandardSettings.LOGGER.error("Couldn't open file because Operating System isn't supported");
                }
            }else {
                StandardSettings.save(StandardSettings.standardoptionsFile);
            }
        }
    }
}