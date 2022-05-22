package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.ResetSettings;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ResetSettingsMixin {
    @Inject(method = "startGame", at = @At("HEAD"))
    void ResetSettings(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        StandardSettings.LOGGER.info("Reset to StandardSettings...");
        ResetSettings.LoadStandardSettings();
        StandardSettings.LOGGER.info("Checking Settings...");
        ResetSettings.CheckSettings();
        MinecraftClient.getInstance().options.save();
    }
}