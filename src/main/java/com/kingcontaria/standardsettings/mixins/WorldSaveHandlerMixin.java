package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.entity.player.PlayerEntity;
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
import java.nio.file.StandardCopyOption;

@Mixin(WorldSaveHandler.class)

public class WorldSaveHandlerMixin {

    @Shadow @Final private File playerDataDir;

    @Inject(method = "savePlayerData", at = @At("TAIL"))
    private void saveOptionsTxt(PlayerEntity playerEntity, CallbackInfo ci){
        try {
            Files.copy(StandardSettings.optionsFile.toPath(), new File(playerDataDir.getParentFile(), "options.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            StandardSettings.LOGGER.error("Couldn't save options.txt to world file", e);
        }
    }

}
