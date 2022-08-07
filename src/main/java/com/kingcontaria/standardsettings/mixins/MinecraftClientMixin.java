package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mixin(MinecraftClient.class)

public abstract class MinecraftClientMixin {

    private static boolean bl = true;
    @Shadow public abstract boolean isWindowFocused();

    @Inject(method = "initializeGame", at = @At("RETURN"))
    private void initializeStandardSettings(CallbackInfo ci) {
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

    @Inject(method = "startGame", at = @At("HEAD"))
    private void resetSettings(String fileName, String worldName, LevelInfo levelInfo, CallbackInfo ci) {
        if (!new File("saves", fileName).exists()) {
            if (bl) {
                StandardSettings.changeOnWindowActivation = false;
                StandardSettings.LOGGER.info("Reset to StandardSettings...");
                StandardSettings.load();
                StandardSettings.LOGGER.info("Checking Settings...");
                StandardSettings.checkSettings();
                StandardSettings.options.save();
                bl = false;
            }
        } else {
            StandardSettings.optionsCache.load(fileName);
        }
        StandardSettings.lastQuitWorld = fileName;
    }

    @Inject(method = "startGame", at = @At("RETURN"))
    private void onWorldJoin(String fileName, String worldName, LevelInfo levelInfo, CallbackInfo ci) {
        if (this.isWindowFocused()) {
            StandardSettings.changeSettingsOnJoin();
        } else {
            StandardSettings.changeOnWindowActivation = true;
        }
        bl = true;
    }

}