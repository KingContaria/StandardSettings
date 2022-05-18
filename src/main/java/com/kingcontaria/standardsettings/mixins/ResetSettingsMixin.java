package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.ResetSettings;
import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ResetSettingsMixin {
    @Shadow private ClientConnection clientConnection;

    @Inject(method = "startGame", at = @At("HEAD"))
    void ResetSettings(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        StandardSettings.LOGGER.info("Reset to StandardSettings...");
        ResetSettings.LoadStandardSettings();
    }
    @Inject(method = "startGame", at = @At("TAIL"))
    void ChangeRDonJoin(String string, String levelInfo, LevelInfo par3, CallbackInfo ci) {
        if(ResetSettings.changerdonjoin) {
            StandardSettings.LOGGER.info("Reset RD to " + ResetSettings.rdonworldjoin + "on joining the world.");
            MinecraftClient.getInstance().options.viewDistance = ResetSettings.rdonworldjoin;
        }
    }
}