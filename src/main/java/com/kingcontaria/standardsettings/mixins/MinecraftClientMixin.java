package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;

@Mixin(MinecraftClient.class)

public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initializeStandardSettings(RunArgs args, CallbackInfo ci) {
        if (!StandardSettings.standardoptionsFile.exists()) {
            StandardSettings.LOGGER.info("Creating StandardSettings File...");

            long start = System.nanoTime();

            if (!StandardSettings.standardoptionsFile.getParentFile().exists()) {
                StandardSettings.standardoptionsFile.getParentFile().mkdir();
            }

            try {
                Files.write(StandardSettings.standardoptionsFile.toPath(), StandardSettings.getStandardoptionsTxt().getBytes());
                StandardSettings.LOGGER.info("Finished creating StandardSettings File ({} ms)", (System.nanoTime() - start) / 1000000.0f);
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to create StandardSettings File", e);
            }
        }
    }

    @Inject(method = "onWindowFocusChanged", at = @At("RETURN"))
    private void changeSettingsOnJoin(boolean focused, CallbackInfo ci) {
        if (focused && StandardSettings.changeOnWindowActivation) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    private void changeSettingsOnResize(CallbackInfo ci) {
        if (StandardSettings.changeOnWindowActivation && StandardSettings.changeOnResize) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void cacheOptions(CallbackInfo ci) {
        StandardSettings.changeOnWindowActivation = false;
        try {
            StandardSettings.lastQuitWorld = StandardSettings.client.getServer().getIconFile().getParentFile().getName();
        } catch (Exception e) {
            // empty catch block
        }
    }

}