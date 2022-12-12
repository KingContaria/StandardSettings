package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    // initialize StandardSettings, doesn't use ClientModInitializer because GameOptions need to be initialized first
    @Inject(method = "<init>", at = @At("RETURN"))
    private void standardSettings_initialize(CallbackInfo ci) {
        StandardSettings.initialize();
    }

    // activate OnWorldJoin options when focusing the instance
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    private void standardSettings_changeSettingsOnFocus(CallbackInfo ci) {
        if (StandardSettings.changeOnFocus) {
            StandardSettings.onWorldJoin();
        }
    }

    // activate OnWorldJoin Options when resizing the instance
    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    private void standardSettings_changeSettingsOnResize(CallbackInfo ci) {
        if (StandardSettings.changeOnFocus & StandardSettings.changeOnResize) {
            StandardSettings.onWorldJoin();
        }
    }

    // try loading OptionsCache when joining old worlds
    @Inject(method = "startIntegratedServer(Ljava/lang/String;)V", at = @At("HEAD"))
    private void standardSettings_loadCache(String worldName, CallbackInfo ci) {
        StandardSettings.optionsCache.load(worldName);
        StandardSettings.lastWorld = worldName;
    }

    // reset settings to standardoptions at the start of world creation
    @Inject(method = "method_29607", at = @At("HEAD"))
    private void standardSettings_resetSettings(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        StandardSettings.load();
        StandardSettings.inPreview = true;
        StandardSettings.lastWorld = worldName;
    }

    // gets called when the world finishes loading
    @Inject(method = "method_29607", at = @At("TAIL"))
    private void standardSettings_onWorldLoad(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        StandardSettings.onWorldLoad(worldName);
        StandardSettings.inPreview = false;
    }
}