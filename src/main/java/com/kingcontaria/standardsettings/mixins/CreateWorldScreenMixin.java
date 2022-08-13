package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)

public class CreateWorldScreenMixin {
    private static boolean bl = true;

    @Inject(method = "method_18847", at = @At("HEAD"))
    private void resetSettings(CallbackInfo info) {
        if (bl) {
            StandardSettings.LOGGER.info("Reset to StandardSettings...");
            StandardSettings.load();
            StandardSettings.LOGGER.info("Checking Settings...");
            StandardSettings.checkSettings();
            StandardSettings.client.options.save();
            bl = false;
        }
    }

    @Inject(method = "method_18847", at = @At("RETURN"))
    private void onWorldJoin(CallbackInfo ci) {
        if (StandardSettings.client.isWindowFocused()) {
            StandardSettings.changeSettingsOnJoin();
        } else {
            StandardSettings.changeOnWindowActivation = true;
        }
        bl = true;
    }

}