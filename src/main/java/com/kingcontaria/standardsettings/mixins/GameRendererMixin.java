package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)

public class GameRendererMixin {

    @Inject(method = "method_9775", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseInput;updateMouse()V"))
    private void changeSettingsOnJoin(float par1, CallbackInfo ci) {
        if (StandardSettings.changeOnWindowActivation) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

    @Inject(method = "onResized", at = @At("HEAD"))
    private void changeSettingsOnResize(CallbackInfo ci) {
        if (StandardSettings.changeOnWindowActivation && StandardSettings.changeOnResize) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

}