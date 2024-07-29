package tech.aurasoftware.aurautilitiesplus.file;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Getter
public class YamlFile {

    private final String name;
    private final String path;
    private final String folder;
    private final Plugin plugin;

    private final File file;

    private final YamlConfiguration config;


    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public YamlFile(String name, String path, String folder, Plugin plugin) {
        this.plugin = plugin;
        this.name = name;
        this.path = path;
        this.folder = folder;

        if (this.folder == null) {
            File directory = new File(path);
            if(!directory.exists()){
                directory.mkdir();
            }
            this.file = new File(directory, this.name);
        } else {
            new File(this.path + File.separator + folder).mkdir();
            this.file = new File(this.path + File.separator + folder, this.name);
        }
        if (!this.file.exists()) {
            try {
                this.saveResource();
            } catch (IllegalArgumentException e) {
                this.file.createNewFile();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.config.options().copyDefaults(true);
        this.saveConfig();
    }

    private void saveResource() {
        if (this.folder != null) {
            plugin.saveResource(this.folder + File.separator + this.name, false);
        } else {
            plugin.saveResource(this.name, false);
        }
    }

}
