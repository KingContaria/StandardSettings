package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.ResetSettings;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CreateWorldScreen.class)

public class ResetSettingsMixin{

    @Inject(at = @At("HEAD"), method = "createLevel")
    private void createLevel(CallbackInfo info){
        new ResetSettings();
    }

}



