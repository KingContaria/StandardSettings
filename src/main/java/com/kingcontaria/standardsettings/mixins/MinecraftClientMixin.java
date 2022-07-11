package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Mixin(MinecraftClient.class)

public abstract class MinecraftClientMixin {

    private static boolean bl = true;
    @Shadow public abstract boolean isWindowFocused();

    @Inject(method = "initializeGame", at = @At("RETURN"))
    private void initializeStandardSettings(CallbackInfo ci) {
        if (StandardSettings.standardoptionsFile.exists()) {
            StandardSettings.LOGGER.info("Loading StandardSettings...");
            StandardSettings.load();
            StandardSettings.LOGGER.info("Checking StandardSettings...");
            StandardSettings.checkSettings();
            StandardSettings.options.save();
        } else {
            StandardSettings.LOGGER.info("Creating StandardSettings File...");

            long start = System.nanoTime();

            if (!StandardSettings.optionsFile.exists()) {
                StandardSettings.options.save();
            }
            if (!StandardSettings.standardoptionsFile.getParentFile().exists()) {
                StandardSettings.standardoptionsFile.getParentFile().mkdir();
            }

            try {
                String l = System.lineSeparator();
                Files.copy(StandardSettings.optionsFile.toPath(), StandardSettings.standardoptionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.write(StandardSettings.standardoptionsFile.toPath(), ("hitboxes:" + l + "perspective:" + l + "piedirectory:" + l + "renderDistanceOnWorldJoin:" + l + "fovOnWorldJoin:").getBytes(), StandardOpenOption.APPEND);
                StandardSettings.LOGGER.info("Finished creating StandardSettings File ({} ms)", (System.nanoTime() - start) / 1000000.0f);
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to create StandardSettings File", e);
            }
        }
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    private void resetSettings(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        if (bl) {
            StandardSettings.LOGGER.info("Reset to StandardSettings...");
            StandardSettings.load();
            StandardSettings.LOGGER.info("Checking Settings...");
            StandardSettings.checkSettings();
            StandardSettings.options.save();
            bl = false;
        }
    }

    @Inject(method = "startGame", at = @At("RETURN"))
    private void onWorldJoin(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        if (this.isWindowFocused()) {
            StandardSettings.changeSettingsOnJoin();
        } else {
            StandardSettings.changeOnGainedFocus = true;
        }
        bl = true;
    }

}