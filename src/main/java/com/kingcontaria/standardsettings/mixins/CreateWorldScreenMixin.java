package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)

public class CreateWorldScreenMixin {

    private static boolean bl = true;

    @Inject(method = "createLevel()V", at = @At("HEAD"))
    private void changeSettings(CallbackInfo info){
        if(bl) {
            StandardSettings.LOGGER.info("Reset to StandardSettings...");
            StandardSettings.load();
            StandardSettings.LOGGER.info("Checking Settings...");
            StandardSettings.checkSettings();
            StandardSettings.client.options.write();
            bl = false;
        }
    }

    @Inject(method = "createLevel", at = @At("TAIL"))
    private void setCondition(CallbackInfo ci){
        if(StandardSettings.client.isWindowFocused()){
            StandardSettings.changeSettingsOnJoin();
        }else {
            StandardSettings.changeOnGainedFocus = true;
        }
        bl = true;
    }

}