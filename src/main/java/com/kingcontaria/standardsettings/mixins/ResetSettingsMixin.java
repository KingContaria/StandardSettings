package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.ResetSettings;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)

public class ResetSettingsMixin{

    private static boolean bl = true;

    @Inject(method = "createLevel()V", at = @At("HEAD"))
    private void createLevel(CallbackInfo info){
        if(bl) {
            StandardSettings.LOGGER.info("Reset to StandardSettings...");
            ResetSettings.LoadStandardSettings();
            StandardSettings.LOGGER.info("Checking Settings...");
            ResetSettings.CheckSettings();
            MinecraftClient.getInstance().options.write();
            bl = false;
        }
    }

    @Inject(method = "createLevel", at = @At("TAIL"))
    private void setcondition(CallbackInfo ci){
        bl = true;
    }

}