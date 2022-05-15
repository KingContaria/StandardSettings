package com.kingcontaria.standardsettings;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StandardSettings implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();
    @Override
    public void onInitialize() {
        LogManager.getLogger().info("StandardSettings Mod initialized");
    }
}