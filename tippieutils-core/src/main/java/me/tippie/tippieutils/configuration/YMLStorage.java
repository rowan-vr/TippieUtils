package me.tippie.tippieutils.configuration;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * A class for handling YML configuration files
 */
@Getter
public class YMLStorage {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private String configName;


    /**
     * This constructor creates a new YMLStorage object
     * Note that this method uses the hooked plugin instance as Data Folder
     * @param plugin The JavaPlugin instance
     * @param configName The name of the configuration file <b>with</b> .yml at the end
     * @param path The optional path to the configuration file
     */
    public YMLStorage(JavaPlugin plugin, @NotNull String configName, @Nullable String... path) {
        this.plugin = plugin;
        init(configName, path);
    }


    /**
     * This method creates a new configuration file named configName.yml
     * Note that this method uses the hooked plugin instance as Data Folder
     * @param configName The name of the configuration file
     * @param path The optional path to the configuration file
     * @return The FileConfiguration object
     */
    public CompletableFuture<Boolean> init(@NotNull String configName, String... path) {
        String paths = path.length > 0 && path[0] != null ? path[0] : "yml-storage";
        try {
            this.configName = configName;
            File configPath = new File(plugin.getDataFolder(), paths);
            if(!configPath.exists()) {
                boolean suc = configPath.mkdirs();
                if(!suc) throw new IOException("[YMLStorage] Failed to create directory "+configPath);
            }
            this.configFile = new File(configPath, configName);
            if(!configFile.exists()) {
                boolean suc = configFile.getParentFile().mkdirs() && configFile.createNewFile();
                if(suc) plugin.saveResource(configName, false);
            }
            this.config = new YamlConfiguration();
            try {
                config.load(configFile);
                plugin.getLogger().log(Level.FINEST, "[YMLStorage] "+configName + ".yml loaded successfully");
                return CompletableFuture.completedFuture(true);
            } catch(IOException | InvalidConfigurationException e) {
                plugin.getLogger().log(Level.SEVERE, "[YMLStorage] "+plugin.getDataFolder()+"\\"+paths+"\\"+configName + ".yml failed to load!", e);
                return CompletableFuture.completedFuture(false);
            }
        } catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[YMLStorage] Failed to initialize " + configName + ".yml", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * This method saves the configuration file
     * @return Whether the configuration file was saved successfully
     */
    public boolean save() {
        try {
            config.save(configFile);
            return true;
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "[YMLStorage] Failed to save " + configName + ".yml", e);
            return false;
        }
    }
    /**
     * This method gets a {@link ConfigurationSection} or creates a new one if it doesn't exist
     * @param section The name of the section
     * @return The {@link ConfigurationSection} object
     */
    @NotNull public ConfigurationSection getSection(@NotNull String section) {
        ConfigurationSection result = config.getConfigurationSection(section) == null
                ? config.createSection(section)
                : config.getConfigurationSection(section);
        save();
        return result;
    }
    /**
     * This method reloads the {@link FileConfiguration} object.
     * Useful for when the file is changed externally
     */
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }


    // STATIC UTILITY METHODS:


    /**
     * This method creates a new YMLStorage object with the given configuration file name
     * @param plugin The JavaPlugin instance
     * @param configName The name of the configuration file
     * @param path The optional path to the configuration file
     * @return The YMLStorage object
     */
    @NotNull public static YMLStorage createConfig(JavaPlugin plugin, @NotNull String configName, String... path) {
        return new YMLStorage(plugin, configName, path);
    }

}
