package com.kingcontaria.standardsettings.mixins;
import com.kingcontaria.standardsettings.ResetSettings;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TitleScreen.class})
public class TitleScreenMixin extends Screen {
    private static final Identifier SET_STANDARDSETTINGS_BUTTON_TEXTURE = new Identifier("textures/items/book_writable.png");
    private ButtonWidget SetStandardSettingsButton = null;

    @Inject(method = {"initWidgetsNormal"}, at = {@At("HEAD")})
    private void addCustomButton(int y, int spacingY, CallbackInfo ci){
        SetStandardSettingsButton = new ButtonWidget(420,this.width / 2 + 104, y, 20, 20, "");
        this.buttons.add(SetStandardSettingsButton);
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void writableBookOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTexture(SET_STANDARDSETTINGS_BUTTON_TEXTURE);
        this.drawTexture(SetStandardSettingsButton.x+2, SetStandardSettingsButton.y+2, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    private void standardsettingsButtonPressed(ButtonWidget button, CallbackInfo ci){
        if(button.id == 420){
            StandardSettings.LOGGER.info("Saving StandardSettings...");
            ResetSettings.SetStandardSettings();
        }
    }
}