package me.contaria.standardsettings.mixin.fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.contaria.standardsettings.StandardSettings;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @ModifyExpressionValue(
            method = "onKey",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=1",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
                            ordinal = 0
                    )
            )
    )
    private int dontToggleFullscreenOrScreenshotWhenFocusingKeyBinding(int one) {
        if (StandardSettings.config.hasFocusedKeyBinding()) {
            return Integer.MIN_VALUE;
        }
        return one;
    }
}
