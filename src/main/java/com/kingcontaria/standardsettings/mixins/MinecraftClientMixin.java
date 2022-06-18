package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftClient.class)

public class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadStandardSettings(RunArgs args, CallbackInfo ci){
        File oldStandardoptionsFile = new File("standardoptions.txt");
        if(!StandardSettings.standardoptionsFile.exists() && oldStandardoptionsFile.exists()){
            StandardSettings.LOGGER.info("Moving standardoptions.txt to config folder...");
            if(!StandardSettings.standardoptionsFile.getParentFile().exists()) StandardSettings.standardoptionsFile.getParentFile().mkdir();
            oldStandardoptionsFile.renameTo(StandardSettings.standardoptionsFile);
        }
        StandardSettings.LOGGER.info("Loading StandardSettings...");
        StandardSettings.load();
        StandardSettings.LOGGER.info("Checking StandardSettings...");
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
