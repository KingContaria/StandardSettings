package com.kingcontaria.standardsettings.mixins;
import com.kingcontaria.standardsettings.ResetSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TitleScreen.class})
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    private static final Identifier SET_STANDARDSETTINGS_BUTTON_TEXTURE = new Identifier("textures/item/writable_book.png");
    private ButtonWidget SetStandardSettingsButton = null;

    @Inject(method = {"initWidgetsNormal"}, at = {@At("HEAD")})
    private void addCustomButton(int y, int spacingY, CallbackInfo ci){
        SetStandardSettingsButton = new ButtonWidget(this.width / 2 + 104, y, 20, 20, new TranslatableText(""), buttonWidget -> ResetSettings.SetStandardSettings());
        this.addButton(SetStandardSettingsButton);
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void writableBookOverlay(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTexture(SET_STANDARDSETTINGS_BUTTON_TEXTURE);
        this.drawTexture(matrices, SetStandardSettingsButton.x+2, SetStandardSettingsButton.y+2, 0.0f, 0.0f, 16, 16, 16, 16);
    }
}