package com.kingcontaria.standardsettings;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class StandardSettings implements ModInitializer {
    public static Logger LOGGER;
    private static final File instancenumberFile = new File("instancenumber.txt");
    public static String instancenumber;
    @Override
    public void onInitialize() {
        LOGGER = LogManager.getLogger();
        LogManager.getLogger().info("StandardSettings Mod initialized");
        if(instancenumberFile.exists()) {
            try {
                BufferedReader bufferedReader = Files.newReader(instancenumberFile, Charsets.UTF_8);
                instancenumber = bufferedReader.readLine();
            } catch (IOException e) {
                LOGGER.error("instancenumber.txt is missing");
            }
            if (instancenumber != null) {
                LOGGER.info("Renamed Window: Instance " + instancenumber);
            }
        }
    }
}