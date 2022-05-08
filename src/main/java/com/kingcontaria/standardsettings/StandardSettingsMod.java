package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class StandardSettingsMod implements ModInitializer {

    private File instancenumber;
    public static String instancenumberstring;

    @Override
    public void onInitialize() {

        instancenumber = new File(instancenumber, "instancenumber.txt");
        if(instancenumber.exists()) {
            try (BufferedReader bufferedReader = Files.newReader((File) this.instancenumber, (Charset) Charsets.UTF_8);) {
                bufferedReader.lines().forEach(string -> {
                    instancenumberstring = string;
                });
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
