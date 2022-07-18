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
import java.nio.file.StandardCopyOption;
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

            if (!StandardSettings.optionsFile.exists()) {
                StandardSettings.options.write();
            }
            if (!StandardSettings.standardoptionsFile.getParentFile().exists()) {
                StandardSettings.standardoptionsFile.getParentFile().mkdir();
            }

            try {
                String l = System.lineSeparator();
                Files.copy(StandardSettings.optionsFile.toPath(), StandardSettings.standardoptionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.write(StandardSettings.standardoptionsFile.toPath(), ("sneaking:" + l + "sprinting:" + l + "chunkborders:" + l + "hitboxes:" + l + "perspective:" + l + "piedirectory:" + l + "renderDistanceOnWorldJoin:" + l + "fovOnWorldJoin:").getBytes(), StandardOpenOption.APPEND);
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
