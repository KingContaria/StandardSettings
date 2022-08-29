package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mixin(WorldSaveHandler.class)

public class WorldSaveHandlerMixin {

    @Shadow @Final private File worldDir;
    private boolean isNewWorld;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/File;mkdirs()Z", ordinal = 0))
    private void isNewWorld(File worldsDirectory, String worldName, MinecraftServer server, DataFixer dataFixer, CallbackInfo ci) {
        isNewWorld = !worldDir.exists();
    }

    // saves the standardoptions to world file for verification purposes
    @Inject(method = "<init>", at = @At("TAIL"))
    private void saveStandardoptionsTxt(File worldsDirectory, String worldName, MinecraftServer server, DataFixer dataFixer, CallbackInfo ci) {
        if (isNewWorld && StandardSettings.standardoptionsCache != null) {
            try {
                Files.write(worldDir.toPath().resolve("standardoptions.txt"), String.join(System.lineSeparator(), StandardSettings.standardoptionsCache).getBytes());
                StandardSettings.LOGGER.info("Saved standardoptions.txt to world file");
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to save standardoptions.txt to world file", e);
            }
        }
    }

}
