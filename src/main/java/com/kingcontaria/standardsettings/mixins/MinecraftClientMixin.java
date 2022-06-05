package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)

public abstract class MinecraftClientMixin {

    @Shadow public GameOptions options;
    @Shadow public abstract boolean isWindowFocused();

    @Inject(method = "initializeGame", at = @At("TAIL"))
    private void loadStandardSettings(CallbackInfo ci){
        StandardSettings.LOGGER.info("Loading StandardSettings...");
        StandardSettings.load();
        StandardSettings.checkSettings();
        this.options.save();
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    private void ResetSettings(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        StandardSettings.LOGGER.info("Reset to StandardSettings...");
        StandardSettings.load();
        StandardSettings.LOGGER.info("Checking Settings...");
        StandardSettings.checkSettings();
        this.options.save();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void onWorldJoin(String string, String levelInfo, LevelInfo par3, CallbackInfo ci){
        if(this.isWindowFocused()){
            StandardSettings.changeSettingsOnJoin();
        }else {
            StandardSettings.changeOnGainedFocus = true;
        }
    }
}