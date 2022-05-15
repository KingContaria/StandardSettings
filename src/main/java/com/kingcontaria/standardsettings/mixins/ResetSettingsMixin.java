package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.ResetSettings;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class ResetSettingsMixin {
    @Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextLong()J"))
    void ResetSettings(ButtonWidget button, CallbackInfo ci) {
        StandardSettings.LOGGER.info("Reset to StandardSettings...");
        ResetSettings.LoadStandardSettings();
    }
}
