package com.kingcontaria.standardsettings.mixins;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

@Mixin(MinecraftClient.class)
public class InstancenumberMixin {

    private static final File instancenumberFile = new File("instancenumber.txt");
    private static final String instancenumber;

    static {
        try {
            BufferedReader bufferedReader = Files.newReader(instancenumberFile, Charsets.UTF_8);
            instancenumber = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(at = @At("TAIL"), method = "updateWindowTitle")
    public void setInstancenumber(CallbackInfo ci) {
        if(instancenumber != null) {
            MinecraftClient.getInstance().getWindow().setTitle("Instance " + instancenumber);
        }
    }
}