package dev.loat.msmp_entity.config;

import dev.loat.config_lib.ConfigManager;
import dev.loat.msmp_entity.config.files.MSMPEntityConfigFile;


public class Config {
    
    private Config() {}
    
    private static final String ROOT_DIRECTORY = "msmp/entity";
    private static final ConfigManager CONFIG_MANAGER = new ConfigManager(ROOT_DIRECTORY);

    public static void register() {
        
        CONFIG_MANAGER.add("config.yml", MSMPEntityConfigFile.class);
    }

    public static MSMPEntityConfigFile getConfig() {

        return CONFIG_MANAGER.get(MSMPEntityConfigFile.class);
    }
}
