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
import java.nio.file.StandardOpenOption;

@Mixin(MinecraftClient.class)

public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initializeStandardSettings(RunArgs args, CallbackInfo ci) {
        if (StandardSettings.standardoptionsFile.exists()) {
            StandardSettings.LOGGER.info("Loading StandardSettings...");
            StandardSettings.load();
            StandardSettings.LOGGER.info("Checking StandardSettings...");
            StandardSettings.checkSettings();
            StandardSettings.options.write();
        } else {
            StandardSettings.LOGGER.info("Creating StandardSettings File...");

            long start = System.nanoTime();

            if (!StandardSettings.standardoptionsFile.getParentFile().exists()) {
                StandardSettings.standardoptionsFile.getParentFile().mkdir();
            }

            try {
                Files.write(StandardSettings.standardoptionsFile.toPath(), StandardSettings.getStandardoptionsTxt().getBytes(), StandardOpenOption.CREATE_NEW);
                StandardSettings.LOGGER.info("Finished creating StandardSettings File ({} ms)", (System.nanoTime() - start) / 1000000.0f);
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to create StandardSettings File", e);
            }
        }
    }

    @Inject(method = "onWindowFocusChanged", at = @At("RETURN"))
    private void changeSettingsOnJoin(boolean focused, CallbackInfo ci) {
        if (focused && StandardSettings.changeOnGainedFocus) {
            StandardSettings.changeOnGainedFocus = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

}