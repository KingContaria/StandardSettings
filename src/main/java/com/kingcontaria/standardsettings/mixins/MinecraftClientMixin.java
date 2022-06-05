package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)

public class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadStandardSettings(RunArgs args, CallbackInfo ci){
        StandardSettings.LOGGER.info("Loading StandardSettings...");
        StandardSettings.load();
        StandardSettings.checkSettings();
        this.options.write();
    }

    @Inject(method = "onWindowFocusChanged", at = @At("TAIL"))
    private void changeSettingsOnJoin(boolean focused, CallbackInfo ci){
        if(focused && StandardSettings.changeOnGainedFocus){
            StandardSettings.changeOnGainedFocus = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

}